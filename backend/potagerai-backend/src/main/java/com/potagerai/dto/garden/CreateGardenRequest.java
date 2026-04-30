package com.potagerai.dto.garden;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record CreateGardenRequest(

    @NotNull(message = "La surface totale est obligatoire")
    @DecimalMin(value = "1.0", message = "La surface minimale est 1 m²")
    @DecimalMax(value = "10000.0", message = "La surface maximale est 10 000 m²")
    BigDecimal totalSurfaceM2,

    @NotNull(message = "La taille du foyer est obligatoire")
    @Min(value = 1, message = "Le foyer doit compter au moins 1 personne")
    @Max(value = 20, message = "Le foyer ne peut pas dépasser 20 personnes")
    Integer householdSize,

    @NotBlank(message = "Le code de zone climatique est obligatoire")
    String climateZoneCode,

    @NotBlank(message = "Le code pays est obligatoire")
    String countryIsoCode,

    String cultivationMethod  // optionnel, défaut = OPEN_AIR
) {}
