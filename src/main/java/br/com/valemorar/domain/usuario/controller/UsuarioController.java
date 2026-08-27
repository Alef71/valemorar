package br.com.valemorar.domain.usuario.controller;

import br.com.valemorar.domain.usuario.dto.UsuarioCreateDTO;
import br.com.valemorar.domain.usuario.dto.UsuarioResponseDTO;
import br.com.valemorar.domain.usuario.service.UsuarioService;
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

    @Operation(summary = "Listar todos os usuários", description = "Retorna uma lista de todos os usuários cadastrados (Uso Admin)")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    @GetMapping
    public ResponseEntity<List<UsuarioResponseDTO>> listarTodos() {
        return ResponseEntity.ok(service.listarTodos());
    }

    @Operation(summary = "Buscar usuário por ID", description = "Busca as informações do perfil pelo UUID do usuário")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário encontrado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Usuário não encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @Operation(summary = "Atualizar usuário", description = "Atualiza os dados pessoais cadastrados (nome, telefone, foto) (RF01)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou usuário não encontrado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> atualizar(@PathVariable UUID id,
            @RequestBody @Valid UsuarioCreateDTO dto) {
        return ResponseEntity.ok(service.atualizar(id, dto));
    }

    @Operation(summary = "Desativar conta temporariamente", description = "Permite que o usuário desative sua conta temporariamente, ocultando perfil e anúncios (RF03 / LGPD)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Conta desativada temporariamente com sucesso"),
            @ApiResponse(responseCode = "400", description = "Usuário não encontrado")
    })
    @PatchMapping("/{id}/desativar")
    public ResponseEntity<UsuarioResponseDTO> desativarConta(@PathVariable UUID id) {
        return ResponseEntity.ok(service.desativarConta(id));
    }

    @Operation(summary = "Bloquear/Banir usuário (Admin)", description = "Ação administrativa para banir/bloquear usuários mal-intencionados (RF13)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário bloqueado/banido com sucesso"),
            @ApiResponse(responseCode = "400", description = "Usuário não encontrado")
    })
    @PatchMapping("/{id}/bloquear")
    public ResponseEntity<UsuarioResponseDTO> bloquearUsuario(@PathVariable UUID id) {
        return ResponseEntity.ok(service.bloquearUsuario(id));
    }

    @Operation(summary = "Excluir conta definitivamente", description = "Exclui definitivamente a conta do usuário do banco de dados (RF03 / LGPD)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Conta excluída definitivamente com sucesso"),
            @ApiResponse(responseCode = "400", description = "Usuário não encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable UUID id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}