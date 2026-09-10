package br.com.valemorar.domain.admin.service;

import br.com.valemorar.domain.notificacao.dto.NotificacaoCreateDTO;
import br.com.valemorar.domain.notificacao.dto.NotificacaoResponseDTO;
import br.com.valemorar.domain.notificacao.service.NotificacaoService;
import br.com.valemorar.domain.usuario.Usuario;
import br.com.valemorar.domain.usuario.dto.UsuarioResponseDTO;
import br.com.valemorar.domain.usuario.enums.StatusUsuarioEnum;
import br.com.valemorar.domain.usuario.repository.UsuarioRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class AdminService {

    private final UsuarioRepository usuarioRepository;
    private final NotificacaoService notificacaoService;

    public AdminService(UsuarioRepository usuarioRepository, NotificacaoService notificacaoService) {
        this.usuarioRepository = usuarioRepository;
        this.notificacaoService = notificacaoService;
    }

    @Transactional(readOnly = true)
    public Page<UsuarioResponseDTO> listarTodosUsuarios(Pageable pageable) {
        return usuarioRepository.findAll(pageable)
                .map(UsuarioResponseDTO::fromEntity);
    }

    @Transactional
    public UsuarioResponseDTO alterarStatusUsuario(UUID id, String novoStatus) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        try {
            usuario.setStatus(StatusUsuarioEnum.valueOf(novoStatus.toUpperCase()));
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Status inválido: " + novoStatus);
        }

        usuario.setAtualizadoEm(LocalDateTime.now());

        Usuario salvo = usuarioRepository.save(usuario);
        return UsuarioResponseDTO.fromEntity(salvo);
    }

    @Transactional
    public void deletarUsuario(UUID id) {
        if (!usuarioRepository.existsById(id)) {
            throw new RuntimeException("Usuário não encontrado");
        }
        usuarioRepository.deleteById(id);
    }

    @Transactional
    public NotificacaoResponseDTO enviarNotificacao(NotificacaoCreateDTO dto) {
        return notificacaoService.criar(dto);
    }
}