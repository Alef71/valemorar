package br.com.valemorar.domain.locador.service;

import br.com.valemorar.domain.locador.Locador;
import br.com.valemorar.domain.locador.dto.LocadorContatoDTO;
import br.com.valemorar.domain.locador.dto.LocadorCreateDTO;
import br.com.valemorar.domain.locador.dto.LocadorResponseDTO;
import br.com.valemorar.domain.locador.repository.LocadorRepository;
import br.com.valemorar.infra.SecurityUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
public class LocadorService {

    private final LocadorRepository repository;

    public LocadorService(LocadorRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public LocadorResponseDTO criar(LocadorCreateDTO dto) {
        UUID usuarioId = SecurityUtils.resolverDono(dto.usuarioId());
        if (repository.existsById(usuarioId)) {
            throw new IllegalArgumentException("Já existe um cadastro de locador para este usuário");
        }

        if (repository.existsByDocumento(dto.documento())) {
            throw new IllegalArgumentException("Já existe um locador cadastrado com este documento");
        }

        Locador entity = new Locador();
        entity.setUsuarioId(usuarioId);
        entity.setTelefone(dto.telefone());
        entity.setWhatsapp(dto.whatsapp());
        entity.setDocumento(dto.documento());
        entity.setCriadoEm(LocalDateTime.now());

        Locador salvo = repository.save(entity);
        return LocadorResponseDTO.fromEntity(salvo);
    }

    @Transactional(readOnly = true)
    public Optional<LocadorResponseDTO> buscarMeuCadastro() {
        return repository.findById(SecurityUtils.getUsuarioAutenticadoId()).map(LocadorResponseDTO::fromEntity);
    }

    /** Cria ou atualiza telefone/WhatsApp do usuário autenticado, sem exigir documento. */
    @Transactional
    public LocadorResponseDTO salvarMeuContato(LocadorContatoDTO dto) {
        UUID usuarioId = SecurityUtils.getUsuarioAutenticadoId();
        Locador entity = repository.findById(usuarioId).orElseGet(() -> {
            Locador novo = new Locador();
            novo.setUsuarioId(usuarioId);
            novo.setCriadoEm(LocalDateTime.now());
            return novo;
        });
        entity.setTelefone(vazioParaNulo(dto.telefone()));
        entity.setWhatsapp(vazioParaNulo(dto.whatsapp()));
        return LocadorResponseDTO.fromEntity(repository.save(entity));
    }

    private String vazioParaNulo(String valor) {
        return valor == null || valor.isBlank() ? null : valor.trim();
    }

    @Transactional(readOnly = true)
    public Page<LocadorResponseDTO> listarTodos(Pageable pageable) {
        SecurityUtils.exigirAdmin();
        return repository.findAll(pageable)
                .map(LocadorResponseDTO::fromEntity);
    }

    @Transactional(readOnly = true)
    public LocadorResponseDTO buscarPorId(UUID usuarioId) {
        SecurityUtils.exigirDonoOuAdmin(usuarioId);
        Locador entity = repository.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Locador não encontrado"));
        return LocadorResponseDTO.fromEntity(entity);
    }

    @Transactional(readOnly = true)
    public LocadorResponseDTO buscarPorDocumento(String documento) {
        SecurityUtils.exigirAdmin();
        Locador entity = repository.findByDocumento(documento)
                .orElseThrow(() -> new IllegalArgumentException("Locador não encontrado para o documento informado"));
        return LocadorResponseDTO.fromEntity(entity);
    }

    @Transactional
    public LocadorResponseDTO atualizar(UUID usuarioId, LocadorCreateDTO dto) {
        SecurityUtils.exigirDonoOuAdmin(usuarioId);
        Locador entity = repository.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Locador não encontrado"));

        if (!Objects.equals(entity.getDocumento(), dto.documento()) && repository.existsByDocumento(dto.documento())) {
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
        SecurityUtils.exigirDonoOuAdmin(usuarioId);
        if (!repository.existsById(usuarioId)) {
            throw new IllegalArgumentException("Locador não encontrado");
        }
        repository.deleteById(usuarioId);
    }
}