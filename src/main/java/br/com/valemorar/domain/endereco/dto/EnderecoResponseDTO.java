package br.com.valemorar.domain.endereco.dto;

import br.com.valemorar.domain.endereco.Endereco;

import java.util.UUID;

public record EnderecoResponseDTO(
        UUID id,
        String logradouro,
        String numero,
        String complemento,
        String bairro,
        String cidade,
        String estado,
        String cep,
        Double latitude,
        Double longitude) {
    public static EnderecoResponseDTO fromEntity(Endereco entity) {
        return new EnderecoResponseDTO(
                entity.getId(),
                entity.getLogradouro(),
                entity.getNumero(),
                entity.getComplemento(),
                entity.getBairro(),
                entity.getCidade(),
                entity.getEstado(),
                entity.getCep(),
                entity.getLatitude(),
                entity.getLongitude());
    }
}