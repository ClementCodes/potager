package com.potagerai.dto.crop;

import java.math.BigDecimal;

public record NutritionalProfileDto(
    BigDecimal caloriesPer100g,
    BigDecimal proteinsPer100g,
    BigDecimal carbsPer100g,
    BigDecimal fatsPer100g,
    BigDecimal fiberPer100g,
    Integer andiScore
) {}
