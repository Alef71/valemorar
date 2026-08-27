package br.com.valemorar.domain.sessao.controller;

import br.com.valemorar.domain.sessao.dto.SessaoCreateDTO;
import br.com.valemorar.domain.sessao.dto.SessaoResponseDTO;
import br.com.valemorar.domain.sessao.service.SessaoService;
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
@RequestMapping("/api/sessoes")
@Tag(name = "Sessões", description = "Endpoints para gerenciamento de sessões de usuário no sistema")
public class SessaoController {

    private final SessaoService service;

    public SessaoController(SessaoService service) {
        this.service = service;
    }

    @Operation(summary = "Criar uma sessão", description = "Registra uma nova sessão de usuário no sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Sessão criada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos")
    })
    @PostMapping
    public ResponseEntity<SessaoResponseDTO> criar(@RequestBody @Valid SessaoCreateDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(dto));
    }

    @Operation(summary = "Listar todas as sessões", description = "Retorna uma lista com todas as sessões registradas")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    @GetMapping
    public ResponseEntity<List<SessaoResponseDTO>> listarTodos() {
        return ResponseEntity.ok(service.listarTodos());
    }

    @Operation(summary = "Buscar sessão por ID", description = "Busca os detalhes de uma sessão específica pelo seu UUID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sessão encontrada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Sessão não encontrada")
    })
    @GetMapping("/{id}")
    public ResponseEntity<SessaoResponseDTO> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @Operation(summary = "Buscar sessões por usuário", description = "Retorna todas as sessões registradas para um usuário específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de sessões do usuário retornada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Usuário não encontrado ou sem sessões")
    })
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<SessaoResponseDTO>> buscarPorUsuario(@PathVariable UUID usuarioId) {
        return ResponseEntity.ok(service.buscarPorUsuario(usuarioId));
    }

    @Operation(summary = "Revogar sessão", description = "Invalida ou revoga uma sessão ativa pelo seu UUID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sessão revogada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Sessão não encontrada")
    })
    @PatchMapping("/{id}/revogar")
    public ResponseEntity<SessaoResponseDTO> revogarSessao(@PathVariable UUID id) {
        return ResponseEntity.ok(service.revogarSessao(id));
    }

    @Operation(summary = "Deletar sessão", description = "Remove o registro de uma sessão do sistema pelo seu UUID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Sessão deletada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Sessão não encontrada")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable UUID id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}