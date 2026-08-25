package br.com.valemorar.domain.notificacao.service;

import br.com.valemorar.domain.notificacao.Notificacao;
import br.com.valemorar.domain.notificacao.dto.NotificacaoCreateDTO;
import br.com.valemorar.domain.notificacao.dto.NotificacaoResponseDTO;
import br.com.valemorar.domain.notificacao.repository.NotificacaoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class NotificacaoService {

    private final NotificacaoRepository repository;

    public NotificacaoService(NotificacaoRepository repository) {
        this.repository = repository;
    }

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

    public List<NotificacaoResponseDTO> listarTodos() {
        return repository.findAll()
                .stream()
                .map(NotificacaoResponseDTO::fromEntity)
                .toList();
    }

    public NotificacaoResponseDTO buscarPorId(UUID id) {
        Notificacao entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notificação não encontrada"));
        return NotificacaoResponseDTO.fromEntity(entity);
    }

    public List<NotificacaoResponseDTO> buscarPorUsuario(UUID usuarioId) {
        return repository.findByUsuarioId(usuarioId)
                .stream()
                .map(NotificacaoResponseDTO::fromEntity)
                .toList();
    }

    public List<NotificacaoResponseDTO> buscarNaoLidasPorUsuario(UUID usuarioId) {
        return repository.findByUsuarioIdAndLidaFalse(usuarioId)
                .stream()
                .map(NotificacaoResponseDTO::fromEntity)
                .toList();
    }

    public NotificacaoResponseDTO marcarComoLida(UUID id) {
        Notificacao entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notificação não encontrada"));

        entity.setLida(true);
        Notificacao atualizado = repository.save(entity);
        return NotificacaoResponseDTO.fromEntity(atualizado);
    }

    public void deletar(UUID id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Notificação não encontrada");
        }
        repository.deleteById(id);
    }
}
