package br.com.valemorar.domain.sessao.dto;

import br.com.valemorar.domain.sessao.Sessao;
import java.time.LocalDateTime;
import java.util.UUID;

public record SessaoResponseDTO(
        UUID id,
        UUID usuarioId,
        String refreshTokenHash,
        String ip,
        String userAgent,
        LocalDateTime criadoEm,
        LocalDateTime expiraEm,
        LocalDateTime revogadoEm) {
    public static SessaoResponseDTO fromEntity(Sessao entity) {
        return new SessaoResponseDTO(
                entity.getId(),
                entity.getUsuarioId(),
                entity.getRefreshTokenHash(),
                entity.getIp(),
                entity.getUserAgent(),
                entity.getCriadoEm(),
                entity.getExpiraEm(),
                entity.getRevogadoEm());
    }
}
