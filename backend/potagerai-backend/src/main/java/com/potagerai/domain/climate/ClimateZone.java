package com.potagerai.domain.climate;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "climate_zones")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClimateZone {

    @Id
    @Column(length = 10)
    private String code;

    @NotBlank
    @Column(nullable = false, length = 100)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "yield_multiplier", nullable = false, precision = 4, scale = 2)
    private BigDecimal yieldMultiplier;
}
