package br.com.valemorar.domain.Imovel_caracteristica.controller;

import br.com.valemorar.domain.Imovel_caracteristica.dto.ImovelCaracteristicaCreateDTO;
import br.com.valemorar.domain.Imovel_caracteristica.dto.ImovelCaracteristicaResponseDTO;
import br.com.valemorar.domain.Imovel_caracteristica.service.ImovelCaracteristicaService;
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
@RequestMapping("/api/imovel-caracteristicas")
@Tag(name = "Características do Imóvel", description = "Endpoints para associação e gerenciamento de características dos imóveis")
public class ImovelCaracteristicaController {

    private final ImovelCaracteristicaService service;

    public ImovelCaracteristicaController(ImovelCaracteristicaService service) {
        this.service = service;
    }

    @Operation(summary = "Associar característica ao imóvel", description = "Cadastra o vínculo de uma característica a um imóvel específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Característica associada ao imóvel com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos")
    })
    @PostMapping
    public ResponseEntity<ImovelCaracteristicaResponseDTO> criar(
            @RequestBody @Valid ImovelCaracteristicaCreateDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(dto));
    }

    @Operation(summary = "Listar todas as características de imóveis", description = "Retorna uma lista com todas as associações de características registradas nos imóveis")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    @GetMapping
    public ResponseEntity<List<ImovelCaracteristicaResponseDTO>> listarTodos() {
        return ResponseEntity.ok(service.listarTodos());
    }

    @Operation(summary = "Buscar associação por ID", description = "Busca os detalhes de uma característica do imóvel pelo seu UUID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Registro encontrado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Registro não encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ImovelCaracteristicaResponseDTO> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @Operation(summary = "Buscar características por imóvel", description = "Retorna todas as características associadas a um imóvel específico pelo UUID do imóvel")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de características do imóvel retornada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Imóvel não encontrado ou sem características")
    })
    @GetMapping("/imovel/{imovelId}")
    public ResponseEntity<List<ImovelCaracteristicaResponseDTO>> buscarPorImovelId(@PathVariable UUID imovelId) {
        return ResponseEntity.ok(service.buscarPorImovelId(imovelId));
    }

    @Operation(summary = "Atualizar característica do imóvel", description = "Atualiza os dados de uma associação existente pelo seu UUID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Registro atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou registro não encontrado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ImovelCaracteristicaResponseDTO> atualizar(@PathVariable UUID id,
            @RequestBody @Valid ImovelCaracteristicaCreateDTO dto) {
        return ResponseEntity.ok(service.atualizar(id, dto));
    }

    @Operation(summary = "Remover característica do imóvel", description = "Remove a associação de uma característica com o imóvel pelo seu UUID")
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