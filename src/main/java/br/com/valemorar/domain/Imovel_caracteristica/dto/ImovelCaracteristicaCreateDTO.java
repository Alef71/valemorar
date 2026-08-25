package br.com.valemorar.domain.Imovel_caracteristica.dto;

import java.util.UUID;

public record ImovelCaracteristicaCreateDTO(
        UUID imovelId,
        UUID caracteristicaId,
        String valor) {
}
