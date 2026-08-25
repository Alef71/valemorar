package br.com.valemorar.domain.foto_imovel.repository;

import br.com.valemorar.domain.foto_imovel.FotoImovel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface FotoImovelRepository extends JpaRepository<FotoImovel, UUID> {
    List<FotoImovel> findByImovelId(UUID imovelId);
}