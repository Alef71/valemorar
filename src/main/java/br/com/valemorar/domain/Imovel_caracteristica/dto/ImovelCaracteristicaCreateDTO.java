package br.com.valemorar.domain.imovel_caracteristica.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record ImovelCaracteristicaCreateDTO(

                @NotNull(message = "O ID do imóvel é obrigatório") UUID imovelId,

                @NotNull(message = "O ID da característica é obrigatório") UUID caracteristicaId,

                @Size(max = 255, message = "O valor deve ter no máximo 255 caracteres") String valor) {
}