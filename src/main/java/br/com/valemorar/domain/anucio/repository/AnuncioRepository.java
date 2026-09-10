package br.com.valemorar.domain.anucio.repository;

import br.com.valemorar.domain.anucio.Anuncio;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Repository
public interface AnuncioRepository extends JpaRepository<Anuncio, UUID> {

        Page<Anuncio> findByAnuncianteId(UUID anuncianteId, Pageable pageable);

        boolean existsByImovelIdAndStatus(UUID imovelId, String status);

        @Query(value = "SELECT DISTINCT a FROM Anuncio a LEFT JOIN a.tags t WHERE " +
                        "(:cidade IS NULL OR LOWER(a.cidade) LIKE LOWER(CONCAT('%', :cidade, '%'))) AND " +
                        "(:precoMin IS NULL OR a.valor >= :precoMin) AND " +
                        "(:precoMax IS NULL OR a.valor <= :precoMax) AND " +
                        "(:quartos IS NULL OR a.quartos >= :quartos) AND " +
                        "(COALESCE(:tags, NULL) IS NULL OR t IN :tags)", countQuery = "SELECT COUNT(DISTINCT a) FROM Anuncio a LEFT JOIN a.tags t WHERE "
                                        +
                                        "(:cidade IS NULL OR LOWER(a.cidade) LIKE LOWER(CONCAT('%', :cidade, '%'))) AND "
                                        +
                                        "(:precoMin IS NULL OR a.valor >= :precoMin) AND " +
                                        "(:precoMax IS NULL OR a.valor <= :precoMax) AND " +
                                        "(:quartos IS NULL OR a.quartos >= :quartos) AND " +
                                        "(COALESCE(:tags, NULL) IS NULL OR t IN :tags)")
        Page<Anuncio> buscarComFiltros(
                        @Param("cidade") String cidade,
                        @Param("precoMin") BigDecimal precoMin,
                        @Param("precoMax") BigDecimal precoMax,
                        @Param("quartos") Integer quartos,
                        @Param("tags") List<String> tags,
                        Pageable pageable);
}