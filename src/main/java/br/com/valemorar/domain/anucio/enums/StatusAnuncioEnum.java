package br.com.valemorar.domain.anucio.enums;

import java.util.Locale;

public enum StatusAnuncioEnum {
    ATIVO,
    PAUSADO,
    ALUGADO,
    INDISPONIVEL,
    FINALIZADO,
    /** Recém-criado ou editado: aguardando a revisão automática (RevisaoAnuncioService). */
    IN_REVIEW,
    /** Reprovado pela revisão automática: só um admin pode liberar. */
    MANUAL_REVIEW_REQUIRED;

    public boolean emRevisao() {
        return this == IN_REVIEW || this == MANUAL_REVIEW_REQUIRED;
    }

    public static StatusAnuncioEnum from(String valor) {
        try {
            return valueOf(valor.trim().toUpperCase(Locale.ROOT));
        } catch (RuntimeException e) {
            throw new IllegalArgumentException("Status de anúncio inválido: " + valor);
        }
    }
}
