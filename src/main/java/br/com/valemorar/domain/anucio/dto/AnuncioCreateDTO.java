package br.com.valemorar.domain.anucio.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record AnuncioCreateDTO(

        @NotNull(message = "O ID do imóvel é obrigatório") UUID imovelId,

        @NotNull(message = "O ID do anunciante é obrigatório") UUID anuncianteId,

        @NotBlank(message = "A cidade é obrigatória") @Size(max = 100, message = "A cidade deve ter no máximo 100 caracteres") String cidade,

        @Min(value = 0, message = "O número de quartos não pode ser negativo") Integer quartos,

        List<String> tags,

        @NotNull(message = "O valor é obrigatório") @Positive(message = "O valor deve ser maior que zero") BigDecimal valor,

        @NotBlank(message = "A modalidade é obrigatória") @Size(max = 50, message = "A modalidade deve ter no máximo 50 caracteres") String modalidade,

        @PositiveOrZero(message = "O valor do condomínio não pode ser negativo") BigDecimal valorCondominio,

        @PositiveOrZero(message = "O valor do IPTU não pode ser negativo") BigDecimal valorIptu,

        @Size(max = 30, message = "O status deve ter no máximo 30 caracteres") String status,

        @Future(message = "A data de expiração deve ser no futuro") LocalDateTime expiraEm) {
}