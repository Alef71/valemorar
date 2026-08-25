package br.com.valemorar.domain.locatario.service;

import br.com.valemorar.domain.locatario.Locatario;
import br.com.valemorar.domain.locatario.dto.LocatarioCreateDTO;
import br.com.valemorar.domain.locatario.dto.LocatarioResponseDTO;
import br.com.valemorar.domain.locatario.repository.LocatarioRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class LocatarioService {

    private final LocatarioRepository repository;

    public LocatarioService(LocatarioRepository repository) {
        this.repository = repository;
    }

    public LocatarioResponseDTO criar(LocatarioCreateDTO dto) {
        if (repository.existsById(dto.usuarioId())) {
            throw new RuntimeException("Já existe um cadastro de locatário para este usuário");
        }

        Locatario entity = new Locatario();
        entity.setUsuarioId(dto.usuarioId());
        entity.setCriadoEm(LocalDateTime.now());

        Locatario salvo = repository.save(entity);
        return LocatarioResponseDTO.fromEntity(salvo);
    }

    public List<LocatarioResponseDTO> listarTodos() {
        return repository.findAll()
                .stream()
                .map(LocatarioResponseDTO::fromEntity)
                .toList();
    }

    public LocatarioResponseDTO buscarPorId(UUID usuarioId) {
        Locatario entity = repository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Locatário não encontrado"));
        return LocatarioResponseDTO.fromEntity(entity);
    }

    public void deletar(UUID usuarioId) {
        if (!repository.existsById(usuarioId)) {
            throw new RuntimeException("Locatário não encontrado");
        }
        repository.deleteById(usuarioId);
    }
}
