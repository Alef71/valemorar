package br.com.valemorar.domain.categoria_caracteristica.controller;

import br.com.valemorar.domain.categoria_caracteristica.dto.CategoriaCaracteristicaCreateDTO;
import br.com.valemorar.domain.categoria_caracteristica.dto.CategoriaCaracteristicaResponseDTO;
import br.com.valemorar.domain.categoria_caracteristica.service.CategoriaCaracteristicaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/categorias-caracteristica")
@Tag(name = "Categorias de Características", description = "Endpoints para gerenciamento das categorias de características dos imóveis")
public class CategoriaCaracteristicaController {

    private final CategoriaCaracteristicaService service;

    public CategoriaCaracteristicaController(CategoriaCaracteristicaService service) {
        this.service = service;
    }

    @Operation(summary = "Criar categoria de característica", description = "Cadastra uma nova categoria para agrupamento de características")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Categoria criada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos ou nome já cadastrado")
    })
    @PostMapping
    public ResponseEntity<CategoriaCaracteristicaResponseDTO> criar(
            @RequestBody @Valid CategoriaCaracteristicaCreateDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(dto));
    }

    @Operation(summary = "Listar todas as categorias de características", description = "Retorna uma lista com todas as categorias de características ordenadas")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    @GetMapping
    public ResponseEntity<List<CategoriaCaracteristicaResponseDTO>> listarTodos() {
        return ResponseEntity.ok(service.listarTodos());
    }

    @Operation(summary = "Buscar categoria de característica por ID", description = "Busca os detalhes de uma categoria específica pelo seu UUID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Categoria encontrada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Categoria não encontrada")
    })
    @GetMapping("/{id}")
    public ResponseEntity<CategoriaCaracteristicaResponseDTO> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @Operation(summary = "Atualizar categoria de característica", description = "Atualiza os dados de uma categoria existente pelo seu UUID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Categoria atualizada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou categoria não encontrada")
    })
    @PutMapping("/{id}")
    public ResponseEntity<CategoriaCaracteristicaResponseDTO> atualizar(@PathVariable UUID id,
            @RequestBody @Valid CategoriaCaracteristicaCreateDTO dto) {
        return ResponseEntity.ok(service.atualizar(id, dto));
    }

    @Operation(summary = "Deletar categoria de característica", description = "Remove uma categoria de característica do sistema pelo seu UUID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Categoria deletada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Categoria não encontrada")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable UUID id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}