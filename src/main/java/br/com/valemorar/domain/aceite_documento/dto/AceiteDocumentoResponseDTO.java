package br.com.valemorar.domain.aceite_documento.dto;

import br.com.valemorar.domain.aceite_documento.AceiteDocumento;
import java.time.LocalDateTime;
import java.util.UUID;

public record AceiteDocumentoResponseDTO(
        UUID id,
        UUID usuarioId,
        UUID documentoId,
        String ip,
        LocalDateTime aceitoEm) {
    public static AceiteDocumentoResponseDTO fromEntity(AceiteDocumento entity) {
        return new AceiteDocumentoResponseDTO(
                entity.getId(),
                entity.getUsuarioId(),
                entity.getDocumentoId(),
                entity.getIp(),
                entity.getAceitoEm());
    }
}
