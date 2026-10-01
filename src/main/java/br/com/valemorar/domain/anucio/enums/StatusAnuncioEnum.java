package br.com.valemorar.domain.anucio.enums;

import java.util.Locale;

public enum StatusAnuncioEnum {
    ATIVO,
    PAUSADO,
    ALUGADO,
    INDISPONIVEL,
    FINALIZADO;

    public static StatusAnuncioEnum from(String valor) {
        try {
            return valueOf(valor.trim().toUpperCase(Locale.ROOT));
        } catch (RuntimeException e) {
            throw new IllegalArgumentException("Status de anúncio inválido: " + valor);
        }
    }
}
