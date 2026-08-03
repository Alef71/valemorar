package br.com.valemorar.domain.caracteristica;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "CARACTERISTICA")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Caracteristica {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "categoria_id")
    private UUID categoriaId;

    @Column(nullable = false, unique = true)
    private String nome;

    @Column(name = "tipo_valor")
    private String tipoValor;

    private String unidade;

    @Column(name = "permite_multiplos")
    private Boolean permiteMultiplos;

    private Boolean obrigatoria;
    private Integer ordem;
}
