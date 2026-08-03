package br.com.valemorar.domain.imovel;

import java.time.LocalDateTime;
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
@Table(name = "IMOVEL")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Imovel {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "locador_id")
    private UUID locadorId;

    @Column(name = "endereco_id")
    private UUID enderecoId;

    private String titulo;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    @Column(name = "tipo_imovel")
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

    @Column(name = "criado_em")
    private LocalDateTime criadoEm;

    @Column(name = "atualizado_em")
    private LocalDateTime atualizadoEm;
}