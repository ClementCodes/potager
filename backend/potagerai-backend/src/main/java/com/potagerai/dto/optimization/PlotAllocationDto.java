package com.potagerai.dto.optimization;

import java.math.BigDecimal;

public record PlotAllocationDto(
    Long cropId,
    String cropName,
    String botanicalFamily,
    BigDecimal allocatedSurfaceM2,
    BigDecimal estimatedYieldKg,
    BigDecimal estimatedCalories,
    BigDecimal plantSpacingM2
) {}
