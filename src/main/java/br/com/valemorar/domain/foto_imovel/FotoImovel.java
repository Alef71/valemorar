package br.com.valemorar.domain.foto_imovel;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "FOTO_IMOVEL")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FotoImovel {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "imovel_id")
    private UUID imovelId;

    private String url;
    private Boolean capa;
    private Integer ordem;

    @Column(name = "criado_em")
    private LocalDateTime criadoEm;
}