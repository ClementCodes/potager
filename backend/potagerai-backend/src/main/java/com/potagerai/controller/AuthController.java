package com.potagerai.controller;

import com.potagerai.dto.auth.AuthResponse;
import com.potagerai.dto.auth.LoginRequest;
import com.potagerai.dto.auth.RegisterRequest;
import com.potagerai.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Endpoints publics d'authentification.
 *
 * POST /api/auth/register  → inscription + JWT
 * POST /api/auth/login     → connexion + JWT
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentification", description = "Inscription et connexion utilisateur")
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "Créer un compte", description = "Inscrit un nouvel utilisateur et retourne un JWT 24h.")
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Se connecter", description = "Authentifie un utilisateur existant et retourne un JWT 24h.")
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }
}
