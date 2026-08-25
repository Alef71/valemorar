package br.com.valemorar.domain.denucia.dto;

import java.util.UUID;

public record DenunciaCreateDTO(
        UUID denuncianteId,
        UUID anuncioId,
        String motivo,
        String descricao) {
}
