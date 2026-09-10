package br.com.valemorar.domain.favorito.repository;

import br.com.valemorar.domain.favorito.Favorito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface FavoritoRepository extends JpaRepository<Favorito, UUID> {

    Page<Favorito> findByUsuarioIdOrderByAdicionadoEmDesc(UUID usuarioId, Pageable pageable);

    boolean existsByUsuarioIdAndAnuncioId(UUID usuarioId, UUID anuncioId);

    Optional<Favorito> findByUsuarioIdAndAnuncioId(UUID usuarioId, UUID anuncioId);

    void deleteByUsuarioIdAndAnuncioId(UUID usuarioId, UUID anuncioId);
}