package br.com.valemorar.domain.usuario.dto;

public record UsuarioCreateDTO(
        String nome,
        String email,
        String senha,
        String fotoPerfil) {
}
