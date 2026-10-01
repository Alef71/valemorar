package br.com.valemorar.domain.anucio.repository;

import br.com.valemorar.domain.anucio.Anuncio;
import br.com.valemorar.domain.anucio.enums.StatusAnuncioEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AnuncioRepository extends JpaRepository<Anuncio, UUID> {

    @Override
    @EntityGraph(attributePaths = { "tags", "imovel" })
    Optional<Anuncio> findById(UUID id);

    @Override
    @EntityGraph(attributePaths = { "tags", "imovel" })
    Page<Anuncio> findAll(Pageable pageable);

    @EntityGraph(attributePaths = { "tags", "imovel" })
    Page<Anuncio> findByAnuncianteId(UUID anuncianteId, Pageable pageable);

    boolean existsByImovelIdAndStatus(UUID imovelId, StatusAnuncioEnum status);

    @EntityGraph(attributePaths = { "tags", "imovel" })
    Page<Anuncio> findByStatus(StatusAnuncioEnum status, Pageable pageable);

    // CAST evita que o Postgres receba o parâmetro nulo como bytea
    String FILTROS_BUSCA = "a.status = br.com.valemorar.domain.anucio.enums.StatusAnuncioEnum.ATIVO AND " +
            "(:tipoImovel IS NULL OR UPPER(i.tipoImovel) = UPPER(CAST(:tipoImovel AS string))) AND " +
            "(:precoMin IS NULL OR a.valor >= :precoMin) AND " +
            "(:precoMax IS NULL OR a.valor <= :precoMax) AND " +
            "(:quartos IS NULL OR i.quartos >= :quartos) AND " +
            "(:cidade IS NULL OR EXISTS (SELECT 1 FROM Endereco e WHERE e.id = i.enderecoId " +
            "    AND LOWER(e.cidade) = LOWER(CAST(:cidade AS string)))) AND " +
            // Referência: trecho do bairro do imóvel OU de alguma tag (ex.: "centro", "ufvjm")
            "(:referencia IS NULL OR EXISTS (SELECT 1 FROM Endereco e2 WHERE e2.id = i.enderecoId " +
            "    AND LOWER(e2.bairro) LIKE CAST(:referencia AS string)) " +
            "  OR EXISTS (SELECT 1 FROM Anuncio a3 JOIN a3.tags t3 WHERE a3 = a AND LOWER(t3) LIKE CAST(:referencia AS string))) AND " +
            "(COALESCE(:tags, NULL) IS NULL OR EXISTS (SELECT 1 FROM Anuncio a2 JOIN a2.tags t WHERE a2 = a AND LOWER(t) IN :tags))";

    @EntityGraph(attributePaths = { "tags", "imovel" })
    @Query(value = "SELECT a FROM Anuncio a JOIN a.imovel i WHERE " + FILTROS_BUSCA,
            countQuery = "SELECT COUNT(a) FROM Anuncio a JOIN a.imovel i WHERE " + FILTROS_BUSCA)
    Page<Anuncio> buscarComFiltros(
            @Param("tipoImovel") String tipoImovel,
            @Param("precoMin") BigDecimal precoMin,
            @Param("precoMax") BigDecimal precoMax,
            @Param("quartos") Integer quartos,
            @Param("cidade") String cidade,
            @Param("referencia") String referencia,
            @Param("tags") List<String> tags,
            Pageable pageable);
}