package br.com.valemorar.domain.admin.controller;

import br.com.valemorar.domain.admin.service.AdminService;
import br.com.valemorar.domain.notificacao.dto.NotificacaoCreateDTO;
import br.com.valemorar.domain.notificacao.dto.NotificacaoResponseDTO;
import br.com.valemorar.domain.usuario.dto.UsuarioResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Administração", description = "Endpoints com privilégios de gerenciamento total")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @Operation(summary = "Listar todos os usuários")
    @GetMapping("/usuarios")
    public ResponseEntity<List<UsuarioResponseDTO>> listarUsuarios() {
        return ResponseEntity.ok(adminService.listarTodosUsuarios());
    }

    @Operation(summary = "Bloquear usuário")
    @PatchMapping("/usuarios/{id}/bloquear")
    public ResponseEntity<UsuarioResponseDTO> bloquearUsuario(@PathVariable UUID id) {
        return ResponseEntity.ok(adminService.alterarStatusUsuario(id, "BLOQUEADO"));
    }

    @Operation(summary = "Ativar/Desbloquear usuário")
    @PatchMapping("/usuarios/{id}/ativar")
    public ResponseEntity<UsuarioResponseDTO> ativarUsuario(@PathVariable UUID id) {
        return ResponseEntity.ok(adminService.alterarStatusUsuario(id, "ATIVO"));
    }

    @Operation(summary = "Excluir qualquer usuário")
    @DeleteMapping("/usuarios/{id}")
    public ResponseEntity<Void> deletarUsuario(@PathVariable UUID id) {
        adminService.deletarUsuario(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Enviar notificação")
    @PostMapping("/notificacoes")
    public ResponseEntity<NotificacaoResponseDTO> enviarNotificacao(@RequestBody @Valid NotificacaoCreateDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(adminService.enviarNotificacao(dto));
    }
}