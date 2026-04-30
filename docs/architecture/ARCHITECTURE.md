# Architecture Technique — PotagerAI

> Ce document décrit l'architecture globale du système.  
> **Référence détaillée :** voir [SPECIFICATION_TECHNIQUE_FONCTIONNELLE.md](specifications/SPECIFICATION_TECHNIQUE_FONCTIONNELLE.md)

## Vue d'ensemble

```
┌─────────────────────────────────────────────────────────────────────┐
│                        UTILISATEUR (navigateur)                      │
└─────────────────────────┬───────────────────────────────────────────┘
                          │ HTTPS
                          ▼
┌─────────────────────────────────────────────────────────────────────┐
│                    FRONTEND — Angular 18 SPA                         │
│  ┌──────────────┐  ┌──────────────────┐  ┌────────────────────────┐ │
│  │ Auth Module  │  │ Garden Config    │  │ Results (D3.js/Chart)  │ │
│  └──────────────┘  └──────────────────┘  └────────────────────────┘ │
│            Nginx / CDN — Port 4200 (dev) / 80 (prod)                 │
└─────────────────────────┬───────────────────────────────────────────┘
                          │ REST + JWT
                          ▼
┌─────────────────────────────────────────────────────────────────────┐
│                  BACKEND — Java 21 Spring Boot 3.3                   │
│  ┌──────────┐  ┌──────────┐  ┌──────────────────┐  ┌────────────┐  │
│  │   Auth   │  │  Crops   │  │    Optimization  │  │   Garden   │  │
│  │ Controller│  │Controller│  │    Controller    │  │ Controller │  │
│  └────┬─────┘  └────┬─────┘  └────────┬─────────┘  └─────┬──────┘  │
│       │              │                  │                   │         │
│  ┌────▼──────────────▼──────────────────▼───────────────────▼──────┐ │
│  │                       Service Layer                               │ │
│  │   AuthService · CropService · GardenOptimizerService             │ │
│  │                  [Apache Commons Math — SimplexSolver]            │ │
│  └────────────────────────────┬─────────────────────────────────────┘ │
│                                │ JPA/Hibernate                         │
│  Port 8080                     │                                       │
└────────────────────────────────┼──────────────────────────────────────┘
                                 │
                                 ▼
┌─────────────────────────────────────────────────────────────────────┐
│                   PostgreSQL 16 — Port 5432                          │
│  Tables: users · crops · yield_data · consumption_data              │
│          garden_profiles · optimization_results · plot_allocations  │
│          countries · climate_zones · nutritional_profiles           │
└─────────────────────────────────────────────────────────────────────┘
```

## Sources de données externes (Phase 2+)

```
FAOSTAT ──── ETL Spring Batch ──────► consumption_data (table BDD)
GAEZ v4 ──── ETL asynchrone ────────► yield_data (ajustement sol)
Köppen ────── API géolocalisation ──► ClimateAdjustmentService
Open Food ──► REST Client ──────────► nutritional_profiles
Permapeople ► REST Client ──────────► companion_planting (Phase 3)
```

## Flux de données principal (requête d'optimisation)

```
1. POST /api/v1/optimize { gardenProfileId: 42 }
2. Charger GardenProfile depuis BDD
3. CropService → sélectionner cultures éligibles (zone + pays)
4. ClimateAdjustmentService → appliquer coefficients Y_i
5. GardenOptimizerService → construire modèle LP + résoudre (Simplex)
6. Persister OptimizationResult + PlotAllocation[] en BDD
7. Retourner OptimizationResultDTO avec allocations + selfSufficiencyPercent
```
