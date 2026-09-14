package br.com.valemorar.domain.aceite_documento.repository;

import br.com.valemorar.domain.aceite_documento.AceiteDocumento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AceiteDocumentoRepository extends JpaRepository<AceiteDocumento, UUID> {

    Page<AceiteDocumento> findByUsuarioId(UUID usuarioId, Pageable pageable);

    Page<AceiteDocumento> findByDocumentoId(UUID documentoId, Pageable pageable);
}