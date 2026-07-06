package com.alura.literalura.controller;

import com.alura.literalura.dto.*;
import com.alura.literalura.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Autenticación", description = "Login del personal y gestión de usuarios")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(summary = "Autentica un usuario del personal y devuelve un JWT")
    @PostMapping("/login")
    public TokenResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @Operation(summary = "Lista los usuarios del personal (solo ADMIN)",
            security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/users")
    public List<UserDTO> listarUsuarios() {
        return authService.listarUsuarios();
    }

    @Operation(summary = "Crea un usuario del personal (solo ADMIN)",
            security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping("/users")
    public ResponseEntity<UserDTO> crearUsuario(@Valid @RequestBody UserRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(authService.crearUsuario(request));
    }
}
