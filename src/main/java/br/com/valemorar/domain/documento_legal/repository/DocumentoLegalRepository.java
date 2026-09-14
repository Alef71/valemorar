package br.com.valemorar.domain.documento_legal.repository;

import br.com.valemorar.domain.documento_legal.DocumentoLegal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DocumentoLegalRepository extends JpaRepository<DocumentoLegal, UUID> {

    List<DocumentoLegal> findAllByOrderByPublicadoEmDesc();

    List<DocumentoLegal> findByTipoOrderByPublicadoEmDesc(String tipo);

    Optional<DocumentoLegal> findByTipoAndVersao(String tipo, String versao);

    boolean existsByTipoAndVersao(String tipo, String versao);

    boolean existsByTipoAndVersaoAndIdNot(String tipo, String versao, UUID id);
}