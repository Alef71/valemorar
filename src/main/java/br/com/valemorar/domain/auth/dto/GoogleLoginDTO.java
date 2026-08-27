package br.com.valemorar.domain.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record GoogleLoginDTO(
        @NotBlank(message = "O token do Google é obrigatório") String idToken) {
}