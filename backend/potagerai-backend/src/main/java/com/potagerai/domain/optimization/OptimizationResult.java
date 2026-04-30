package com.potagerai.domain.optimization;

import com.potagerai.domain.garden.GardenProfile;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "optimization_results")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OptimizationResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "garden_profile_id", nullable = false)
    private GardenProfile gardenProfile;

    @Column(name = "computed_at", nullable = false)
    private LocalDateTime computedAt;

    @Column(name = "total_calories_produced", precision = 15, scale = 2)
    private BigDecimal totalCaloriesProduced;

    @Column(name = "calorie_target_annual", precision = 15, scale = 2)
    private BigDecimal calorieTargetAnnual;

    @Column(name = "self_sufficiency_percent", precision = 5, scale = 2)
    private BigDecimal selfSufficiencyPercent;

    @OneToMany(mappedBy = "result", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<PlotAllocation> plotAllocations = new ArrayList<>();
}
