package br.com.valemorar.domain.favorito.controller;

import br.com.valemorar.domain.favorito.dto.FavoritoCreateDTO;
import br.com.valemorar.domain.favorito.dto.FavoritoResponseDTO;
import br.com.valemorar.domain.favorito.service.FavoritoService;
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
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou imóvel já favoritado")
    })
    @PostMapping
    public ResponseEntity<FavoritoResponseDTO> criar(@RequestBody @Valid FavoritoCreateDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(dto));
    }

    @Operation(summary = "Listar todos os favoritos", description = "Retorna uma página com todos os registros de favoritos cadastrados")
    @ApiResponse(responseCode = "200", description = "Página retornada com sucesso")
    @GetMapping
    public ResponseEntity<Page<FavoritoResponseDTO>> listarTodos(@PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(service.listarTodos(pageable));
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

    @Operation(summary = "Buscar favoritos por usuário", description = "Retorna uma página com os anúncios favoritados por um usuário específico ordenados pelo mais recente")
    @ApiResponse(responseCode = "200", description = "Página de favoritos do usuário retornada com sucesso")
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<Page<FavoritoResponseDTO>> buscarPorUsuario(
            @PathVariable UUID usuarioId,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(service.buscarPorUsuario(usuarioId, pageable));
    }

    @Operation(summary = "Verificar se anúncio é favorito", description = "Verifica se um determinado anúncio está na lista de favoritos de um usuário")
    @ApiResponse(responseCode = "200", description = "Resultado da verificação retornado com sucesso")
    @GetMapping("/check")
    public ResponseEntity<Boolean> isFavorito(
            @RequestParam UUID usuarioId,
            @RequestParam UUID anuncioId) {
        return ResponseEntity.ok(service.isFavorito(usuarioId, anuncioId));
    }

    @Operation(summary = "Remover dos favoritos por ID", description = "Remove um imóvel da lista de favoritos pelo seu UUID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Favorito removido com sucesso"),
            @ApiResponse(responseCode = "400", description = "Favorito não encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable UUID id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Remover favorito por Usuário e Anúncio", description = "Remove o vínculo de favorito informando o ID do usuário e do anúncio")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Favorito removido com sucesso"),
            @ApiResponse(responseCode = "400", description = "Favorito não encontrado")
    })
    @DeleteMapping("/usuario/{usuarioId}/anuncio/{anuncioId}")
    public ResponseEntity<Void> deletarPorUsuarioEAnuncio(
            @PathVariable UUID usuarioId,
            @PathVariable UUID anuncioId) {
        service.deletarPorUsuarioEAnuncio(usuarioId, anuncioId);
        return ResponseEntity.noContent().build();
    }
}