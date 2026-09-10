package br.com.valemorar.domain.anucio.controller;

import br.com.valemorar.domain.anucio.dto.AnuncioCreateDTO;
import br.com.valemorar.domain.anucio.dto.AnuncioResponseDTO;
import br.com.valemorar.domain.anucio.service.AnuncioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/anuncios")
@Tag(name = "Anúncios", description = "Endpoints para gerenciamento, filtros e status de anúncios de imóveis")
public class AnuncioController {

    private final AnuncioService anuncioService;

    public AnuncioController(AnuncioService anuncioService) {
        this.anuncioService = anuncioService;
    }

    @Operation(summary = "Criar um novo anúncio", description = "Cadastra um novo anúncio de imóvel no sistema (RF06)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Anúncio criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos ou imóvel já possui anúncio ativo")
    })
    @PostMapping
    public ResponseEntity<AnuncioResponseDTO> criar(@RequestBody @Valid AnuncioCreateDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(anuncioService.criar(dto));
    }

    @Operation(summary = "Listar todos os anúncios", description = "Retorna uma página com todos os anúncios cadastrados")
    @ApiResponse(responseCode = "200", description = "Página retornada com sucesso")
    @GetMapping
    public ResponseEntity<Page<AnuncioResponseDTO>> listarTodos(@PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(anuncioService.listarTodos(pageable));
    }

    @Operation(summary = "Buscar anúncios com filtros avançados", description = "Filtra anúncios por cidade, faixa de preço, número de quartos e tags de proximidade (RF07 e RF08)")
    @ApiResponse(responseCode = "200", description = "Busca realizada com sucesso")
    @GetMapping("/busca")
    public ResponseEntity<Page<AnuncioResponseDTO>> buscarComFiltros(
            @Parameter(description = "Cidade para limitação geográfica (RF08)") @RequestParam(required = false) String cidade,
            @Parameter(description = "Valor mínimo do aluguel") @RequestParam(required = false) BigDecimal precoMin,
            @Parameter(description = "Valor máximo do aluguel") @RequestParam(required = false) BigDecimal precoMax,
            @Parameter(description = "Número mínimo de quartos") @RequestParam(required = false) Integer quartos,
            @Parameter(description = "Tags de pontos de interesse ex: IFNMG, UFVJM (RF07)") @RequestParam(required = false) List<String> tags,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(anuncioService.buscarComFiltros(cidade, precoMin, precoMax, quartos, tags, pageable));
    }

    @Operation(summary = "Buscar anúncio por ID", description = "Busca os detalhes de um anúncio específico pelo seu UUID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Anúncio encontrado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Anúncio não encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<AnuncioResponseDTO> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(anuncioService.buscarPorId(id));
    }

    @Operation(summary = "Buscar anúncios por anunciante", description = "Retorna os anúncios publicados por um anunciante específico")
    @ApiResponse(responseCode = "200", description = "Página retornada com sucesso")
    @GetMapping("/anunciante/{anuncianteId}")
    public ResponseEntity<Page<AnuncioResponseDTO>> buscarPorAnunciante(
            @PathVariable UUID anuncianteId,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(anuncioService.buscarPorAnunciante(anuncianteId, pageable));
    }

    @Operation(summary = "Atualizar anúncio", description = "Atualiza as informações de um anúncio existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Anúncio atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou anúncio não encontrado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<AnuncioResponseDTO> atualizar(
            @PathVariable UUID id,
            @RequestBody @Valid AnuncioCreateDTO dto) {
        return ResponseEntity.ok(anuncioService.atualizar(id, dto));
    }

    @Operation(summary = "Alterar status do anúncio", description = "Altera o status do anúncio para ALUGADO, INDISPONIVEL ou ATIVO sem deletar o registro (RF10)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Status alterado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Anúncio não encontrado ou status inválido")
    })
    @PatchMapping("/{id}/status")
    public ResponseEntity<AnuncioResponseDTO> alterarStatus(@PathVariable UUID id, @RequestParam String status) {
        return ResponseEntity.ok(anuncioService.alterarStatus(id, status));
    }

    @Operation(summary = "Renovar anúncio por mais 90 dias", description = "Renova o ciclo de veiculação ativa do anúncio antes da expiração de 90 dias (RF10)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Anúncio renovado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Anúncio não encontrado")
    })
    @PostMapping("/{id}/renovar")
    public ResponseEntity<AnuncioResponseDTO> renovarAnuncio(@PathVariable UUID id) {
        return ResponseEntity.ok(anuncioService.renovarAnuncio(id));
    }

    @Operation(summary = "Deletar anúncio", description = "Remove um anúncio do sistema pelo seu UUID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Anúncio deletado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Anúncio não encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable UUID id) {
        anuncioService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}