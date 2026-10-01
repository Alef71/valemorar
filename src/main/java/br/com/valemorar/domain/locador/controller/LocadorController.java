package br.com.valemorar.domain.locador.controller;

import br.com.valemorar.domain.locador.dto.LocadorContatoDTO;
import br.com.valemorar.domain.locador.dto.LocadorCreateDTO;
import br.com.valemorar.domain.locador.dto.LocadorResponseDTO;
import br.com.valemorar.domain.locador.service.LocadorService;
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
@RequestMapping("/api/locadores")
@Tag(name = "Locadores", description = "Endpoints para gerenciamento do perfil de locadores")
public class LocadorController {

    private final LocadorService service;

    public LocadorController(LocadorService service) {
        this.service = service;
    }

    @Operation(summary = "Meu cadastro de locador", description = "Telefone e WhatsApp do usuário autenticado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cadastro encontrado"),
            @ApiResponse(responseCode = "204", description = "Usuário ainda não cadastrou contato")
    })
    @GetMapping("/me")
    public ResponseEntity<LocadorResponseDTO> meuCadastro() {
        return service.buscarMeuCadastro()
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.noContent().build());
    }

    @Operation(summary = "Salvar meu contato", description = "Cria ou atualiza telefone e WhatsApp do usuário autenticado")
    @ApiResponse(responseCode = "200", description = "Contato salvo com sucesso")
    @PutMapping("/me")
    public ResponseEntity<LocadorResponseDTO> salvarMeuContato(@RequestBody @Valid LocadorContatoDTO dto) {
        return ResponseEntity.ok(service.salvarMeuContato(dto));
    }

    @Operation(summary = "Criar perfil de locador", description = "Cadastra um novo perfil de locador no sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Locador criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos ou documento/usuário já cadastrado")
    })
    @PostMapping
    public ResponseEntity<LocadorResponseDTO> criar(@RequestBody @Valid LocadorCreateDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(dto));
    }

    @Operation(summary = "Listar todos os locadores", description = "Retorna uma página com todos os locadores cadastrados")
    @ApiResponse(responseCode = "200", description = "Página de locadores retornada com sucesso")
    @GetMapping
    public ResponseEntity<Page<LocadorResponseDTO>> listarTodos(@PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(service.listarTodos(pageable));
    }

    @Operation(summary = "Buscar locador por ID do usuário", description = "Busca os detalhes do perfil de locador pelo UUID do usuário")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Locador encontrado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Locador não encontrado")
    })
    @GetMapping("/{usuarioId}")
    public ResponseEntity<LocadorResponseDTO> buscarPorId(@PathVariable UUID usuarioId) {
        return ResponseEntity.ok(service.buscarPorId(usuarioId));
    }

    @Operation(summary = "Buscar locador por documento", description = "Busca os detalhes do perfil de locador pelo número do documento (CPF/CNPJ)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Locador encontrado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Locador não encontrado")
    })
    @GetMapping("/documento/{documento}")
    public ResponseEntity<LocadorResponseDTO> buscarPorDocumento(@PathVariable String documento) {
        return ResponseEntity.ok(service.buscarPorDocumento(documento));
    }

    @Operation(summary = "Atualizar perfil de locador", description = "Atualiza os dados do perfil de locador pelo UUID do usuário")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Locador atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou locador não encontrado")
    })
    @PutMapping("/{usuarioId}")
    public ResponseEntity<LocadorResponseDTO> atualizar(
            @PathVariable UUID usuarioId,
            @RequestBody @Valid LocadorCreateDTO dto) {
        return ResponseEntity.ok(service.atualizar(usuarioId, dto));
    }

    @Operation(summary = "Deletar perfil de locador", description = "Remove o perfil de locador do sistema pelo UUID do usuário")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Locador deletado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Locador não encontrado")
    })
    @DeleteMapping("/{usuarioId}")
    public ResponseEntity<Void> deletar(@PathVariable UUID usuarioId) {
        service.deletar(usuarioId);
        return ResponseEntity.noContent().build();
    }
}