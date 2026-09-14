package br.com.valemorar.domain.categoria_caracteristica.repository;

import br.com.valemorar.domain.categoria_caracteristica.CategoriaCaracteristica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CategoriaCaracteristicaRepository extends JpaRepository<CategoriaCaracteristica, UUID> {

    List<CategoriaCaracteristica> findAllByOrderByOrdemAsc();

    boolean existsByNome(String nome);

    boolean existsByNomeAndIdNot(String nome, UUID id);
}