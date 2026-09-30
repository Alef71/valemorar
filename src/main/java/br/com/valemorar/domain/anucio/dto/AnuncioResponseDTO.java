package br.com.valemorar.domain.anucio.dto;

import br.com.valemorar.domain.anucio.Anuncio;
import br.com.valemorar.domain.imovel.Imovel;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public record AnuncioResponseDTO(
        UUID id,
        UUID imovelId,
        UUID anuncianteId,
        String tipoImovel,
        Integer quartos,
        List<String> tags,
        BigDecimal valor,
        String modalidade,
        BigDecimal valorCondominio,
        BigDecimal valorIptu,
        BigDecimal notaMedia,
        Integer totalAvaliacoes,
        String status,
        LocalDateTime publicadoEm,
        LocalDateTime expiraEm,
        LocalDateTime atualizadoEm) {

    public static AnuncioResponseDTO fromEntity(Anuncio anuncio) {
        Imovel imovel = anuncio.getImovel();

        return new AnuncioResponseDTO(
                anuncio.getId(),
                imovel != null ? imovel.getId() : null,
                anuncio.getAnuncianteId(),
                imovel != null ? imovel.getTipoImovel() : null,
                imovel != null ? imovel.getQuartos() : null,
                anuncio.getTags() != null ? new ArrayList<>(anuncio.getTags()) : List.of(),
                anuncio.getValor(),
                anuncio.getModalidade(),
                imovel != null ? imovel.getValorCondominio() : null,
                imovel != null ? imovel.getValorIptu() : null,
                anuncio.getNotaMedia(),
                anuncio.getTotalAvaliacoes(),
                anuncio.getStatus(),
                anuncio.getPublicadoEm(),
                anuncio.getExpiraEm(),
                anuncio.getAtualizadoEm());
    }
}