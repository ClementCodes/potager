package com.potagerai;

import com.potagerai.domain.climate.ClimateZone;
import com.potagerai.domain.climate.ClimateZoneRepository;
import com.potagerai.domain.country.Country;
import com.potagerai.domain.crop.ConsumptionData;
import com.potagerai.domain.crop.ConsumptionDataRepository;
import com.potagerai.domain.crop.Crop;
import com.potagerai.domain.crop.CropRepository;
import com.potagerai.domain.crop.NutritionalProfile;
import com.potagerai.domain.crop.YieldData;
import com.potagerai.domain.crop.YieldDataRepository;
import com.potagerai.domain.garden.GardenProfile;
import com.potagerai.domain.optimization.OptimizationResult;
import com.potagerai.domain.optimization.OptimizationResultRepository;
import com.potagerai.dto.optimization.OptimizationResultDto;
import com.potagerai.exception.NoFeasibleSolutionException;
import com.potagerai.service.ClimateAdjustmentService;
import com.potagerai.domain.garden.GardenProfileRepository;
import com.potagerai.service.GardenOptimizerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Tests unitaires du solveur LP.
 *
 * Les repositories sont mockés. Le SimplexSolver tourne réellement.
 * Les cultures de test ont des rendements artificiellement élevés
 * pour garantir la faisabilité avec une petite surface.
 */
@ExtendWith(MockitoExtension.class)
class GardenOptimizerServiceTest {

    // -------------------------------------------------------------------------
    // Constantes de test
    // -------------------------------------------------------------------------
    private static final String USER_EMAIL    = "optimizer@potagerai.com";
    private static final Long   GARDEN_ID     = 1L;
    private static final String COUNTRY_ISO   = "FRA";
    private static final String ZONE_CODE     = "FR-OCC";
    private static final String REF_ZONE      = "FR-OCC";

    // Surface garantissant la faisabilité LP :
    // target 1 personne = 2500 × 365 = 912 500 kcal/an
    // crop A : yield=100 kg/m², 100 kcal/100g → 1000 kcal/kg → 100 000 kcal/m²
    // avec C3 (30% de 100m²=30m²) : 30 × 100 000 = 3 000 000 kcal >> 912 500 OK
    private static final double SURFACE_OK    = 100.0;
    private static final double SURFACE_KO    =   0.5;  // clairement insuffisante

    // -------------------------------------------------------------------------
    // Mocks
    // -------------------------------------------------------------------------
    @Mock CropRepository               cropRepository;
    @Mock YieldDataRepository          yieldDataRepository;
    @Mock ConsumptionDataRepository    consumptionDataRepository;
    @Mock ClimateAdjustmentService     climateAdjustmentService;
    @Mock OptimizationResultRepository optimizationResultRepository;
    @Mock GardenProfileRepository      gardenProfileRepository;

    @InjectMocks GardenOptimizerService service;

    // -------------------------------------------------------------------------
    // Fixtures
    // -------------------------------------------------------------------------
    private GardenProfile garden;
    private List<Crop>    crops;

    @BeforeEach
    void setUp() {
        // Pays et zone climatique
        Country country = Country.builder()
                .isoCode(COUNTRY_ISO).name("France")
                .primaryKoppenZone("Cfb").frClimateZone("FR-CON")
                .build();

        ClimateZone zone = ClimateZone.builder()
                .code(ZONE_CODE).name("Océanique")
                .yieldMultiplier(BigDecimal.ONE)
                .build();

        // Utilisateur (minimal — seul l'email est vérifié)
        com.potagerai.domain.user.User user = com.potagerai.domain.user.User.builder()
                .id(1L).email(USER_EMAIL).password("hashed")
                .build();

        // Jardin — surface par défaut OK (peut être surchargée par test)
        garden = GardenProfile.builder()
                .id(GARDEN_ID)
                .user(user)
                .totalSurfaceM2(BigDecimal.valueOf(SURFACE_OK))
                .householdSize(1)
                .climateZone(zone)
                .country(country)
                .cultivationMethod("OPEN_AIR")
                .build();

        // 3 cultures fictives avec rendement élevé
        Crop cropA = buildCrop(1L, "Culture A", 100.0);
        Crop cropB = buildCrop(2L, "Culture B", 80.0);
        Crop cropC = buildCrop(3L, "Culture C", 60.0);
        crops = List.of(cropA, cropB, cropC);
    }

    // -------------------------------------------------------------------------
    // Tests
    // -------------------------------------------------------------------------

    @Test
    void optimize_sufficientSurface_shouldReturnResultWithAllocations() {
        stubRepositories(SURFACE_OK);

        OptimizationResultDto result = service.optimize(GARDEN_ID, USER_EMAIL);

        assertThat(result).isNotNull();
        assertThat(result.selfSufficiencyPercent())
                .isGreaterThanOrEqualTo(BigDecimal.valueOf(99.0));
        assertThat(result.plotAllocations()).isNotEmpty();
        assertThat(result.totalCaloriesProduced())
                .isGreaterThanOrEqualTo(result.calorieTargetAnnual());
    }

    @Test
    void optimize_insufficientSurface_shouldThrowNoFeasibleSolutionException() {
        // Surface trop petite pour atteindre la cible calorique
        garden = GardenProfile.builder()
                .id(GARDEN_ID)
                .user(garden.getUser())
                .totalSurfaceM2(BigDecimal.valueOf(SURFACE_KO))
                .householdSize(1)
                .climateZone(garden.getClimateZone())
                .country(garden.getCountry())
                .cultivationMethod("OPEN_AIR")
                .build();

        stubRepositories(SURFACE_KO);

        assertThatThrownBy(() -> service.optimize(GARDEN_ID, USER_EMAIL))
                .isInstanceOf(NoFeasibleSolutionException.class)
                .hasMessageContaining("Surface insuffisante")
                .extracting(e -> ((NoFeasibleSolutionException) e).getRequiredSurfaceM2())
                .satisfies(required -> assertThat((double) required).isGreaterThan(SURFACE_KO));
    }

    @Test
    void optimize_gardenNotFound_shouldThrowNoSuchElement() {
        when(gardenProfileRepository.findByIdWithDetails(GARDEN_ID))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.optimize(GARDEN_ID, USER_EMAIL))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void optimize_gardenBelongsToOtherUser_shouldThrowNoSuchElement() {
        when(gardenProfileRepository.findByIdWithDetails(GARDEN_ID))
                .thenReturn(Optional.of(garden));

        assertThatThrownBy(() -> service.optimize(GARDEN_ID, "other@potagerai.com"))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void optimize_bigHousehold_shouldScaleCalorieTarget() {
        // 4 personnes → cible × 4 — nécessite plus de surface
        garden = GardenProfile.builder()
                .id(GARDEN_ID)
                .user(garden.getUser())
                .totalSurfaceM2(BigDecimal.valueOf(500.0))
                .householdSize(4)
                .climateZone(garden.getClimateZone())
                .country(garden.getCountry())
                .cultivationMethod("OPEN_AIR")
                .build();

        stubRepositories(500.0);

        OptimizationResultDto result = service.optimize(GARDEN_ID, USER_EMAIL);

        // Cible 4 pers. = 4 × 912 500 = 3 650 000 kcal
        assertThat(result.calorieTargetAnnual())
                .isEqualByComparingTo(BigDecimal.valueOf(3_650_000.00));
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    /**
     * Configure tous les mocks nécessaires à l'exécution du solveur.
     * Les 3 cultures ont des rendements élevés pour garantir la faisabilité
     * si la surface est suffisante.
     */
    private void stubRepositories(double surface) {
        when(gardenProfileRepository.findByIdWithDetails(GARDEN_ID))
                .thenReturn(Optional.of(garden));

        when(cropRepository.findAllWithNutritionalProfile())
                .thenReturn(crops);

        for (Crop crop : crops) {
            // Rendement de base (zone FR-OCC)
            YieldData yd = buildYield(crop, 100.0); // 100 kg/m²
            when(yieldDataRepository
                    .findByCropIdAndCountryIsoCodeAndClimateZoneCode(
                            crop.getId(), COUNTRY_ISO, REF_ZONE))
                    .thenReturn(Optional.of(yd));

            // Ajustement climatique → identité (×1.0)
            when(climateAdjustmentService.adjust(anyDouble(), eq(ZONE_CODE)))
                    .thenAnswer(inv -> inv.getArgument(0));

            // Poids de consommation
            ConsumptionData cd = buildConsumption(crop, 0.33);
            when(consumptionDataRepository
                    .findByCropIdAndCountryIsoCode(crop.getId(), COUNTRY_ISO))
                    .thenReturn(Optional.of(cd));
        }

        // Le save renvoie l'objet passé (sans auto-increment id)
        when(optimizationResultRepository.save(any(OptimizationResult.class)))
                .thenAnswer(inv -> {
                    OptimizationResult r = inv.getArgument(0);
                    // Simule l'id généré
                    try {
                        var field = OptimizationResult.class.getDeclaredField("id");
                        field.setAccessible(true);
                        field.set(r, 42L);
                    } catch (Exception ignored) {}
                    return r;
                });
    }

    private Crop buildCrop(long id, String name, double cal100g) {
        NutritionalProfile np = NutritionalProfile.builder()
                .caloriesPer100g(BigDecimal.valueOf(cal100g))
                .proteinsPer100g(BigDecimal.valueOf(5.0))
                .carbsPer100g(BigDecimal.valueOf(15.0))
                .fatsPer100g(BigDecimal.valueOf(1.0))
                .fiberPer100g(BigDecimal.valueOf(2.0))
                .andiScore(200)
                .build();

        Crop crop = Crop.builder()
                .id(id)
                .commonName(name)
                .scientificName("Species " + id)
                .botanicalFamily("TestFamily")
                .rootDepthCm(BigDecimal.valueOf(30))
                .growingDaysMin(60)
                .growingDaysMax(90)
                .build();

        np.setCrop(crop);
        crop.setNutritionalProfile(np);
        return crop;
    }

    private YieldData buildYield(Crop crop, double avgKgM2) {
        return YieldData.builder()
                .id(crop.getId() * 10)
                .crop(crop)
                .yieldMinKgPerM2(BigDecimal.valueOf(avgKgM2 * 0.8))
                .yieldMaxKgPerM2(BigDecimal.valueOf(avgKgM2 * 1.2))
                .yieldAverageKgPerM2(BigDecimal.valueOf(avgKgM2))
                .cultivationMethod("OPEN_AIR")
                .build();
    }

    private ConsumptionData buildConsumption(Crop crop, double weight) {
        return ConsumptionData.builder()
                .id(crop.getId() * 100)
                .crop(crop)
                .preferenceWeight(BigDecimal.valueOf(weight))
                .dataYear(2023)
                .dataSource("TEST")
                .build();
    }
}
