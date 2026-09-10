package br.com.valemorar.domain.caracteristica.service;

import br.com.valemorar.domain.caracteristica.Caracteristica;
import br.com.valemorar.domain.caracteristica.dto.CaracteristicaCreateDTO;
import br.com.valemorar.domain.caracteristica.dto.CaracteristicaResponseDTO;
import br.com.valemorar.domain.caracteristica.repository.CaracteristicaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class CaracteristicaService {

    private final CaracteristicaRepository repository;

    public CaracteristicaService(CaracteristicaRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public CaracteristicaResponseDTO criar(CaracteristicaCreateDTO dto) {
        if (repository.existsByNome(dto.nome())) {
            throw new IllegalArgumentException("Já existe uma característica cadastrada com este nome");
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

    @Transactional(readOnly = true)
    public Page<CaracteristicaResponseDTO> listarTodos(Pageable pageable) {
        return repository.findAll(pageable)
                .map(CaracteristicaResponseDTO::fromEntity);
    }

    @Transactional(readOnly = true)
    public CaracteristicaResponseDTO buscarPorId(UUID id) {
        Caracteristica entity = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Característica não encontrada"));
        return CaracteristicaResponseDTO.fromEntity(entity);
    }

    @Transactional(readOnly = true)
    public List<CaracteristicaResponseDTO> buscarPorCategoria(UUID categoriaId) {
        return repository.findByCategoriaIdOrderByOrdemAsc(categoriaId)
                .stream()
                .map(CaracteristicaResponseDTO::fromEntity)
                .toList();
    }

    @Transactional
    public CaracteristicaResponseDTO atualizar(UUID id, CaracteristicaCreateDTO dto) {
        Caracteristica entity = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Característica não encontrada"));

        if (repository.existsByNomeAndIdNot(dto.nome(), id)) {
            throw new IllegalArgumentException("Já existe outra característica cadastrada com este nome");
        }

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

    @Transactional
    public void deletar(UUID id) {
        if (!repository.existsById(id)) {
            throw new IllegalArgumentException("Característica não encontrada");
        }
        repository.deleteById(id);
    }
}