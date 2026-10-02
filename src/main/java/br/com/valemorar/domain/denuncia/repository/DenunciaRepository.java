package br.com.valemorar.domain.denuncia.repository;

import br.com.valemorar.domain.denuncia.Denuncia;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface DenunciaRepository extends JpaRepository<Denuncia, UUID> {

    Page<Denuncia> findByDenuncianteId(UUID denuncianteId, Pageable pageable);

    Page<Denuncia> findByAnuncioId(UUID anuncioId, Pageable pageable);

    Page<Denuncia> findByStatus(String status, Pageable pageable);

    /** Denúncias não rejeitadas contra qualquer anúncio do anunciante. */
    @Query("SELECT COUNT(d) FROM Denuncia d, Anuncio a WHERE a.id = d.anuncioId " +
            "AND a.anuncianteId = :anuncianteId AND (d.status IS NULL OR d.status <> 'REJEITADA')")
    long contarContraAnunciante(@Param("anuncianteId") UUID anuncianteId);
}