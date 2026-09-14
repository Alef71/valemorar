package br.com.valemorar.domain.usuario.service;

import br.com.valemorar.domain.usuario.Usuario;
import br.com.valemorar.domain.usuario.dto.UsuarioCreateDTO;
import br.com.valemorar.domain.usuario.dto.UsuarioResponseDTO;
import br.com.valemorar.domain.usuario.dto.UsuarioUpdateDTO;
import br.com.valemorar.domain.usuario.enums.PerfilEnum;
import br.com.valemorar.domain.usuario.enums.StatusUsuarioEnum;
import br.com.valemorar.domain.usuario.repository.UsuarioRepository;
import br.com.valemorar.infra.SecurityUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UsuarioResponseDTO criar(UsuarioCreateDTO dto) {
        if (usuarioRepository.existsByEmail(dto.email())) {
            throw new RuntimeException("E-mail já cadastrado");
        }

        Usuario usuario = new Usuario();
        usuario.setNome(dto.nome());
        usuario.setEmail(dto.email());
        usuario.setSenhaHash(passwordEncoder.encode(dto.senha()));
        usuario.setFotoPerfil(dto.fotoPerfil());
        usuario.setStatus(StatusUsuarioEnum.ATIVO);
        usuario.setPerfil(PerfilEnum.ROLE_USER);
        usuario.setCriadoEm(LocalDateTime.now());
        usuario.setAtualizadoEm(LocalDateTime.now());

        Usuario salvo = usuarioRepository.save(usuario);
        return UsuarioResponseDTO.fromEntity(salvo);
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> listarTodos() {
        return usuarioRepository.findAll()
                .stream()
                .map(UsuarioResponseDTO::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public UsuarioResponseDTO buscarPorId(UUID id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        return UsuarioResponseDTO.fromEntity(usuario);
    }

    @Transactional(readOnly = true)
    public UsuarioResponseDTO buscarPerfilProprio() {
        Usuario usuarioLogado = SecurityUtils.getUsuarioAutenticado();
        return UsuarioResponseDTO.fromEntity(usuarioLogado);
    }

    @Transactional
    public UsuarioResponseDTO atualizarPerfilProprio(UsuarioUpdateDTO dto) {
        Usuario usuarioLogado = usuarioRepository.findById(SecurityUtils.getUsuarioAutenticado().getId())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        usuarioLogado.setNome(dto.nome());
        usuarioLogado.setFotoPerfil(dto.fotoPerfil());
        usuarioLogado.setAtualizadoEm(LocalDateTime.now());

        Usuario atualizado = usuarioRepository.save(usuarioLogado);
        return UsuarioResponseDTO.fromEntity(atualizado);
    }

    @Transactional
    public UsuarioResponseDTO atualizar(UUID id, UsuarioUpdateDTO dto) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        usuario.setNome(dto.nome());
        usuario.setFotoPerfil(dto.fotoPerfil());
        usuario.setAtualizadoEm(LocalDateTime.now());

        Usuario atualizado = usuarioRepository.save(usuario);
        return UsuarioResponseDTO.fromEntity(atualizado);
    }

    @Transactional
    public UsuarioResponseDTO desativarMinhaConta() {
        Usuario usuarioLogado = usuarioRepository.findById(SecurityUtils.getUsuarioAutenticado().getId())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        usuarioLogado.setStatus(StatusUsuarioEnum.INATIVO);
        usuarioLogado.setAtualizadoEm(LocalDateTime.now());

        Usuario atualizado = usuarioRepository.save(usuarioLogado);
        return UsuarioResponseDTO.fromEntity(atualizado);
    }

    @Transactional
    public UsuarioResponseDTO desativarConta(UUID id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        usuario.setStatus(StatusUsuarioEnum.INATIVO);
        usuario.setAtualizadoEm(LocalDateTime.now());

        Usuario atualizado = usuarioRepository.save(usuario);
        return UsuarioResponseDTO.fromEntity(atualizado);
    }

    @Transactional
    public UsuarioResponseDTO bloquearUsuario(UUID id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        usuario.setStatus(StatusUsuarioEnum.BLOQUEADO);
        usuario.setAtualizadoEm(LocalDateTime.now());

        Usuario atualizado = usuarioRepository.save(usuario);
        return UsuarioResponseDTO.fromEntity(atualizado);
    }

    @Transactional
    public void deletar(UUID id) {
        if (!usuarioRepository.existsById(id)) {
            throw new RuntimeException("Usuário não encontrado");
        }
        usuarioRepository.deleteById(id);
    }
}