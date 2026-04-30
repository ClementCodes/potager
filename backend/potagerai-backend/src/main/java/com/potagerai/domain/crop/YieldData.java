package com.potagerai.domain.crop;

import com.potagerai.domain.climate.ClimateZone;
import com.potagerai.domain.country.Country;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "yield_data")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class YieldData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "crop_id", nullable = false)
    private Crop crop;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "country_iso", nullable = false)
    private Country country;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "climate_zone_code")
    private ClimateZone climateZone;

    @Column(name = "yield_min_kg_per_m2", precision = 8, scale = 3)
    private BigDecimal yieldMinKgPerM2;

    @Column(name = "yield_max_kg_per_m2", precision = 8, scale = 3)
    private BigDecimal yieldMaxKgPerM2;

    @NotNull
    @Column(name = "yield_average_kg_per_m2", nullable = false, precision = 8, scale = 3)
    private BigDecimal yieldAverageKgPerM2;

    @Column(name = "cultivation_method", nullable = false, length = 20)
    private String cultivationMethod;
}
