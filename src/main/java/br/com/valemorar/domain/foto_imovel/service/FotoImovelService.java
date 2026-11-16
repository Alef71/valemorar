package br.com.valemorar.domain.foto_imovel.service;

import br.com.valemorar.domain.foto_imovel.FotoImovel;
import br.com.valemorar.domain.foto_imovel.dto.FotoImovelCreateDTO;
import br.com.valemorar.domain.foto_imovel.dto.FotoImovelResponseDTO;
import br.com.valemorar.domain.foto_imovel.repository.FotoImovelRepository;
import br.com.valemorar.domain.imovel.Imovel;
import br.com.valemorar.domain.imovel.repository.ImovelRepository;
import br.com.valemorar.infra.ImageStorageService;
import br.com.valemorar.infra.SecurityUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class FotoImovelService {

    private static final long MAX_FOTOS_POR_IMOVEL = 6;
    private final FotoImovelRepository repository;
    private final ImovelRepository imovelRepository;
    private final ImageStorageService imageStorageService;

    public FotoImovelService(
            FotoImovelRepository repository,
            ImovelRepository imovelRepository,
            ImageStorageService imageStorageService) {
        this.repository = repository;
        this.imovelRepository = imovelRepository;
        this.imageStorageService = imageStorageService;
    }

    @Transactional
    public FotoImovelResponseDTO criar(FotoImovelCreateDTO dto) {
        verificarPropriedade(dto.imovelId());
        verificarLimite(dto.imovelId());

        boolean eCapa = Boolean.TRUE.equals(dto.capa());

        if (eCapa) {
            desmarcarCapaAtual(dto.imovelId());
        }

        FotoImovel entity = new FotoImovel();
        entity.setImovelId(dto.imovelId());
        entity.setUrl(dto.url());
        entity.setCapa(eCapa);
        entity.setOrdem(dto.ordem() != null ? dto.ordem() : 0);
        entity.setCriadoEm(LocalDateTime.now());

        FotoImovel salvo = repository.save(entity);
        return FotoImovelResponseDTO.fromEntity(salvo);
    }

    @Transactional
    public FotoImovelResponseDTO criarUpload(
            UUID imovelId,
            MultipartFile file,
            Boolean capa,
            Integer ordem,
            String publicBaseUrl) {
        verificarPropriedade(imovelId);
        verificarLimite(imovelId);

        String imagePath = imageStorageService.store(file);
        try {
            boolean eCapa = Boolean.TRUE.equals(capa);
            if (eCapa) {
                desmarcarCapaAtual(imovelId);
            }

            FotoImovel entity = new FotoImovel();
            entity.setImovelId(imovelId);
            entity.setUrl(publicBaseUrl + imagePath);
            entity.setCapa(eCapa);
            entity.setOrdem(ordem != null ? ordem : 0);
            entity.setCriadoEm(LocalDateTime.now());
            return FotoImovelResponseDTO.fromEntity(repository.save(entity));
        } catch (RuntimeException e) {
            imageStorageService.delete(imagePath);
            throw e;
        }
    }

    @Transactional(readOnly = true)
    public Page<FotoImovelResponseDTO> listarTodos(Pageable pageable) {
        return repository.findAll(pageable)
                .map(FotoImovelResponseDTO::fromEntity);
    }

    @Transactional(readOnly = true)
    public FotoImovelResponseDTO buscarPorId(UUID id) {
        FotoImovel entity = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Foto do imóvel não encontrada"));
        return FotoImovelResponseDTO.fromEntity(entity);
    }

    @Transactional(readOnly = true)
    public List<FotoImovelResponseDTO> buscarPorImovel(UUID imovelId) {
        return repository.findByImovelIdOrderByOrdemAsc(imovelId)
                .stream()
                .map(FotoImovelResponseDTO::fromEntity)
                .toList();
    }

    @Transactional
    public FotoImovelResponseDTO atualizar(UUID id, FotoImovelCreateDTO dto) {
        FotoImovel entity = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Foto do imóvel não encontrada"));

        verificarPropriedade(entity.getImovelId());
        if (!entity.getImovelId().equals(dto.imovelId())) {
            verificarPropriedade(dto.imovelId());
            verificarLimite(dto.imovelId());
        }

        boolean eCapa = Boolean.TRUE.equals(dto.capa());

        if (eCapa && !Boolean.TRUE.equals(entity.getCapa())) {
            desmarcarCapaAtual(dto.imovelId());
        }

        entity.setImovelId(dto.imovelId());
        entity.setUrl(dto.url());
        entity.setCapa(eCapa);
        entity.setOrdem(dto.ordem());

        FotoImovel atualizado = repository.save(entity);
        return FotoImovelResponseDTO.fromEntity(atualizado);
    }

    @Transactional
    public void deletar(UUID id) {
        FotoImovel foto = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Foto do imóvel não encontrada"));
        verificarPropriedade(foto.getImovelId());
        repository.delete(foto);
    }

    private void verificarPropriedade(UUID imovelId) {
        Imovel imovel = imovelRepository.findById(imovelId)
                .orElseThrow(() -> new IllegalArgumentException("Imóvel não encontrado"));
        if (!imovel.getLocadorId().equals(SecurityUtils.getUsuarioAutenticado().getId())) {
            throw new IllegalArgumentException("Você não tem permissão para alterar as fotos deste imóvel.");
        }
    }

    private void verificarLimite(UUID imovelId) {
        if (repository.countByImovelId(imovelId) >= MAX_FOTOS_POR_IMOVEL) {
            throw new IllegalArgumentException("Cada anúncio pode ter no máximo 6 fotos.");
        }
    }

    private void desmarcarCapaAtual(UUID imovelId) {
        repository.findByImovelIdAndCapaTrue(imovelId).ifPresent(foto -> {
            foto.setCapa(false);
            repository.save(foto);
        });
    }
}