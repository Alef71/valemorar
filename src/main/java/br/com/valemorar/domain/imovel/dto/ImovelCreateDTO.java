package br.com.valemorar.domain.imovel.dto;

import java.util.UUID;

public record ImovelCreateDTO(
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
        Boolean aceitaPet) {
}