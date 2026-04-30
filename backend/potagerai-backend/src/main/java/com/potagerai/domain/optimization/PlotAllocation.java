package com.potagerai.domain.optimization;

import com.potagerai.domain.crop.Crop;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "plot_allocations")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlotAllocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "result_id", nullable = false)
    private OptimizationResult result;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "crop_id", nullable = false)
    private Crop crop;

    @NotNull
    @Column(name = "allocated_surface_m2", nullable = false, precision = 10, scale = 4)
    private BigDecimal allocatedSurfaceM2;

    @Column(name = "estimated_yield_kg", precision = 10, scale = 3)
    private BigDecimal estimatedYieldKg;

    @Column(name = "estimated_calories", precision = 15, scale = 2)
    private BigDecimal estimatedCalories;
}
