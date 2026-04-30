package com.potagerai.controller;

import com.potagerai.domain.climate.ClimateZone;
import com.potagerai.domain.climate.ClimateZoneRepository;
import com.potagerai.dto.climate.ClimateZoneDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Comparator;
import java.util.List;

/**
 * Zones climatiques disponibles (lecture seule — données seed).
 *
 * GET /api/climate-zones → liste toutes les zones avec leur multiplicateur de rendement
 */
@RestController
@RequestMapping("/api/climate-zones")
@RequiredArgsConstructor
@Tag(name = "Zones climatiques", description = "Catalogue des zones climatiques et leurs multiplicateurs de rendement")
public class ClimateZoneController {

    private final ClimateZoneRepository climateZoneRepository;

    @Operation(summary = "Lister les zones climatiques")
    @GetMapping
    public ResponseEntity<List<ClimateZoneDto>> findAll() {
        List<ClimateZoneDto> zones = climateZoneRepository.findAll().stream()
                .sorted(Comparator.comparing(ClimateZone::getCode))
                .map(z -> new ClimateZoneDto(z.getCode(), z.getName(), z.getDescription(), z.getYieldMultiplier()))
                .toList();
        return ResponseEntity.ok(zones);
    }
}
