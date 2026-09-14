package br.com.valemorar.domain.locatario.controller;

import br.com.valemorar.domain.locatario.dto.LocatarioCreateDTO;
import br.com.valemorar.domain.locatario.dto.LocatarioResponseDTO;
import br.com.valemorar.domain.locatario.service.LocatarioService;
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

import java.util.UUID;

@RestController
@RequestMapping("/api/locatarios")
@Tag(name = "Locatários", description = "Endpoints para gerenciamento do perfil de locatários")
public class LocatarioController {

    private final LocatarioService service;

    public LocatarioController(LocatarioService service) {
        this.service = service;
    }

    @Operation(summary = "Criar perfil de locatário", description = "Cadastra um novo perfil de locatário no sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Locatário criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos ou usuário já cadastrado")
    })
    @PostMapping
    public ResponseEntity<LocatarioResponseDTO> criar(@RequestBody @Valid LocatarioCreateDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(dto));
    }

    @Operation(summary = "Listar todos os locatários", description = "Retorna uma página com todos os locatários cadastrados")
    @ApiResponse(responseCode = "200", description = "Página de locatários retornada com sucesso")
    @GetMapping
    public ResponseEntity<Page<LocatarioResponseDTO>> listarTodos(@PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(service.listarTodos(pageable));
    }

    @Operation(summary = "Buscar locatário por ID do usuário", description = "Busca os detalhes do perfil de locatário pelo UUID do usuário")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Locatário encontrado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Locatário não encontrado")
    })
    @GetMapping("/{usuarioId}")
    public ResponseEntity<LocatarioResponseDTO> buscarPorId(@PathVariable UUID usuarioId) {
        return ResponseEntity.ok(service.buscarPorId(usuarioId));
    }

    @Operation(summary = "Deletar perfil de locatário", description = "Remove o perfil de locatário do sistema pelo UUID do usuário")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Locatário deletado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Locatário não encontrado")
    })
    @DeleteMapping("/{usuarioId}")
    public ResponseEntity<Void> deletar(@PathVariable UUID usuarioId) {
        service.deletar(usuarioId);
        return ResponseEntity.noContent().build();
    }
}