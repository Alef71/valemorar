package br.com.valemorar.domain.caracteristica.dto;

import br.com.valemorar.domain.caracteristica.Caracteristica;
import java.util.UUID;

public record CaracteristicaResponseDTO(
        UUID id,
        UUID categoriaId,
        String nome,
        String tipoValor,
        String unidade,
        Boolean permiteMultiplos,
        Boolean obrigatoria,
        Integer ordem) {
    public static CaracteristicaResponseDTO fromEntity(Caracteristica entity) {
        return new CaracteristicaResponseDTO(
                entity.getId(),
                entity.getCategoriaId(),
                entity.getNome(),
                entity.getTipoValor(),
                entity.getUnidade(),
                entity.getPermiteMultiplos(),
                entity.getObrigatoria(),
                entity.getOrdem());
    }
}