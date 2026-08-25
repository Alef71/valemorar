package br.com.valemorar.domain.documento_legal.controller;

import br.com.valemorar.domain.documento_legal.dto.DocumentoLegalCreateDTO;
import br.com.valemorar.domain.documento_legal.dto.DocumentoLegalResponseDTO;
import br.com.valemorar.domain.documento_legal.service.DocumentoLegalService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/documentos-legais")
public class DocumentoLegalController {

    private final DocumentoLegalService service;

    public DocumentoLegalController(DocumentoLegalService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<DocumentoLegalResponseDTO> criar(@RequestBody DocumentoLegalCreateDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(dto));
    }

    @GetMapping
    public ResponseEntity<List<DocumentoLegalResponseDTO>> listarTodos() {
        return ResponseEntity.ok(service.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DocumentoLegalResponseDTO> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<List<DocumentoLegalResponseDTO>> buscarPorTipo(@PathVariable String tipo) {
        return ResponseEntity.ok(service.buscarPorTipo(tipo));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DocumentoLegalResponseDTO> atualizar(@PathVariable UUID id,
            @RequestBody DocumentoLegalCreateDTO dto) {
        return ResponseEntity.ok(service.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable UUID id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
