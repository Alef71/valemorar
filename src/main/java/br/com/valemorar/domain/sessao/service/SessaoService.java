package br.com.valemorar.domain.sessao.service;

import br.com.valemorar.domain.sessao.Sessao;
import br.com.valemorar.domain.sessao.dto.SessaoCreateDTO;
import br.com.valemorar.domain.sessao.dto.SessaoResponseDTO;
import br.com.valemorar.domain.sessao.repository.SessaoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class SessaoService {

    private final SessaoRepository repository;

    public SessaoService(SessaoRepository repository) {
        this.repository = repository;
    }

    public SessaoResponseDTO criar(SessaoCreateDTO dto) {
        Sessao entity = new Sessao();
        entity.setUsuarioId(dto.usuarioId());
        entity.setRefreshTokenHash(dto.refreshTokenHash());
        entity.setIp(dto.ip());
        entity.setUserAgent(dto.userAgent());
        entity.setCriadoEm(LocalDateTime.now());
        entity.setExpiraEm(dto.expiraEm());

        Sessao salvo = repository.save(entity);
        return SessaoResponseDTO.fromEntity(salvo);
    }

    public List<SessaoResponseDTO> listarTodos() {
        return repository.findAll()
                .stream()
                .map(SessaoResponseDTO::fromEntity)
                .toList();
    }

    public SessaoResponseDTO buscarPorId(UUID id) {
        Sessao entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sessão não encontrada"));
        return SessaoResponseDTO.fromEntity(entity);
    }

    public List<SessaoResponseDTO> buscarPorUsuario(UUID usuarioId) {
        return repository.findByUsuarioId(usuarioId)
                .stream()
                .map(SessaoResponseDTO::fromEntity)
                .toList();
    }

    public SessaoResponseDTO revogarSessao(UUID id) {
        Sessao entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sessão não encontrada"));

        entity.setRevogadoEm(LocalDateTime.now());
        Sessao atualizado = repository.save(entity);
        return SessaoResponseDTO.fromEntity(atualizado);
    }

    public void deletar(UUID id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Sessão não encontrada");
        }
        repository.deleteById(id);
    }
}
