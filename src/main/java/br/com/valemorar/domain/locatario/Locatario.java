package br.com.valemorar.domain.locatario;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "LOCATARIO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Locatario {

    @Id
    @Column(name = "usuario_id")
    private UUID usuarioId;

    @Column(name = "criado_em")
    private LocalDateTime criadoEm;
}
