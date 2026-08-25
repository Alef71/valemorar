package br.com.valemorar.domain.avaliacao_anucio.controller;

import br.com.valemorar.domain.avaliacao_anucio.dto.AvaliacaoAnuncioCreateDTO;
import br.com.valemorar.domain.avaliacao_anucio.dto.AvaliacaoAnuncioResponseDTO;
import br.com.valemorar.domain.avaliacao_anucio.service.AvaliacaoAnuncioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/avaliacoes-anuncio")
public class AvaliacaoAnuncioController {

    private final AvaliacaoAnuncioService service;

    public AvaliacaoAnuncioController(AvaliacaoAnuncioService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<AvaliacaoAnuncioResponseDTO> criar(@RequestBody AvaliacaoAnuncioCreateDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(dto));
    }

    @GetMapping
    public ResponseEntity<List<AvaliacaoAnuncioResponseDTO>> listarTodos() {
        return ResponseEntity.ok(service.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AvaliacaoAnuncioResponseDTO> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @GetMapping("/anuncio/{anuncioId}")
    public ResponseEntity<List<AvaliacaoAnuncioResponseDTO>> buscarPorAnuncio(@PathVariable UUID anuncioId) {
        return ResponseEntity.ok(service.buscarPorAnuncio(anuncioId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AvaliacaoAnuncioResponseDTO> atualizar(@PathVariable UUID id,
            @RequestBody AvaliacaoAnuncioCreateDTO dto) {
        return ResponseEntity.ok(service.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable UUID id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
