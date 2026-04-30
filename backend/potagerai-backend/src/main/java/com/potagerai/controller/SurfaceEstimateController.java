package com.potagerai.controller;

import com.potagerai.dto.estimate.SurfaceEstimateDto;
import com.potagerai.service.GardenOptimizerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Endpoint public permettant d'estimer la surface de jardin nécessaire
 * AVANT de créer un profil.
 *
 * <p>Aucune authentification requise : utilisé dès l'affichage du formulaire
 * de création pour orienter l'utilisateur.
 */
@Tag(name = "Surface Estimate", description = "Estimation de surface pour l'autosuffisance")
@RestController
@RequestMapping("/api/v1/surface-estimate")
@RequiredArgsConstructor
@Validated
public class SurfaceEstimateController {

    private final GardenOptimizerService gardenOptimizerService;

    @Operation(
        summary = "Estimer la surface nécessaire",
        description = "Calcule la surface minimale de jardin pour atteindre l'autosuffisance " +
                      "calorique (2500 kcal/pers/jour) selon la taille du foyer et la zone climatique."
    )
    @GetMapping
    public SurfaceEstimateDto estimate(
            @Parameter(description = "Nombre de personnes à nourrir", example = "4")
            @RequestParam @Min(1) @Max(20) int householdSize,
            @Parameter(description = "Code de la zone climatique (ex. FR-OCC)", example = "FR-OCC")
            @RequestParam String climateZoneCode) {

        return gardenOptimizerService.estimateSurface(householdSize, climateZoneCode);
    }
}
