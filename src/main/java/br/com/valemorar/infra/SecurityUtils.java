package br.com.valemorar.infra;

import br.com.valemorar.domain.usuario.Usuario;
import br.com.valemorar.domain.usuario.enums.PerfilEnum;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;
import java.util.UUID;

public class SecurityUtils {

    private SecurityUtils() {
    }

    public static Usuario getUsuarioAutenticado() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()
                || !(authentication.getPrincipal() instanceof Usuario usuario)) {
            throw new IllegalStateException("Nenhum usuário autenticado encontrado no contexto de segurança.");
        }

        return usuario;
    }

    /** Usuário autenticado, se houver (para endpoints públicos que mudam de comportamento com login). */
    public static Optional<Usuario> getUsuarioAutenticadoOpcional() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()
                && authentication.getPrincipal() instanceof Usuario usuario) {
            return Optional.of(usuario);
        }
        return Optional.empty();
    }

    public static UUID getUsuarioAutenticadoId() {
        return getUsuarioAutenticado().getId();
    }

    public static boolean isAdmin() {
        return PerfilEnum.ROLE_ADMIN.equals(getUsuarioAutenticado().getPerfil());
    }

    public static void exigirAdmin() {
        if (!isAdmin()) {
            throw new AccessDeniedException("Apenas administradores podem acessar este recurso");
        }
    }

    /** Garante que o recurso pertence ao usuário autenticado (ou que ele é admin). */
    public static void exigirDonoOuAdmin(UUID donoId) {
        if (!isAdmin() && !getUsuarioAutenticadoId().equals(donoId)) {
            throw new AccessDeniedException("Você não tem permissão para acessar recursos de outro usuário");
        }
    }

    /**
     * Resolve o dono de um recurso a ser criado: o usuário autenticado, a menos que um admin
     * informe explicitamente outro usuário. Nunca confia no ID enviado por um usuário comum.
     */
    public static UUID resolverDono(UUID usuarioIdInformado) {
        UUID donoId = usuarioIdInformado != null ? usuarioIdInformado : getUsuarioAutenticadoId();
        exigirDonoOuAdmin(donoId);
        return donoId;
    }
}
