package br.com.valemorar.domain.locador.service;

import br.com.valemorar.domain.locador.Locador;
import br.com.valemorar.domain.locador.dto.LocadorCreateDTO;
import br.com.valemorar.domain.locador.dto.LocadorResponseDTO;
import br.com.valemorar.domain.locador.repository.LocadorRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class LocadorService {

    private final LocadorRepository repository;

    public LocadorService(LocadorRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public LocadorResponseDTO criar(LocadorCreateDTO dto) {
        if (repository.existsById(dto.usuarioId())) {
            throw new IllegalArgumentException("Já existe um cadastro de locador para este usuário");
        }

        if (repository.existsByDocumento(dto.documento())) {
            throw new IllegalArgumentException("Já existe um locador cadastrado com este documento");
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

    @Transactional(readOnly = true)
    public Page<LocadorResponseDTO> listarTodos(Pageable pageable) {
        return repository.findAll(pageable)
                .map(LocadorResponseDTO::fromEntity);
    }

    @Transactional(readOnly = true)
    public LocadorResponseDTO buscarPorId(UUID usuarioId) {
        Locador entity = repository.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Locador não encontrado"));
        return LocadorResponseDTO.fromEntity(entity);
    }

    @Transactional(readOnly = true)
    public LocadorResponseDTO buscarPorDocumento(String documento) {
        Locador entity = repository.findByDocumento(documento)
                .orElseThrow(() -> new IllegalArgumentException("Locador não encontrado para o documento informado"));
        return LocadorResponseDTO.fromEntity(entity);
    }

    @Transactional
    public LocadorResponseDTO atualizar(UUID usuarioId, LocadorCreateDTO dto) {
        Locador entity = repository.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Locador não encontrado"));

        if (!entity.getDocumento().equals(dto.documento()) && repository.existsByDocumento(dto.documento())) {
            throw new IllegalArgumentException("Já existe um locador cadastrado com este documento");
        }

        entity.setTelefone(dto.telefone());
        entity.setWhatsapp(dto.whatsapp());
        entity.setDocumento(dto.documento());

        Locador atualizado = repository.save(entity);
        return LocadorResponseDTO.fromEntity(atualizado);
    }

    @Transactional
    public void deletar(UUID usuarioId) {
        if (!repository.existsById(usuarioId)) {
            throw new IllegalArgumentException("Locador não encontrado");
        }
        repository.deleteById(usuarioId);
    }
}