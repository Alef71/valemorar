package br.com.valemorar.domain.usuario.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UsuarioCreateDTO(
                @NotBlank(message = "O nome é obrigatório") @Size(min = 3, max = 100, message = "O nome deve ter entre 3 e 100 caracteres") String nome,

                @NotBlank(message = "O e-mail é obrigatório") @Email(message = "Formato de e-mail inválido") String email,

                @NotBlank(message = "A senha é obrigatória") @Size(min = 8, message = "A senha deve conter no mínimo 8 caracteres") String senha,

                String fotoPerfil) {
}