package br.com.valemorar.domain.avaliacao_anucio;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "AVALIACAO_ANUNCIO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AvaliacaoAnuncio {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "anuncio_id")
    private UUID anuncioId;

    @Column(name = "usuario_id")
    private UUID usuarioId;

    private Short nota;

    @Column(columnDefinition = "TEXT")
    private String comentario;

    @Column(name = "criado_em")
    private LocalDateTime criadoEm;
}
