package br.com.valemorar.domain.categoria_caracteristica.service;

import br.com.valemorar.domain.categoria_caracteristica.CategoriaCaracteristica;
import br.com.valemorar.domain.categoria_caracteristica.dto.CategoriaCaracteristicaCreateDTO;
import br.com.valemorar.domain.categoria_caracteristica.dto.CategoriaCaracteristicaResponseDTO;
import br.com.valemorar.domain.categoria_caracteristica.repository.CategoriaCaracteristicaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class CategoriaCaracteristicaService {

    private final CategoriaCaracteristicaRepository repository;

    public CategoriaCaracteristicaService(CategoriaCaracteristicaRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public CategoriaCaracteristicaResponseDTO criar(CategoriaCaracteristicaCreateDTO dto) {
        if (repository.existsByNome(dto.nome())) {
            throw new IllegalArgumentException("Já existe uma categoria de característica cadastrada com este nome");
        }

        CategoriaCaracteristica entity = new CategoriaCaracteristica();
        entity.setNome(dto.nome());
        entity.setIcone(dto.icone());
        entity.setOrdem(dto.ordem());

        CategoriaCaracteristica salvo = repository.save(entity);
        return CategoriaCaracteristicaResponseDTO.fromEntity(salvo);
    }

    @Transactional(readOnly = true)
    public List<CategoriaCaracteristicaResponseDTO> listarTodos() {
        return repository.findAllByOrderByOrdemAsc()
                .stream()
                .map(CategoriaCaracteristicaResponseDTO::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public CategoriaCaracteristicaResponseDTO buscarPorId(UUID id) {
        CategoriaCaracteristica entity = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Categoria de característica não encontrada"));
        return CategoriaCaracteristicaResponseDTO.fromEntity(entity);
    }

    @Transactional
    public CategoriaCaracteristicaResponseDTO atualizar(UUID id, CategoriaCaracteristicaCreateDTO dto) {
        CategoriaCaracteristica entity = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Categoria de característica não encontrada"));

        if (repository.existsByNomeAndIdNot(dto.nome(), id)) {
            throw new IllegalArgumentException("Já existe outra categoria de característica cadastrada com este nome");
        }

        entity.setNome(dto.nome());
        entity.setIcone(dto.icone());
        entity.setOrdem(dto.ordem());

        CategoriaCaracteristica atualizado = repository.save(entity);
        return CategoriaCaracteristicaResponseDTO.fromEntity(atualizado);
    }

    @Transactional
    public void deletar(UUID id) {
        if (!repository.existsById(id)) {
            throw new IllegalArgumentException("Categoria de característica não encontrada");
        }
        repository.deleteById(id);
    }
}