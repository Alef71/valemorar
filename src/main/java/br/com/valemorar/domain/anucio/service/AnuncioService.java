package br.com.valemorar.domain.anucio.service;

import br.com.valemorar.domain.anucio.Anuncio;
import br.com.valemorar.domain.anucio.dto.AnuncioCreateDTO;
import br.com.valemorar.domain.anucio.dto.AnuncioResponseDTO;
import br.com.valemorar.domain.anucio.repository.AnuncioRepository;
import br.com.valemorar.domain.endereco.Endereco;
import br.com.valemorar.domain.endereco.repository.EnderecoRepository;
import br.com.valemorar.domain.imovel.Imovel;
import br.com.valemorar.domain.imovel.repository.ImovelRepository;
import br.com.valemorar.infra.SecurityUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AnuncioService {

    private static final String STATUS_ATIVO = "ATIVO";
    private static final String MENSAGEM_NAO_ENCONTRADO = "Anúncio não encontrado";
    private static final String MENSAGEM_IMOVEL_NAO_ENCONTRADO = "Imóvel não encontrado com o ID fornecido";
    private static final String MENSAGEM_ACESSO_NEGADO = "Você não tem permissão para acessar este anúncio";

    private final AnuncioRepository anuncioRepository;
    private final ImovelRepository imovelRepository;
    private final EnderecoRepository enderecoRepository;

    public AnuncioService(
            AnuncioRepository anuncioRepository,
            ImovelRepository imovelRepository,
            EnderecoRepository enderecoRepository) {
        this.anuncioRepository = anuncioRepository;
        this.imovelRepository = imovelRepository;
        this.enderecoRepository = enderecoRepository;
    }

    @Transactional
    public AnuncioResponseDTO criar(AnuncioCreateDTO dto) {
        String statusInicial = dto.status() != null ? dto.status() : STATUS_ATIVO;

        if (STATUS_ATIVO.equalsIgnoreCase(statusInicial)
                && anuncioRepository.existsByImovelIdAndStatus(dto.imovelId(), STATUS_ATIVO)) {
            throw new IllegalArgumentException("Já existe um anúncio ativo cadastrado para este imóvel");
        }

        Imovel imovel = imovelRepository.findById(dto.imovelId())
                .orElseThrow(() -> new IllegalArgumentException(MENSAGEM_IMOVEL_NAO_ENCONTRADO));

        Anuncio anuncio = new Anuncio();
        anuncio.setImovel(imovel);
        anuncio.setAnuncianteId(getUsuarioAutenticadoId());
        anuncio.setValor(dto.valor());
        anuncio.setModalidade(dto.modalidade());
        anuncio.setTags(dto.tags() != null ? new ArrayList<>(dto.tags()) : new ArrayList<>());
        anuncio.setNotaMedia(BigDecimal.ZERO);
        anuncio.setTotalAvaliacoes(0);
        anuncio.setStatus(statusInicial);
        anuncio.setPublicadoEm(LocalDateTime.now());
        anuncio.setExpiraEm(dto.expiraEm() != null ? dto.expiraEm() : LocalDateTime.now().plusDays(90));
        anuncio.setAtualizadoEm(LocalDateTime.now());

        Anuncio salvo = anuncioRepository.save(anuncio);
        return toResponse(salvo);
    }

    @Transactional(readOnly = true)
    public Page<AnuncioResponseDTO> listarTodos(Pageable pageable) {
        return toResponsePage(anuncioRepository.findAll(pageable));
    }

    @Transactional(readOnly = true)
    public Page<AnuncioResponseDTO> buscarComFiltros(String tipoImovel, BigDecimal precoMin, BigDecimal precoMax,
            Integer quartos, List<String> tags, Pageable pageable) {
        return toResponsePage(
                anuncioRepository.buscarComFiltros(tipoImovel, precoMin, precoMax, quartos, tags, pageable));
    }

    @Transactional(readOnly = true)
    public AnuncioResponseDTO buscarPorId(UUID id) {
        Anuncio anuncio = anuncioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(MENSAGEM_NAO_ENCONTRADO));
        return toResponse(anuncio);
    }

    @Transactional(readOnly = true)
    public Page<AnuncioResponseDTO> buscarPorAnunciante(UUID anuncianteId, Pageable pageable) {
        validarAcessoAnunciante(anuncianteId);
        return toResponsePage(anuncioRepository.findByAnuncianteId(anuncianteId, pageable));
    }

    @Transactional
    public AnuncioResponseDTO atualizar(UUID id, AnuncioCreateDTO dto) {
        Anuncio anuncio = anuncioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(MENSAGEM_NAO_ENCONTRADO));
        validarOwnership(anuncio);

        Imovel imovel = imovelRepository.findById(dto.imovelId())
                .orElseThrow(() -> new IllegalArgumentException(MENSAGEM_IMOVEL_NAO_ENCONTRADO));

        anuncio.setImovel(imovel);
        anuncio.setAnuncianteId(getUsuarioAutenticadoId());
        anuncio.setValor(dto.valor());
        anuncio.setModalidade(dto.modalidade());
        anuncio.setTags(dto.tags() != null ? new ArrayList<>(dto.tags()) : new ArrayList<>());

        if (dto.status() != null) {
            anuncio.setStatus(dto.status());
        }
        if (dto.expiraEm() != null) {
            anuncio.setExpiraEm(dto.expiraEm());
        }
        anuncio.setAtualizadoEm(LocalDateTime.now());

        Anuncio atualizado = anuncioRepository.save(anuncio);
        return toResponse(atualizado);
    }

    @Transactional
    public AnuncioResponseDTO alterarStatus(UUID id, String status) {
        Anuncio anuncio = anuncioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(MENSAGEM_NAO_ENCONTRADO));
        validarOwnership(anuncio);

        anuncio.setStatus(status);
        anuncio.setAtualizadoEm(LocalDateTime.now());

        Anuncio atualizado = anuncioRepository.save(anuncio);
        return toResponse(atualizado);
    }

    @Transactional
    public AnuncioResponseDTO renovarAnuncio(UUID id) {
        Anuncio anuncio = anuncioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(MENSAGEM_NAO_ENCONTRADO));
        validarOwnership(anuncio);

        anuncio.setExpiraEm(LocalDateTime.now().plusDays(90));
        anuncio.setStatus(STATUS_ATIVO);
        anuncio.setAtualizadoEm(LocalDateTime.now());

        Anuncio atualizado = anuncioRepository.save(anuncio);
        return toResponse(atualizado);
    }

    @Transactional
    public void deletar(UUID id) {
        Anuncio anuncio = anuncioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(MENSAGEM_NAO_ENCONTRADO));
        validarOwnership(anuncio);
        anuncioRepository.delete(anuncio);
    }

    private UUID getUsuarioAutenticadoId() {
        return SecurityUtils.getUsuarioAutenticado().getId();
    }

    private Page<AnuncioResponseDTO> toResponsePage(Page<Anuncio> anuncios) {
        Set<UUID> enderecoIds = anuncios.getContent().stream()
                .map(Anuncio::getImovel)
                .filter(Objects::nonNull)
                .map(Imovel::getEnderecoId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        Map<UUID, Endereco> enderecos = new HashMap<>();
        enderecoRepository.findAllById(enderecoIds)
                .forEach(endereco -> enderecos.put(endereco.getId(), endereco));

        return anuncios.map(anuncio -> {
            Imovel imovel = anuncio.getImovel();
            Endereco endereco = imovel != null ? enderecos.get(imovel.getEnderecoId()) : null;
            return AnuncioResponseDTO.fromEntity(anuncio, endereco);
        });
    }

    private AnuncioResponseDTO toResponse(Anuncio anuncio) {
        Imovel imovel = anuncio.getImovel();
        Endereco endereco = imovel != null
                ? enderecoRepository.findById(imovel.getEnderecoId()).orElse(null)
                : null;
        return AnuncioResponseDTO.fromEntity(anuncio, endereco);
    }

    private void validarAcessoAnunciante(UUID anuncianteId) {
        if (!getUsuarioAutenticadoId().equals(anuncianteId)) {
            throw new AccessDeniedException(MENSAGEM_ACESSO_NEGADO);
        }
    }

    private void validarOwnership(Anuncio anuncio) {
        if (!getUsuarioAutenticadoId().equals(anuncio.getAnuncianteId())) {
            throw new AccessDeniedException(MENSAGEM_ACESSO_NEGADO);
        }
    }
}