package br.com.valemorar.domain.notificacao.service;

import br.com.valemorar.domain.notificacao.Notificacao;
import br.com.valemorar.domain.notificacao.dto.NotificacaoCreateDTO;
import br.com.valemorar.domain.notificacao.dto.NotificacaoResponseDTO;
import br.com.valemorar.domain.notificacao.repository.NotificacaoRepository;
import br.com.valemorar.infra.SecurityUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
        SecurityUtils.exigirDonoOuAdmin(entity.getUsuarioId());
        return NotificacaoResponseDTO.fromEntity(entity);
    }

    @Transactional(readOnly = true)
    public List<NotificacaoResponseDTO> buscarPorUsuario(UUID usuarioId) {
        SecurityUtils.exigirDonoOuAdmin(usuarioId);
        return repository.findByUsuarioIdOrderByCriadoEmDesc(usuarioId)
                .stream()
                .map(NotificacaoResponseDTO::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<NotificacaoResponseDTO> buscarNaoLidasPorUsuario(UUID usuarioId) {
        SecurityUtils.exigirDonoOuAdmin(usuarioId);
        return repository.findByUsuarioIdAndLidaFalseOrderByCriadoEmDesc(usuarioId)
                .stream()
                .map(NotificacaoResponseDTO::fromEntity)
                .toList();
    }

    @Transactional
    public NotificacaoResponseDTO marcarComoLida(UUID id) {
        Notificacao entity = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Notificação não encontrada"));
        SecurityUtils.exigirDonoOuAdmin(entity.getUsuarioId());

        entity.setLida(true);
        Notificacao atualizado = repository.save(entity);
        return NotificacaoResponseDTO.fromEntity(atualizado);
    }

    @Transactional
    public void marcarTodasComoLidasPorUsuario(UUID usuarioId) {
        SecurityUtils.exigirDonoOuAdmin(usuarioId);
        repository.marcarTodasComoLidasPorUsuario(usuarioId);
    }

    @Transactional
    public void deletar(UUID id) {
        Notificacao entity = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Notificação não encontrada"));
        SecurityUtils.exigirDonoOuAdmin(entity.getUsuarioId());
        repository.delete(entity);
    }
}