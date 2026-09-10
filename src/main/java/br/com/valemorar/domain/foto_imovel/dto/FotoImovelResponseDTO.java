package br.com.valemorar.domain.foto_imovel.dto;

import br.com.valemorar.domain.foto_imovel.FotoImovel;

import java.time.LocalDateTime;
import java.util.UUID;

public record FotoImovelResponseDTO(
        UUID id,
        UUID imovelId,
        String url,
        Boolean capa,
        Integer ordem,
        LocalDateTime criadoEm) {
    public static FotoImovelResponseDTO fromEntity(FotoImovel entity) {
        return new FotoImovelResponseDTO(
                entity.getId(),
                entity.getImovelId(),
                entity.getUrl(),
                entity.getCapa(),
                entity.getOrdem(),
                entity.getCriadoEm());
    }
}