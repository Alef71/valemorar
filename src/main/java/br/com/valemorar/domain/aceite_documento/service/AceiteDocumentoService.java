package br.com.valemorar.domain.aceite_documento.service;

import br.com.valemorar.domain.aceite_documento.AceiteDocumento;
import br.com.valemorar.domain.aceite_documento.dto.AceiteDocumentoCreateDTO;
import br.com.valemorar.domain.aceite_documento.dto.AceiteDocumentoResponseDTO;
import br.com.valemorar.domain.aceite_documento.repository.AceiteDocumentoRepository;
import br.com.valemorar.infra.SecurityUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class AceiteDocumentoService {

    private final AceiteDocumentoRepository repository;

    public AceiteDocumentoService(AceiteDocumentoRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public AceiteDocumentoResponseDTO registrarAceite(AceiteDocumentoCreateDTO dto, String ip) {
        AceiteDocumento aceite = new AceiteDocumento();
        aceite.setUsuarioId(SecurityUtils.resolverDono(dto.usuarioId()));
        aceite.setDocumentoId(dto.documentoId());
        aceite.setIp(ip);
        aceite.setAceitoEm(LocalDateTime.now());

        AceiteDocumento salvo = repository.save(aceite);
        return AceiteDocumentoResponseDTO.fromEntity(salvo);
    }

    @Transactional(readOnly = true)
    public Page<AceiteDocumentoResponseDTO> listarTodos(Pageable pageable) {
        SecurityUtils.exigirAdmin();
        return repository.findAll(pageable)
                .map(AceiteDocumentoResponseDTO::fromEntity);
    }

    @Transactional(readOnly = true)
    public AceiteDocumentoResponseDTO buscarPorId(UUID id) {
        AceiteDocumento aceite = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Registro de aceite não encontrado"));
        SecurityUtils.exigirDonoOuAdmin(aceite.getUsuarioId());
        return AceiteDocumentoResponseDTO.fromEntity(aceite);
    }

    @Transactional(readOnly = true)
    public Page<AceiteDocumentoResponseDTO> buscarPorUsuario(UUID usuarioId, Pageable pageable) {
        SecurityUtils.exigirDonoOuAdmin(usuarioId);
        return repository.findByUsuarioId(usuarioId, pageable)
                .map(AceiteDocumentoResponseDTO::fromEntity);
    }

    @Transactional
    public void deletar(UUID id) {
        SecurityUtils.exigirAdmin();
        if (!repository.existsById(id)) {
            throw new IllegalArgumentException("Registro de aceite não encontrado");
        }
        repository.deleteById(id);
    }
}