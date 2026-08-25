package br.com.valemorar.domain.sessao.repository;

import br.com.valemorar.domain.sessao.Sessao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SessaoRepository extends JpaRepository<Sessao, UUID> {
    List<Sessao> findByUsuarioId(UUID usuarioId);

    Optional<Sessao> findByRefreshTokenHash(String refreshTokenHash);
}
