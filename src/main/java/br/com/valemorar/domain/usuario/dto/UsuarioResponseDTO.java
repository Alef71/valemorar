package br.com.valemorar.domain.usuario.dto;

import br.com.valemorar.domain.usuario.Usuario;
import br.com.valemorar.domain.usuario.enums.PerfilEnum;
import br.com.valemorar.domain.usuario.enums.StatusUsuarioEnum;

import java.time.LocalDateTime;
import java.util.UUID;

public record UsuarioResponseDTO(
        UUID id,
        String nome,
        String email,
        String fotoPerfil,
        StatusUsuarioEnum status,
        PerfilEnum perfil,
        LocalDateTime criadoEm) {

    public static UsuarioResponseDTO fromEntity(Usuario usuario) {
        return new UsuarioResponseDTO(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getFotoPerfil(),
                usuario.getStatus(),
                usuario.getPerfil(),
                usuario.getCriadoEm());
    }
}