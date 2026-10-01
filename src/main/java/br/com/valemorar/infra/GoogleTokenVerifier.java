package br.com.valemorar.infra;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

/**
 * Valida o ID token do Google Sign-In no endpoint oficial tokeninfo, que confere assinatura e expiração.
 * Aqui conferimos ainda emissor, audiência (nosso client ID) e e-mail verificado.
 */
@Component
public class GoogleTokenVerifier {

    private static final String TOKENINFO_URL = "https://oauth2.googleapis.com/tokeninfo?id_token={token}";

    private final RestClient restClient;
    private final String clientId;

    public GoogleTokenVerifier(@Value("${google.client-id:}") String clientId) {
        this.restClient = RestClient.create();
        this.clientId = clientId;
    }

    public GoogleUsuario verificar(String idToken) {
        if (clientId == null || clientId.isBlank()) {
            throw new IllegalStateException("Login com Google não está configurado (GOOGLE_CLIENT_ID)");
        }

        TokenInfo info;
        try {
            info = restClient.get().uri(TOKENINFO_URL, idToken).retrieve().body(TokenInfo.class);
        } catch (RestClientException e) {
            throw new IllegalArgumentException("Token do Google inválido ou expirado");
        }

        if (info == null
                || !clientId.equals(info.aud())
                || !("accounts.google.com".equals(info.iss()) || "https://accounts.google.com".equals(info.iss()))
                || !"true".equals(info.emailVerified())
                || info.email() == null) {
            throw new IllegalArgumentException("Token do Google inválido ou expirado");
        }

        return new GoogleUsuario(info.email().toLowerCase(), info.name() != null ? info.name() : info.email());
    }

    public record GoogleUsuario(String email, String nome) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    record TokenInfo(String aud, String iss, String email, @JsonProperty("email_verified") String emailVerified,
            String name) {
    }
}
