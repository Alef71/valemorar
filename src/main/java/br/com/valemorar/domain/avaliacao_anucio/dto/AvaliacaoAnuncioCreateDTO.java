package br.com.valemorar.domain.avaliacao_anucio.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record AvaliacaoAnuncioCreateDTO(
        @NotNull(message = "O ID do anúncio é obrigatório") UUID anuncioId,
        // Opcional: a avaliação é sempre do usuário autenticado
        UUID usuarioId,
        @NotNull(message = "A nota é obrigatória") @Min(value = 1, message = "A nota mínima é 1") @Max(value = 5, message = "A nota máxima é 5") Short nota,
        @Size(max = 1000, message = "O comentário deve ter no máximo 1000 caracteres") String comentario) {
}
