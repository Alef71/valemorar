package br.com.valemorar.domain.endereco.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record EnderecoCreateDTO(

                @NotBlank(message = "O logradouro é obrigatório") @Size(max = 255, message = "O logradouro deve ter no máximo 255 caracteres") String logradouro,

                @NotBlank(message = "O número é obrigatório") @Size(max = 20, message = "O número deve ter no máximo 20 caracteres") String numero,

                @Size(max = 100, message = "O complemento deve ter no máximo 100 caracteres") String complemento,

                @NotBlank(message = "O bairro é obrigatório") @Size(max = 100, message = "O bairro deve ter no máximo 100 caracteres") String bairro,

                @NotBlank(message = "A cidade é obrigatória") @Size(max = 100, message = "A cidade deve ter no máximo 100 caracteres") String cidade,

                @NotBlank(message = "O estado (UF) é obrigatório") @Size(min = 2, max = 2, message = "O estado deve conter exatamente 2 caracteres (ex: SP, RJ)") String estado,

                @NotBlank(message = "O CEP é obrigatório") @Pattern(regexp = "\\d{5}-?\\d{3}", message = "O CEP deve estar no formato 00000-000 ou 00000000") String cep,

                Double latitude,

                Double longitude) {
}