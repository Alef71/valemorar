package br.com.valemorar.domain.denuncia.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record DenunciaCreateDTO(

                @NotNull(message = "O ID do denunciante é obrigatório") UUID denuncianteId,

                @NotNull(message = "O ID do anúncio é obrigatório") UUID anuncioId,

                @NotBlank(message = "O motivo é obrigatório") @Size(max = 100, message = "O motivo deve ter no máximo 100 caracteres") String motivo,

                @NotBlank(message = "A descrição é obrigatória") @Size(max = 1000, message = "A descrição deve ter no máximo 1000 caracteres") String descricao) {
}