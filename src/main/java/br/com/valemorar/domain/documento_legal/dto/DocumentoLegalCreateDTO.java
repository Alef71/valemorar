package br.com.valemorar.domain.documento_legal.dto;

public record DocumentoLegalCreateDTO(
        String tipo,
        String versao,
        String conteudo) {
}
