package br.com.valemorar.domain.sessao.service;

import br.com.valemorar.domain.sessao.Sessao;
import br.com.valemorar.domain.sessao.dto.SessaoCreateDTO;
import br.com.valemorar.domain.sessao.dto.SessaoResponseDTO;
import br.com.valemorar.domain.sessao.repository.SessaoRepository;
import br.com.valemorar.infra.SecurityUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class SessaoService {

    private final SessaoRepository repository;

    public SessaoService(SessaoRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public SessaoResponseDTO criar(SessaoCreateDTO dto) {
        UUID usuarioAutenticadoId = SecurityUtils.resolverDono(dto.usuarioId());

        Sessao entity = new Sessao();
        entity.setUsuarioId(usuarioAutenticadoId);
        entity.setRefreshTokenHash(dto.refreshTokenHash());
        entity.setIp(dto.ip());
        entity.setUserAgent(dto.userAgent());
        entity.setCriadoEm(LocalDateTime.now());
        entity.setExpiraEm(dto.expiraEm());

        Sessao salvo = repository.save(entity);
        return SessaoResponseDTO.fromEntity(salvo);
    }

    @Transactional(readOnly = true)
    public Page<SessaoResponseDTO> listarTodos(Pageable pageable) {
        SecurityUtils.exigirAdmin();
        return repository.findAll(pageable)
                .map(SessaoResponseDTO::fromEntity);
    }

    @Transactional(readOnly = true)
    public SessaoResponseDTO buscarPorId(UUID id) {
        Sessao entity = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Sessão não encontrada"));
        SecurityUtils.exigirDonoOuAdmin(entity.getUsuarioId());
        return SessaoResponseDTO.fromEntity(entity);
    }

    @Transactional(readOnly = true)
    public List<SessaoResponseDTO> buscarPorUsuario(UUID usuarioId) {
        SecurityUtils.exigirDonoOuAdmin(usuarioId);
        return repository.findByUsuarioIdOrderByCriadoEmDesc(usuarioId)
                .stream()
                .map(SessaoResponseDTO::fromEntity)
                .toList();
    }

    @Transactional
    public SessaoResponseDTO revogarSessao(UUID id) {
        Sessao entity = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Sessão não encontrada"));
        SecurityUtils.exigirDonoOuAdmin(entity.getUsuarioId());

        entity.setRevogadoEm(LocalDateTime.now());
        Sessao atualizado = repository.save(entity);
        return SessaoResponseDTO.fromEntity(atualizado);
    }

    @Transactional
    public void revogarTodasDoUsuario(UUID usuarioId) {
        SecurityUtils.exigirDonoOuAdmin(usuarioId);
        repository.revogarTodasDoUsuario(usuarioId);
    }

    @Transactional
    public void deletar(UUID id) {
        Sessao entity = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Sessão não encontrada"));
        SecurityUtils.exigirDonoOuAdmin(entity.getUsuarioId());
        repository.delete(entity);
    }
}