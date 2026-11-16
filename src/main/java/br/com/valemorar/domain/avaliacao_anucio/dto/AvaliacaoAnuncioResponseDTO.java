package br.com.valemorar.domain.avaliacao_anucio.dto;

import br.com.valemorar.domain.avaliacao_anucio.AvaliacaoAnuncio;
import java.time.LocalDateTime;
import java.util.UUID;

public record AvaliacaoAnuncioResponseDTO(
        UUID id,
        UUID anuncioId,
        UUID usuarioId,
        String usuarioNome,
        Short nota,
        String comentario,
        LocalDateTime criadoEm) {
    public static AvaliacaoAnuncioResponseDTO fromEntity(AvaliacaoAnuncio avaliacao, String usuarioNome) {
        return new AvaliacaoAnuncioResponseDTO(
                avaliacao.getId(),
                avaliacao.getAnuncioId(),
                avaliacao.getUsuarioId(),
                usuarioNome,
                avaliacao.getNota(),
                avaliacao.getComentario(),
                avaliacao.getCriadoEm());
    }
}
