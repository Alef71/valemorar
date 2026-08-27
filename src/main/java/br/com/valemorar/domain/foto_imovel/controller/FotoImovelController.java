package br.com.valemorar.domain.foto_imovel.controller;

import br.com.valemorar.domain.foto_imovel.dto.FotoImovelCreateDTO;
import br.com.valemorar.domain.foto_imovel.dto.FotoImovelResponseDTO;
import br.com.valemorar.domain.foto_imovel.service.FotoImovelService;
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
@RequestMapping("/api/fotos-imovel")
@Tag(name = "Fotos do Imóvel", description = "Endpoints para gerenciamento de fotos dos imóveis")
public class FotoImovelController {

    private final FotoImovelService service;

    public FotoImovelController(FotoImovelService service) {
        this.service = service;
    }

    @Operation(summary = "Adicionar foto ao imóvel", description = "Cadastra uma nova foto associada a um imóvel")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Foto cadastrada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos")
    })
    @PostMapping
    public ResponseEntity<FotoImovelResponseDTO> criar(@RequestBody @Valid FotoImovelCreateDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(dto));
    }

    @Operation(summary = "Listar todas as fotos", description = "Retorna uma lista com todas as fotos cadastradas no sistema")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    @GetMapping
    public ResponseEntity<List<FotoImovelResponseDTO>> listarTodos() {
        return ResponseEntity.ok(service.listarTodos());
    }

    @Operation(summary = "Buscar foto por ID", description = "Busca os detalhes de uma foto específica pelo seu UUID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Foto encontrada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Foto não encontrada")
    })
    @GetMapping("/{id}")
    public ResponseEntity<FotoImovelResponseDTO> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @Operation(summary = "Buscar fotos por imóvel", description = "Retorna todas as fotos vinculadas a um imóvel específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de fotos do imóvel retornada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Imóvel não encontrado ou sem fotos")
    })
    @GetMapping("/imovel/{imovelId}")
    public ResponseEntity<List<FotoImovelResponseDTO>> buscarPorImovel(@PathVariable UUID imovelId) {
        return ResponseEntity.ok(service.buscarPorImovel(imovelId));
    }

    @Operation(summary = "Atualizar foto do imóvel", description = "Atualiza as informações de uma foto existente pelo seu UUID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Foto atualizada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou foto não encontrada")
    })
    @PutMapping("/{id}")
    public ResponseEntity<FotoImovelResponseDTO> atualizar(@PathVariable UUID id,
            @RequestBody @Valid FotoImovelCreateDTO dto) {
        return ResponseEntity.ok(service.atualizar(id, dto));
    }

    @Operation(summary = "Deletar foto do imóvel", description = "Remove uma foto do sistema pelo seu UUID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Foto deletada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Foto não encontrada")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable UUID id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}