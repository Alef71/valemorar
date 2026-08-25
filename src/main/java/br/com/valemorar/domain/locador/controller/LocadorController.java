package br.com.valemorar.domain.locador.controller;

import br.com.valemorar.domain.locador.dto.LocadorCreateDTO;
import br.com.valemorar.domain.locador.dto.LocadorResponseDTO;
import br.com.valemorar.domain.locador.service.LocadorService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/locadores")
public class LocadorController {

    private final LocadorService service;

    public LocadorController(LocadorService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<LocadorResponseDTO> criar(@RequestBody LocadorCreateDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(dto));
    }

    @GetMapping
    public ResponseEntity<List<LocadorResponseDTO>> listarTodos() {
        return ResponseEntity.ok(service.listarTodos());
    }

    @GetMapping("/{usuarioId}")
    public ResponseEntity<LocadorResponseDTO> buscarPorId(@PathVariable UUID usuarioId) {
        return ResponseEntity.ok(service.buscarPorId(usuarioId));
    }

    @PutMapping("/{usuarioId}")
    public ResponseEntity<LocadorResponseDTO> atualizar(@PathVariable UUID usuarioId,
            @RequestBody LocadorCreateDTO dto) {
        return ResponseEntity.ok(service.atualizar(usuarioId, dto));
    }

    @DeleteMapping("/{usuarioId}")
    public ResponseEntity<Void> deletar(@PathVariable UUID usuarioId) {
        service.deletar(usuarioId);
        return ResponseEntity.noContent().build();
    }
}
