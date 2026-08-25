package br.com.valemorar.domain.avaliacao_anucio.repository;

import br.com.valemorar.domain.avaliacao_anucio.AvaliacaoAnuncio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AvaliacaoAnuncioRepository extends JpaRepository<AvaliacaoAnuncio, UUID> {
    List<AvaliacaoAnuncio> findByAnuncioId(UUID anuncioId);

    List<AvaliacaoAnuncio> findByUsuarioId(UUID usuarioId);
}
