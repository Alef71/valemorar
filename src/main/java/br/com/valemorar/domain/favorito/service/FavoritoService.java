package br.com.valemorar.domain.favorito.service;

import br.com.valemorar.domain.favorito.Favorito;
import br.com.valemorar.domain.favorito.dto.FavoritoCreateDTO;
import br.com.valemorar.domain.favorito.dto.FavoritoResponseDTO;
import br.com.valemorar.domain.favorito.repository.FavoritoRepository;
import br.com.valemorar.domain.usuario.Usuario;
import br.com.valemorar.domain.usuario.enums.PerfilEnum;
import br.com.valemorar.infra.SecurityUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class FavoritoService {

    private final FavoritoRepository repository;

    public FavoritoService(FavoritoRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public FavoritoResponseDTO criar(FavoritoCreateDTO dto) {
        UUID usuarioAutenticadoId = getUsuarioAutenticadoId();
        if (dto.usuarioId() != null && !usuarioAutenticadoId.equals(dto.usuarioId()) && !isAdmin()) {
            throw new AccessDeniedException("Você não tem permissão para criar favoritos para outro usuário");
        }

        if (repository.existsByUsuarioIdAndAnuncioId(usuarioAutenticadoId, dto.anuncioId())) {
            throw new IllegalArgumentException("Anúncio já está nos favoritos deste usuário");
        }

        Favorito entity = new Favorito();
        entity.setUsuarioId(usuarioAutenticadoId);
        entity.setAnuncioId(dto.anuncioId());
        entity.setAdicionadoEm(LocalDateTime.now());

        Favorito salvo = repository.save(entity);
        return FavoritoResponseDTO.fromEntity(salvo);
    }

    @Transactional(readOnly = true)
    public Page<FavoritoResponseDTO> listarTodos(Pageable pageable) {
        return repository.findAll(pageable)
                .map(FavoritoResponseDTO::fromEntity);
    }

    @Transactional(readOnly = true)
    public FavoritoResponseDTO buscarPorId(UUID id) {
        Favorito entity = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Favorito não encontrado"));
        return FavoritoResponseDTO.fromEntity(entity);
    }

    @Transactional(readOnly = true)
    public Page<FavoritoResponseDTO> buscarPorUsuario(UUID usuarioId, Pageable pageable) {
        validarAcessoUsuario(usuarioId);
        return repository.findByUsuarioIdOrderByAdicionadoEmDesc(usuarioId, pageable)
                .map(FavoritoResponseDTO::fromEntity);
    }

    @Transactional(readOnly = true)
    public boolean isFavorito(UUID usuarioId, UUID anuncioId) {
        validarAcessoUsuario(usuarioId);
        return repository.existsByUsuarioIdAndAnuncioId(usuarioId, anuncioId);
    }

    @Transactional
    public void deletar(UUID id) {
        Favorito favorito = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Favorito não encontrado"));
        validarAcessoUsuario(favorito.getUsuarioId());
        repository.delete(favorito);
    }

    @Transactional
    public void deletarPorUsuarioEAnuncio(UUID usuarioId, UUID anuncioId) {
        validarAcessoUsuario(usuarioId);
        if (!repository.existsByUsuarioIdAndAnuncioId(usuarioId, anuncioId)) {
            throw new IllegalArgumentException("Favorito não encontrado para este usuário e anúncio");
        }
        repository.deleteByUsuarioIdAndAnuncioId(usuarioId, anuncioId);
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
            throw new AccessDeniedException("Você não tem permissão para acessar favoritos de outro usuário");
        }
    }
}