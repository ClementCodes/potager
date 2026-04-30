package com.potagerai.domain.garden;

import com.potagerai.domain.climate.ClimateZone;
import com.potagerai.domain.country.Country;
import com.potagerai.domain.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "garden_profiles")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GardenProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotNull
    @Column(name = "total_surface_m2", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalSurfaceM2;

    @Min(1)
    @Column(name = "household_size", nullable = false)
    private Integer householdSize;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "climate_zone_code")
    private ClimateZone climateZone;

    @Column(name = "cultivation_method", nullable = false, length = 20)
    private String cultivationMethod;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "country_iso_code")
    private Country country;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
