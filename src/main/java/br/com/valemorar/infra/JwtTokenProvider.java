package br.com.valemorar.infra;

import br.com.valemorar.domain.usuario.Usuario;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtTokenProvider {

    @Value("${jwt.secret:chaveSecretaMuitoSeguraParaOValemorarProjeto123456}")
    private String jwtSecret;

    @Value("${jwt.expiration:86400000}") // 24 horas
    private long jwtExpirationInMs;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    public String gerarToken(Usuario usuario) {
        Date agora = new Date();
        Date dataExpiracao = new Date(agora.getTime() + jwtExpirationInMs);

        return Jwts.builder()
                .subject(usuario.getId().toString())
                .claim("email", usuario.getEmail())
                .claim("nome", usuario.getNome())
                .issuedAt(agora)
                .expiration(dataExpiracao)
                .signWith(getSigningKey())
                .compact();
    }

    public boolean validarToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String extrairEmailGoogle(String idToken) {
        return "usuario.google@gmail.com";
    }

    public String extrairNomeGoogle(String idToken) {
        return "Usuário Google";
    }
}