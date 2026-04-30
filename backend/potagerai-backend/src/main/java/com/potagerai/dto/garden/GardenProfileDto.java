package com.potagerai.dto.garden;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record GardenProfileDto(
    Long id,
    BigDecimal totalSurfaceM2,
    Integer householdSize,
    String climateZoneCode,
    String climateZoneName,
    String countryIsoCode,
    String countryName,
    String cultivationMethod,
    LocalDateTime createdAt
) {}
