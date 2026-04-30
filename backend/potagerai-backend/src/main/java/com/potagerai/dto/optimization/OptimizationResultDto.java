package com.potagerai.dto.optimization;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OptimizationResultDto(
    Long id,
    Long gardenProfileId,
    LocalDateTime computedAt,
    BigDecimal totalCaloriesProduced,
    BigDecimal calorieTargetAnnual,
    BigDecimal selfSufficiencyPercent,
    List<PlotAllocationDto> plotAllocations
) {}
