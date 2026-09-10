package br.com.valemorar.domain.caracteristica.repository;

import br.com.valemorar.domain.caracteristica.Caracteristica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CaracteristicaRepository extends JpaRepository<Caracteristica, UUID> {

    List<Caracteristica> findByCategoriaIdOrderByOrdemAsc(UUID categoriaId);

    boolean existsByNome(String nome);

    boolean existsByNomeAndIdNot(String nome, UUID id);
}