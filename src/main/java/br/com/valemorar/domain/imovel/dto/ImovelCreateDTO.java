package br.com.valemorar.domain.imovel.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record ImovelCreateDTO(

                @NotNull(message = "O ID do locador é obrigatório") UUID locadorId,

                @NotNull(message = "O ID do endereço é obrigatório") UUID enderecoId,

                @NotBlank(message = "O título é obrigatório") @Size(max = 150, message = "O título deve ter no máximo 150 caracteres") String titulo,

                @Size(max = 2000, message = "A descrição deve ter no máximo 2000 caracteres") String descricao,

                @NotBlank(message = "O tipo do imóvel é obrigatório") @Size(max = 50, message = "O tipo do imóvel deve ter no máximo 50 caracteres") String tipoImovel,

                @Positive(message = "A área total deve ser um valor positivo") Double areaTotal,

                @Positive(message = "A área construída deve ser um valor positivo") Double areaConstruida,

                @Min(value = 0, message = "O número de quartos não pode ser negativo") Integer quartos,

                @Min(value = 0, message = "O número de suítes não pode ser negativo") Integer suites,

                @Min(value = 0, message = "O número de banheiros não pode ser negativo") Integer banheiros,

                @Min(value = 0, message = "O número de vagas de garagem não pode ser negativo") Integer vagasGaragem,

                @Min(value = 0, message = "O andar não pode ser negativo") Integer andar,

                Boolean mobiliado,

                Boolean aceitaPet) {
}