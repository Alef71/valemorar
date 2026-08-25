package br.com.valemorar.domain.endereco.dto;

public record EnderecoCreateDTO(
        String logradouro,
        String numero,
        String complemento,
        String bairro,
        String cidade,
        String estado,
        String cep,
        Double latitude,
        Double longitude) {
}
