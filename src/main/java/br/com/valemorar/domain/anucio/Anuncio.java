package br.com.valemorar.domain.anucio;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "ANUNCIO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Anuncio {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "imovel_id")
    private UUID imovelId;

    @Column(name = "anunciante_id")
    private UUID anuncianteId;

    private String cidade;

    private Integer quartos;

    @ElementCollection
    @CollectionTable(name = "anuncio_tags", joinColumns = @JoinColumn(name = "anuncio_id"))
    @Column(name = "tag")
    private List<String> tags;

    private BigDecimal valor;

    private String modalidade;

    @Column(name = "valor_condominio")
    private BigDecimal valorCondominio;

    @Column(name = "valor_iptu")
    private BigDecimal valorIptu;

    @Column(name = "nota_media")
    private BigDecimal notaMedia;

    @Column(name = "total_avaliacoes")
    private Integer totalAvaliacoes;

    private String status;

    @Column(name = "publicado_em")
    private LocalDateTime publicadoEm;

    @Column(name = "expira_em")
    private LocalDateTime expiraEm;

    @Column(name = "atualizado_em")
    private LocalDateTime atualizadoEm;
}