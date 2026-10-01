package br.com.valemorar.domain.locatario.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record LocatarioCreateDTO(

                // Opcional: o cadastro é sempre do usuário autenticado; apenas administradores podem informar outro usuário
                UUID usuarioId) {
}