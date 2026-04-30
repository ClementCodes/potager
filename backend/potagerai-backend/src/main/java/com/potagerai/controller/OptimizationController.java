package com.potagerai.controller;

import com.potagerai.dto.optimization.OptimizationResultDto;
import com.potagerai.service.GardenOptimizerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

/**
 * Endpoints d'optimisation LP.
 *
 * POST /api/gardens/{id}/optimize  → lance le solveur et retourne le plan d'allocation
 */
@RestController
@RequestMapping("/api/gardens")
@RequiredArgsConstructor
@Tag(name = "Optimisation", description = "Planification optimale du potager par programmation linéaire")
@SecurityRequirement(name = "bearerAuth")
public class OptimizationController {

    private final GardenOptimizerService gardenOptimizerService;

    @Operation(
        summary = "Optimiser le jardin",
        description = """
            Lance le solveur LP (Apache Commons Math — SimplexSolver) sur le profil jardin.
            
            **Modèle** : maximise Z = Σ(Y_i × Cal_i × P_i × x_i) sous contraintes :
            - C1 : surface totale ≤ S
            - C2 : calories produites ≥ foyer × 2500 × 365
            - C3 : monoculture ≤ 30% de la surface
            
            **HTTP 422** : si la surface est insuffisante pour atteindre l'autonomie calorique.
            """
    )
    @PostMapping("/{id}/optimize")
    public ResponseEntity<OptimizationResultDto> optimize(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {

        OptimizationResultDto result = gardenOptimizerService.optimize(id, userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }
}
