package br.com.valemorar.domain.sessao.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record SessaoCreateDTO(
        UUID usuarioId,
        String refreshTokenHash,
        String ip,
        String userAgent,
        LocalDateTime expiraEm) {
}
