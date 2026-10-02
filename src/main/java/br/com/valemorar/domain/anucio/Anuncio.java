package br.com.valemorar.domain.anucio;

import br.com.valemorar.domain.anucio.enums.StatusAnuncioEnum;
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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusAnuncioEnum status;

    @Column(name = "publicado_em")
    private LocalDateTime publicadoEm;

    @Column(name = "expira_em")
    private LocalDateTime expiraEm;

    @Column(name = "atualizado_em")
    private LocalDateTime atualizadoEm;

    /** Motivos da última reprovação na revisão automática (um por linha). */
    @Column(name = "motivo_revisao", columnDefinition = "TEXT")
    private String motivoRevisao;

    /** Mediana do preço/quarto dos comparáveis ÷ preço/quarto deste anúncio (> 1 = abaixo do mercado). */
    @Column(name = "indice_valor")
    private BigDecimal indiceValor;

    /** Quanto o anúncio está abaixo (positivo) ou acima (negativo) do preço de mercado estimado. */
    @Column(name = "economia_mercado")
    private BigDecimal economiaMercado;
}