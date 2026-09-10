package br.com.valemorar.domain.locatario.repository;

import br.com.valemorar.domain.locatario.Locatario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface LocatarioRepository extends JpaRepository<Locatario, UUID> {
}