package br.com.valemorar.domain.anucio.service;

import br.com.valemorar.domain.anucio.Anuncio;
import br.com.valemorar.domain.anucio.dto.AnuncianteContatoDTO;
import br.com.valemorar.domain.anucio.dto.AnuncioCreateDTO;
import br.com.valemorar.domain.anucio.dto.AnuncioPublicacaoDTO;
import br.com.valemorar.domain.anucio.dto.AnuncioResponseDTO;
import br.com.valemorar.domain.anucio.enums.StatusAnuncioEnum;
import br.com.valemorar.domain.anucio.repository.AnuncioRepository;
import br.com.valemorar.domain.endereco.Endereco;
import br.com.valemorar.domain.endereco.dto.EnderecoResponseDTO;
import br.com.valemorar.domain.endereco.repository.EnderecoRepository;
import br.com.valemorar.domain.endereco.service.EnderecoService;
import br.com.valemorar.domain.foto_imovel.FotoImovel;
import br.com.valemorar.domain.foto_imovel.dto.FotoImovelResponseDTO;
import br.com.valemorar.domain.foto_imovel.repository.FotoImovelRepository;
import br.com.valemorar.domain.imovel.Imovel;
import br.com.valemorar.domain.imovel.dto.ImovelCreateDTO;
import br.com.valemorar.domain.imovel.dto.ImovelResponseDTO;
import br.com.valemorar.domain.imovel.service.ImovelService;
import br.com.valemorar.domain.locador.Locador;
import br.com.valemorar.domain.locador.repository.LocadorRepository;
import br.com.valemorar.domain.usuario.Usuario;
import br.com.valemorar.domain.usuario.enums.PerfilEnum;
import br.com.valemorar.domain.usuario.repository.UsuarioRepository;
import br.com.valemorar.infra.SecurityUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class AnuncioService {

    private static final String MENSAGEM_NAO_ENCONTRADO = "Anúncio não encontrado";
    private static final int DIAS_VALIDADE = 90;
    private static final List<StatusAnuncioEnum> STATUS_OCUPANDO_IMOVEL = List.of(
            StatusAnuncioEnum.ATIVO, StatusAnuncioEnum.IN_REVIEW, StatusAnuncioEnum.MANUAL_REVIEW_REQUIRED);

    private final AnuncioRepository anuncioRepository;
    private final ImovelService imovelService;
    private final EnderecoService enderecoService;
    private final EnderecoRepository enderecoRepository;
    private final FotoImovelRepository fotoRepository;
    private final UsuarioRepository usuarioRepository;
    private final LocadorRepository locadorRepository;

    public AnuncioService(AnuncioRepository anuncioRepository, ImovelService imovelService,
            EnderecoService enderecoService, EnderecoRepository enderecoRepository,
            FotoImovelRepository fotoRepository, UsuarioRepository usuarioRepository,
            LocadorRepository locadorRepository) {
        this.anuncioRepository = anuncioRepository;
        this.imovelService = imovelService;
        this.enderecoService = enderecoService;
        this.enderecoRepository = enderecoRepository;
        this.fotoRepository = fotoRepository;
        this.usuarioRepository = usuarioRepository;
        this.locadorRepository = locadorRepository;
    }

    @Transactional
    public AnuncioResponseDTO criar(AnuncioCreateDTO dto) {
        Imovel imovel = imovelService.buscarProprio(dto.imovelId());

        if (anuncioRepository.existsByImovelIdAndStatusIn(imovel.getId(), STATUS_OCUPANDO_IMOVEL)) {
            throw new IllegalArgumentException("Já existe um anúncio ativo ou em análise para este imóvel");
        }

        Anuncio anuncio = new Anuncio();
        anuncio.setImovel(imovel);
        anuncio.setAnuncianteId(imovel.getLocadorId());
        anuncio.setValor(dto.valor());
        anuncio.setModalidade(dto.modalidade());
        anuncio.setTags(normalizarTags(dto.tags()));
        anuncio.setNotaMedia(BigDecimal.ZERO);
        anuncio.setTotalAvaliacoes(0);
        // Todo anúncio novo passa pela revisão automática (RevisaoAnuncioService) antes de ir ao ar
        anuncio.setStatus(StatusAnuncioEnum.IN_REVIEW);
        anuncio.setPublicadoEm(LocalDateTime.now());
        anuncio.setExpiraEm(dto.expiraEm() != null ? dto.expiraEm() : LocalDateTime.now().plusDays(DIAS_VALIDADE));
        anuncio.setAtualizadoEm(LocalDateTime.now());

        return paraDTO(anuncioRepository.save(anuncio));
    }

    /** Publica um anúncio completo (endereço + imóvel + fotos + anúncio) de forma atômica. */
    @Transactional
    public AnuncioResponseDTO publicar(AnuncioPublicacaoDTO dto) {
        EnderecoResponseDTO endereco = enderecoService.criar(dto.endereco());
        ImovelResponseDTO imovel = imovelService.criar(paraImovelDTO(dto, endereco.id()));
        salvarFotos(imovel.id(), dto.fotos());

        return criar(new AnuncioCreateDTO(imovel.id(), dto.valor(), dto.modalidade(), dto.tags(),
                null, null));
    }

    /** Atualiza um anúncio completo (endereço + imóvel + fotos + anúncio) de forma atômica. */
    @Transactional
    public AnuncioResponseDTO atualizarPublicacao(UUID id, AnuncioPublicacaoDTO dto) {
        Anuncio anuncio = buscarProprio(id);
        Imovel imovel = anuncio.getImovel();

        enderecoService.atualizar(imovel.getEnderecoId(), dto.endereco());
        imovelService.atualizar(imovel.getId(), paraImovelDTO(dto, imovel.getEnderecoId()));
        fotoRepository.deleteByImovelId(imovel.getId());
        salvarFotos(imovel.getId(), dto.fotos());

        anuncio.setValor(dto.valor());
        anuncio.setModalidade(dto.modalidade());
        anuncio.setTags(normalizarTags(dto.tags()));
        voltarParaRevisao(anuncio);
        anuncio.setAtualizadoEm(LocalDateTime.now());
        return paraDTO(anuncioRepository.save(anuncio));
    }

    /** Vitrine pública: apenas anúncios ativos. */
    @Transactional(readOnly = true)
    public Page<AnuncioResponseDTO> listarTodos(Pageable pageable) {
        return paraDTO(anuncioRepository.findByStatus(StatusAnuncioEnum.ATIVO, pageable));
    }

    @Transactional(readOnly = true)
    public Page<AnuncioResponseDTO> buscarComFiltros(String tipoImovel, BigDecimal precoMin, BigDecimal precoMax,
            Integer quartos, String cidade, String referencia, List<String> tags, Pageable pageable) {
        List<String> tagsNormalizadas = tags == null || tags.isEmpty() ? null
                : tags.stream().map(String::toLowerCase).toList();
        return paraDTO(anuncioRepository.buscarComFiltros(tipoImovel, precoMin, precoMax, quartos,
                textoOuNulo(cidade), padraoLike(referencia), tagsNormalizadas, pageable));
    }

    @Transactional(readOnly = true)
    public AnuncioResponseDTO buscarPorId(UUID id) {
        return paraDTO(buscarVisivel(id));
    }

    /** Contato do anunciante: o endpoint exige login (SecurityConfig); o anúncio precisa estar visível. */
    @Transactional(readOnly = true)
    public AnuncianteContatoDTO buscarContatoAnunciante(UUID anuncioId) {
        Anuncio anuncio = buscarVisivel(anuncioId);
        Usuario anunciante = usuarioRepository.findById(anuncio.getAnuncianteId())
                .orElseThrow(() -> new IllegalArgumentException("Anunciante não encontrado"));
        Locador locador = locadorRepository.findById(anunciante.getId()).orElse(null);

        return new AnuncianteContatoDTO(
                anunciante.getNome(),
                anunciante.getEmail(),
                anunciante.getFotoPerfil(),
                locador != null ? locador.getTelefone() : null,
                locador != null ? locador.getWhatsapp() : null);
    }

    /** Anúncios fora do ar (pausados, alugados...) só são visíveis para o dono ou admin. */
    private Anuncio buscarVisivel(UUID id) {
        Anuncio anuncio = anuncioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(MENSAGEM_NAO_ENCONTRADO));
        if (anuncio.getStatus() == StatusAnuncioEnum.ATIVO) {
            return anuncio;
        }
        boolean podeVer = SecurityUtils.getUsuarioAutenticadoOpcional()
                .map(u -> u.getId().equals(anuncio.getAnuncianteId()) || u.getPerfil() == PerfilEnum.ROLE_ADMIN)
                .orElse(false);
        if (!podeVer) {
            throw new IllegalArgumentException(MENSAGEM_NAO_ENCONTRADO);
        }
        return anuncio;
    }

    @Transactional(readOnly = true)
    public Page<AnuncioResponseDTO> buscarPorAnunciante(UUID anuncianteId, Pageable pageable) {
        SecurityUtils.exigirDonoOuAdmin(anuncianteId);
        return paraDTO(anuncioRepository.findByAnuncianteId(anuncianteId, pageable));
    }

    @Transactional
    public AnuncioResponseDTO atualizar(UUID id, AnuncioCreateDTO dto) {
        Anuncio anuncio = buscarProprio(id);
        Imovel imovel = imovelService.buscarProprio(dto.imovelId());

        anuncio.setImovel(imovel);
        anuncio.setValor(dto.valor());
        anuncio.setModalidade(dto.modalidade());
        anuncio.setTags(normalizarTags(dto.tags()));

        if (dto.status() != null) {
            mudarStatus(anuncio, dto.status());
        }
        voltarParaRevisao(anuncio);
        if (dto.expiraEm() != null) {
            anuncio.setExpiraEm(dto.expiraEm());
        }
        anuncio.setAtualizadoEm(LocalDateTime.now());

        return paraDTO(anuncioRepository.save(anuncio));
    }

    @Transactional
    public AnuncioResponseDTO alterarStatus(UUID id, String status) {
        Anuncio anuncio = buscarProprio(id);

        mudarStatus(anuncio, StatusAnuncioEnum.from(status));
        anuncio.setAtualizadoEm(LocalDateTime.now());

        return paraDTO(anuncioRepository.save(anuncio));
    }

    @Transactional
    public AnuncioResponseDTO renovarAnuncio(UUID id) {
        Anuncio anuncio = buscarProprio(id);

        anuncio.setExpiraEm(LocalDateTime.now().plusDays(DIAS_VALIDADE));
        mudarStatus(anuncio, StatusAnuncioEnum.ATIVO);
        anuncio.setAtualizadoEm(LocalDateTime.now());

        return paraDTO(anuncioRepository.save(anuncio));
    }

    @Transactional
    public void deletar(UUID id) {
        anuncioRepository.delete(buscarProprio(id));
    }

    /** Fila da revisão manual (admin): reprovados pela revisão automática, com os motivos em motivoRevisao. */
    @Transactional(readOnly = true)
    public Page<AnuncioResponseDTO> listarRevisaoManual(Pageable pageable) {
        SecurityUtils.exigirAdmin();
        return paraDTO(anuncioRepository.findByStatus(StatusAnuncioEnum.MANUAL_REVIEW_REQUIRED, pageable));
    }

    /** Só admin tira um anúncio da revisão (aprovar = ATIVO) ou o coloca direto em revisão manual. */
    private void mudarStatus(Anuncio anuncio, StatusAnuncioEnum novo) {
        if (novo == anuncio.getStatus()) {
            return;
        }
        if ((anuncio.getStatus().emRevisao() || novo == StatusAnuncioEnum.MANUAL_REVIEW_REQUIRED)
                && !SecurityUtils.isAdmin()) {
            throw new IllegalArgumentException("Anúncio em análise: aguarde a revisão para alterar o status");
        }
        if (novo == StatusAnuncioEnum.ATIVO) {
            anuncio.setMotivoRevisao(null);
        }
        anuncio.setStatus(novo);
    }

    /** Edição de anúncio no ar volta para a revisão automática (evita trocar o conteúdo depois de aprovado). */
    private void voltarParaRevisao(Anuncio anuncio) {
        if (anuncio.getStatus() == StatusAnuncioEnum.ATIVO && !SecurityUtils.isAdmin()) {
            anuncio.setStatus(StatusAnuncioEnum.IN_REVIEW);
        }
    }

    private Anuncio buscarProprio(UUID id) {
        Anuncio anuncio = anuncioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(MENSAGEM_NAO_ENCONTRADO));
        SecurityUtils.exigirDonoOuAdmin(anuncio.getAnuncianteId());
        return anuncio;
    }

    private ImovelCreateDTO paraImovelDTO(AnuncioPublicacaoDTO dto, UUID enderecoId) {
        String titulo = dto.titulo() != null && !dto.titulo().isBlank()
                ? dto.titulo()
                : dto.tipoImovel() + " em " + dto.endereco().cidade();
        return new ImovelCreateDTO(null, enderecoId, titulo, null, dto.tipoImovel(), null, null,
                dto.quartos(), null, null, null, null, null, null, dto.valorCondominio(), dto.valorIptu());
    }

    private void salvarFotos(UUID imovelId, List<AnuncioPublicacaoDTO.Foto> fotos) {
        if (fotos == null || fotos.isEmpty()) {
            return;
        }
        // Apenas uma capa: a primeira marcada, ou a primeira foto
        int indiceCapa = 0;
        for (int i = 0; i < fotos.size(); i++) {
            if (Boolean.TRUE.equals(fotos.get(i).capa())) {
                indiceCapa = i;
                break;
            }
        }
        List<FotoImovel> entidades = new ArrayList<>();
        for (int i = 0; i < fotos.size(); i++) {
            FotoImovel foto = new FotoImovel();
            foto.setImovelId(imovelId);
            foto.setUrl(fotos.get(i).url());
            foto.setCapa(i == indiceCapa);
            foto.setOrdem(i);
            foto.setCriadoEm(LocalDateTime.now());
            entidades.add(foto);
        }
        fotoRepository.saveAll(entidades);
    }

    private String textoOuNulo(String valor) {
        return valor == null || valor.isBlank() ? null : valor.trim();
    }

    /** "Centro" -> "%centro%", escapando curingas digitados pelo usuário. */
    private String padraoLike(String valor) {
        String texto = textoOuNulo(valor);
        if (texto == null) {
            return null;
        }
        return "%" + texto.toLowerCase().replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_") + "%";
    }

    private List<String> normalizarTags(List<String> tags) {
        return tags == null ? new ArrayList<>()
                : new ArrayList<>(tags.stream().map(String::trim).filter(t -> !t.isEmpty()).distinct().toList());
    }

    private AnuncioResponseDTO paraDTO(Anuncio anuncio) {
        return paraDTO(new PageImpl<>(List.of(anuncio))).getContent().get(0);
    }

    /** Converte uma página carregando endereços e fotos em lote (evita N+1). */
    private Page<AnuncioResponseDTO> paraDTO(Page<Anuncio> pagina) {
        List<Imovel> imoveis = pagina.getContent().stream().map(Anuncio::getImovel).filter(Objects::nonNull).toList();

        Map<UUID, Endereco> enderecos = enderecoRepository
                .findAllById(imoveis.stream().map(Imovel::getEnderecoId).filter(Objects::nonNull).distinct().toList())
                .stream().collect(Collectors.toMap(Endereco::getId, Function.identity()));

        Map<UUID, List<FotoImovelResponseDTO>> fotos = fotoRepository
                .findByImovelIdInOrderByOrdemAsc(imoveis.stream().map(Imovel::getId).toList())
                .stream().collect(Collectors.groupingBy(FotoImovel::getImovelId,
                        Collectors.mapping(FotoImovelResponseDTO::fromEntity, Collectors.toList())));

        return pagina.map(anuncio -> {
            Imovel imovel = anuncio.getImovel();
            return AnuncioResponseDTO.fromEntity(anuncio,
                    imovel != null ? enderecos.get(imovel.getEnderecoId()) : null,
                    imovel != null ? fotos.get(imovel.getId()) : null);
        });
    }
}
