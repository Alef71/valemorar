package br.com.valemorar.domain.auth.dto;

import java.util.UUID;

public record AuthResponseDTO(
        String token,
        String tipo,
        UUID usuarioId,
        String nome,
        String email,
        String perfil) {
    public AuthResponseDTO(String token, UUID usuarioId, String nome, String email, String perfil) {
        this(token, "Bearer", usuarioId, nome, email, perfil);
    }
}
