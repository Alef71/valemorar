package br.com.valemorar.domain.locador.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record LocadorCreateDTO(

                @NotNull(message = "O ID do usuário é obrigatório") UUID usuarioId,

                @NotBlank(message = "O telefone é obrigatório") @Size(max = 20, message = "O telefone deve ter no máximo 20 caracteres") String telefone,

                @Size(max = 20, message = "O WhatsApp deve ter no máximo 20 caracteres") String whatsapp,

                @NotBlank(message = "O documento é obrigatório") @Size(max = 20, message = "O documento deve ter no máximo 20 caracteres") String documento) {
}