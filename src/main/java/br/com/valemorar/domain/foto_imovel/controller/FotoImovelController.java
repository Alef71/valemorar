package br.com.valemorar.domain.foto_imovel.controller;

import br.com.valemorar.domain.foto_imovel.dto.FotoImovelCreateDTO;
import br.com.valemorar.domain.foto_imovel.dto.FotoImovelResponseDTO;
import br.com.valemorar.domain.foto_imovel.service.FotoImovelService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/fotos-imovel")
public class FotoImovelController {

    private final FotoImovelService service;

    public FotoImovelController(FotoImovelService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<FotoImovelResponseDTO> criar(@RequestBody FotoImovelCreateDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(dto));
    }

    @GetMapping
    public ResponseEntity<List<FotoImovelResponseDTO>> listarTodos() {
        return ResponseEntity.ok(service.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<FotoImovelResponseDTO> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @GetMapping("/imovel/{imovelId}")
    public ResponseEntity<List<FotoImovelResponseDTO>> buscarPorImovel(@PathVariable UUID imovelId) {
        return ResponseEntity.ok(service.buscarPorImovel(imovelId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<FotoImovelResponseDTO> atualizar(@PathVariable UUID id,
            @RequestBody FotoImovelCreateDTO dto) {
        return ResponseEntity.ok(service.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable UUID id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
