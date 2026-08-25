package br.com.valemorar.domain.endereco.repository;

import br.com.valemorar.domain.endereco.Endereco;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface EnderecoRepository extends JpaRepository<Endereco, UUID> {
    List<Endereco> findByCidade(String cidade);

    List<Endereco> findByCep(String cep);
}