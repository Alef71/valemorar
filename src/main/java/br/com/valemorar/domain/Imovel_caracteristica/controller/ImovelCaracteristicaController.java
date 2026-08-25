package br.com.valemorar.domain.Imovel_caracteristica.controller;

import br.com.valemorar.domain.Imovel_caracteristica.dto.ImovelCaracteristicaCreateDTO;
import br.com.valemorar.domain.Imovel_caracteristica.dto.ImovelCaracteristicaResponseDTO;
import br.com.valemorar.domain.Imovel_caracteristica.service.ImovelCaracteristicaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/imovel-caracteristicas")
public class ImovelCaracteristicaController {

    private final ImovelCaracteristicaService service;

    public ImovelCaracteristicaController(ImovelCaracteristicaService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ImovelCaracteristicaResponseDTO> criar(@RequestBody ImovelCaracteristicaCreateDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(dto));
    }

    @GetMapping
    public ResponseEntity<List<ImovelCaracteristicaResponseDTO>> listarTodos() {
        return ResponseEntity.ok(service.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ImovelCaracteristicaResponseDTO> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @GetMapping("/imovel/{imovelId}")
    public ResponseEntity<List<ImovelCaracteristicaResponseDTO>> buscarPorImovelId(@PathVariable UUID imovelId) {
        return ResponseEntity.ok(service.buscarPorImovelId(imovelId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ImovelCaracteristicaResponseDTO> atualizar(@PathVariable UUID id,
            @RequestBody ImovelCaracteristicaCreateDTO dto) {
        return ResponseEntity.ok(service.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable UUID id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
