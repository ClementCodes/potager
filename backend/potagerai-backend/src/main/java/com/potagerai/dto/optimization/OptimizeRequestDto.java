package com.potagerai.dto.optimization;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

import java.util.List;

/**
 * Corps optionnel de {@code POST /api/gardens/{id}/optimize}.
 *
 * <p>Si {@code selectedCropIds} est {@code null} ou vide, l'optimiseur utilise
 * toutes les cultures disponibles pour le pays/zone.
 *
 * @param selectedCropIds identifiants des cultures à inclure dans l'optimisation
 */
public record OptimizeRequestDto(
    @Size(max = 50, message = "Maximum 50 cultures sélectionnables")
    List<@Min(value = 1, message = "ID de culture invalide") Long> selectedCropIds
) {}
