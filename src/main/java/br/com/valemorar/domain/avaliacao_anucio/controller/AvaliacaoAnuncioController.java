package br.com.valemorar.domain.avaliacao_anucio.controller;

import br.com.valemorar.domain.avaliacao_anucio.dto.AvaliacaoAnuncioCreateDTO;
import br.com.valemorar.domain.avaliacao_anucio.dto.AvaliacaoAnuncioResponseDTO;
import br.com.valemorar.domain.avaliacao_anucio.service.AvaliacaoAnuncioService;
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
@RequestMapping("/api/avaliacoes-anuncio")
@Tag(name = "Avaliações de Anúncios", description = "Endpoints para gerenciamento de avaliações e comentários nos anúncios")
public class AvaliacaoAnuncioController {

    private final AvaliacaoAnuncioService service;

    public AvaliacaoAnuncioController(AvaliacaoAnuncioService service) {
        this.service = service;
    }

    @Operation(summary = "Criar uma avaliação", description = "Cadastra uma nova avaliação para um anúncio específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Avaliação criada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos")
    })
    @PostMapping
    public ResponseEntity<AvaliacaoAnuncioResponseDTO> criar(@RequestBody @Valid AvaliacaoAnuncioCreateDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(dto));
    }

    @Operation(summary = "Listar todas as avaliações", description = "Retorna uma lista com todas as avaliações registradas no sistema")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    @GetMapping
    public ResponseEntity<List<AvaliacaoAnuncioResponseDTO>> listarTodos() {
        return ResponseEntity.ok(service.listarTodos());
    }

    @Operation(summary = "Buscar avaliação por ID", description = "Busca os detalhes de uma avaliação específica pelo seu UUID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Avaliação encontrada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Avaliação não encontrada")
    })
    @GetMapping("/{id}")
    public ResponseEntity<AvaliacaoAnuncioResponseDTO> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @Operation(summary = "Buscar avaliações por anúncio", description = "Retorna todas as avaliações cadastradas para um determinado anúncio")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de avaliações do anúncio retornada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Anúncio não encontrado ou sem avaliações")
    })
    @GetMapping("/anuncio/{anuncioId}")
    public ResponseEntity<List<AvaliacaoAnuncioResponseDTO>> buscarPorAnuncio(@PathVariable UUID anuncioId) {
        return ResponseEntity.ok(service.buscarPorAnuncio(anuncioId));
    }

    @Operation(summary = "Atualizar avaliação", description = "Atualiza a nota ou o comentário de uma avaliação existente pelo UUID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Avaliação atualizada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou avaliação não encontrada")
    })
    @PutMapping("/{id}")
    public ResponseEntity<AvaliacaoAnuncioResponseDTO> atualizar(@PathVariable UUID id,
            @RequestBody @Valid AvaliacaoAnuncioCreateDTO dto) {
        return ResponseEntity.ok(service.atualizar(id, dto));
    }

    @Operation(summary = "Deletar avaliação", description = "Remove uma avaliação do sistema pelo seu UUID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Avaliação deletada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Avaliação não encontrada")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable UUID id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}