package br.com.valemorar.domain.usuario;

import br.com.valemorar.domain.usuario.enums.PerfilEnum;
import br.com.valemorar.domain.usuario.enums.StatusUsuarioEnum;
import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "USUARIO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "senha_hash", nullable = false)
    private String senhaHash;

    @Column(name = "foto_perfil")
    private String fotoPerfil;

    @Column(name = "email_verificado_em")
    private LocalDateTime emailVerificadoEm;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusUsuarioEnum status = StatusUsuarioEnum.ATIVO;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PerfilEnum perfil = PerfilEnum.ROLE_USER;

    @Column(name = "criado_em")
    private LocalDateTime criadoEm;

    @Column(name = "atualizado_em")
    private LocalDateTime atualizadoEm;
}