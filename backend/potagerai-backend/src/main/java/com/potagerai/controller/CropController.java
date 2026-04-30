package com.potagerai.controller;

import com.potagerai.dto.crop.CropDto;
import com.potagerai.service.CropService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Endpoints cultures (lecture seule — données seed).
 *
 * GET /api/crops        → liste toutes les cultures + profil nutritionnel
 * GET /api/crops/{id}   → détail d'une culture
 */
@RestController
@RequestMapping("/api/crops")
@RequiredArgsConstructor
@Tag(name = "Cultures", description = "Catalogue des cultures potagères disponibles")
@SecurityRequirement(name = "bearerAuth")
public class CropController {

    private final CropService cropService;

    @Operation(summary = "Lister les cultures", description = "Retourne les 14 cultures avec leur profil nutritionnel.")
    @GetMapping
    public ResponseEntity<List<CropDto>> findAll() {
        return ResponseEntity.ok(cropService.findAll());
    }

    @Operation(summary = "Détail d'une culture")
    @GetMapping("/{id}")
    public ResponseEntity<CropDto> findById(@PathVariable Long id) {
        return ResponseEntity.ok(cropService.findById(id));
    }
}
