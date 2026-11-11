package br.com.valemorar.domain.auth.service;

import br.com.valemorar.domain.auth.dto.*;
import br.com.valemorar.domain.auth.entity.TokenRecuperacao;
import br.com.valemorar.domain.auth.repository.TokenRecuperacaoRepository;
import br.com.valemorar.domain.usuario.Usuario;
import br.com.valemorar.domain.usuario.enums.PerfilEnum;
import br.com.valemorar.domain.usuario.enums.StatusUsuarioEnum;
import br.com.valemorar.domain.usuario.repository.UsuarioRepository;
import br.com.valemorar.infra.EmailService;
import br.com.valemorar.infra.JwtTokenProvider;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final TokenRecuperacaoRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final EmailService emailService;

    public AuthService(
            UsuarioRepository usuarioRepository,
            TokenRecuperacaoRepository tokenRepository,
            PasswordEncoder passwordEncoder,
            JwtTokenProvider jwtTokenProvider,
            EmailService emailService) {
        this.usuarioRepository = usuarioRepository;
        this.tokenRepository = tokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.emailService = emailService;
    }

    @Transactional(readOnly = true)
    public AuthResponseDTO login(LoginRequestDTO dto) {
        Usuario usuario = usuarioRepository.findByEmail(dto.email())
                .orElseThrow(() -> new IllegalArgumentException("E-mail ou senha inválidos"));

        if (!passwordEncoder.matches(dto.senha(), usuario.getSenhaHash())) {
            throw new IllegalArgumentException("E-mail ou senha inválidos");
        }

        if (usuario.getStatus() == StatusUsuarioEnum.INATIVO || usuario.getStatus() == StatusUsuarioEnum.BLOQUEADO) {
            throw new IllegalStateException("Conta desativada ou bloqueada");
        }

        String tokenJwt = jwtTokenProvider.gerarToken(usuario);
        return new AuthResponseDTO(
                tokenJwt,
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getStatus(),
                usuario.getPerfil());
    }

    @Transactional
    public AuthResponseDTO loginGoogle(GoogleLoginDTO dto) {
        String emailGoogle = jwtTokenProvider.extrairEmailGoogle(dto.idToken());
        String nomeGoogle = jwtTokenProvider.extrairNomeGoogle(dto.idToken());

        Usuario usuario = usuarioRepository.findByEmail(emailGoogle)
                .orElseGet(() -> {
                    Usuario novoUsuario = new Usuario();
                    novoUsuario.setEmail(emailGoogle);
                    novoUsuario.setNome(nomeGoogle);
                    novoUsuario.setSenhaHash(passwordEncoder.encode(UUID.randomUUID().toString()));
                    novoUsuario.setStatus(StatusUsuarioEnum.ATIVO);
                    novoUsuario.setPerfil(PerfilEnum.ROLE_USER);
                    novoUsuario.setCriadoEm(LocalDateTime.now());
                    novoUsuario.setAtualizadoEm(LocalDateTime.now());
                    return usuarioRepository.save(novoUsuario);
                });

        String tokenJwt = jwtTokenProvider.gerarToken(usuario);
        return new AuthResponseDTO(
                tokenJwt,
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getStatus(),
                usuario.getPerfil());
    }

    @Transactional
    public void solicitarRecuperacaoSenha(EsqueciSenhaDTO dto) {
        Usuario usuario = usuarioRepository.findByEmail(dto.email())
                .orElseThrow(() -> new IllegalArgumentException("E-mail não encontrado no sistema"));

        String tokenUnico = UUID.randomUUID().toString();
        TokenRecuperacao token = new TokenRecuperacao(tokenUnico, usuario, LocalDateTime.now().plusHours(1));
        tokenRepository.save(token);

        emailService.enviarEmailRecuperacaoSenha(usuario.getEmail(), tokenUnico);
    }

    @Transactional
    public void redefinirSenha(RedefinirSenhaDTO dto) {
        TokenRecuperacao token = tokenRepository.findByToken(dto.token())
                .orElseThrow(() -> new IllegalArgumentException("Token de recuperação inválido"));

        if (token.isUsado() || token.isExpirado()) {
            throw new IllegalArgumentException("Token de recuperação expirado ou já utilizado");
        }

        Usuario usuario = token.getUsuario();
        usuario.setSenhaHash(passwordEncoder.encode(dto.novaSenha()));
        usuario.setAtualizadoEm(LocalDateTime.now());
        usuarioRepository.save(usuario);

        token.setUsado(true);
        tokenRepository.save(token);
    }
}