package br.com.valemorar.domain.notificacao.service;

import br.com.valemorar.domain.notificacao.Notificacao;
import br.com.valemorar.domain.notificacao.dto.NotificacaoCreateDTO;
import br.com.valemorar.domain.notificacao.dto.NotificacaoResponseDTO;
import br.com.valemorar.domain.notificacao.repository.NotificacaoRepository;
import br.com.valemorar.domain.usuario.Usuario;
import br.com.valemorar.domain.usuario.enums.PerfilEnum;
import br.com.valemorar.infra.SecurityUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class NotificacaoService {

    private final NotificacaoRepository repository;

    public NotificacaoService(NotificacaoRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public NotificacaoResponseDTO criar(NotificacaoCreateDTO dto) {
        Notificacao entity = new Notificacao();
        entity.setUsuarioId(dto.usuarioId());
        entity.setAnuncioId(dto.anuncioId());
        entity.setMensagem(dto.mensagem());
        entity.setTipo(dto.tipo());
        entity.setLida(false);
        entity.setCriadoEm(LocalDateTime.now());

        Notificacao salvo = repository.save(entity);
        return NotificacaoResponseDTO.fromEntity(salvo);
    }

    @Transactional(readOnly = true)
    public Page<NotificacaoResponseDTO> listarTodos(Pageable pageable) {
        return repository.findAll(pageable)
                .map(NotificacaoResponseDTO::fromEntity);
    }

    @Transactional(readOnly = true)
    public NotificacaoResponseDTO buscarPorId(UUID id) {
        Notificacao entity = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Notificação não encontrada"));
        validarAcessoUsuario(entity.getUsuarioId());
        return NotificacaoResponseDTO.fromEntity(entity);
    }

    @Transactional(readOnly = true)
    public List<NotificacaoResponseDTO> buscarPorUsuario(UUID usuarioId) {
        validarAcessoUsuario(usuarioId);
        return repository.findByUsuarioIdOrderByCriadoEmDesc(usuarioId)
                .stream()
                .map(NotificacaoResponseDTO::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<NotificacaoResponseDTO> buscarNaoLidasPorUsuario(UUID usuarioId) {
        validarAcessoUsuario(usuarioId);
        return repository.findByUsuarioIdAndLidaFalseOrderByCriadoEmDesc(usuarioId)
                .stream()
                .map(NotificacaoResponseDTO::fromEntity)
                .toList();
    }

    @Transactional
    public NotificacaoResponseDTO marcarComoLida(UUID id) {
        Notificacao entity = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Notificação não encontrada"));
        validarAcessoUsuario(entity.getUsuarioId());

        entity.setLida(true);
        Notificacao atualizado = repository.save(entity);
        return NotificacaoResponseDTO.fromEntity(atualizado);
    }

    @Transactional
    public void marcarTodasComoLidasPorUsuario(UUID usuarioId) {
        validarAcessoUsuario(usuarioId);
        repository.marcarTodasComoLidasPorUsuario(usuarioId);
    }

    @Transactional
    public void deletar(UUID id) {
        Notificacao entity = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Notificação não encontrada"));
        validarAcessoUsuario(entity.getUsuarioId());
        repository.delete(entity);
    }

    private UUID getUsuarioAutenticadoId() {
        return SecurityUtils.getUsuarioAutenticado().getId();
    }

    private boolean isAdmin() {
        Usuario usuario = SecurityUtils.getUsuarioAutenticado();
        return PerfilEnum.ROLE_ADMIN.equals(usuario.getPerfil());
    }

    private void validarAcessoUsuario(UUID usuarioId) {
        if (!isAdmin() && !getUsuarioAutenticadoId().equals(usuarioId)) {
            throw new AccessDeniedException("Você não tem permissão para acessar notificações de outro usuário");
        }
    }
}