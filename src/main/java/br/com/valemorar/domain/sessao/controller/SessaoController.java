package br.com.valemorar.domain.sessao.controller;

import br.com.valemorar.domain.sessao.dto.SessaoCreateDTO;
import br.com.valemorar.domain.sessao.dto.SessaoResponseDTO;
import br.com.valemorar.domain.sessao.service.SessaoService;
import br.com.valemorar.domain.usuario.Usuario;
import br.com.valemorar.domain.usuario.dto.UsuarioResponseDTO;
import br.com.valemorar.infra.SecurityUtils;
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

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/sessao")
@Tag(name = "Sessão", description = "Endpoints para gerenciamento e verificação de sessões de usuário")
public class SessaoController {

    private final SessaoService service;

    public SessaoController(SessaoService service) {
        this.service = service;
    }

    @Operation(summary = "Obter dados do usuário logado", description = "Retorna os dados cadastrais do usuário ativo na sessão sem necessidade de informar ID")
    @GetMapping("/me")
    public ResponseEntity<UsuarioResponseDTO> obterSessaoAtiva() {
        Usuario usuarioLogado = SecurityUtils.getUsuarioAutenticado();
        return ResponseEntity.ok(UsuarioResponseDTO.fromEntity(usuarioLogado));
    }

    @Operation(summary = "Criar sessão", description = "Registra uma nova sessão ativa para um usuário")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Sessão criada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos")
    })
    @PostMapping
    public ResponseEntity<SessaoResponseDTO> criar(@RequestBody @Valid SessaoCreateDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(dto));
    }

    @Operation(summary = "Listar todas as sessões", description = "Retorna uma página com todas as sessões registradas")
    @ApiResponse(responseCode = "200", description = "Página de sessões retornada com sucesso")
    @GetMapping
    public ResponseEntity<Page<SessaoResponseDTO>> listarTodos(@PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(service.listarTodos(pageable));
    }

    @Operation(summary = "Buscar sessão por ID", description = "Busca os detalhes de uma sessão específica pelo seu UUID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sessão encontrada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Sessão não encontrada")
    })
    @GetMapping("/{id}")
    public ResponseEntity<SessaoResponseDTO> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @Operation(summary = "Buscar sessões por usuário", description = "Retorna o histórico de sessões associadas a um usuário")
    @ApiResponse(responseCode = "200", description = "Lista de sessões do usuário retornada com sucesso")
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<SessaoResponseDTO>> buscarPorUsuario(@PathVariable UUID usuarioId) {
        return ResponseEntity.ok(service.buscarPorUsuario(usuarioId));
    }

    @Operation(summary = "Revogar sessão por ID", description = "Revoga uma sessão ativa específica")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sessão revogada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Sessão não encontrada")
    })
    @PatchMapping("/{id}/revogar")
    public ResponseEntity<SessaoResponseDTO> revogarSessao(@PathVariable UUID id) {
        return ResponseEntity.ok(service.revogarSessao(id));
    }

    @Operation(summary = "Revogar todas as sessões do usuário", description = "Revoga todas as sessões ativas de um usuário específico")
    @ApiResponse(responseCode = "204", description = "Todas as sessões do usuário foram revogadas com sucesso")
    @PatchMapping("/usuario/{usuarioId}/revogar-todas")
    public ResponseEntity<Void> revogarTodasDoUsuario(@PathVariable UUID usuarioId) {
        service.revogarTodasDoUsuario(usuarioId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Deletar sessão", description = "Remove o registro de uma sessão do sistema pelo UUID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Sessão deletada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Sessão não encontrada")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable UUID id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}