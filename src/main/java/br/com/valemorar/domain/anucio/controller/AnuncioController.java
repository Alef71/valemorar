package br.com.valemorar.domain.anucio.controller;

import br.com.valemorar.domain.anucio.dto.AnuncioCreateDTO;
import br.com.valemorar.domain.anucio.dto.AnuncioResponseDTO;
import br.com.valemorar.domain.anucio.service.AnuncioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/anuncios")
public class AnuncioController {

    private final AnuncioService anuncioService;

    public AnuncioController(AnuncioService anuncioService) {
        this.anuncioService = anuncioService;
    }

    @PostMapping
    public ResponseEntity<AnuncioResponseDTO> criar(@RequestBody AnuncioCreateDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(anuncioService.criar(dto));
    }

    @GetMapping
    public ResponseEntity<List<AnuncioResponseDTO>> listarTodos() {
        return ResponseEntity.ok(anuncioService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AnuncioResponseDTO> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(anuncioService.buscarPorId(id));
    }

    @GetMapping("/anunciante/{anuncianteId}")
    public ResponseEntity<List<AnuncioResponseDTO>> buscarPorAnunciante(@PathVariable UUID anuncianteId) {
        return ResponseEntity.ok(anuncioService.buscarPorAnunciante(anuncianteId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AnuncioResponseDTO> atualizar(@PathVariable UUID id, @RequestBody AnuncioCreateDTO dto) {
        return ResponseEntity.ok(anuncioService.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable UUID id) {
        anuncioService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
