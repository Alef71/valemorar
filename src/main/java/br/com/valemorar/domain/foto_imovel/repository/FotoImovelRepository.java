package br.com.valemorar.domain.foto_imovel.repository;

import br.com.valemorar.domain.foto_imovel.FotoImovel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FotoImovelRepository extends JpaRepository<FotoImovel, UUID> {

    List<FotoImovel> findByImovelIdOrderByOrdemAsc(UUID imovelId);

    List<FotoImovel> findByImovelIdInOrderByOrdemAsc(Collection<UUID> imovelIds);

    void deleteByImovelId(UUID imovelId);

    Optional<FotoImovel> findByImovelIdAndCapaTrue(UUID imovelId);

    List<FotoImovel> findByHashIsNull();

    /** Alguma dessas imagens já aparece em imóvel de outro dono? */
    @Query("SELECT COUNT(f) > 0 FROM FotoImovel f, Imovel i WHERE i.id = f.imovelId " +
            "AND f.hash IN :hashes AND i.locadorId <> :locadorId")
    boolean existsHashDeOutroDono(@Param("hashes") Collection<String> hashes, @Param("locadorId") UUID locadorId);
}