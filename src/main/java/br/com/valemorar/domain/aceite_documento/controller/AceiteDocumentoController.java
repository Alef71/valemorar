package br.com.valemorar.domain.aceite_documento.controller;

import br.com.valemorar.domain.aceite_documento.dto.AceiteDocumentoCreateDTO;
import br.com.valemorar.domain.aceite_documento.dto.AceiteDocumentoResponseDTO;
import br.com.valemorar.domain.aceite_documento.service.AceiteDocumentoService;
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
@RequestMapping("/api/aceites-documento")
@Tag(name = "Aceites de Documentos", description = "Endpoints para gerenciamento do aceite de termos e documentos legais")
public class AceiteDocumentoController {

    private final AceiteDocumentoService service;

    public AceiteDocumentoController(AceiteDocumentoService service) {
        this.service = service;
    }

    @Operation(summary = "Registrar aceite de documento", description = "Registra a aceitação de um termo ou documento legal por um usuário")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Aceite registrado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos")
    })
    @PostMapping
    public ResponseEntity<AceiteDocumentoResponseDTO> registrarAceite(
            @RequestBody @Valid AceiteDocumentoCreateDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.registrarAceite(dto));
    }

    @Operation(summary = "Listar todos os aceites", description = "Retorna uma lista com todos os registros de aceites de documentos")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    @GetMapping
    public ResponseEntity<List<AceiteDocumentoResponseDTO>> listarTodos() {
        return ResponseEntity.ok(service.listarTodos());
    }

    @Operation(summary = "Buscar aceite por ID", description = "Busca os detalhes de um registro de aceite específico pelo seu UUID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Registro encontrado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Registro não encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<AceiteDocumentoResponseDTO> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @Operation(summary = "Buscar aceites por ID do usuário", description = "Retorna todos os registros de aceites pertencentes a um usuário específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de aceites do usuário retornada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Usuário não encontrado ou sem registros")
    })
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<AceiteDocumentoResponseDTO>> buscarPorUsuario(@PathVariable UUID usuarioId) {
        return ResponseEntity.ok(service.buscarPorUsuario(usuarioId));
    }

    @Operation(summary = "Deletar registro de aceite", description = "Remove um registro de aceite pelo seu UUID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Registro deletado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Registro não encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable UUID id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}