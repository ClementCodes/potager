package com.potagerai.controller;

import com.potagerai.dto.garden.CreateGardenRequest;
import com.potagerai.dto.garden.GardenProfileDto;
import com.potagerai.service.GardenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Endpoints de gestion des profils jardin.
 *
 * POST   /api/gardens       → créer un profil jardin
 * GET    /api/gardens        → lister ses jardins
 * GET    /api/gardens/{id}   → détail d'un jardin
 */
@RestController
@RequestMapping("/api/gardens")
@RequiredArgsConstructor
@Tag(name = "Jardins", description = "Gestion des profils jardin utilisateur")
@SecurityRequirement(name = "bearerAuth")
public class GardenController {

    private final GardenService gardenService;

    @Operation(summary = "Créer un profil jardin")
    @PostMapping
    public ResponseEntity<GardenProfileDto> create(
            @Valid @RequestBody CreateGardenRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        GardenProfileDto dto = gardenService.create(userDetails.getUsername(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @Operation(summary = "Lister mes jardins")
    @GetMapping
    public ResponseEntity<List<GardenProfileDto>> findAll(
            @AuthenticationPrincipal UserDetails userDetails) {

        return ResponseEntity.ok(gardenService.findAllByUser(userDetails.getUsername()));
    }

    @Operation(summary = "Détail d'un jardin")
    @GetMapping("/{id}")
    public ResponseEntity<GardenProfileDto> findById(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {

        return ResponseEntity.ok(gardenService.findById(id, userDetails.getUsername()));
    }
}
