package com.potagerai.service;

import com.potagerai.domain.climate.ClimateZoneRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Ajuste le rendement de base (référence zone FR-OCC, multiplicateur = 1.00)
 * selon la zone climatique réelle de l'utilisateur.
 *
 * Formule : yield_adjusted = baseYield × zone.yieldMultiplier
 *
 * Zones Phase 1 :
 *   FR-OCC → ×1.00   FR-MED → ×1.20
 *   FR-CON → ×0.85   FR-MON → ×0.65
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ClimateAdjustmentService {

    private final ClimateZoneRepository climateZoneRepository;

    /**
     * @param baseYieldKgPerM2 rendement base (zone de référence FR-OCC)
     * @param climateZoneCode  code zone de l'utilisateur
     * @return rendement ajusté en kg/m²
     */
    public double adjust(double baseYieldKgPerM2, String climateZoneCode) {
        return climateZoneRepository.findById(climateZoneCode)
                .map(zone -> {
                    double multiplier = zone.getYieldMultiplier().doubleValue();
                    double adjusted = baseYieldKgPerM2 * multiplier;
                    log.debug("Zone {} : rendement {} × {} = {}", climateZoneCode,
                            baseYieldKgPerM2, multiplier, adjusted);
                    return adjusted;
                })
                .orElseGet(() -> {
                    log.warn("Zone climatique inconnue '{}', multiplicateur = 1.0 appliqué", climateZoneCode);
                    return baseYieldKgPerM2;
                });
    }
}
