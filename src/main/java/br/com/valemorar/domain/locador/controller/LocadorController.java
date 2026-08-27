package br.com.valemorar.domain.locador.controller;

import br.com.valemorar.domain.locador.dto.LocadorCreateDTO;
import br.com.valemorar.domain.locador.dto.LocadorResponseDTO;
import br.com.valemorar.domain.locador.service.LocadorService;
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
@RequestMapping("/api/locadores")
@Tag(name = "Locadores", description = "Endpoints para gerenciamento do perfil de locadores")
public class LocadorController {

    private final LocadorService service;

    public LocadorController(LocadorService service) {
        this.service = service;
    }

    @Operation(summary = "Criar perfil de locador", description = "Cadastra um novo perfil de locador no sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Locador criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos")
    })
    @PostMapping
    public ResponseEntity<LocadorResponseDTO> criar(@RequestBody @Valid LocadorCreateDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(dto));
    }

    @Operation(summary = "Listar todos os locadores", description = "Retorna uma lista com todos os locadores cadastrados")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    @GetMapping
    public ResponseEntity<List<LocadorResponseDTO>> listarTodos() {
        return ResponseEntity.ok(service.listarTodos());
    }

    @Operation(summary = "Buscar locador por ID do usuário", description = "Busca os detalhes do perfil de locador pelo UUID do usuário")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Locador encontrado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Locador não encontrado")
    })
    @GetMapping("/{usuarioId}")
    public ResponseEntity<LocadorResponseDTO> buscarPorId(@PathVariable UUID usuarioId) {
        return ResponseEntity.ok(service.buscarPorId(usuarioId));
    }

    @Operation(summary = "Atualizar perfil de locador", description = "Atualiza os dados do perfil de locador pelo UUID do usuário")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Locador atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou locador não encontrado")
    })
    @PutMapping("/{usuarioId}")
    public ResponseEntity<LocadorResponseDTO> atualizar(@PathVariable UUID usuarioId,
            @RequestBody @Valid LocadorCreateDTO dto) {
        return ResponseEntity.ok(service.atualizar(usuarioId, dto));
    }

    @Operation(summary = "Deletar perfil de locador", description = "Remove o perfil de locador do sistema pelo UUID do usuário")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Locador deletado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Locador não encontrado")
    })
    @DeleteMapping("/{usuarioId}")
    public ResponseEntity<Void> deletar(@PathVariable UUID usuarioId) {
        service.deletar(usuarioId);
        return ResponseEntity.noContent().build();
    }
}