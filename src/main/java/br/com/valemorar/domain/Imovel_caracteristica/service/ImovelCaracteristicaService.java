package br.com.valemorar.domain.imovel_caracteristica.service;

import br.com.valemorar.domain.imovel_caracteristica.ImovelCaracteristica;
import br.com.valemorar.domain.imovel_caracteristica.dto.ImovelCaracteristicaCreateDTO;
import br.com.valemorar.domain.imovel_caracteristica.dto.ImovelCaracteristicaResponseDTO;
import br.com.valemorar.domain.imovel_caracteristica.repository.ImovelCaracteristicaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ImovelCaracteristicaService {

    private final ImovelCaracteristicaRepository repository;

    public ImovelCaracteristicaService(ImovelCaracteristicaRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public ImovelCaracteristicaResponseDTO criar(ImovelCaracteristicaCreateDTO dto) {
        if (repository.existsByImovelIdAndCaracteristicaId(dto.imovelId(), dto.caracteristicaId())) {
            throw new IllegalArgumentException("Esta característica já está associada a este imóvel");
        }

        ImovelCaracteristica entity = new ImovelCaracteristica();
        entity.setImovelId(dto.imovelId());
        entity.setCaracteristicaId(dto.caracteristicaId());
        entity.setValor(dto.valor());

        ImovelCaracteristica salvo = repository.save(entity);
        return ImovelCaracteristicaResponseDTO.fromEntity(salvo);
    }

    @Transactional(readOnly = true)
    public Page<ImovelCaracteristicaResponseDTO> listarTodos(Pageable pageable) {
        return repository.findAll(pageable)
                .map(ImovelCaracteristicaResponseDTO::fromEntity);
    }

    @Transactional(readOnly = true)
    public ImovelCaracteristicaResponseDTO buscarPorId(UUID id) {
        ImovelCaracteristica entity = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Característica do imóvel não encontrada"));
        return ImovelCaracteristicaResponseDTO.fromEntity(entity);
    }

    @Transactional(readOnly = true)
    public List<ImovelCaracteristicaResponseDTO> buscarPorImovelId(UUID imovelId) {
        return repository.findByImovelId(imovelId)
                .stream()
                .map(ImovelCaracteristicaResponseDTO::fromEntity)
                .toList();
    }

    @Transactional
    public ImovelCaracteristicaResponseDTO atualizar(UUID id, ImovelCaracteristicaCreateDTO dto) {
        ImovelCaracteristica entity = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Característica do imóvel não encontrada"));

        entity.setImovelId(dto.imovelId());
        entity.setCaracteristicaId(dto.caracteristicaId());
        entity.setValor(dto.valor());

        ImovelCaracteristica atualizado = repository.save(entity);
        return ImovelCaracteristicaResponseDTO.fromEntity(atualizado);
    }

    @Transactional
    public void deletar(UUID id) {
        if (!repository.existsById(id)) {
            throw new IllegalArgumentException("Característica do imóvel não encontrada");
        }
        repository.deleteById(id);
    }

    @Transactional
    public void deletarPorImovelECaracteristica(UUID imovelId, UUID caracteristicaId) {
        if (!repository.existsByImovelIdAndCaracteristicaId(imovelId, caracteristicaId)) {
            throw new IllegalArgumentException("Associação entre imóvel e característica não encontrada");
        }
        repository.deleteByImovelIdAndCaracteristicaId(imovelId, caracteristicaId);
    }
}