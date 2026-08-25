package br.com.valemorar.domain.categoria_caracteristica.repositiry;

import br.com.valemorar.domain.categoria_caracteristica.CategoriaCaracteristica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CategoriaCaracteristicaRepository extends JpaRepository<CategoriaCaracteristica, UUID> {
    boolean existsByNome(String nome);
}
