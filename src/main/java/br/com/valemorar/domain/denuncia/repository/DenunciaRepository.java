package br.com.valemorar.domain.denuncia.repository;

import br.com.valemorar.domain.denuncia.Denuncia;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface DenunciaRepository extends JpaRepository<Denuncia, UUID> {

    Page<Denuncia> findByDenuncianteId(UUID denuncianteId, Pageable pageable);

    Page<Denuncia> findByAnuncioId(UUID anuncioId, Pageable pageable);

    Page<Denuncia> findByStatus(String status, Pageable pageable);
}