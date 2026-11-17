package br.com.valemorar.domain.anucio.dto;

import br.com.valemorar.domain.anucio.Anuncio;
import br.com.valemorar.domain.endereco.Endereco;
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
        String logradouro,
        String numero,
        String complemento,
        String bairro,
        String cidade,
        String estado,
        String cep,
        String titulo,
        String descricao,
        String tipoImovel,
        Integer quartos,
        Boolean aceitaPet,
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
        LocalDateTime atualizadoEm,
        String anuncianteNome,
        String anuncianteEmail,
        String fotoCapa,
        String anuncianteFotoPerfil) {

    public static AnuncioResponseDTO fromEntity(Anuncio anuncio, Endereco endereco) {
        return fromEntity(anuncio, endereco, null, null);
    }

    public static AnuncioResponseDTO fromEntity(
            Anuncio anuncio,
            Endereco endereco,
            String anuncianteNome,
            String anuncianteEmail) {
        return fromEntity(anuncio, endereco, anuncianteNome, anuncianteEmail, null);
    }

    public static AnuncioResponseDTO fromEntity(
            Anuncio anuncio,
            Endereco endereco,
            String anuncianteNome,
            String anuncianteEmail,
            String fotoCapa) {
        return fromEntity(anuncio, endereco, anuncianteNome, anuncianteEmail, fotoCapa, null);
    }

    public static AnuncioResponseDTO fromEntity(
            Anuncio anuncio,
            Endereco endereco,
            String anuncianteNome,
            String anuncianteEmail,
            String fotoCapa,
            String anuncianteFotoPerfil) {
        Imovel imovel = anuncio.getImovel();

        return new AnuncioResponseDTO(
                anuncio.getId(),
                imovel != null ? imovel.getId() : null,
                anuncio.getAnuncianteId(),
                endereco != null ? endereco.getLogradouro() : null,
                endereco != null ? endereco.getNumero() : null,
                endereco != null ? endereco.getComplemento() : null,
                endereco != null ? endereco.getBairro() : null,
                endereco != null ? endereco.getCidade() : null,
                endereco != null ? endereco.getEstado() : null,
                endereco != null ? endereco.getCep() : null,
                imovel != null ? imovel.getTitulo() : null,
                imovel != null ? imovel.getDescricao() : null,
                imovel != null ? imovel.getTipoImovel() : null,
                imovel != null ? imovel.getQuartos() : null,
                imovel != null ? imovel.getAceitaPet() : null,
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
                anuncio.getAtualizadoEm(),
                anuncianteNome,
                anuncianteEmail,
                fotoCapa,
                anuncianteFotoPerfil);
    }
}