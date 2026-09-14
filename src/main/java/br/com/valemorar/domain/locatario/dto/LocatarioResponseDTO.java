package br.com.valemorar.domain.locatario.dto;

import br.com.valemorar.domain.locatario.Locatario;

import java.time.LocalDateTime;
import java.util.UUID;

public record LocatarioResponseDTO(
        UUID usuarioId,
        LocalDateTime criadoEm) {
    public static LocatarioResponseDTO fromEntity(Locatario entity) {
        return new LocatarioResponseDTO(
                entity.getUsuarioId(),
                entity.getCriadoEm());
    }
}