package br.com.valemorar.domain.favorito.dto;

import java.util.UUID;

public record FavoritoCreateDTO(
        UUID usuarioId,
        UUID anuncioId) {
}
