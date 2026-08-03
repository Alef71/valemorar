package br.com.valemorar.domain.denucia;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "DENUNCIA")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Denuncia {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "denunciante_id")
    private UUID denuncianteId;

    @Column(name = "anuncio_id")
    private UUID anuncioId;

    private String motivo;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    private String status;

    @Column(name = "resolvido_por")
    private UUID resolvidoPor;

    @Column(name = "denunciado_em")
    private LocalDateTime denunciadoEm;

    @Column(name = "resolvido_em")
    private LocalDateTime resolvidoEm;
}