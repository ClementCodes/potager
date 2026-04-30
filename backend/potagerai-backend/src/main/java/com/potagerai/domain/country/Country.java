package com.potagerai.domain.country;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
@Table(name = "countries")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Country {

    @Id
    @Column(name = "iso_code", length = 3)
    private String isoCode;

    @NotBlank
    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "primary_koppen_zone", length = 10)
    private String primaryKoppenZone;

    @Column(name = "fr_climate_zone", length = 10)
    private String frClimateZone;
}
