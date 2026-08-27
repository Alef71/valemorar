package br.com.valemorar.domain.auth.controller;

import br.com.valemorar.domain.auth.dto.AuthResponseDTO;
import br.com.valemorar.domain.auth.dto.EsqueciSenhaDTO;
import br.com.valemorar.domain.auth.dto.GoogleLoginDTO;
import br.com.valemorar.domain.auth.dto.LoginRequestDTO;
import br.com.valemorar.domain.auth.dto.RedefinirSenhaDTO;
import br.com.valemorar.domain.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Autenticação", description = "Endpoints para login, autenticação OAuth2 (Google) e recuperação de senha")
public class AuthController {

    private final AuthService service;

    public AuthController(AuthService service) {
        this.service = service;
    }

    @Operation(summary = "Realizar login", description = "Autentica o usuário com e-mail e senha, retornando o token JWT")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Autenticado com sucesso"),
            @ApiResponse(responseCode = "401", description = "Credenciais inválidas")
    })
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody @Valid LoginRequestDTO dto) {
        return ResponseEntity.ok(service.login(dto));
    }

    @Operation(summary = "Login Social (Google)", description = "Autentica ou cadastra o usuário via token OAuth2 do Google (RF02)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Autenticado com sucesso via Google"),
            @ApiResponse(responseCode = "400", description = "Token do Google inválido")
    })
    @PostMapping("/google")
    public ResponseEntity<AuthResponseDTO> loginGoogle(@RequestBody @Valid GoogleLoginDTO dto) {
        return ResponseEntity.ok(service.loginGoogle(dto));
    }

    @Operation(summary = "Solicitar recuperação de senha", description = "Envia um e-mail com token temporário para redefinição de senha (RF15)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "E-mail de recuperação enviado com sucesso"),
            @ApiResponse(responseCode = "404", description = "E-mail não encontrado no sistema")
    })
    @PostMapping("/esqueci-senha")
    public ResponseEntity<Void> esqueciSenha(@RequestBody @Valid EsqueciSenhaDTO dto) {
        service.solicitarRecuperacaoSenha(dto);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Redefinir senha", description = "Redefine a senha do usuário utilizando o token enviado por e-mail (RF15)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Senha alterada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Token inválido, expirado ou senha inválida")
    })
    @PostMapping("/redefinir-senha")
    public ResponseEntity<Void> redefinirSenha(@RequestBody @Valid RedefinirSenhaDTO dto) {
        service.redefinirSenha(dto);
        return ResponseEntity.ok().build();
    }
}
