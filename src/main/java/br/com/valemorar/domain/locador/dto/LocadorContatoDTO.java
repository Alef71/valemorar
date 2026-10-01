package br.com.valemorar.domain.locador.dto;

import jakarta.validation.constraints.Pattern;

// Telefone/WhatsApp exibidos aos interessados nos anúncios; aceita dígitos, espaços, +, -, ( e )
public record LocadorContatoDTO(
        @Pattern(regexp = "^$|^[0-9 ()+-]{10,20}$", message = "Telefone inválido. Ex: (33) 99999-9999") String telefone,
        @Pattern(regexp = "^$|^[0-9 ()+-]{10,20}$", message = "WhatsApp inválido. Ex: (33) 99999-9999") String whatsapp) {
}
