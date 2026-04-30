package com.potagerai.exception;

import lombok.Getter;

/**
 * Levée par GardenOptimizerService quand la surface du jardin est trop petite
 * pour satisfaire la contrainte calorique minimale (C2 du modèle LP).
 *
 * Retourne HTTP 422 avec la surface minimale calculée.
 */
@Getter
public class NoFeasibleSolutionException extends RuntimeException {

    private final double requiredSurfaceM2;

    public NoFeasibleSolutionException(double requiredSurfaceM2) {
        super(String.format(
                "Surface insuffisante. Surface minimale requise : %.1f m²", requiredSurfaceM2));
        this.requiredSurfaceM2 = requiredSurfaceM2;
    }
}
