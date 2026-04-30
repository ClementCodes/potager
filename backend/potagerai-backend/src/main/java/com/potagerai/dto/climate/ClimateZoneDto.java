package com.potagerai.dto.climate;

import java.math.BigDecimal;

public record ClimateZoneDto(
    String code,
    String name,
    String description,
    BigDecimal yieldMultiplier
) {}
