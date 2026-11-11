package br.com.valemorar.domain.locatario.service;

import br.com.valemorar.domain.locatario.Locatario;
import br.com.valemorar.domain.locatario.dto.LocatarioCreateDTO;
import br.com.valemorar.domain.locatario.dto.LocatarioResponseDTO;
import br.com.valemorar.domain.locatario.repository.LocatarioRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class LocatarioService {

    private final LocatarioRepository repository;

    public LocatarioService(LocatarioRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public LocatarioResponseDTO criar(LocatarioCreateDTO dto) {
        if (repository.existsById(dto.usuarioId())) {
            throw new IllegalArgumentException("Já existe um cadastro de locatário para este usuário");
        }

        Locatario entity = new Locatario();
        entity.setUsuarioId(dto.usuarioId());
        entity.setCriadoEm(LocalDateTime.now());

        Locatario salvo = repository.save(entity);
        return LocatarioResponseDTO.fromEntity(salvo);
    }

    @Transactional(readOnly = true)
    public Page<LocatarioResponseDTO> listarTodos(Pageable pageable) {
        return repository.findAll(pageable)
                .map(LocatarioResponseDTO::fromEntity);
    }

    @Transactional(readOnly = true)
    public LocatarioResponseDTO buscarPorId(UUID usuarioId) {
        Locatario entity = repository.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Locatário não encontrado"));
        return LocatarioResponseDTO.fromEntity(entity);
    }

    @Transactional
    public void deletar(UUID usuarioId) {
        if (!repository.existsById(usuarioId)) {
            throw new IllegalArgumentException("Locatário não encontrado");
        }
        repository.deleteById(usuarioId);
    }
}