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
    NutritionalProfileDto nutritionalProfile
) {}
