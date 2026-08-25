package br.com.valemorar.domain.imovel.dto;

import br.com.valemorar.domain.imovel.Imovel;
import java.time.LocalDateTime;
import java.util.UUID;

public record ImovelResponseDTO(
        UUID id,
        UUID locadorId,
        UUID enderecoId,
        String titulo,
        String descricao,
        String tipoImovel,
        Double areaTotal,
        Double areaConstruida,
        Integer quartos,
        Integer suites,
        Integer banheiros,
        Integer vagasGaragem,
        Integer andar,
        Boolean mobiliado,
        Boolean aceitaPet,
        LocalDateTime criadoEm,
        LocalDateTime atualizadoEm) {
    public static ImovelResponseDTO fromEntity(Imovel imovel) {
        return new ImovelResponseDTO(
                imovel.getId(),
                imovel.getLocadorId(),
                imovel.getEnderecoId(),
                imovel.getTitulo(),
                imovel.getDescricao(),
                imovel.getTipoImovel(),
                imovel.getAreaTotal(),
                imovel.getAreaConstruida(),
                imovel.getQuartos(),
                imovel.getSuites(),
                imovel.getBanheiros(),
                imovel.getVagasGaragem(),
                imovel.getAndar(),
                imovel.getMobiliado(),
                imovel.getAceitaPet(),
                imovel.getCriadoEm(),
                imovel.getAtualizadoEm());
    }
}