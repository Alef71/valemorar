package br.com.valemorar.domain.aceite_documento;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "ACEITE_DOCUMENTO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AceiteDocumento {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "usuario_id", nullable = false)
    private UUID usuarioId;

    @Column(name = "documento_id", nullable = false)
    private UUID documentoId;

    private String ip;

    @Column(name = "aceito_em")
    private LocalDateTime aceitoEm;
}
