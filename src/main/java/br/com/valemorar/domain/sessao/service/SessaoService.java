package br.com.valemorar.domain.sessao.service;

import br.com.valemorar.domain.sessao.Sessao;
import br.com.valemorar.domain.sessao.dto.SessaoCreateDTO;
import br.com.valemorar.domain.sessao.dto.SessaoResponseDTO;
import br.com.valemorar.domain.sessao.repository.SessaoRepository;
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
public class SessaoService {

    private final SessaoRepository repository;

    public SessaoService(SessaoRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public SessaoResponseDTO criar(SessaoCreateDTO dto) {
        UUID usuarioAutenticadoId = getUsuarioAutenticadoId();
        if (dto.usuarioId() != null && !dto.usuarioId().equals(usuarioAutenticadoId) && !isAdmin()) {
            throw new AccessDeniedException("Você não tem permissão para criar sessão para outro usuário");
        }

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
        if (!isAdmin()) {
            throw new AccessDeniedException("Apenas administradores podem listar todas as sessões");
        }
        return repository.findAll(pageable)
                .map(SessaoResponseDTO::fromEntity);
    }

    @Transactional(readOnly = true)
    public SessaoResponseDTO buscarPorId(UUID id) {
        Sessao entity = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Sessão não encontrada"));
        validarAcessoUsuario(entity.getUsuarioId());
        return SessaoResponseDTO.fromEntity(entity);
    }

    @Transactional(readOnly = true)
    public List<SessaoResponseDTO> buscarPorUsuario(UUID usuarioId) {
        validarAcessoUsuario(usuarioId);
        return repository.findByUsuarioIdOrderByCriadoEmDesc(usuarioId)
                .stream()
                .map(SessaoResponseDTO::fromEntity)
                .toList();
    }

    @Transactional
    public SessaoResponseDTO revogarSessao(UUID id) {
        Sessao entity = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Sessão não encontrada"));
        validarAcessoUsuario(entity.getUsuarioId());

        entity.setRevogadoEm(LocalDateTime.now());
        Sessao atualizado = repository.save(entity);
        return SessaoResponseDTO.fromEntity(atualizado);
    }

    @Transactional
    public void revogarTodasDoUsuario(UUID usuarioId) {
        validarAcessoUsuario(usuarioId);
        repository.revogarTodasDoUsuario(usuarioId);
    }

    @Transactional
    public void deletar(UUID id) {
        Sessao entity = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Sessão não encontrada"));
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
            throw new AccessDeniedException("Você não tem permissão para acessar esta sessão");
        }
    }
}