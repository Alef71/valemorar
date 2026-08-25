package br.com.valemorar.domain.locador.dto;

import java.util.UUID;

public record LocadorCreateDTO(
        UUID usuarioId,
        String telefone,
        String whatsapp,
        String documento) {
}
