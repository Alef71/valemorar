package br.com.valemorar.domain.anucio.dto;

/** Contato do anunciante, exibido apenas para usuários autenticados. Nunca inclui documento (CPF/CNPJ). */
public record AnuncianteContatoDTO(
        String nome,
        String email,
        String fotoPerfil,
        String telefone,
        String whatsapp) {
}
