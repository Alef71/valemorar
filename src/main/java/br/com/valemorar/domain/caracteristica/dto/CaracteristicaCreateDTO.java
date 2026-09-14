package br.com.valemorar.domain.caracteristica.dto;

import java.util.UUID;

public record CaracteristicaCreateDTO(
        UUID categoriaId,
        String nome,
        String tipoValor,
        String unidade,
        Boolean permiteMultiplos,
        Boolean obrigatoria,
        Integer ordem) {
}
