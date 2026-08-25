package br.com.valemorar.domain.notificacao.dto;

import java.util.UUID;

public record NotificacaoCreateDTO(
        UUID usuarioId,
        UUID anuncioId,
        String mensagem,
        String tipo) {
}
