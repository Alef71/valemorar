package br.com.valemorar.domain.imovel.controller;

import br.com.valemorar.domain.imovel.dto.ImovelCreateDTO;
import br.com.valemorar.domain.imovel.dto.ImovelResponseDTO;
import br.com.valemorar.domain.imovel.service.ImovelService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/imoveis")
public class ImovelController {

    private final ImovelService imovelService;

    public ImovelController(ImovelService imovelService) {
        this.imovelService = imovelService;
    }

    @PostMapping
    public ResponseEntity<ImovelResponseDTO> criar(@RequestBody ImovelCreateDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(imovelService.criar(dto));
    }

    @GetMapping
    public ResponseEntity<List<ImovelResponseDTO>> listarTodos() {
        return ResponseEntity.ok(imovelService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ImovelResponseDTO> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(imovelService.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ImovelResponseDTO> atualizar(@PathVariable UUID id, @RequestBody ImovelCreateDTO dto) {
        return ResponseEntity.ok(imovelService.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable UUID id) {
        imovelService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}