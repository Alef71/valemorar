package br.com.valemorar.domain.anucio.dto;

import br.com.valemorar.domain.endereco.dto.EnderecoCreateDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * Fluxo completo de publicação do painel: cria/atualiza endereço, imóvel, fotos e anúncio em uma única transação.
 */
public record AnuncioPublicacaoDTO(

        @NotNull(message = "O endereço do imóvel é obrigatório") @Valid EnderecoCreateDTO endereco,

        @Size(max = 150, message = "O título deve ter no máximo 150 caracteres") String titulo,

        @NotBlank(message = "O tipo do imóvel é obrigatório") @Size(max = 50) String tipoImovel,

        @Min(value = 0, message = "O número de quartos não pode ser negativo") Integer quartos,

        @PositiveOrZero(message = "O valor do condomínio não pode ser negativo") BigDecimal valorCondominio,

        @PositiveOrZero(message = "O valor do IPTU não pode ser negativo") BigDecimal valorIptu,

        @NotNull(message = "O valor é obrigatório") @Positive(message = "O valor deve ser maior que zero") BigDecimal valor,

        @NotBlank(message = "A modalidade é obrigatória") @Size(max = 50) String modalidade,

        List<@NotBlank @Size(max = 50) String> tags,

        @Size(max = 20, message = "Máximo de 20 fotos por anúncio") List<@Valid Foto> fotos) {

    public record Foto(
            @NotBlank(message = "A URL da foto é obrigatória") @Pattern(regexp = "^(https?://\\S+|/uploads/[\\w./-]+)$", message = "URL de foto inválida") @Size(max = 500) String url,
            Boolean capa) {
    }
}
