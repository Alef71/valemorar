package br.com.valemorar.domain.anucio.dto;

import br.com.valemorar.domain.anucio.enums.StatusAnuncioEnum;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

// O anunciante é sempre o usuário autenticado; o imóvel precisa pertencer a ele
public record AnuncioCreateDTO(

                @NotNull(message = "O ID do imóvel é obrigatório") UUID imovelId,

                @NotNull(message = "O valor é obrigatório") @Positive(message = "O valor deve ser maior que zero") BigDecimal valor,

                @NotBlank(message = "A modalidade é obrigatória") @Size(max = 50, message = "A modalidade deve ter no máximo 50 caracteres") String modalidade,

                List<@NotBlank @Size(max = 50) String> tags,

                StatusAnuncioEnum status,

                @Future(message = "A data de expiração deve ser no futuro") LocalDateTime expiraEm) {
}
