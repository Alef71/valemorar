package br.com.valemorar.domain.anucio;

import br.com.valemorar.domain.imovel.Imovel;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "imovel_id", nullable = false)
    private Imovel imovel;

    @Column(name = "anunciante_id", nullable = false)
    private UUID anuncianteId;

    @Column(nullable = false)
    private String modalidade; // Ex: ALUGUEL, VENDA

    @Column(nullable = false)
    private BigDecimal valor;

    @ElementCollection
    @CollectionTable(name = "anuncio_tags", joinColumns = @JoinColumn(name = "anuncio_id"))
    @Column(name = "tag")
    private List<String> tags;

    @Column(name = "nota_media")
    private BigDecimal notaMedia;

    @Column(name = "total_avaliacoes")
    private Integer totalAvaliacoes;

    @Column(nullable = false)
    private String status; // Ex: ATIVO, PAUSADO, FINALIZADO

    @Column(name = "publicado_em")
    private LocalDateTime publicadoEm;

    @Column(name = "expira_em")
    private LocalDateTime expiraEm;

    @Column(name = "atualizado_em")
    private LocalDateTime atualizadoEm;
}