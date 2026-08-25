package br.com.valemorar.domain.categoria_caracteristica.controller;

import br.com.valemorar.domain.categoria_caracteristica.dto.CategoriaCaracteristicaCreateDTO;
import br.com.valemorar.domain.categoria_caracteristica.dto.CategoriaCaracteristicaResponseDTO;
import br.com.valemorar.domain.categoria_caracteristica.service.CategoriaCaracteristicaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/categorias-caracteristica")
public class CategoriaCaracteristicaController {

    private final CategoriaCaracteristicaService service;

    public CategoriaCaracteristicaController(CategoriaCaracteristicaService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<CategoriaCaracteristicaResponseDTO> criar(@RequestBody CategoriaCaracteristicaCreateDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(dto));
    }

    @GetMapping
    public ResponseEntity<List<CategoriaCaracteristicaResponseDTO>> listarTodos() {
        return ResponseEntity.ok(service.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoriaCaracteristicaResponseDTO> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoriaCaracteristicaResponseDTO> atualizar(@PathVariable UUID id,
            @RequestBody CategoriaCaracteristicaCreateDTO dto) {
        return ResponseEntity.ok(service.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable UUID id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}