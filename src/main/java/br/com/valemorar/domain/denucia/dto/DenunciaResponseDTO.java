package br.com.valemorar.domain.denucia.dto;

import br.com.valemorar.domain.denucia.Denuncia;
import java.time.LocalDateTime;
import java.util.UUID;

public record DenunciaResponseDTO(
        UUID id,
        UUID denuncianteId,
        UUID anuncioId,
        String motivo,
        String descricao,
        String status,
        UUID resolvidoPor,
        LocalDateTime denunciadoEm,
        LocalDateTime resolvidoEm) {
    public static DenunciaResponseDTO fromEntity(Denuncia entity) {
        return new DenunciaResponseDTO(
                entity.getId(),
                entity.getDenuncianteId(),
                entity.getAnuncioId(),
                entity.getMotivo(),
                entity.getDescricao(),
                entity.getStatus(),
                entity.getResolvidoPor(),
                entity.getDenunciadoEm(),
                entity.getResolvidoEm());
    }
}
