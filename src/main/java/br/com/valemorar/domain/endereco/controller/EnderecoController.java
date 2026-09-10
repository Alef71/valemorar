package br.com.valemorar.domain.endereco.controller;

import br.com.valemorar.domain.endereco.dto.EnderecoCreateDTO;
import br.com.valemorar.domain.endereco.dto.EnderecoResponseDTO;
import br.com.valemorar.domain.endereco.service.EnderecoService;
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
@RequestMapping("/api/enderecos")
@Tag(name = "Endereços", description = "Endpoints para gerenciamento de endereços no sistema")
public class EnderecoController {

    private final EnderecoService service;

    public EnderecoController(EnderecoService service) {
        this.service = service;
    }

    @Operation(summary = "Criar um endereço", description = "Cadastra um novo endereço no sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Endereço criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos")
    })
    @PostMapping
    public ResponseEntity<EnderecoResponseDTO> criar(@RequestBody @Valid EnderecoCreateDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(dto));
    }

    @Operation(summary = "Listar todos os endereços", description = "Retorna uma página com todos os endereços cadastrados")
    @ApiResponse(responseCode = "200", description = "Página retornada com sucesso")
    @GetMapping
    public ResponseEntity<Page<EnderecoResponseDTO>> listarTodos(@PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(service.listarTodos(pageable));
    }

    @Operation(summary = "Buscar endereço por ID", description = "Busca os detalhes de um endereço específico pelo seu UUID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Endereço encontrado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Endereço não encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<EnderecoResponseDTO> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @Operation(summary = "Buscar endereços por cidade", description = "Retorna uma página de endereços localizados em uma determinada cidade")
    @ApiResponse(responseCode = "200", description = "Página de endereços da cidade retornada com sucesso")
    @GetMapping("/cidade/{cidade}")
    public ResponseEntity<Page<EnderecoResponseDTO>> buscarPorCidade(
            @PathVariable String cidade,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(service.buscarPorCidade(cidade, pageable));
    }

    @Operation(summary = "Buscar endereços por CEP", description = "Retorna uma página de endereços filtrados pelo CEP informado")
    @ApiResponse(responseCode = "200", description = "Página de endereços do CEP retornada com sucesso")
    @GetMapping("/cep/{cep}")
    public ResponseEntity<Page<EnderecoResponseDTO>> buscarPorCep(
            @PathVariable String cep,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(service.buscarPorCep(cep, pageable));
    }

    @Operation(summary = "Atualizar endereço", description = "Atualiza os dados de um endereço existente pelo seu UUID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Endereço atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou endereço não encontrado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<EnderecoResponseDTO> atualizar(@PathVariable UUID id,
            @RequestBody @Valid EnderecoCreateDTO dto) {
        return ResponseEntity.ok(service.atualizar(id, dto));
    }

    @Operation(summary = "Deletar endereço", description = "Remove um endereço do sistema pelo seu UUID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Endereço deletado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Endereço não encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable UUID id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}