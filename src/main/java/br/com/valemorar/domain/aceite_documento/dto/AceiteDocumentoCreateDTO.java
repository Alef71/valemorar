package br.com.valemorar.domain.aceite_documento.dto;

import java.util.UUID;

public record AceiteDocumentoCreateDTO(
        UUID usuarioId,
        UUID documentoId,
        String ip) {
}