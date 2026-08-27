package br.com.valemorar.domain.auth.repository;

import br.com.valemorar.domain.auth.entity.TokenRecuperacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TokenRecuperacaoRepository extends JpaRepository<TokenRecuperacao, UUID> {
    Optional<TokenRecuperacao> findByToken(String token);
}
