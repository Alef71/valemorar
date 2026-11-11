package br.com.valemorar.domain.anucio.dto;

import br.com.valemorar.domain.anucio.Anuncio;
import br.com.valemorar.domain.endereco.Endereco;
import br.com.valemorar.domain.foto_imovel.dto.FotoImovelResponseDTO;
import br.com.valemorar.domain.imovel.Imovel;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

// Expõe apenas a localização pública do imóvel (cidade/bairro/UF), nunca logradouro ou número
public record AnuncioResponseDTO(
        UUID id,
        UUID imovelId,
        UUID enderecoId,
        UUID anuncianteId,
        String titulo,
        String tipoImovel,
        Integer quartos,
        String cidade,
        String bairro,
        String estado,
        List<String> tags,
        List<FotoImovelResponseDTO> fotos,
        BigDecimal valor,
        String modalidade,
        BigDecimal valorCondominio,
        BigDecimal valorIptu,
        BigDecimal notaMedia,
        Integer totalAvaliacoes,
        String status,
        LocalDateTime publicadoEm,
        LocalDateTime expiraEm,
        LocalDateTime atualizadoEm,
        String motivoRevisao,
        BigDecimal indiceValor,
        BigDecimal economiaMercado) {

    public static AnuncioResponseDTO fromEntity(Anuncio anuncio, Endereco endereco, List<FotoImovelResponseDTO> fotos) {
        Imovel imovel = anuncio.getImovel();

        return new AnuncioResponseDTO(
                anuncio.getId(),
                imovel != null ? imovel.getId() : null,
                imovel != null ? imovel.getEnderecoId() : null,
                anuncio.getAnuncianteId(),
                imovel != null ? imovel.getTitulo() : null,
                imovel != null ? imovel.getTipoImovel() : null,
                imovel != null ? imovel.getQuartos() : null,
                endereco != null ? endereco.getCidade() : null,
                endereco != null ? endereco.getBairro() : null,
                endereco != null ? endereco.getEstado() : null,
                anuncio.getTags() != null ? new ArrayList<>(anuncio.getTags()) : List.of(),
                fotos != null ? fotos : List.of(),
                anuncio.getValor(),
                anuncio.getModalidade(),
                imovel != null ? imovel.getValorCondominio() : null,
                imovel != null ? imovel.getValorIptu() : null,
                anuncio.getNotaMedia(),
                anuncio.getTotalAvaliacoes(),
                anuncio.getStatus() != null ? anuncio.getStatus().name() : null,
                anuncio.getPublicadoEm(),
                anuncio.getExpiraEm(),
                anuncio.getAtualizadoEm(),
                anuncio.getMotivoRevisao(),
                anuncio.getIndiceValor(),
                anuncio.getEconomiaMercado());
    }
}
