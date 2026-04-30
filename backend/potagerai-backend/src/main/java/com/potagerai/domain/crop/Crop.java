package com.potagerai.domain.crop;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "crops")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Crop {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(name = "common_name", nullable = false, length = 100)
    private String commonName;

    @Column(name = "scientific_name", length = 150)
    private String scientificName;

    @Column(name = "botanical_family", length = 100)
    private String botanicalFamily;

    @Column(name = "root_depth_cm", precision = 6, scale = 1)
    private BigDecimal rootDepthCm;

    @Column(name = "growing_days_min")
    private Integer growingDaysMin;

    @Column(name = "growing_days_max")
    private Integer growingDaysMax;

    @Column(name = "plant_spacing_m2", precision = 6, scale = 4)
    private BigDecimal plantSpacingM2;

    @Column(name = "storage_months")
    private Integer storageMonths;

    @Column(name = "frost_sensitive")
    private Boolean frostSensitive;

    @Column(name = "sowing_month_min")
    private Integer sowingMonthMin;

    @Column(name = "sowing_month_max")
    private Integer sowingMonthMax;

    @OneToOne(mappedBy = "crop", fetch = FetchType.LAZY)
    private NutritionalProfile nutritionalProfile;
}
