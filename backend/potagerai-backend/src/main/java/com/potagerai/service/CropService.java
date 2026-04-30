package com.potagerai.service;

import com.potagerai.domain.crop.Crop;
import com.potagerai.domain.crop.CropRepository;
import com.potagerai.domain.crop.NutritionalProfile;
import com.potagerai.dto.crop.CropDto;
import com.potagerai.dto.crop.NutritionalProfileDto;
import com.potagerai.dto.crop.Season;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class CropService {

    private final CropRepository cropRepository;

    @Transactional(readOnly = true)
    public List<CropDto> findAll() {
        return cropRepository.findAllWithNutritionalProfile()
                .stream()
                .map(this::toDto)
                .toList();
    }

    /**
     * Retourne les cultures dont la fenêtre de semis chevauche la saison donnée.
     * Si {@code season} est {@code null} ou {@code TOUTE_ANNEE}, retourne toutes les cultures.
     */
    @Transactional(readOnly = true)
    public List<CropDto> findBySeason(Season season) {
        if (season == null || season == Season.TOUTE_ANNEE) {
            return findAll();
        }
        return cropRepository.findAvailableInSeason(season.monthStart, season.monthEnd, season.wrapsAround)
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public CropDto findById(Long id) {
        return cropRepository.findByIdWithNutritionalProfile(id)
                .map(this::toDto)
                .orElseThrow(() -> new NoSuchElementException("Culture introuvable : id=" + id));
    }

    private CropDto toDto(Crop crop) {
        NutritionalProfileDto nutritionDto = null;
        NutritionalProfile np = crop.getNutritionalProfile();
        if (np != null) {
            nutritionDto = new NutritionalProfileDto(
                    np.getCaloriesPer100g(),
                    np.getProteinsPer100g(),
                    np.getCarbsPer100g(),
                    np.getFatsPer100g(),
                    np.getFiberPer100g(),
                    np.getAndiScore());
        }

        return new CropDto(
                crop.getId(),
                crop.getCommonName(),
                crop.getScientificName(),
                crop.getBotanicalFamily(),
                crop.getRootDepthCm(),
                crop.getGrowingDaysMin(),
                crop.getGrowingDaysMax(),
                crop.getPlantSpacingM2(),
                crop.getStorageMonths(),
                crop.getFrostSensitive(),
                crop.getSowingMonthMin(),
                crop.getSowingMonthMax(),
                nutritionDto);
    }
}
