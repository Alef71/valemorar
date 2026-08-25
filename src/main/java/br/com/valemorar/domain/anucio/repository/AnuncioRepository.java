package br.com.valemorar.domain.anucio.repository;

import br.com.valemorar.domain.anucio.Anuncio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AnuncioRepository extends JpaRepository<Anuncio, UUID> {
    List<Anuncio> findByAnuncianteId(UUID anuncianteId);

    List<Anuncio> findByStatus(String status);
}
