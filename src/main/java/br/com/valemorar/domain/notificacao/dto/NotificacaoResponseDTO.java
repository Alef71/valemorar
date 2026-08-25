package br.com.valemorar.domain.notificacao.dto;

import br.com.valemorar.domain.notificacao.Notificacao;
import java.time.LocalDateTime;
import java.util.UUID;

public record NotificacaoResponseDTO(
        UUID id,
        UUID usuarioId,
        UUID anuncioId,
        String mensagem,
        String tipo,
        Boolean lida,
        LocalDateTime criadoEm) {
    public static NotificacaoResponseDTO fromEntity(Notificacao entity) {
        return new NotificacaoResponseDTO(
                entity.getId(),
                entity.getUsuarioId(),
                entity.getAnuncioId(),
                entity.getMensagem(),
                entity.getTipo(),
                entity.getLida(),
                entity.getCriadoEm());
    }
}
