package br.com.valemorar.domain.favorito.service;

import br.com.valemorar.domain.favorito.Favorito;
import br.com.valemorar.domain.favorito.dto.FavoritoCreateDTO;
import br.com.valemorar.domain.favorito.dto.FavoritoResponseDTO;
import br.com.valemorar.domain.favorito.repository.FavoritoRepository;
import br.com.valemorar.infra.SecurityUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
        UUID usuarioAutenticadoId = SecurityUtils.resolverDono(dto.usuarioId());

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
        SecurityUtils.exigirDonoOuAdmin(usuarioId);
        return repository.findByUsuarioIdOrderByAdicionadoEmDesc(usuarioId, pageable)
                .map(FavoritoResponseDTO::fromEntity);
    }

    @Transactional(readOnly = true)
    public boolean isFavorito(UUID usuarioId, UUID anuncioId) {
        SecurityUtils.exigirDonoOuAdmin(usuarioId);
        return repository.existsByUsuarioIdAndAnuncioId(usuarioId, anuncioId);
    }

    @Transactional
    public void deletar(UUID id) {
        Favorito favorito = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Favorito não encontrado"));
        SecurityUtils.exigirDonoOuAdmin(favorito.getUsuarioId());
        repository.delete(favorito);
    }

    @Transactional
    public void deletarPorUsuarioEAnuncio(UUID usuarioId, UUID anuncioId) {
        SecurityUtils.exigirDonoOuAdmin(usuarioId);
        if (!repository.existsByUsuarioIdAndAnuncioId(usuarioId, anuncioId)) {
            throw new IllegalArgumentException("Favorito não encontrado para este usuário e anúncio");
        }
        repository.deleteByUsuarioIdAndAnuncioId(usuarioId, anuncioId);
    }
}