package com.potagerai.dto.estimate;

/**
 * Réponse de l'endpoint GET /api/v1/surface-estimate.
 *
 * @param householdSize      nombre de personnes à nourrir
 * @param climateZoneCode    code de la zone climatique choisie
 * @param climateZoneName    nom lisible de la zone
 * @param estimatedSurfaceM2 surface minimale estimée pour l'autosuffisance calorique (m²)
 * @param calorieTargetAnnual cible calorique annuelle du foyer (kcal)
 */
public record SurfaceEstimateDto(
    int householdSize,
    String climateZoneCode,
    String climateZoneName,
    long estimatedSurfaceM2,
    long calorieTargetAnnual
) {}
