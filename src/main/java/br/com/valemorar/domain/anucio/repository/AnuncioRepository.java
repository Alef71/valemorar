package br.com.valemorar.domain.anucio.repository;

import br.com.valemorar.domain.anucio.Anuncio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Repository
public interface AnuncioRepository extends JpaRepository<Anuncio, UUID> {

    List<Anuncio> findByAnuncianteId(UUID anuncianteId);

    @Query("SELECT a FROM Anuncio a WHERE " +
            "(:precoMin IS NULL OR a.valor >= :precoMin) AND " +
            "(:precoMax IS NULL OR a.valor <= :precoMax)")
    List<Anuncio> buscarComFiltros(
            @Param("precoMin") BigDecimal precoMin,
            @Param("precoMax") BigDecimal precoMax);
}