package br.com.valemorar.domain.usuario.dto;

import br.com.valemorar.domain.usuario.Usuario;
import java.time.LocalDateTime;
import java.util.UUID;

public record UsuarioResponseDTO(
        UUID id,
        String nome,
        String email,
        String fotoPerfil,
        String status,
        LocalDateTime criadoEm) {
    public static UsuarioResponseDTO fromEntity(Usuario usuario) {
        return new UsuarioResponseDTO(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getFotoPerfil(),
                usuario.getStatus(),
                usuario.getCriadoEm());
    }
}
