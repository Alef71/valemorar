package br.com.valemorar.infra;

import br.com.valemorar.domain.usuario.enums.StatusUsuarioEnum;
import br.com.valemorar.domain.usuario.repository.UsuarioRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Component
public class SecurityFilter extends OncePerRequestFilter {

    private final JwtTokenProvider tokenProvider;
    private final UsuarioRepository usuarioRepository;

    public SecurityFilter(JwtTokenProvider tokenProvider, UsuarioRepository usuarioRepository) {
        this.tokenProvider = tokenProvider;
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String token = recuperarToken(request);

        if (token != null && tokenProvider.validarToken(token)) {
            String idUsuario = tokenProvider.extrairSubject(token);
            var usuarioOptional = usuarioRepository.findById(UUID.fromString(idUsuario));

            if (usuarioOptional.isPresent()) {
                var usuario = usuarioOptional.get();

                if (!StatusUsuarioEnum.ATIVO.equals(usuario.getStatus())) {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    return;
                }

                // 2. Injeção das permissões/perfis (ROLE_USER, ROLE_ADMIN, etc.)
                var authorities = List.of(new SimpleGrantedAuthority(usuario.getPerfil().name()));
                var auth = new UsernamePasswordAuthenticationToken(usuario, null, authorities);
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }

        filterChain.doFilter(request, response);
    }

    private String recuperarToken(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.replace("Bearer ", "");
        }
        return null;
    }
}