package br.com.valemorar.domain.aceite_documento.service;

import br.com.valemorar.domain.aceite_documento.AceiteDocumento;
import br.com.valemorar.domain.aceite_documento.dto.AceiteDocumentoCreateDTO;
import br.com.valemorar.domain.aceite_documento.dto.AceiteDocumentoResponseDTO;
import br.com.valemorar.domain.aceite_documento.repository.AceiteDocumentoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class AceiteDocumentoService {

    private final AceiteDocumentoRepository repository;

    public AceiteDocumentoService(AceiteDocumentoRepository repository) {
        this.repository = repository;
    }

    public AceiteDocumentoResponseDTO registrarAceite(AceiteDocumentoCreateDTO dto) {
        AceiteDocumento aceite = new AceiteDocumento();
        aceite.setUsuarioId(dto.usuarioId());
        aceite.setDocumentoId(dto.documentoId());
        aceite.setIp(dto.ip());
        aceite.setAceitoEm(LocalDateTime.now());

        AceiteDocumento salvo = repository.save(aceite);
        return AceiteDocumentoResponseDTO.fromEntity(salvo);
    }

    public List<AceiteDocumentoResponseDTO> listarTodos() {
        return repository.findAll()
                .stream()
                .map(AceiteDocumentoResponseDTO::fromEntity)
                .toList();
    }

    public AceiteDocumentoResponseDTO buscarPorId(UUID id) {
        AceiteDocumento aceite = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Registro de aceite não encontrado"));
        return AceiteDocumentoResponseDTO.fromEntity(aceite);
    }

    public List<AceiteDocumentoResponseDTO> buscarPorUsuario(UUID usuarioId) {
        return repository.findByUsuarioId(usuarioId)
                .stream()
                .map(AceiteDocumentoResponseDTO::fromEntity)
                .toList();
    }

    public void deletar(UUID id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Registro de aceite não encontrado");
        }
        repository.deleteById(id);
    }
}
