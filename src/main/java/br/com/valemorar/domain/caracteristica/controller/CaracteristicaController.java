package br.com.valemorar.domain.caracteristica.controller;

import br.com.valemorar.domain.caracteristica.dto.CaracteristicaCreateDTO;
import br.com.valemorar.domain.caracteristica.dto.CaracteristicaResponseDTO;
import br.com.valemorar.domain.caracteristica.service.CaracteristicaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/caracteristicas")
public class CaracteristicaController {

    private final CaracteristicaService service;

    public CaracteristicaController(CaracteristicaService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<CaracteristicaResponseDTO> criar(@RequestBody CaracteristicaCreateDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(dto));
    }

    @GetMapping
    public ResponseEntity<List<CaracteristicaResponseDTO>> listarTodos() {
        return ResponseEntity.ok(service.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CaracteristicaResponseDTO> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @GetMapping("/categoria/{categoriaId}")
    public ResponseEntity<List<CaracteristicaResponseDTO>> buscarPorCategoria(@PathVariable UUID categoriaId) {
        return ResponseEntity.ok(service.buscarPorCategoria(categoriaId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CaracteristicaResponseDTO> atualizar(@PathVariable UUID id,
            @RequestBody CaracteristicaCreateDTO dto) {
        return ResponseEntity.ok(service.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable UUID id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
