package br.com.valemorar.domain.foto_imovel.dto;

import java.util.UUID;

public record FotoImovelCreateDTO(
        UUID imovelId,
        String url,
        Boolean capa,
        Integer ordem) {
}