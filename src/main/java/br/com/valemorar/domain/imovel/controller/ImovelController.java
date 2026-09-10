package br.com.valemorar.domain.imovel.controller;

import br.com.valemorar.domain.imovel.dto.ImovelCreateDTO;
import br.com.valemorar.domain.imovel.dto.ImovelResponseDTO;
import br.com.valemorar.domain.imovel.service.ImovelService;
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
@RequestMapping("/api/imoveis")
@Tag(name = "Imóveis", description = "Endpoints para gerenciamento de imóveis")
public class ImovelController {

    private final ImovelService imovelService;

    public ImovelController(ImovelService imovelService) {
        this.imovelService = imovelService;
    }

    @Operation(summary = "Criar um imóvel", description = "Cadastra um novo imóvel no sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Imóvel criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos")
    })
    @PostMapping
    public ResponseEntity<ImovelResponseDTO> criar(@RequestBody @Valid ImovelCreateDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(imovelService.criar(dto));
    }

    @Operation(summary = "Listar todos os imóveis", description = "Retorna uma página com todos os imóveis cadastrados")
    @ApiResponse(responseCode = "200", description = "Página retornada com sucesso")
    @GetMapping
    public ResponseEntity<Page<ImovelResponseDTO>> listarTodos(@PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(imovelService.listarTodos(pageable));
    }

    @Operation(summary = "Buscar imóvel por ID", description = "Busca os detalhes de um imóvel específico pelo seu UUID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Imóvel encontrado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Imóvel não encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ImovelResponseDTO> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(imovelService.buscarPorId(id));
    }

    @Operation(summary = "Buscar imóveis por locador", description = "Retorna uma página com todos os imóveis pertencentes a um locador")
    @ApiResponse(responseCode = "200", description = "Página de imóveis do locador retornada com sucesso")
    @GetMapping("/locador/{locadorId}")
    public ResponseEntity<Page<ImovelResponseDTO>> buscarPorLocador(
            @PathVariable UUID locadorId,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(imovelService.buscarPorLocador(locadorId, pageable));
    }

    @Operation(summary = "Buscar imóveis por tipo", description = "Retorna uma página de imóveis filtrados pelo tipo (ex: CASA, APARTAMENTO)")
    @ApiResponse(responseCode = "200", description = "Página de imóveis por tipo retornada com sucesso")
    @GetMapping("/tipo/{tipoImovel}")
    public ResponseEntity<Page<ImovelResponseDTO>> buscarPorTipo(
            @PathVariable String tipoImovel,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(imovelService.buscarPorTipo(tipoImovel, pageable));
    }

    @Operation(summary = "Atualizar imóvel", description = "Atualiza os dados de um imóvel existente pelo seu UUID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Imóvel atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou imóvel não encontrado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ImovelResponseDTO> atualizar(
            @PathVariable UUID id,
            @RequestBody @Valid ImovelCreateDTO dto) {
        return ResponseEntity.ok(imovelService.atualizar(id, dto));
    }

    @Operation(summary = "Deletar imóvel", description = "Remove um imóvel do sistema pelo seu UUID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Imóvel deletado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Imóvel não encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable UUID id) {
        imovelService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}