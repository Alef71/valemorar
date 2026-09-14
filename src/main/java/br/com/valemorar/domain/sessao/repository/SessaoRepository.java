package br.com.valemorar.domain.sessao.repository;

import br.com.valemorar.domain.sessao.Sessao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SessaoRepository extends JpaRepository<Sessao, UUID> {

    List<Sessao> findByUsuarioIdOrderByCriadoEmDesc(UUID usuarioId);

    Optional<Sessao> findByRefreshTokenHash(String refreshTokenHash);

    @Modifying
    @Query("UPDATE Sessao s SET s.revogadoEm = CURRENT_TIMESTAMP WHERE s.usuarioId = :usuarioId AND s.revogadoEm IS NULL")
    void revogarTodasDoUsuario(UUID usuarioId);
}