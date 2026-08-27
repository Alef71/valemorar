package br.com.valemorar.domain.notificacao.controller;

import br.com.valemorar.domain.notificacao.dto.NotificacaoCreateDTO;
import br.com.valemorar.domain.notificacao.dto.NotificacaoResponseDTO;
import br.com.valemorar.domain.notificacao.service.NotificacaoService;
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
@RequestMapping("/api/notificacoes")
@Tag(name = "Notificações", description = "Endpoints para gerenciamento de notificações enviadas aos usuários")
public class NotificacaoController {

    private final NotificacaoService service;

    public NotificacaoController(NotificacaoService service) {
        this.service = service;
    }

    @Operation(summary = "Criar uma notificação", description = "Cadastra e envia uma nova notificação no sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Notificação criada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos")
    })
    @PostMapping
    public ResponseEntity<NotificacaoResponseDTO> criar(@RequestBody @Valid NotificacaoCreateDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(dto));
    }

    @Operation(summary = "Listar todas as notificações", description = "Retorna uma lista com todas as notificações registradas")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    @GetMapping
    public ResponseEntity<List<NotificacaoResponseDTO>> listarTodos() {
        return ResponseEntity.ok(service.listarTodos());
    }

    @Operation(summary = "Buscar notificação por ID", description = "Busca os detalhes de uma notificação específica pelo seu UUID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Notificação encontrada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Notificação não encontrada")
    })
    @GetMapping("/{id}")
    public ResponseEntity<NotificacaoResponseDTO> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @Operation(summary = "Buscar notificações por usuário", description = "Retorna todas as notificações associadas a um usuário específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de notificações do usuário retornada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Usuário não encontrado ou sem notificações")
    })
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<NotificacaoResponseDTO>> buscarPorUsuario(@PathVariable UUID usuarioId) {
        return ResponseEntity.ok(service.buscarPorUsuario(usuarioId));
    }

    @Operation(summary = "Buscar notificações não lidas por usuário", description = "Retorna a lista de notificações pendentes de leitura de um usuário")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de notificações não lidas retornada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Usuário não encontrado")
    })
    @GetMapping("/usuario/{usuarioId}/nao-lidas")
    public ResponseEntity<List<NotificacaoResponseDTO>> buscarNaoLidasPorUsuario(@PathVariable UUID usuarioId) {
        return ResponseEntity.ok(service.buscarNaoLidasPorUsuario(usuarioId));
    }

    @Operation(summary = "Marcar notificação como lida", description = "Atualiza o status de uma notificação específica para lida pelo seu UUID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Notificação marcada como lida com sucesso"),
            @ApiResponse(responseCode = "400", description = "Notificação não encontrada")
    })
    @PatchMapping("/{id}/ler")
    public ResponseEntity<NotificacaoResponseDTO> marcarComoLida(@PathVariable UUID id) {
        return ResponseEntity.ok(service.marcarComoLida(id));
    }

    @Operation(summary = "Deletar notificação", description = "Remove uma notificação do sistema pelo seu UUID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Notificação deletada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Notificação não encontrada")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable UUID id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}