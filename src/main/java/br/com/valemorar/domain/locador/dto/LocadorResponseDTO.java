package br.com.valemorar.domain.locador.dto;

import br.com.valemorar.domain.locador.Locador;

import java.time.LocalDateTime;
import java.util.UUID;

public record LocadorResponseDTO(
        UUID usuarioId,
        String telefone,
        String whatsapp,
        String documento,
        LocalDateTime criadoEm) {
    public static LocadorResponseDTO fromEntity(Locador entity) {
        return new LocadorResponseDTO(
                entity.getUsuarioId(),
                entity.getTelefone(),
                entity.getWhatsapp(),
                entity.getDocumento(),
                entity.getCriadoEm());
    }
}