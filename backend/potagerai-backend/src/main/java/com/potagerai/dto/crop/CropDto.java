package com.potagerai.dto.crop;

import java.math.BigDecimal;

public record CropDto(
    Long id,
    String commonName,
    String scientificName,
    String botanicalFamily,
    BigDecimal rootDepthCm,
    Integer growingDaysMin,
    Integer growingDaysMax,
    BigDecimal plantSpacingM2,
    Integer storageMonths,
    Boolean frostSensitive,
    Integer sowingMonthMin,
    Integer sowingMonthMax,
    NutritionalProfileDto nutritionalProfile
) {}
