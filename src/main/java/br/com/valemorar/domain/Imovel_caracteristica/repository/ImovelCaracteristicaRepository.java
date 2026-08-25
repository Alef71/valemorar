package br.com.valemorar.domain.Imovel_caracteristica.repository;

import br.com.valemorar.domain.Imovel_caracteristica.ImovelCaracteristica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ImovelCaracteristicaRepository extends JpaRepository<ImovelCaracteristica, UUID> {
    List<ImovelCaracteristica> findByImovelId(UUID imovelId);
}