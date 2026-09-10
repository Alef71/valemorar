package br.com.valemorar.domain.aceite_documento.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record AceiteDocumentoCreateDTO(

                @NotNull(message = "O ID do usuário é obrigatório") UUID usuarioId,

                @NotNull(message = "O ID do documento é obrigatório") UUID documentoId,

                @NotBlank(message = "O IP é obrigatório") @Size(max = 45, message = "O IP deve ter no máximo 45 caracteres") String ip) {
}