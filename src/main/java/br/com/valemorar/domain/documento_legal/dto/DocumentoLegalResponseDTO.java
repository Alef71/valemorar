package br.com.valemorar.domain.documento_legal.dto;

import br.com.valemorar.domain.documento_legal.DocumentoLegal;
import java.time.LocalDateTime;
import java.util.UUID;

public record DocumentoLegalResponseDTO(
        UUID id,
        String tipo,
        String versao,
        String conteudo,
        LocalDateTime publicadoEm) {
    public static DocumentoLegalResponseDTO fromEntity(DocumentoLegal entity) {
        return new DocumentoLegalResponseDTO(
                entity.getId(),
                entity.getTipo(),
                entity.getVersao(),
                entity.getConteudo(),
                entity.getPublicadoEm());
    }
}
