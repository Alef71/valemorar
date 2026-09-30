package br.com.valemorar.domain.imovel;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "IMOVEL")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Imovel {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "locador_id", nullable = false)
    private UUID locadorId;

    @Column(name = "endereco_id", nullable = false)
    private UUID enderecoId;

    @Column(nullable = false)
    private String titulo;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    @Column(name = "tipo_imovel", nullable = false)
    private String tipoImovel;

    @Column(name = "area_total")
    private Double areaTotal;

    @Column(name = "area_construida")
    private Double areaConstruida;

    private Integer quartos;
    private Integer suites;
    private Integer banheiros;

    @Column(name = "vagas_garagem")
    private Integer vagasGaragem;

    private Integer andar;
    private Boolean mobiliado;

    @Column(name = "aceita_pet")
    private Boolean aceitaPet;

    @Column(name = "valor_condominio")
    private BigDecimal valorCondominio;

    @Column(name = "valor_iptu")
    private BigDecimal valorIptu;

    @Column(name = "criado_em")
    private LocalDateTime criadoEm;

    @Column(name = "atualizado_em")
    private LocalDateTime atualizadoEm;
}