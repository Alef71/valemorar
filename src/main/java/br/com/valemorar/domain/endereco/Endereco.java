package br.com.valemorar.domain.endereco;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "ENDERECO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Endereco {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // Dono do endereço; nulo apenas em registros legados sem imóvel vinculado
    @Column(name = "usuario_id")
    private UUID usuarioId;

    // true = endereço pessoal do perfil; false = endereço de imóvel
    @Column(nullable = false)
    private boolean pessoal;

    @Column(nullable = false)
    private String logradouro;

    private String numero;
    private String complemento;
    private String bairro;

    @Column(nullable = false)
    private String cidade;

    @Column(nullable = false)
    private String estado;

    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(length = 8)
    private String cep;

    private Double latitude;
    private Double longitude;
}