package br.com.valemorar.domain.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RedefinirSenhaDTO(
        @NotBlank(message = "O token de verificação é obrigatório") String token,

        @NotBlank(message = "A nova senha é obrigatória") @Size(min = 6, message = "A senha deve conter no mínimo 6 caracteres") String novaSenha) {
}
