package com.potagerai.service;

import com.potagerai.domain.climate.ClimateZone;
import com.potagerai.domain.climate.ClimateZoneRepository;
import com.potagerai.domain.country.Country;
import com.potagerai.domain.country.CountryRepository;
import com.potagerai.domain.garden.GardenProfile;
import com.potagerai.domain.garden.GardenProfileRepository;
import com.potagerai.domain.user.User;
import com.potagerai.domain.user.UserRepository;
import com.potagerai.dto.garden.CreateGardenRequest;
import com.potagerai.dto.garden.GardenProfileDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class GardenService {

    private final GardenProfileRepository gardenProfileRepository;
    private final UserRepository userRepository;
    private final ClimateZoneRepository climateZoneRepository;
    private final CountryRepository countryRepository;

    @Transactional
    public GardenProfileDto create(String userEmail, CreateGardenRequest request) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur introuvable : " + userEmail));

        ClimateZone climateZone = climateZoneRepository.findById(request.climateZoneCode())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Zone climatique inconnue : " + request.climateZoneCode()));

        Country country = countryRepository.findById(request.countryIsoCode())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Pays inconnu : " + request.countryIsoCode()));

        String cultivationMethod = (request.cultivationMethod() != null
                && !request.cultivationMethod().isBlank())
                ? request.cultivationMethod().toUpperCase()
                : "OPEN_AIR";

        GardenProfile profile = GardenProfile.builder()
                .user(user)
                .totalSurfaceM2(request.totalSurfaceM2())
                .householdSize(request.householdSize())
                .climateZone(climateZone)
                .country(country)
                .cultivationMethod(cultivationMethod)
                .build();

        GardenProfile saved = gardenProfileRepository.save(profile);
        return toDto(saved);
    }

    @Transactional(readOnly = true)
    public GardenProfileDto findById(Long id, String userEmail) {
        GardenProfile profile = gardenProfileRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new NoSuchElementException("Profil jardin introuvable : id=" + id));

        // Un utilisateur ne peut accéder qu'à ses propres jardins
        if (!profile.getUser().getEmail().equals(userEmail)) {
            throw new NoSuchElementException("Profil jardin introuvable : id=" + id);
        }

        return toDto(profile);
    }

    @Transactional(readOnly = true)
    public List<GardenProfileDto> findAllByUser(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur introuvable : " + userEmail));

        return gardenProfileRepository.findByUserId(user.getId())
                .stream()
                .map(this::toDto)
                .toList();
    }

    private GardenProfileDto toDto(GardenProfile p) {
        return new GardenProfileDto(
                p.getId(),
                p.getTotalSurfaceM2(),
                p.getHouseholdSize(),
                p.getClimateZone() != null ? p.getClimateZone().getCode() : null,
                p.getClimateZone() != null ? p.getClimateZone().getName() : null,
                p.getCountry() != null ? p.getCountry().getIsoCode() : null,
                p.getCountry() != null ? p.getCountry().getName() : null,
                p.getCultivationMethod(),
                p.getCreatedAt());
    }
}
