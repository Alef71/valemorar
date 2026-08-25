package br.com.valemorar.domain.avaliacao_anucio.dto;

import br.com.valemorar.domain.avaliacao_anucio.AvaliacaoAnuncio;
import java.time.LocalDateTime;
import java.util.UUID;

public record AvaliacaoAnuncioResponseDTO(
        UUID id,
        UUID anuncioId,
        UUID usuarioId,
        Short nota,
        String comentario,
        LocalDateTime criadoEm) {
    public static AvaliacaoAnuncioResponseDTO fromEntity(AvaliacaoAnuncio avaliacao) {
        return new AvaliacaoAnuncioResponseDTO(
                avaliacao.getId(),
                avaliacao.getAnuncioId(),
                avaliacao.getUsuarioId(),
                avaliacao.getNota(),
                avaliacao.getComentario(),
                avaliacao.getCriadoEm());
    }
}
