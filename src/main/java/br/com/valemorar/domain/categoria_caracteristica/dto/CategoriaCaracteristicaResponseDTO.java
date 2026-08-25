package br.com.valemorar.domain.categoria_caracteristica.dto;

import br.com.valemorar.domain.categoria_caracteristica.CategoriaCaracteristica;
import java.util.UUID;

public record CategoriaCaracteristicaResponseDTO(
        UUID id,
        String nome,
        String icone,
        Integer ordem) {
    public static CategoriaCaracteristicaResponseDTO fromEntity(CategoriaCaracteristica entity) {
        return new CategoriaCaracteristicaResponseDTO(
                entity.getId(),
                entity.getNome(),
                entity.getIcone(),
                entity.getOrdem());
    }
}
