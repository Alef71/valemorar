package br.com.valemorar.domain.denucia.controller;

import br.com.valemorar.domain.denucia.dto.DenunciaCreateDTO;
import br.com.valemorar.domain.denucia.dto.DenunciaResponseDTO;
import br.com.valemorar.domain.denucia.service.DenunciaService;
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
@RequestMapping("/api/denuncias")
@Tag(name = "Denúncias", description = "Endpoints para gerenciamento e moderação de denúncias")
public class DenunciaController {

    private final DenunciaService service;

    public DenunciaController(DenunciaService service) {
        this.service = service;
    }

    @Operation(summary = "Criar uma denúncia", description = "Registra uma nova denúncia no sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Denúncia criada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos")
    })
    @PostMapping
    public ResponseEntity<DenunciaResponseDTO> criar(@RequestBody @Valid DenunciaCreateDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(dto));
    }

    @Operation(summary = "Listar todas as denúncias", description = "Retorna uma lista com todas as denúncias registradas")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    @GetMapping
    public ResponseEntity<List<DenunciaResponseDTO>> listarTodos() {
        return ResponseEntity.ok(service.listarTodos());
    }

    @Operation(summary = "Buscar denúncia por ID", description = "Busca os detalhes de uma denúncia específica pelo seu UUID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Denúncia encontrada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Denúncia não encontrada")
    })
    @GetMapping("/{id}")
    public ResponseEntity<DenunciaResponseDTO> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @Operation(summary = "Buscar denúncias por status", description = "Retorna uma lista de denúncias filtradas pelo status informado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de denúncias retornada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Status inválido ou não encontrado")
    })
    @GetMapping("/status/{status}")
    public ResponseEntity<List<DenunciaResponseDTO>> buscarPorStatus(@PathVariable String status) {
        return ResponseEntity.ok(service.buscarPorStatus(status));
    }

    @Operation(summary = "Resolver denúncia", description = "Atualiza o status de uma denúncia e registra o responsável pela resolução")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Denúncia resolvida com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou denúncia não encontrada")
    })
    @PatchMapping("/{id}/resolver")
    public ResponseEntity<DenunciaResponseDTO> resolverDenuncia(
            @PathVariable UUID id,
            @RequestParam UUID resolvidoPor,
            @RequestParam String status) {
        return ResponseEntity.ok(service.resolverDenuncia(id, resolvidoPor, status));
    }

    @Operation(summary = "Deletar denúncia", description = "Remove uma denúncia do sistema pelo seu UUID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Denúncia deletada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Denúncia não encontrada")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable UUID id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}