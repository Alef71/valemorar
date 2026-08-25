package br.com.valemorar.domain.Imovel_caracteristica.service;

import br.com.valemorar.domain.Imovel_caracteristica.ImovelCaracteristica;
import br.com.valemorar.domain.Imovel_caracteristica.dto.ImovelCaracteristicaCreateDTO;
import br.com.valemorar.domain.Imovel_caracteristica.dto.ImovelCaracteristicaResponseDTO;
import br.com.valemorar.domain.Imovel_caracteristica.repository.ImovelCaracteristicaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ImovelCaracteristicaService {

    private final ImovelCaracteristicaRepository repository;

    public ImovelCaracteristicaService(ImovelCaracteristicaRepository repository) {
        this.repository = repository;
    }

    public ImovelCaracteristicaResponseDTO criar(ImovelCaracteristicaCreateDTO dto) {
        ImovelCaracteristica entity = new ImovelCaracteristica();
        entity.setImovelId(dto.imovelId());
        entity.setCaracteristicaId(dto.caracteristicaId());
        entity.setValor(dto.valor());

        ImovelCaracteristica salvo = repository.save(entity);
        return ImovelCaracteristicaResponseDTO.fromEntity(salvo);
    }

    public List<ImovelCaracteristicaResponseDTO> listarTodos() {
        return repository.findAll()
                .stream()
                .map(ImovelCaracteristicaResponseDTO::fromEntity)
                .toList();
    }

    public List<ImovelCaracteristicaResponseDTO> buscarPorImovelId(UUID imovelId) {
        return repository.findByImovelId(imovelId)
                .stream()
                .map(ImovelCaracteristicaResponseDTO::fromEntity)
                .toList();
    }

    public ImovelCaracteristicaResponseDTO buscarPorId(UUID id) {
        ImovelCaracteristica entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Característica do imóvel não encontrada"));
        return ImovelCaracteristicaResponseDTO.fromEntity(entity);
    }

    public ImovelCaracteristicaResponseDTO atualizar(UUID id, ImovelCaracteristicaCreateDTO dto) {
        ImovelCaracteristica entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Característica do imóvel não encontrada"));

        entity.setImovelId(dto.imovelId());
        entity.setCaracteristicaId(dto.caracteristicaId());
        entity.setValor(dto.valor());

        ImovelCaracteristica atualizado = repository.save(entity);
        return ImovelCaracteristicaResponseDTO.fromEntity(atualizado);
    }

    public void deletar(UUID id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Característica do imóvel não encontrada");
        }
        repository.deleteById(id);
    }
}
