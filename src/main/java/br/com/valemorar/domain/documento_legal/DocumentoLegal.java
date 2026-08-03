package br.com.valemorar.domain.documento_legal;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "DOCUMENTO_LEGAL")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DocumentoLegal {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String tipo;
    private String versao;

    @Column(columnDefinition = "TEXT")
    private String conteudo;

    @Column(name = "publicado_em")
    private LocalDateTime publicadoEm;
}
