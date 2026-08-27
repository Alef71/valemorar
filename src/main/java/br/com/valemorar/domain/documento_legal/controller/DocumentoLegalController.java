package br.com.valemorar.domain.documento_legal.controller;

import br.com.valemorar.domain.documento_legal.dto.DocumentoLegalCreateDTO;
import br.com.valemorar.domain.documento_legal.dto.DocumentoLegalResponseDTO;
import br.com.valemorar.domain.documento_legal.service.DocumentoLegalService;
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
@RequestMapping("/api/documentos-legais")
@Tag(name = "Documentos Legais", description = "Endpoints para gerenciamento de documentos e termos legais do sistema")
public class DocumentoLegalController {

    private final DocumentoLegalService service;

    public DocumentoLegalController(DocumentoLegalService service) {
        this.service = service;
    }

    @Operation(summary = "Criar um documento legal", description = "Cadastra um novo documento ou termo legal no sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Documento legal criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos")
    })
    @PostMapping
    public ResponseEntity<DocumentoLegalResponseDTO> criar(@RequestBody @Valid DocumentoLegalCreateDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(dto));
    }

    @Operation(summary = "Listar todos os documentos legais", description = "Retorna uma lista com todos os documentos legais cadastrados")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    @GetMapping
    public ResponseEntity<List<DocumentoLegalResponseDTO>> listarTodos() {
        return ResponseEntity.ok(service.listarTodos());
    }

    @Operation(summary = "Buscar documento legal por ID", description = "Busca os detalhes de um documento legal específico pelo seu UUID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Documento legal encontrado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Documento legal não encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<DocumentoLegalResponseDTO> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @Operation(summary = "Buscar documentos legais por tipo", description = "Retorna uma lista de documentos legais filtrados pelo tipo informado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de documentos do tipo informado retornada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Tipo inválido ou sem registros")
    })
    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<List<DocumentoLegalResponseDTO>> buscarPorTipo(@PathVariable String tipo) {
        return ResponseEntity.ok(service.buscarPorTipo(tipo));
    }

    @Operation(summary = "Atualizar documento legal", description = "Atualiza os dados de um documento legal existente pelo seu UUID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Documento legal atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou documento legal não encontrado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<DocumentoLegalResponseDTO> atualizar(@PathVariable UUID id,
            @RequestBody @Valid DocumentoLegalCreateDTO dto) {
        return ResponseEntity.ok(service.atualizar(id, dto));
    }

    @Operation(summary = "Deletar documento legal", description = "Remove um documento legal do sistema pelo seu UUID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Documento legal deletado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Documento legal não encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable UUID id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}