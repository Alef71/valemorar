package br.com.valemorar.domain.caracteristica.controller;

import br.com.valemorar.domain.caracteristica.dto.CaracteristicaCreateDTO;
import br.com.valemorar.domain.caracteristica.dto.CaracteristicaResponseDTO;
import br.com.valemorar.domain.caracteristica.service.CaracteristicaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/caracteristicas")
@Tag(name = "Características", description = "Endpoints para gerenciamento das características dos imóveis")
public class CaracteristicaController {

    private final CaracteristicaService service;

    public CaracteristicaController(CaracteristicaService service) {
        this.service = service;
    }

    @Operation(summary = "Criar uma característica", description = "Cadastra uma nova característica de imóvel no sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Característica criada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou nome já cadastrado")
    })
    @PostMapping
    public ResponseEntity<CaracteristicaResponseDTO> criar(@RequestBody @Valid CaracteristicaCreateDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(dto));
    }

    @Operation(summary = "Listar todas as características", description = "Retorna uma página com todas as características cadastradas")
    @ApiResponse(responseCode = "200", description = "Página retornada com sucesso")
    @GetMapping
    public ResponseEntity<Page<CaracteristicaResponseDTO>> listarTodos(@PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(service.listarTodos(pageable));
    }

    @Operation(summary = "Buscar característica por ID", description = "Busca os detalhes de uma característica específica pelo seu UUID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Característica encontrada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Característica não encontrada")
    })
    @GetMapping("/{id}")
    public ResponseEntity<CaracteristicaResponseDTO> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @Operation(summary = "Buscar características por categoria", description = "Retorna a lista de características vinculadas a uma categoria específica, ordenadas pelo campo de ordem")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de características da categoria retornada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Categoria não encontrada ou sem características")
    })
    @GetMapping("/categoria/{categoriaId}")
    public ResponseEntity<List<CaracteristicaResponseDTO>> buscarPorCategoria(@PathVariable UUID categoriaId) {
        return ResponseEntity.ok(service.buscarPorCategoria(categoriaId));
    }

    @Operation(summary = "Atualizar característica", description = "Atualiza os dados de uma característica existente pelo seu UUID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Característica atualizada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou característica não encontrada")
    })
    @PutMapping("/{id}")
    public ResponseEntity<CaracteristicaResponseDTO> atualizar(@PathVariable UUID id,
            @RequestBody @Valid CaracteristicaCreateDTO dto) {
        return ResponseEntity.ok(service.atualizar(id, dto));
    }

    @Operation(summary = "Deletar característica", description = "Remove uma característica do sistema pelo seu UUID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Característica deletada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Característica não encontrada")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable UUID id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}