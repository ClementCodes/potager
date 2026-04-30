package com.potagerai.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * Gestionnaire global des exceptions — traduit les exceptions métier en réponses HTTP structurées.
 * Toutes les réponses d'erreur ont le format {@link ApiError}.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // -------------------------------------------------------------------------
    // 400 — Validation Bean Validation (@Valid)
    // -------------------------------------------------------------------------
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidationErrors(
            MethodArgumentNotValidException ex, HttpServletRequest request) {

        List<String> details = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .toList();

        return ResponseEntity.badRequest().body(
                ApiError.of(400, "Validation échouée", "Un ou plusieurs champs sont invalides",
                        request.getRequestURI(), details));
    }

    // -------------------------------------------------------------------------
    // 400 — Contrainte violée (path variable / query param)
    // -------------------------------------------------------------------------
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> handleConstraintViolation(
            ConstraintViolationException ex, HttpServletRequest request) {

        List<String> details = ex.getConstraintViolations().stream()
                .map(v -> v.getPropertyPath() + " : " + v.getMessage())
                .toList();

        return ResponseEntity.badRequest().body(
                ApiError.of(400, "Validation échouée", "Paramètre invalide",
                        request.getRequestURI(), details));
    }

    // -------------------------------------------------------------------------
    // 400 — Règle métier (email déjà existant, surface insuffisante, etc.)
    // -------------------------------------------------------------------------
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleIllegalArgument(
            IllegalArgumentException ex, HttpServletRequest request) {

        return ResponseEntity.badRequest().body(
                ApiError.of(400, "Requête invalide", ex.getMessage(), request.getRequestURI()));
    }

    // -------------------------------------------------------------------------
    // 401 — Identifiants incorrects
    // -------------------------------------------------------------------------
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiError> handleBadCredentials(
            BadCredentialsException ex, HttpServletRequest request) {

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                ApiError.of(401, "Non autorisé", "Email ou mot de passe incorrect",
                        request.getRequestURI()));
    }

    @ExceptionHandler({AuthenticationException.class, DisabledException.class})
    public ResponseEntity<ApiError> handleAuthentication(
            AuthenticationException ex, HttpServletRequest request) {

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                ApiError.of(401, "Non autorisé", "Authentification requise", request.getRequestURI()));
    }

    // -------------------------------------------------------------------------
    // 404 — Ressource introuvable
    // -------------------------------------------------------------------------
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ApiError> handleNotFound(
            NoSuchElementException ex, HttpServletRequest request) {

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ApiError.of(404, "Introuvable", ex.getMessage(), request.getRequestURI()));
    }

    // -------------------------------------------------------------------------
    // 422 — Solution non faisable (solveur LP — surface insuffisante)
    // -------------------------------------------------------------------------
    @ExceptionHandler(NoFeasibleSolutionException.class)
    public ResponseEntity<Map<String, Object>> handleNoFeasibleSolution(
            NoFeasibleSolutionException ex, HttpServletRequest request) {

        long needed = (long) Math.ceil(ex.getRequiredSurfaceM2());
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(Map.of(
                "status", 422,
                "error", "Surface insuffisante",
                "message", String.format(
                        "Il vous faut au moins %d m² pour atteindre l'autonomie calorique.", needed),
                "requiredSurfaceM2", needed,
                "path", request.getRequestURI()
        ));
    }

    // -------------------------------------------------------------------------
    // 500 — Erreur inattendue
    // -------------------------------------------------------------------------
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleUnexpected(Exception ex, HttpServletRequest request) {
        log.error("Erreur inattendue sur {}: {}", request.getRequestURI(), ex.getMessage(), ex);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ApiError.of(500, "Erreur interne", "Une erreur inattendue est survenue",
                        request.getRequestURI()));
    }
}
