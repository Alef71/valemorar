package br.com.valemorar.domain.aceite_documento.controller;

import br.com.valemorar.domain.aceite_documento.dto.AceiteDocumentoCreateDTO;
import br.com.valemorar.domain.aceite_documento.dto.AceiteDocumentoResponseDTO;
import br.com.valemorar.domain.aceite_documento.service.AceiteDocumentoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/aceites-documento")
public class AceiteDocumentoController {

    private final AceiteDocumentoService service;

    public AceiteDocumentoController(AceiteDocumentoService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<AceiteDocumentoResponseDTO> registrarAceite(@RequestBody AceiteDocumentoCreateDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.registrarAceite(dto));
    }

    @GetMapping
    public ResponseEntity<List<AceiteDocumentoResponseDTO>> listarTodos() {
        return ResponseEntity.ok(service.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AceiteDocumentoResponseDTO> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<AceiteDocumentoResponseDTO>> buscarPorUsuario(@PathVariable UUID usuarioId) {
        return ResponseEntity.ok(service.buscarPorUsuario(usuarioId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable UUID id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
