package com.potagerai.domain.crop;

import com.potagerai.domain.country.Country;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "consumption_data")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConsumptionData {

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

    @Column(name = "kg_per_capita_per_year", precision = 10, scale = 3)
    private BigDecimal kgPerCapitaPerYear;

    @NotNull
    @Column(name = "preference_weight", nullable = false, precision = 6, scale = 4)
    private BigDecimal preferenceWeight;

    @Column(name = "data_year")
    private Integer dataYear;

    @Column(name = "data_source", nullable = false, length = 50)
    private String dataSource;
}
