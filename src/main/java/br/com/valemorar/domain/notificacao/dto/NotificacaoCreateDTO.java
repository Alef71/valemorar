package br.com.valemorar.domain.notificacao.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record NotificacaoCreateDTO(

                @NotNull(message = "O ID do usuário é obrigatório") UUID usuarioId,

                UUID anuncioId,

                @NotBlank(message = "A mensagem é obrigatória") @Size(max = 500, message = "A mensagem deve ter no máximo 500 caracteres") String mensagem,

                @NotBlank(message = "O tipo é obrigatório") @Size(max = 50, message = "O tipo deve ter no máximo 50 caracteres") String tipo) {
}