package br.com.valemorar.domain.foto_imovel.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.URL;

import java.util.UUID;

public record FotoImovelCreateDTO(

                @NotNull(message = "O ID do imóvel é obrigatório") UUID imovelId,

                @NotBlank(message = "A URL da foto é obrigatória") @URL(message = "A URL informada deve ser válida") @Size(max = 500, message = "A URL deve ter no máximo 500 caracteres") String url,

                Boolean capa,

                @Min(value = 0, message = "A ordem deve ser um número maior ou igual a zero") Integer ordem) {
}