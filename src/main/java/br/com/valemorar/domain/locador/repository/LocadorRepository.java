package br.com.valemorar.domain.locador.repository;

import br.com.valemorar.domain.locador.Locador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface LocadorRepository extends JpaRepository<Locador, UUID> {
    Optional<Locador> findByDocumento(String documento);

    boolean existsByDocumento(String documento);
}
