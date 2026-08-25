package br.com.valemorar.domain.anucio.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record AnuncioCreateDTO(
        UUID imovelId,
        UUID anuncianteId,
        BigDecimal valor,
        String modalidade,
        BigDecimal valorCondominio,
        BigDecimal valorIptu,
        String status,
        LocalDateTime expiraEm) {
}
