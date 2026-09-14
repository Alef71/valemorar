package br.com.valemorar.domain.aceite_documento.service;

import br.com.valemorar.domain.aceite_documento.AceiteDocumento;
import br.com.valemorar.domain.aceite_documento.dto.AceiteDocumentoCreateDTO;
import br.com.valemorar.domain.aceite_documento.dto.AceiteDocumentoResponseDTO;
import br.com.valemorar.domain.aceite_documento.repository.AceiteDocumentoRepository;
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
    public AceiteDocumentoResponseDTO registrarAceite(AceiteDocumentoCreateDTO dto) {
        AceiteDocumento aceite = new AceiteDocumento();
        aceite.setUsuarioId(dto.usuarioId());
        aceite.setDocumentoId(dto.documentoId());
        aceite.setIp(dto.ip());
        aceite.setAceitoEm(LocalDateTime.now());

        AceiteDocumento salvo = repository.save(aceite);
        return AceiteDocumentoResponseDTO.fromEntity(salvo);
    }

    @Transactional(readOnly = true)
    public Page<AceiteDocumentoResponseDTO> listarTodos(Pageable pageable) {
        return repository.findAll(pageable)
                .map(AceiteDocumentoResponseDTO::fromEntity);
    }

    @Transactional(readOnly = true)
    public AceiteDocumentoResponseDTO buscarPorId(UUID id) {
        AceiteDocumento aceite = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Registro de aceite não encontrado"));
        return AceiteDocumentoResponseDTO.fromEntity(aceite);
    }

    @Transactional(readOnly = true)
    public Page<AceiteDocumentoResponseDTO> buscarPorUsuario(UUID usuarioId, Pageable pageable) {
        return repository.findByUsuarioId(usuarioId, pageable)
                .map(AceiteDocumentoResponseDTO::fromEntity);
    }

    @Transactional
    public void deletar(UUID id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Registro de aceite não encontrado");
        }
        repository.deleteById(id);
    }
}