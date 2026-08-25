package br.com.valemorar.domain.anucio.dto;

import br.com.valemorar.domain.anucio.Anuncio;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record AnuncioResponseDTO(
    UUID id,
    UUID imovelId,
    UUID anuncianteId,
    BigDecimal valor,
    String modalidade,
    BigDecimal valorCondominio,
    BigDecimal valorIptu,
    BigDecimal notaMedia,
    Integer totalAvaliacoes,
    String status,
    LocalDateTime publicadoEm,
    LocalDateTime expiraEm,
    LocalDateTime atualizadoEm
) {
    public static AnuncioResponseDTO fromEntity(Anuncio anuncio) {
        return new AnuncioResponseDTO(
            anuncio.getId(),
            anuncio.getImovelId(),
            anuncio.getAnuncianteId(),
            anuncio.getValor(),
            anuncio.getModalidade(),
            anuncio.getValorCondominio(),
            anuncio.getValorIptu(),
            anuncio.getNotaMedia(),
            anuncio.getTotalAvaliacoes(),
            anuncio.getStatus(),
            anuncio.getPublicadoEm(),
            anuncio.getExpiraEm(),
            anuncio.getAtualizadoEm()
        );
    }
}