package br.com.valemorar.domain.caracteristica.service;

import br.com.valemorar.domain.caracteristica.Caracteristica;
import br.com.valemorar.domain.caracteristica.dto.CaracteristicaCreateDTO;
import br.com.valemorar.domain.caracteristica.dto.CaracteristicaResponseDTO;
import br.com.valemorar.domain.caracteristica.repository.CaracteristicaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class CaracteristicaService {

    private final CaracteristicaRepository repository;

    public CaracteristicaService(CaracteristicaRepository repository) {
        this.repository = repository;
    }

    public CaracteristicaResponseDTO criar(CaracteristicaCreateDTO dto) {
        if (repository.existsByNome(dto.nome())) {
            throw new RuntimeException("Já existe uma característica cadastrada com este nome");
        }

        Caracteristica entity = new Caracteristica();
        entity.setCategoriaId(dto.categoriaId());
        entity.setNome(dto.nome());
        entity.setTipoValor(dto.tipoValor());
        entity.setUnidade(dto.unidade());
        entity.setPermiteMultiplos(dto.permiteMultiplos());
        entity.setObrigatoria(dto.obrigatoria());
        entity.setOrdem(dto.ordem());

        Caracteristica salvo = repository.save(entity);
        return CaracteristicaResponseDTO.fromEntity(salvo);
    }

    public List<CaracteristicaResponseDTO> listarTodos() {
        return repository.findAll()
                .stream()
                .map(CaracteristicaResponseDTO::fromEntity)
                .toList();
    }

    public CaracteristicaResponseDTO buscarPorId(UUID id) {
        Caracteristica entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Característica não encontrada"));
        return CaracteristicaResponseDTO.fromEntity(entity);
    }

    public List<CaracteristicaResponseDTO> buscarPorCategoria(UUID categoriaId) {
        return repository.findByCategoriaId(categoriaId)
                .stream()
                .map(CaracteristicaResponseDTO::fromEntity)
                .toList();
    }

    public CaracteristicaResponseDTO atualizar(UUID id, CaracteristicaCreateDTO dto) {
        Caracteristica entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Característica não encontrada"));

        entity.setCategoriaId(dto.categoriaId());
        entity.setNome(dto.nome());
        entity.setTipoValor(dto.tipoValor());
        entity.setUnidade(dto.unidade());
        entity.setPermiteMultiplos(dto.permiteMultiplos());
        entity.setObrigatoria(dto.obrigatoria());
        entity.setOrdem(dto.ordem());

        Caracteristica atualizado = repository.save(entity);
        return CaracteristicaResponseDTO.fromEntity(atualizado);
    }

    public void deletar(UUID id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Característica não encontrada");
        }
        repository.deleteById(id);
    }
}
