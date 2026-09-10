package br.com.valemorar.domain.auth.dto;

import br.com.valemorar.domain.usuario.enums.PerfilEnum;
import br.com.valemorar.domain.usuario.enums.StatusUsuarioEnum;

import java.util.UUID;

public record AuthResponseDTO(
        String token,
        String tipo,
        UUID usuarioId,
        String nome,
        String email,
        StatusUsuarioEnum status,
        PerfilEnum perfil) {

    public AuthResponseDTO(String token, UUID usuarioId, String nome, String email, StatusUsuarioEnum status,
            PerfilEnum perfil) {
        this(token, "Bearer", usuarioId, nome, email, status, perfil);
    }
}