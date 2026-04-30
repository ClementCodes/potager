package com.potagerai.domain.crop;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "nutritional_profiles")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NutritionalProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "crop_id", nullable = false, unique = true)
    private Crop crop;

    @Column(name = "calories_per_100g", precision = 8, scale = 2)
    private BigDecimal caloriesPer100g;

    @Column(name = "proteins_per_100g", precision = 8, scale = 2)
    private BigDecimal proteinsPer100g;

    @Column(name = "carbs_per_100g", precision = 8, scale = 2)
    private BigDecimal carbsPer100g;

    @Column(name = "fats_per_100g", precision = 8, scale = 2)
    private BigDecimal fatsPer100g;

    @Column(name = "fiber_per_100g", precision = 8, scale = 2)
    private BigDecimal fiberPer100g;

    @Column(name = "andi_score")
    private Integer andiScore;
}
