package br.com.valemorar.domain.locatario.controller;

import br.com.valemorar.domain.locatario.dto.LocatarioCreateDTO;
import br.com.valemorar.domain.locatario.dto.LocatarioResponseDTO;
import br.com.valemorar.domain.locatario.service.LocatarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/locatarios")
public class LocatarioController {

    private final LocatarioService service;

    public LocatarioController(LocatarioService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<LocatarioResponseDTO> criar(@RequestBody LocatarioCreateDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(dto));
    }

    @GetMapping
    public ResponseEntity<List<LocatarioResponseDTO>> listarTodos() {
        return ResponseEntity.ok(service.listarTodos());
    }

    @GetMapping("/{usuarioId}")
    public ResponseEntity<LocatarioResponseDTO> buscarPorId(@PathVariable UUID usuarioId) {
        return ResponseEntity.ok(service.buscarPorId(usuarioId));
    }

    @DeleteMapping("/{usuarioId}")
    public ResponseEntity<Void> deletar(@PathVariable UUID usuarioId) {
        service.deletar(usuarioId);
        return ResponseEntity.noContent().build();
    }
}
