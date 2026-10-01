package br.com.valemorar.domain.foto_imovel.service;

import br.com.valemorar.domain.foto_imovel.FotoImovel;
import br.com.valemorar.domain.foto_imovel.dto.FotoImovelCreateDTO;
import br.com.valemorar.domain.foto_imovel.dto.FotoImovelResponseDTO;
import br.com.valemorar.domain.foto_imovel.repository.FotoImovelRepository;
import br.com.valemorar.domain.imovel.service.ImovelService;
import br.com.valemorar.infra.SecurityUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class FotoImovelService {

    private static final String MENSAGEM_NAO_ENCONTRADA = "Foto do imóvel não encontrada";

    private final FotoImovelRepository repository;
    private final ImovelService imovelService;

    public FotoImovelService(FotoImovelRepository repository, ImovelService imovelService) {
        this.repository = repository;
        this.imovelService = imovelService;
    }

    @Transactional
    public FotoImovelResponseDTO criar(FotoImovelCreateDTO dto) {
        imovelService.buscarProprio(dto.imovelId());
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

    @Transactional(readOnly = true)
    public Page<FotoImovelResponseDTO> listarTodos(Pageable pageable) {
        SecurityUtils.exigirAdmin();
        return repository.findAll(pageable)
                .map(FotoImovelResponseDTO::fromEntity);
    }

    @Transactional(readOnly = true)
    public FotoImovelResponseDTO buscarPorId(UUID id) {
        FotoImovel entity = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(MENSAGEM_NAO_ENCONTRADA));
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
        FotoImovel entity = buscarPropria(id);
        // Também exige ser dono do imóvel de destino, caso a foto esteja sendo movida
        imovelService.buscarProprio(dto.imovelId());

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
        repository.delete(buscarPropria(id));
    }

    private FotoImovel buscarPropria(UUID id) {
        FotoImovel entity = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(MENSAGEM_NAO_ENCONTRADA));
        imovelService.buscarProprio(entity.getImovelId());
        return entity;
    }

    private void desmarcarCapaAtual(UUID imovelId) {
        repository.findByImovelIdAndCapaTrue(imovelId).ifPresent(foto -> {
            foto.setCapa(false);
            repository.save(foto);
        });
    }
}