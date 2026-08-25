package br.com.valemorar.domain.categoria_caracteristica.dto;

public record CategoriaCaracteristicaCreateDTO(
        String nome,
        String icone,
        Integer ordem) {
}