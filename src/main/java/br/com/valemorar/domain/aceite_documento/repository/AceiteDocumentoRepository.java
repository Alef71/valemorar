package br.com.valemorar.domain.aceite_documento.repository;

import br.com.valemorar.domain.aceite_documento.AceiteDocumento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AceiteDocumentoRepository extends JpaRepository<AceiteDocumento, UUID> {
    List<AceiteDocumento> findByUsuarioId(UUID usuarioId);

    List<AceiteDocumento> findByDocumentoId(UUID documentoId);
}
