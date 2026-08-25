package br.com.valemorar.domain.locador.service;

import br.com.valemorar.domain.locador.Locador;
import br.com.valemorar.domain.locador.dto.LocadorCreateDTO;
import br.com.valemorar.domain.locador.dto.LocadorResponseDTO;
import br.com.valemorar.domain.locador.repository.LocadorRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class LocadorService {

    private final LocadorRepository repository;

    public LocadorService(LocadorRepository repository) {
        this.repository = repository;
    }

    public LocadorResponseDTO criar(LocadorCreateDTO dto) {
        if (repository.existsById(dto.usuarioId())) {
            throw new RuntimeException("Já existe um cadastro de locador para este usuário");
        }

        Locador entity = new Locador();
        entity.setUsuarioId(dto.usuarioId());
        entity.setTelefone(dto.telefone());
        entity.setWhatsapp(dto.whatsapp());
        entity.setDocumento(dto.documento());
        entity.setCriadoEm(LocalDateTime.now());

        Locador salvo = repository.save(entity);
        return LocadorResponseDTO.fromEntity(salvo);
    }

    public List<LocadorResponseDTO> listarTodos() {
        return repository.findAll()
                .stream()
                .map(LocadorResponseDTO::fromEntity)
                .toList();
    }

    public LocadorResponseDTO buscarPorId(UUID usuarioId) {
        Locador entity = repository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Locador não encontrado"));
        return LocadorResponseDTO.fromEntity(entity);
    }

    public LocadorResponseDTO atualizar(UUID usuarioId, LocadorCreateDTO dto) {
        Locador entity = repository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Locador não encontrado"));

        entity.setTelefone(dto.telefone());
        entity.setWhatsapp(dto.whatsapp());
        entity.setDocumento(dto.documento());

        Locador atualizado = repository.save(entity);
        return LocadorResponseDTO.fromEntity(atualizado);
    }

    public void deletar(UUID usuarioId) {
        if (!repository.existsById(usuarioId)) {
            throw new RuntimeException("Locador não encontrado");
        }
        repository.deleteById(usuarioId);
    }
}
