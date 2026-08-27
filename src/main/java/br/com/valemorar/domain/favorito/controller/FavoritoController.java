package br.com.valemorar.domain.favorito.controller;

import br.com.valemorar.domain.favorito.dto.FavoritoCreateDTO;
import br.com.valemorar.domain.favorito.dto.FavoritoResponseDTO;
import br.com.valemorar.domain.favorito.service.FavoritoService;
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
@RequestMapping("/api/favoritos")
@Tag(name = "Favoritos", description = "Endpoints para gerenciamento de imóveis favoritados pelos usuários")
public class FavoritoController {

    private final FavoritoService service;

    public FavoritoController(FavoritoService service) {
        this.service = service;
    }

    @Operation(summary = "Adicionar aos favoritos", description = "Salva um anúncio na lista de favoritos do usuário")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Favorito adicionado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos")
    })
    @PostMapping
    public ResponseEntity<FavoritoResponseDTO> criar(@RequestBody @Valid FavoritoCreateDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(dto));
    }

    @Operation(summary = "Listar todos os favoritos", description = "Retorna uma lista com todos os registros de favoritos cadastrados")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    @GetMapping
    public ResponseEntity<List<FavoritoResponseDTO>> listarTodos() {
        return ResponseEntity.ok(service.listarTodos());
    }

    @Operation(summary = "Buscar favorito por ID", description = "Busca os detalhes de um registro de favorito específico pelo seu UUID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Favorito encontrado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Favorito não encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<FavoritoResponseDTO> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @Operation(summary = "Buscar favoritos por usuário", description = "Retorna a lista de anúncios favoritados por um usuário específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de favoritos do usuário retornada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Usuário não encontrado ou sem favoritos")
    })
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<FavoritoResponseDTO>> buscarPorUsuario(@PathVariable UUID usuarioId) {
        return ResponseEntity.ok(service.buscarPorUsuario(usuarioId));
    }

    @Operation(summary = "Remover dos favoritos", description = "Remove um imóvel da lista de favoritos pelo seu UUID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Favorito removido com sucesso"),
            @ApiResponse(responseCode = "400", description = "Favorito não encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable UUID id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}