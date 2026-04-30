package com.potagerai.service;

import com.potagerai.domain.climate.ClimateZone;
import com.potagerai.domain.climate.ClimateZoneRepository;
import com.potagerai.domain.crop.Crop;
import com.potagerai.domain.crop.ConsumptionDataRepository;
import com.potagerai.domain.crop.CropRepository;
import com.potagerai.domain.crop.NutritionalProfile;
import com.potagerai.domain.crop.YieldDataRepository;
import com.potagerai.domain.garden.GardenProfile;
import com.potagerai.domain.garden.GardenProfileRepository;
import com.potagerai.domain.optimization.OptimizationResult;
import com.potagerai.domain.optimization.OptimizationResultRepository;
import com.potagerai.domain.optimization.PlotAllocation;
import com.potagerai.dto.estimate.SurfaceEstimateDto;
import com.potagerai.dto.optimization.OptimizationResultDto;
import com.potagerai.dto.optimization.PlotAllocationDto;
import com.potagerai.exception.NoFeasibleSolutionException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.math3.optim.MaxIter;
import org.apache.commons.math3.optim.PointValuePair;
import org.apache.commons.math3.optim.linear.LinearConstraint;
import org.apache.commons.math3.optim.linear.LinearConstraintSet;
import org.apache.commons.math3.optim.linear.LinearObjectiveFunction;
import org.apache.commons.math3.optim.linear.NonNegativeConstraint;
import org.apache.commons.math3.optim.linear.PivotSelectionRule;
import org.apache.commons.math3.optim.linear.Relationship;
import org.apache.commons.math3.optim.linear.SimplexSolver;
import org.apache.commons.math3.optim.nonlinear.scalar.GoalType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * Cœur algorithmique de PotagerAI.
 *
 * Modèle de programmation linéaire (Phase 1 — Apache Commons Math 3.6.1) :
 *
 *   Maximiser  Z = Σ_i (Y_i × Cal_i × P_i × x_i)
 *
 *   Sous contraintes :
 *     C1  Σ x_i          ≤ S           (surface totale du jardin)
 *     C2  Σ (Y_i×Cal_i×x_i) ≥ T       (autonomie calorique T = n × 2500 × 365)
 *     C3  x_i            ≤ 0.30 × S   (max 30% par culture — anti-monoculture)
 *     C4  x_i            ≥ 0           (NonNegativeConstraint)
 *
 *   Où :
 *     x_i   = surface allouée en m² à la culture i     (variable)
 *     Y_i   = rendement ajusté kg/m² (base FR-OCC × multiplicateur zone)
 *     Cal_i = calories/kg = caloriesPer100g × 10
 *     P_i   = poids de préférence consommateur [0-1]
 *     S     = surface totale du jardin (m²)
 *     T     = kcal cible annuelle = householdSize × 2 500 × 365
 *     n     = nombre de personnes dans le foyer
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GardenOptimizerService {

    // -------------------------------------------------------------------------
    // Constantes du modèle LP
    // -------------------------------------------------------------------------
    private static final double DAILY_KCAL_PER_PERSON    = 2_500.0;
    private static final double DAYS_PER_YEAR            = 365.0;
    private static final double MONOCULTURE_MAX_RATIO    = 0.30;
    private static final int    MAX_LP_ITERATIONS        = 10_000;
    /** Zone de référence pour les rendements stockés en base (multiplicateur = 1.00) */
    private static final String REFERENCE_ZONE           = "FR-OCC";
    /** Seuil en dessous duquel une allocation est considérée nulle (m²) */
    private static final double NEGLIGIBLE_SURFACE_M2    = 1e-4;

    // -------------------------------------------------------------------------
    // Dépendances
    // -------------------------------------------------------------------------
    private final CropRepository               cropRepository;
    private final YieldDataRepository          yieldDataRepository;
    private final ConsumptionDataRepository    consumptionDataRepository;
    private final ClimateAdjustmentService     climateAdjustmentService;
    private final ClimateZoneRepository        climateZoneRepository;
    private final OptimizationResultRepository optimizationResultRepository;
    private final GardenProfileRepository      gardenProfileRepository;

    // -------------------------------------------------------------------------
    // Point d'entrée principal
    // -------------------------------------------------------------------------

    /**
     * Lance l'optimisation LP pour le jardin identifié et persiste le résultat.
     *
     * @param gardenProfileId identifiant du profil jardin
     * @param userEmail       email de l'utilisateur authentifié (contrôle d'accès)
     * @return DTO du résultat avec les allocations par culture
     * @throws NoSuchElementException       si le jardin n'existe pas ou n'appartient pas à l'utilisateur
     * @throws NoFeasibleSolutionException  si la surface est trop petite (HTTP 422)
     */
    @Transactional
    public OptimizationResultDto optimize(Long gardenProfileId, String userEmail) {

        // --- Chargement et contrôle d'accès ---------------------------------
        GardenProfile garden = gardenProfileRepository.findByIdWithDetails(gardenProfileId)
                .orElseThrow(() -> new NoSuchElementException(
                        "Profil jardin introuvable : id=" + gardenProfileId));

        if (!garden.getUser().getEmail().equals(userEmail)) {
            throw new NoSuchElementException("Profil jardin introuvable : id=" + gardenProfileId);
        }

        String countryIso      = garden.getCountry().getIsoCode();
        String climateZoneCode = garden.getClimateZone().getCode();
        double surfaceM2       = garden.getTotalSurfaceM2().doubleValue();
        int    householdSize   = garden.getHouseholdSize();
        double calorieTarget   = householdSize * DAILY_KCAL_PER_PERSON * DAYS_PER_YEAR;

        log.info("Optimisation jardin id={}, surface={}m², foyer={}, zone={}, cible={}kcal/an",
                gardenProfileId, surfaceM2, householdSize, climateZoneCode, calorieTarget);

        // --- Données agronomiques -------------------------------------------
        List<Crop> allCrops = cropRepository.findAllWithNutritionalProfile();
        List<CropData> cropDataList = buildCropDataList(allCrops, countryIso, climateZoneCode);

        if (cropDataList.isEmpty()) {
            throw new IllegalStateException(
                    "Aucune donnée agronomique disponible pour le pays " + countryIso);
        }

        // --- Construction du modèle LP --------------------------------------
        int n = cropDataList.size();
        PointValuePair solution = solveLp(n, cropDataList, surfaceM2, calorieTarget);

        // --- Persistance du résultat ----------------------------------------
        OptimizationResult result = buildResult(garden, solution.getPoint(),
                cropDataList, calorieTarget);
        OptimizationResult saved = optimizationResultRepository.save(result);

        log.info("Optimisation réussie : auto-suffisance = {}%", saved.getSelfSufficiencyPercent());
        return toDto(saved);
    }

    /**
     * Récupère la dernière optimisation persistée pour un jardin (sans recalcul).
     *
     * @return DTO du dernier résultat, ou {@link Optional#empty()} si aucun résultat n'existe
     * @throws NoSuchElementException si le jardin n'existe pas ou n'appartient pas à l'utilisateur
     */
    @Transactional(readOnly = true)
    public Optional<OptimizationResultDto> findLatest(Long gardenProfileId, String userEmail) {
        GardenProfile garden = gardenProfileRepository.findByIdWithDetails(gardenProfileId)
                .orElseThrow(() -> new NoSuchElementException(
                        "Profil jardin introuvable : id=" + gardenProfileId));

        if (!garden.getUser().getEmail().equals(userEmail)) {
            throw new NoSuchElementException("Profil jardin introuvable : id=" + gardenProfileId);
        }

        List<OptimizationResult> results = optimizationResultRepository
                .findByGardenProfileId(gardenProfileId);
        if (results.isEmpty()) {
            return Optional.empty();
        }
        // Recharge avec allocations + crop pour éviter LazyInitializationException
        OptimizationResult latest = optimizationResultRepository
                .findByIdWithAllocations(results.get(0).getId())
                .orElseThrow();
        return Optional.of(toDto(latest));
    }

    /**
     * Retourne toutes les optimisations pour un jardin, triées de la plus récente à la plus ancienne.
     *
     * @throws NoSuchElementException si le jardin n'existe pas ou n'appartient pas à l'utilisateur
     */
    @Transactional(readOnly = true)
    public List<OptimizationResultDto> findAll(Long gardenProfileId, String userEmail) {
        GardenProfile garden = gardenProfileRepository.findByIdWithDetails(gardenProfileId)
                .orElseThrow(() -> new NoSuchElementException(
                        "Profil jardin introuvable : id=" + gardenProfileId));

        if (!garden.getUser().getEmail().equals(userEmail)) {
            throw new NoSuchElementException("Profil jardin introuvable : id=" + gardenProfileId);
        }

        return optimizationResultRepository.findByGardenProfileId(gardenProfileId).stream()
                .map(r -> optimizationResultRepository.findByIdWithAllocations(r.getId()).orElseThrow())
                .map(this::toDto)
                .toList();
    }

    // -------------------------------------------------------------------------
    // Estimation de surface (endpoint public, avant création du jardin)
    // -------------------------------------------------------------------------

    /**
     * Calcule la surface minimale requise pour l'autosuffisance calorique d'un foyer.
     *
     * <p>Formule (identique à celle du 422 handler) :
     * <pre>
     *   T = householdSize × 2500 × 365   (kcal cible annuelle)
     *   maxKcalPerM2 = max_i(Y_i × Cal_i)   (meilleure culture ajustée pour la zone)
     *   S_min = T / (0.30 × maxKcalPerM2)   (contrainte anti-monoculture 30%)
     * </pre>
     *
     * @param householdSize   nombre de personnes à nourrir (≥ 1)
     * @param climateZoneCode code de la zone climatique (ex. "FR-OCC")
     * @return estimation de surface arrondie au m² supérieur
     * @throws IllegalArgumentException si la zone est inconnue ou si aucune donnée agronomique n'est disponible
     */
    @Transactional(readOnly = true)
    public SurfaceEstimateDto estimateSurface(int householdSize, String climateZoneCode) {
        ClimateZone zone = climateZoneRepository.findById(climateZoneCode)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Zone climatique inconnue : " + climateZoneCode));

        double calorieTarget = householdSize * DAILY_KCAL_PER_PERSON * DAYS_PER_YEAR;

        List<Crop> allCrops = cropRepository.findAllWithNutritionalProfile();
        List<CropData> cropDataList = buildCropDataList(allCrops, "FRA", climateZoneCode);

        if (cropDataList.isEmpty()) {
            throw new IllegalArgumentException(
                    "Aucune donnée agronomique disponible pour la zone " + climateZoneCode);
        }

        double maxKcalPerM2 = cropDataList.stream()
                .mapToDouble(cd -> cd.adjustedYield() * cd.calDensityPerKg())
                .max()
                .orElse(1.0);

        // Surface minimale compte tenu de la contrainte 30% max par culture
        double requiredSurface = calorieTarget / (MONOCULTURE_MAX_RATIO * maxKcalPerM2);
        long estimatedSurfaceM2 = (long) Math.ceil(requiredSurface);

        log.info("Estimation surface : {} personnes, zone={} → {} m²",
                householdSize, climateZoneCode, estimatedSurfaceM2);

        return new SurfaceEstimateDto(
                householdSize,
                climateZoneCode,
                zone.getName(),
                estimatedSurfaceM2,
                (long) calorieTarget
        );
    }

    // -------------------------------------------------------------------------
    // Construction des données par culture
    // -------------------------------------------------------------------------

    private List<CropData> buildCropDataList(List<Crop> crops,
                                              String countryIso,
                                              String climateZoneCode) {
        List<CropData> result = new ArrayList<>();

        for (Crop crop : crops) {
            // Rendement de base (zone de référence FR-OCC)
            var yieldOpt = yieldDataRepository
                    .findByCropIdAndCountryIsoCodeAndClimateZoneCode(
                            crop.getId(), countryIso, REFERENCE_ZONE);
            if (yieldOpt.isEmpty()) {
                log.debug("Pas de donnée de rendement pour culture={}, pays={}", crop.getId(), countryIso);
                continue;
            }

            // Profil nutritionnel
            NutritionalProfile np = crop.getNutritionalProfile();
            if (np == null || np.getCaloriesPer100g() == null) {
                log.debug("Profil nutritionnel manquant pour culture={}", crop.getId());
                continue;
            }

            // Poids de consommation
            var consOpt = consumptionDataRepository
                    .findByCropIdAndCountryIsoCode(crop.getId(), countryIso);
            if (consOpt.isEmpty()) {
                log.debug("Poids de consommation manquant pour culture={}, pays={}", crop.getId(), countryIso);
                continue;
            }

            double baseYield       = yieldOpt.get().getYieldAverageKgPerM2().doubleValue();
            double adjustedYield   = climateAdjustmentService.adjust(baseYield, climateZoneCode);
            double calDensityPerKg = np.getCaloriesPer100g().doubleValue() * 10.0; // kcal/100g → kcal/kg
            double prefWeight      = consOpt.get().getPreferenceWeight().doubleValue();

            result.add(new CropData(crop, adjustedYield, calDensityPerKg, prefWeight));
        }

        return result;
    }

    // -------------------------------------------------------------------------
    // Résolution LP — Apache Commons Math 3.6.1 SimplexSolver
    // -------------------------------------------------------------------------

    private PointValuePair solveLp(int n, List<CropData> cropDataList,
                                   double surfaceM2, double calorieTarget) {

        // Fonction objectif : maximiser Σ(Y_i × Cal_i × P_i × x_i)
        double[] objCoeffs = new double[n];
        for (int i = 0; i < n; i++) {
            CropData cd = cropDataList.get(i);
            objCoeffs[i] = cd.adjustedYield() * cd.calDensityPerKg() * cd.preferenceWeight();
        }
        LinearObjectiveFunction objective = new LinearObjectiveFunction(objCoeffs, 0.0);

        List<LinearConstraint> constraints = new ArrayList<>();

        // C1 : Σ x_i ≤ S
        double[] c1 = new double[n];
        Arrays.fill(c1, 1.0);
        constraints.add(new LinearConstraint(c1, Relationship.LEQ, surfaceM2));

        // C2 : Σ (Y_i × Cal_i × x_i) ≥ T
        double[] c2 = new double[n];
        for (int i = 0; i < n; i++) {
            CropData cd = cropDataList.get(i);
            c2[i] = cd.adjustedYield() * cd.calDensityPerKg();
        }
        constraints.add(new LinearConstraint(c2, Relationship.GEQ, calorieTarget));

        // C3 : x_i ≤ 0.30 × S  (anti-monoculture, une contrainte par culture)
        double maxPerCrop = MONOCULTURE_MAX_RATIO * surfaceM2;
        for (int i = 0; i < n; i++) {
            double[] c3 = new double[n];
            c3[i] = 1.0;
            constraints.add(new LinearConstraint(c3, Relationship.LEQ, maxPerCrop));
        }

        // Résolution (epsilon=1e-4, cutOff=1e-8, règle anti-cyclage Bland)
        SimplexSolver solver = new SimplexSolver(1e-4, 10, 1e-8);

        try {
            return solver.optimize(
                    new MaxIter(MAX_LP_ITERATIONS),
                    objective,
                    new LinearConstraintSet(constraints),
                    GoalType.MAXIMIZE,
                    new NonNegativeConstraint(true),
                    PivotSelectionRule.BLAND
            );
        } catch (org.apache.commons.math3.optim.linear.NoFeasibleSolutionException e) {
            // Calcul de la surface minimale requise (borne conservative avec C3)
            double maxCalDensity = cropDataList.stream()
                    .mapToDouble(cd -> cd.adjustedYield() * cd.calDensityPerKg())
                    .max()
                    .orElse(1.0);
            // Sans C3 : S_min = T / max(Y_i × Cal_i)
            // Avec C3 (30% max par culture) : S_min_c3 = T / (0.30 × max)
            double requiredSurface = calorieTarget / (MONOCULTURE_MAX_RATIO * maxCalDensity);
            log.info("LP infaisable : surface requise estimée = {}m²", requiredSurface);
            throw new NoFeasibleSolutionException(
                    BigDecimal.valueOf(requiredSurface).setScale(1, RoundingMode.CEILING).doubleValue());
        } catch (org.apache.commons.math3.exception.TooManyIterationsException e) {
            log.error("LP : trop d'itérations ({})", MAX_LP_ITERATIONS);
            throw new IllegalStateException(
                    "Le solveur LP n'a pas convergé. Essayez avec une surface différente.");
        }
    }

    // -------------------------------------------------------------------------
    // Construction et persistance du résultat
    // -------------------------------------------------------------------------

    private OptimizationResult buildResult(GardenProfile garden,
                                            double[] allocations,
                                            List<CropData> cropDataList,
                                            double calorieTarget) {
        OptimizationResult result = new OptimizationResult();
        result.setGardenProfile(garden);
        result.setComputedAt(LocalDateTime.now());
        result.setCalorieTargetAnnual(
                BigDecimal.valueOf(calorieTarget).setScale(2, RoundingMode.HALF_UP));

        double totalCalories = 0.0;
        List<PlotAllocation> plotAllocations = new ArrayList<>();

        for (int i = 0; i < allocations.length; i++) {
            double allocM2 = allocations[i];
            if (allocM2 < NEGLIGIBLE_SURFACE_M2) continue;

            CropData cd       = cropDataList.get(i);
            double yieldKg    = allocM2 * cd.adjustedYield();
            double calories   = yieldKg * cd.calDensityPerKg();
            totalCalories    += calories;

            PlotAllocation pa = new PlotAllocation();
            pa.setResult(result);
            pa.setCrop(cd.crop());
            pa.setAllocatedSurfaceM2(BigDecimal.valueOf(allocM2).setScale(4, RoundingMode.HALF_UP));
            pa.setEstimatedYieldKg(BigDecimal.valueOf(yieldKg).setScale(3, RoundingMode.HALF_UP));
            pa.setEstimatedCalories(BigDecimal.valueOf(calories).setScale(2, RoundingMode.HALF_UP));
            plotAllocations.add(pa);
        }

        result.setTotalCaloriesProduced(
                BigDecimal.valueOf(totalCalories).setScale(2, RoundingMode.HALF_UP));
        double selfSufficiency = calorieTarget > 0 ? (totalCalories / calorieTarget) * 100.0 : 0.0;
        result.setSelfSufficiencyPercent(
                BigDecimal.valueOf(selfSufficiency).setScale(2, RoundingMode.HALF_UP));
        result.setPlotAllocations(plotAllocations);

        return result;
    }

    // -------------------------------------------------------------------------
    // Mapping DTO
    // -------------------------------------------------------------------------

    private OptimizationResultDto toDto(OptimizationResult r) {
        List<PlotAllocationDto> allocDtos = r.getPlotAllocations().stream()
                .sorted((a, b) -> b.getAllocatedSurfaceM2().compareTo(a.getAllocatedSurfaceM2()))
                .map(pa -> new PlotAllocationDto(
                        pa.getCrop().getId(),
                        pa.getCrop().getCommonName(),
                        pa.getCrop().getBotanicalFamily(),
                        pa.getAllocatedSurfaceM2(),
                        pa.getEstimatedYieldKg(),
                        pa.getEstimatedCalories(),
                        pa.getCrop().getPlantSpacingM2()))
                .toList();

        return new OptimizationResultDto(
                r.getId(),
                r.getGardenProfile().getId(),
                r.getComputedAt(),
                r.getTotalCaloriesProduced(),
                r.getCalorieTargetAnnual(),
                r.getSelfSufficiencyPercent(),
                allocDtos);
    }

    // -------------------------------------------------------------------------
    // Record interne — données agronomiques pré-calculées par culture
    // -------------------------------------------------------------------------

    private record CropData(
            Crop crop,
            double adjustedYield,       // kg/m² ajusté selon la zone climatique
            double calDensityPerKg,     // kcal/kg
            double preferenceWeight     // P_i ∈ [0, 1]
    ) {}
}
