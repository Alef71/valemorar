package br.com.valemorar.domain.locador;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "LOCADOR")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Locador {

    @Id
    @Column(name = "usuario_id")
    private UUID usuarioId;

    private String telefone;
    private String whatsapp;
    private String documento;

    @Column(name = "criado_em")
    private LocalDateTime criadoEm;
}