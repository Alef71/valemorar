package br.com.valemorar.domain.anucio.service;

import br.com.valemorar.domain.anucio.Anuncio;
import br.com.valemorar.domain.anucio.dto.AnuncioCreateDTO;
import br.com.valemorar.domain.anucio.dto.AnuncioResponseDTO;
import br.com.valemorar.domain.anucio.repository.AnuncioRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class AnuncioService {

    private static final String STATUS_ATIVO = "ATIVO";
    private static final String MENSAGEM_NAO_ENCONTRADO = "Anúncio não encontrado";

    private final AnuncioRepository anuncioRepository;

    public AnuncioService(AnuncioRepository anuncioRepository) {
        this.anuncioRepository = anuncioRepository;
    }

    @Transactional
    public AnuncioResponseDTO criar(AnuncioCreateDTO dto) {
        String statusInicial = dto.status() != null ? dto.status() : STATUS_ATIVO;

        if (STATUS_ATIVO.equalsIgnoreCase(statusInicial)
                && anuncioRepository.existsByImovelIdAndStatus(dto.imovelId(), STATUS_ATIVO)) {
            throw new IllegalArgumentException("Já existe um anúncio ativo cadastrado para este imóvel");
        }

        Anuncio anuncio = new Anuncio();
        anuncio.setImovelId(dto.imovelId());
        anuncio.setAnuncianteId(dto.anuncianteId());
        anuncio.setCidade(dto.cidade());
        anuncio.setQuartos(dto.quartos());
        anuncio.setTags(dto.tags());
        anuncio.setValor(dto.valor());
        anuncio.setModalidade(dto.modalidade());
        anuncio.setValorCondominio(dto.valorCondominio());
        anuncio.setValorIptu(dto.valorIptu());
        anuncio.setNotaMedia(BigDecimal.ZERO);
        anuncio.setTotalAvaliacoes(0);
        anuncio.setStatus(statusInicial);
        anuncio.setPublicadoEm(LocalDateTime.now());
        anuncio.setExpiraEm(dto.expiraEm() != null ? dto.expiraEm() : LocalDateTime.now().plusDays(90));
        anuncio.setAtualizadoEm(LocalDateTime.now());

        Anuncio salvo = anuncioRepository.save(anuncio);
        return AnuncioResponseDTO.fromEntity(salvo);
    }

    @Transactional(readOnly = true)
    public Page<AnuncioResponseDTO> listarTodos(Pageable pageable) {
        return anuncioRepository.findAll(pageable)
                .map(AnuncioResponseDTO::fromEntity);
    }

    @Transactional(readOnly = true)
    public Page<AnuncioResponseDTO> buscarComFiltros(String cidade, BigDecimal precoMin, BigDecimal precoMax,
            Integer quartos, List<String> tags, Pageable pageable) {
        return anuncioRepository.buscarComFiltros(cidade, precoMin, precoMax, quartos, tags, pageable)
                .map(AnuncioResponseDTO::fromEntity);
    }

    @Transactional(readOnly = true)
    public AnuncioResponseDTO buscarPorId(UUID id) {
        Anuncio anuncio = anuncioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(MENSAGEM_NAO_ENCONTRADO));
        return AnuncioResponseDTO.fromEntity(anuncio);
    }

    @Transactional(readOnly = true)
    public Page<AnuncioResponseDTO> buscarPorAnunciante(UUID anuncianteId, Pageable pageable) {
        return anuncioRepository.findByAnuncianteId(anuncianteId, pageable)
                .map(AnuncioResponseDTO::fromEntity);
    }

    @Transactional
    public AnuncioResponseDTO atualizar(UUID id, AnuncioCreateDTO dto) {
        Anuncio anuncio = anuncioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(MENSAGEM_NAO_ENCONTRADO));

        anuncio.setImovelId(dto.imovelId());
        anuncio.setAnuncianteId(dto.anuncianteId());
        anuncio.setCidade(dto.cidade());
        anuncio.setQuartos(dto.quartos());
        anuncio.setTags(dto.tags());
        anuncio.setValor(dto.valor());
        anuncio.setModalidade(dto.modalidade());
        anuncio.setValorCondominio(dto.valorCondominio());
        anuncio.setValorIptu(dto.valorIptu());

        if (dto.status() != null) {
            anuncio.setStatus(dto.status());
        }
        if (dto.expiraEm() != null) {
            anuncio.setExpiraEm(dto.expiraEm());
        }
        anuncio.setAtualizadoEm(LocalDateTime.now());

        Anuncio atualizado = anuncioRepository.save(anuncio);
        return AnuncioResponseDTO.fromEntity(atualizado);
    }

    @Transactional
    public AnuncioResponseDTO alterarStatus(UUID id, String status) {
        Anuncio anuncio = anuncioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(MENSAGEM_NAO_ENCONTRADO));

        anuncio.setStatus(status);
        anuncio.setAtualizadoEm(LocalDateTime.now());

        Anuncio atualizado = anuncioRepository.save(anuncio);
        return AnuncioResponseDTO.fromEntity(atualizado);
    }

    @Transactional
    public AnuncioResponseDTO renovarAnuncio(UUID id) {
        Anuncio anuncio = anuncioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(MENSAGEM_NAO_ENCONTRADO));

        anuncio.setExpiraEm(LocalDateTime.now().plusDays(90));
        anuncio.setStatus(STATUS_ATIVO);
        anuncio.setAtualizadoEm(LocalDateTime.now());

        Anuncio atualizado = anuncioRepository.save(anuncio);
        return AnuncioResponseDTO.fromEntity(atualizado);
    }

    @Transactional
    public void deletar(UUID id) {
        if (!anuncioRepository.existsById(id)) {
            throw new IllegalArgumentException(MENSAGEM_NAO_ENCONTRADO);
        }
        anuncioRepository.deleteById(id);
    }
}