package br.com.valemorar.domain.categoria_caracteristica;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.UUID;

@Entity
@Table(name = "CATEGORIA_CARACTERISTICA")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CategoriaCaracteristica {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String nome;

    private String icone;
    private Integer ordem;
}
