package com.potagerai.domain.crop;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface YieldDataRepository extends JpaRepository<YieldData, Long> {

    /**
     * Charge le rendement de base (zone FR-OCC) pour un crop donné.
     * Utilisé par ClimateAdjustmentService pour le calcul ajusté.
     */
    Optional<YieldData> findByCropIdAndCountryIsoCodeAndClimateZoneCode(
            Long cropId, String countryIsoCode, String climateZoneCode);

    List<YieldData> findByCropIdAndCountryIsoCode(Long cropId, String countryIsoCode);
}
