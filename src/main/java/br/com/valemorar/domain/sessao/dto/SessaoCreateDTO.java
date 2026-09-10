package br.com.valemorar.domain.sessao.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.UUID;

public record SessaoCreateDTO(

                @NotNull(message = "O ID do usuário é obrigatório") UUID usuarioId,

                @NotBlank(message = "O hash do refresh token é obrigatório") String refreshTokenHash,

                @NotBlank(message = "O IP é obrigatório") @Size(max = 45, message = "O IP deve ter no máximo 45 caracteres") String ip,

                @Size(max = 500, message = "O User Agent deve ter no máximo 500 caracteres") String userAgent,

                @NotNull(message = "A data de expiração é obrigatória") @Future(message = "A data de expiração deve ser no futuro") LocalDateTime expiraEm) {
}