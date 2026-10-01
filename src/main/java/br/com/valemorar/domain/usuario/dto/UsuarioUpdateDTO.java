package br.com.valemorar.domain.usuario.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UsuarioUpdateDTO(
                @NotBlank(message = "O nome é obrigatório") @Size(min = 3, max = 100, message = "O nome deve ter entre 3 e 100 caracteres") String nome,

                // Nulo mantém a foto atual; para enviar arquivo use POST /api/usuarios/me/foto
                @Size(max = 500) @Pattern(regexp = "^(https?://\\S+|/uploads/[\\w./-]+)$", message = "URL de foto inválida") String fotoPerfil) {
}