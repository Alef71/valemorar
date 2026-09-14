package br.com.valemorar.domain.usuario.controller;

import br.com.valemorar.domain.usuario.dto.UsuarioCreateDTO;
import br.com.valemorar.domain.usuario.dto.UsuarioResponseDTO;
import br.com.valemorar.domain.usuario.dto.UsuarioUpdateDTO;
import br.com.valemorar.domain.usuario.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/usuarios")
@Tag(name = "Usuários", description = "Endpoints para gerenciamento de contas, perfis e ações administrativas de usuários")
public class UsuarioController {

    private final UsuarioService service;

    public UsuarioController(UsuarioService service) {
        this.service = service;
    }

    @Operation(summary = "Criar um novo usuário", description = "Realiza o autocadastro de um novo usuário na plataforma (RF01)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuário cadastrado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou e-mail já cadastrado")
    })
    @PostMapping
    public ResponseEntity<UsuarioResponseDTO> criar(@RequestBody @Valid UsuarioCreateDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(dto));
    }

    @Operation(summary = "Obter perfil logado", description = "Retorna os dados do usuário autenticado via token JWT sem risco de IDOR")
    @ApiResponse(responseCode = "200", description = "Perfil retornado com sucesso")
    @GetMapping("/me")
    public ResponseEntity<UsuarioResponseDTO> obterPerfilProprio() {
        return ResponseEntity.ok(service.buscarPerfilProprio());
    }

    @Operation(summary = "Atualizar perfil próprio", description = "Atualiza os dados pessoais do usuário autenticado no token (RF01)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    @PutMapping("/me")
    public ResponseEntity<UsuarioResponseDTO> atualizarPerfilProprio(@RequestBody @Valid UsuarioUpdateDTO dto) {
        return ResponseEntity.ok(service.atualizarPerfilProprio(dto));
    }

    @Operation(summary = "Desativar própria conta", description = "Permite que o usuário autenticado desative sua conta temporariamente (RF03 / LGPD)")
    @ApiResponse(responseCode = "200", description = "Conta desativada temporariamente com sucesso")
    @PatchMapping("/me/desativar")
    public ResponseEntity<UsuarioResponseDTO> desativarMinhaConta() {
        return ResponseEntity.ok(service.desativarMinhaConta());
    }

    @Operation(summary = "Listar todos os usuários (Admin)", description = "Retorna uma lista de todos os usuários cadastrados")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UsuarioResponseDTO>> listarTodos() {
        return ResponseEntity.ok(service.listarTodos());
    }

    @Operation(summary = "Buscar usuário por ID (Admin)", description = "Busca as informações de um perfil pelo UUID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário encontrado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UsuarioResponseDTO> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @Operation(summary = "Atualizar usuário por ID (Admin)", description = "Permite que administradores alterem dados cadastrais por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UsuarioResponseDTO> atualizar(@PathVariable UUID id,
            @RequestBody @Valid UsuarioUpdateDTO dto) {
        return ResponseEntity.ok(service.atualizar(id, dto));
    }

    @Operation(summary = "Desativar conta por ID (Admin)", description = "Ação administrativa para desativar uma conta por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Conta desativada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    @PatchMapping("/{id}/desativar")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UsuarioResponseDTO> desativarConta(@PathVariable UUID id) {
        return ResponseEntity.ok(service.desativarConta(id));
    }

    @Operation(summary = "Bloquear/Banir usuário (Admin)", description = "Ação administrativa para banir/bloquear usuários mal-intencionados (RF13)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário bloqueado/banido com sucesso"),
            @ApiResponse(responseCode = "403", description = "Acesso negado"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    @PatchMapping("/{id}/bloquear")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UsuarioResponseDTO> bloquearUsuario(@PathVariable UUID id) {
        return ResponseEntity.ok(service.bloquearUsuario(id));
    }

    @Operation(summary = "Excluir conta definitivamente (Admin)", description = "Exclui definitivamente a conta do usuário pelo ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Conta excluída definitivamente com sucesso"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletar(@PathVariable UUID id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}