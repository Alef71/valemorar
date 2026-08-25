package br.com.valemorar.domain.denucia.repository;

import br.com.valemorar.domain.denucia.Denuncia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DenunciaRepository extends JpaRepository<Denuncia, UUID> {
    List<Denuncia> findByDenuncianteId(UUID denuncianteId);

    List<Denuncia> findByAnuncioId(UUID anuncioId);

    List<Denuncia> findByStatus(String status);
}