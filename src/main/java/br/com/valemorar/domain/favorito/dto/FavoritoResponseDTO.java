package br.com.valemorar.domain.favorito.dto;

import br.com.valemorar.domain.favorito.Favorito;
import java.time.LocalDateTime;
import java.util.UUID;

public record FavoritoResponseDTO(
        UUID id,
        UUID usuarioId,
        UUID anuncioId,
        LocalDateTime adicionadoEm) {
    public static FavoritoResponseDTO fromEntity(Favorito entity) {
        return new FavoritoResponseDTO(
                entity.getId(),
                entity.getUsuarioId(),
                entity.getAnuncioId(),
                entity.getAdicionadoEm());
    }
}
