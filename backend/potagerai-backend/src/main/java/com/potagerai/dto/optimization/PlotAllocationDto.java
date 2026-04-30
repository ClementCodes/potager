package com.potagerai.dto.optimization;

import java.math.BigDecimal;

public record PlotAllocationDto(
    Long cropId,
    String cropName,
    BigDecimal allocatedSurfaceM2,
    BigDecimal estimatedYieldKg,
    BigDecimal estimatedCalories
) {}
