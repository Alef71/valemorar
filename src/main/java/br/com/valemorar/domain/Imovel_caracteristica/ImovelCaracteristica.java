package br.com.valemorar.domain.Imovel_caracteristica;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.UUID;

@Entity
@Table(name = "IMOVEL_CARACTERISTICA")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ImovelCaracteristica {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "imovel_id")
    private UUID imovelId;

    @Column(name = "caracteristica_id")
    private UUID caracteristicaId;

    private String valor;
}