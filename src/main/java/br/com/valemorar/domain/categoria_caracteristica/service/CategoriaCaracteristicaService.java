package br.com.valemorar.domain.categoria_caracteristica.service;

import br.com.valemorar.domain.categoria_caracteristica.CategoriaCaracteristica;
import br.com.valemorar.domain.categoria_caracteristica.dto.CategoriaCaracteristicaCreateDTO;
import br.com.valemorar.domain.categoria_caracteristica.dto.CategoriaCaracteristicaResponseDTO;
import br.com.valemorar.domain.categoria_caracteristica.repositiry.CategoriaCaracteristicaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class CategoriaCaracteristicaService {

    private final CategoriaCaracteristicaRepository repository;

    public CategoriaCaracteristicaService(CategoriaCaracteristicaRepository repository) {
        this.repository = repository;
    }

    public CategoriaCaracteristicaResponseDTO criar(CategoriaCaracteristicaCreateDTO dto) {
        if (repository.existsByNome(dto.nome())) {
            throw new RuntimeException("Já existe uma categoria de característica cadastrada com este nome");
        }

        CategoriaCaracteristica entity = new CategoriaCaracteristica();
        entity.setNome(dto.nome());
        entity.setIcone(dto.icone());
        entity.setOrdem(dto.ordem());

        CategoriaCaracteristica salvo = repository.save(entity);
        return CategoriaCaracteristicaResponseDTO.fromEntity(salvo);
    }

    public List<CategoriaCaracteristicaResponseDTO> listarTodos() {
        return repository.findAll()
                .stream()
                .map(CategoriaCaracteristicaResponseDTO::fromEntity)
                .toList();
    }

    public CategoriaCaracteristicaResponseDTO buscarPorId(UUID id) {
        CategoriaCaracteristica entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoria de característica não encontrada"));
        return CategoriaCaracteristicaResponseDTO.fromEntity(entity);
    }

    public CategoriaCaracteristicaResponseDTO atualizar(UUID id, CategoriaCaracteristicaCreateDTO dto) {
        CategoriaCaracteristica entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoria de característica não encontrada"));

        entity.setNome(dto.nome());
        entity.setIcone(dto.icone());
        entity.setOrdem(dto.ordem());

        CategoriaCaracteristica atualizado = repository.save(entity);
        return CategoriaCaracteristicaResponseDTO.fromEntity(atualizado);
    }

    public void deletar(UUID id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Categoria de característica não encontrada");
        }
        repository.deleteById(id);
    }
}