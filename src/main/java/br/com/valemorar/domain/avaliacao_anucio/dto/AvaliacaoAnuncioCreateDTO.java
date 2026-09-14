package br.com.valemorar.domain.avaliacao_anucio.dto;

import java.util.UUID;

public record AvaliacaoAnuncioCreateDTO(
        UUID anuncioId,
        UUID usuarioId,
        Short nota,
        String comentario) {
}
