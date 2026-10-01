package br.com.valemorar.domain.aceite_documento.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record AceiteDocumentoCreateDTO(

                // Opcional: o aceite é sempre do usuário autenticado; apenas administradores podem informar outro usuário
                UUID usuarioId,

                @NotNull(message = "O ID do documento é obrigatório") UUID documentoId) {
}