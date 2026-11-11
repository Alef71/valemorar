package br.com.valemorar.domain.endereco.repository;

import br.com.valemorar.domain.endereco.Endereco;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface EnderecoRepository extends JpaRepository<Endereco, UUID> {

    Page<Endereco> findByCidadeIgnoreCase(String cidade, Pageable pageable);

    Page<Endereco> findByCep(String cep, Pageable pageable);

    Page<Endereco> findByEstadoIgnoreCase(String estado, Pageable pageable);
}