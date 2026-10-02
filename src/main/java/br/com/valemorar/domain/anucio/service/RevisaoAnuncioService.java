package br.com.valemorar.domain.anucio.service;

import br.com.valemorar.domain.anucio.Anuncio;
import br.com.valemorar.domain.anucio.enums.StatusAnuncioEnum;
import br.com.valemorar.domain.anucio.repository.AnuncioRepository;
import br.com.valemorar.domain.denuncia.repository.DenunciaRepository;
import br.com.valemorar.domain.endereco.Endereco;
import br.com.valemorar.domain.endereco.repository.EnderecoRepository;
import br.com.valemorar.domain.foto_imovel.FotoImovel;
import br.com.valemorar.domain.foto_imovel.repository.FotoImovelRepository;
import br.com.valemorar.domain.imovel.Imovel;
import br.com.valemorar.domain.imovel.repository.ImovelRepository;
import br.com.valemorar.domain.usuario.enums.StatusUsuarioEnum;
import br.com.valemorar.domain.usuario.repository.UsuarioRepository;
import br.com.valemorar.infra.ArmazenamentoArquivos;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.Normalizer;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

/**
 * Revisão automática dos anúncios IN_REVIEW: aprova (ATIVO) ou manda para MANUAL_REVIEW_REQUIRED
 * com os motivos em motivoRevisao. Também calcula o índice de valor usado no ranking "abaixo do mercado".
 * ponytail: sem lock distribuído; com mais de uma instância da aplicação, usar ShedLock ou SELECT ... FOR UPDATE SKIP LOCKED
 */
@Service
public class RevisaoAnuncioService {

    private static final Logger log = LoggerFactory.getLogger(RevisaoAnuncioService.class);

    static final BigDecimal VALOR_MINIMO = new BigDecimal("100");
    /** Índice acima disso = preço/quarto abaixo da metade da mediana: bom demais para ser verdade. */
    static final BigDecimal INDICE_SUSPEITO = new BigDecimal("2");
    static final int MIN_COMPARAVEIS = 3;
    static final int MAX_QUARTOS = 20;
    static final int MAX_ANUNCIOS_24H = 5;
    static final int MAX_DENUNCIAS = 3;
    static final int MIN_DESCRICAO_COPIADA = 50;

    // Sem acentos e em minúsculas: o texto do anúncio é normalizado antes da comparação
    // ponytail: lista fixa de termos; trocar por classificador (ou LLM) se os golpistas contornarem
    static final List<String> TERMOS_GOLPE = List.of(
            "pix antecipado", "pagamento antecipado", "deposito antecipado", "adiantamento", "sinal para reservar",
            "taxa de reserva", "pague antes", "transferencia antes", "western union", "moneygram",
            "sem visita", "nao precisa visitar", "nao e possivel visitar", "envio as chaves", "envio a chave",
            "pelos correios", "estou fora do pais", "moro no exterior", "urgente, so hoje");

    private final AnuncioRepository anuncioRepository;
    private final FotoImovelRepository fotoRepository;
    private final ImovelRepository imovelRepository;
    private final EnderecoRepository enderecoRepository;
    private final UsuarioRepository usuarioRepository;
    private final DenunciaRepository denunciaRepository;
    private final ArmazenamentoArquivos armazenamento;
    private final TransactionTemplate transacao;

    public RevisaoAnuncioService(AnuncioRepository anuncioRepository, FotoImovelRepository fotoRepository,
            ImovelRepository imovelRepository, EnderecoRepository enderecoRepository,
            UsuarioRepository usuarioRepository, DenunciaRepository denunciaRepository,
            ArmazenamentoArquivos armazenamento, TransactionTemplate transacao) {
        this.anuncioRepository = anuncioRepository;
        this.fotoRepository = fotoRepository;
        this.imovelRepository = imovelRepository;
        this.enderecoRepository = enderecoRepository;
        this.usuarioRepository = usuarioRepository;
        this.denunciaRepository = denunciaRepository;
        this.armazenamento = armazenamento;
        this.transacao = transacao;
    }

    @Scheduled(fixedDelayString = "${app.revisao.intervalo-ms:60000}")
    public void revisarPendentes() {
        transacao.executeWithoutResult(s -> hashearFotosPendentes());

        for (Anuncio pendente : anuncioRepository.findTop50ByStatusOrderByAtualizadoEmAsc(StatusAnuncioEnum.IN_REVIEW)) {
            UUID id = pendente.getId();
            try {
                transacao.executeWithoutResult(s -> revisar(anuncioRepository.findById(id).orElseThrow()));
            } catch (RuntimeException e) {
                // Falha na revisão nunca aprova: o anúncio vai para a fila manual
                log.error("Erro na revisão automática do anúncio {}", id, e);
                transacao.executeWithoutResult(s -> anuncioRepository.findById(id).ifPresent(a ->
                        concluir(a, List.of("Erro na revisão automática: " + e.getMessage()))));
            }
        }
    }

    /** Fotos sem hash (novas ou anteriores a esta revisão) entram na comparação com os outros anunciantes. */
    private void hashearFotosPendentes() {
        // ponytail: fotos locais apagadas ficam com hash nulo e são relidas a cada execução
        List<FotoImovel> pendentes = fotoRepository.findByHashIsNull();
        pendentes.forEach(f -> f.setHash(f.getUrl() != null ? armazenamento.hash(f.getUrl()) : null));
        fotoRepository.saveAll(pendentes);
    }

    void revisar(Anuncio anuncio) {
        Imovel imovel = anuncio.getImovel();
        UUID donoId = anuncio.getAnuncianteId();
        BigDecimal valor = anuncio.getValor() != null ? anuncio.getValor() : BigDecimal.ZERO;
        int quartos = imovel.getQuartos() != null ? imovel.getQuartos() : 0;
        List<String> problemas = new ArrayList<>();

        // Preço e quartos
        if (valor.compareTo(VALOR_MINIMO) < 0) {
            problemas.add("Preço zerado ou abaixo de R$ " + VALOR_MINIMO);
        }
        if (quartos <= 0) {
            problemas.add("Imóvel sem quartos");
        } else if (quartos > MAX_QUARTOS) {
            problemas.add("Número de quartos fora do padrão (" + quartos + ")");
        }

        // Fotos: existência e reaproveitamento de imagens de outros anunciantes (conteúdo roubado)
        // ponytail: SHA-256 só pega a mesma imagem byte a byte; usar hash perceptual (pHash) se recortes/compressão burlarem
        List<FotoImovel> fotos = fotoRepository.findByImovelIdOrderByOrdemAsc(imovel.getId());
        List<String> hashes = fotos.stream().map(FotoImovel::getHash).filter(Objects::nonNull).toList();
        if (fotos.isEmpty()) {
            problemas.add("Anúncio sem fotos");
        } else if (hashes.size() < fotos.size()) {
            problemas.add("Foto do anúncio não encontrada no armazenamento");
        }
        if (!hashes.isEmpty() && fotoRepository.existsHashDeOutroDono(hashes, donoId)) {
            problemas.add("Fotos já usadas em anúncio de outro usuário");
        }

        String descricao = imovel.getDescricao();
        if (descricao != null && descricao.strip().length() >= MIN_DESCRICAO_COPIADA
                && imovelRepository.existsByDescricaoAndLocadorIdNot(descricao, donoId)) {
            problemas.add("Descrição copiada de anúncio de outro usuário");
        }

        // Linguagem de golpe
        String texto = String.join(" ", Stream.concat(Stream.of(imovel.getTitulo(), descricao),
                anuncio.getTags() != null ? anuncio.getTags().stream() : Stream.empty())
                .filter(Objects::nonNull).toList());
        termoGolpe(texto).ifPresent(t -> problemas.add("Linguagem suspeita de golpe: \"" + t + "\""));

        // Qualidade e consistência
        if (imovel.getTitulo() == null || imovel.getTitulo().strip().length() < 10) {
            problemas.add("Título muito curto");
        }
        if (imovel.getSuites() != null && imovel.getSuites() > quartos) {
            problemas.add("Mais suítes do que quartos");
        }
        BigDecimal encargos = Objects.requireNonNullElse(imovel.getValorCondominio(), BigDecimal.ZERO)
                .add(Objects.requireNonNullElse(imovel.getValorIptu(), BigDecimal.ZERO));
        if (valor.signum() > 0 && encargos.compareTo(valor) > 0) {
            problemas.add("Condomínio + IPTU maiores que o valor do anúncio");
        }

        // Comportamento do anunciante
        usuarioRepository.findById(donoId)
                .filter(u -> u.getStatus() != StatusUsuarioEnum.ATIVO)
                .ifPresent(u -> problemas.add("Anunciante com conta " + u.getStatus()));
        long recentes = anuncioRepository.countByAnuncianteIdAndPublicadoEmAfter(donoId, LocalDateTime.now().minusHours(24));
        if (recentes > MAX_ANUNCIOS_24H) {
            problemas.add(recentes + " anúncios criados nas últimas 24h");
        }
        long denuncias = denunciaRepository.contarContraAnunciante(donoId);
        if (denuncias >= MAX_DENUNCIAS) {
            problemas.add("Anunciante com " + denuncias + " denúncias");
        }

        // Preço vs. mercado + ranking
        anuncio.setIndiceValor(null);
        anuncio.setEconomiaMercado(null);
        String cidade = enderecoRepository.findById(imovel.getEnderecoId()).map(Endereco::getCidade).orElse(null);
        if (quartos > 0 && valor.signum() > 0 && cidade != null) {
            List<BigDecimal> comparaveis = anuncioRepository.precosPorQuartoComparaveis(
                    anuncio.getId(), anuncio.getModalidade(), cidade);
            if (comparaveis.size() >= MIN_COMPARAVEIS) {
                BigDecimal mediana = mediana(comparaveis);
                BigDecimal indice = indiceValor(mediana, valor, quartos);
                anuncio.setIndiceValor(indice);
                anuncio.setEconomiaMercado(mediana.multiply(BigDecimal.valueOf(quartos)).subtract(valor)
                        .setScale(2, RoundingMode.HALF_UP));
                if (indice.compareTo(INDICE_SUSPEITO) > 0) {
                    problemas.add("Preço muito abaixo do mercado (menos da metade da mediana de " + cidade + ")");
                }
            }
        }

        concluir(anuncio, problemas);
    }

    private void concluir(Anuncio anuncio, List<String> problemas) {
        anuncio.setStatus(problemas.isEmpty() ? StatusAnuncioEnum.ATIVO : StatusAnuncioEnum.MANUAL_REVIEW_REQUIRED);
        anuncio.setMotivoRevisao(problemas.isEmpty() ? null : String.join("\n", problemas));
        anuncio.setAtualizadoEm(LocalDateTime.now());
        anuncioRepository.save(anuncio);
    }

    /** value_index = mediana do preço/quarto dos comparáveis ÷ preço/quarto deste anúncio. */
    static BigDecimal indiceValor(BigDecimal medianaPorQuarto, BigDecimal valor, int quartos) {
        return medianaPorQuarto.multiply(BigDecimal.valueOf(quartos)).divide(valor, 4, RoundingMode.HALF_UP);
    }

    static BigDecimal mediana(List<BigDecimal> valores) {
        List<BigDecimal> ordenados = valores.stream().sorted().toList();
        int meio = ordenados.size() / 2;
        return ordenados.size() % 2 == 1 ? ordenados.get(meio)
                : ordenados.get(meio - 1).add(ordenados.get(meio)).divide(BigDecimal.TWO, 2, RoundingMode.HALF_UP);
    }

    static Optional<String> termoGolpe(String texto) {
        String normalizado = Normalizer.normalize(texto.toLowerCase(), Normalizer.Form.NFD).replaceAll("\\p{M}", "");
        return TERMOS_GOLPE.stream().filter(normalizado::contains).findFirst();
    }
}
