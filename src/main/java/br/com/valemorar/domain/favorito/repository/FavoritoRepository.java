package br.com.valemorar.domain.favorito.repository;

import br.com.valemorar.domain.favorito.Favorito;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FavoritoRepository extends JpaRepository<Favorito, UUID> {
    List<Favorito> findByUsuarioId(UUID usuarioId);

    boolean existsByUsuarioIdAndAnuncioId(UUID usuarioId, UUID anuncioId);

    Optional<Favorito> findByUsuarioIdAndAnuncioId(UUID usuarioId, UUID anuncioId);
}
