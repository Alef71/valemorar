package br.com.valemorar.domain.locatario.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record LocatarioCreateDTO(

                @NotNull(message = "O ID do usuário é obrigatório") UUID usuarioId) {
}