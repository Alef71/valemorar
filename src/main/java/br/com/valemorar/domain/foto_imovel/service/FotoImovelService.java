package br.com.valemorar.domain.foto_imovel.service;

import br.com.valemorar.domain.foto_imovel.FotoImovel;
import br.com.valemorar.domain.foto_imovel.dto.FotoImovelCreateDTO;
import br.com.valemorar.domain.foto_imovel.dto.FotoImovelResponseDTO;
import br.com.valemorar.domain.foto_imovel.repository.FotoImovelRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class FotoImovelService {

    private final FotoImovelRepository repository;

    public FotoImovelService(FotoImovelRepository repository) {
        this.repository = repository;
    }

    public FotoImovelResponseDTO criar(FotoImovelCreateDTO dto) {
        FotoImovel entity = new FotoImovel();
        entity.setImovelId(dto.imovelId());
        entity.setUrl(dto.url());
        entity.setCapa(dto.capa() != null ? dto.capa() : false);
        entity.setOrdem(dto.ordem());
        entity.setCriadoEm(LocalDateTime.now());

        FotoImovel salvo = repository.save(entity);
        return FotoImovelResponseDTO.fromEntity(salvo);
    }

    public List<FotoImovelResponseDTO> listarTodos() {
        return repository.findAll()
                .stream()
                .map(FotoImovelResponseDTO::fromEntity)
                .toList();
    }

    public FotoImovelResponseDTO buscarPorId(UUID id) {
        FotoImovel entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Foto do imóvel não encontrada"));
        return FotoImovelResponseDTO.fromEntity(entity);
    }

    public List<FotoImovelResponseDTO> buscarPorImovel(UUID imovelId) {
        return repository.findByImovelId(imovelId)
                .stream()
                .map(FotoImovelResponseDTO::fromEntity)
                .toList();
    }

    public FotoImovelResponseDTO atualizar(UUID id, FotoImovelCreateDTO dto) {
        FotoImovel entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Foto do imóvel não encontrada"));

        entity.setImovelId(dto.imovelId());
        entity.setUrl(dto.url());
        entity.setCapa(dto.capa());
        entity.setOrdem(dto.ordem());

        FotoImovel atualizado = repository.save(entity);
        return FotoImovelResponseDTO.fromEntity(atualizado);
    }

    public void deletar(UUID id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Foto do imóvel não encontrada");
        }
        repository.deleteById(id);
    }
}
