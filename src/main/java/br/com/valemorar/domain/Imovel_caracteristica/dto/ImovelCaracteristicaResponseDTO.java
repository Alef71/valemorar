package br.com.valemorar.domain.Imovel_caracteristica.dto;

import br.com.valemorar.domain.Imovel_caracteristica.ImovelCaracteristica;
import java.util.UUID;

public record ImovelCaracteristicaResponseDTO(
        UUID id,
        UUID imovelId,
        UUID caracteristicaId,
        String valor) {
    public static ImovelCaracteristicaResponseDTO fromEntity(ImovelCaracteristica entity) {
        return new ImovelCaracteristicaResponseDTO(
                entity.getId(),
                entity.getImovelId(),
                entity.getCaracteristicaId(),
                entity.getValor());
    }
}
