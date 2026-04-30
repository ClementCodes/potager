# Sprint 1 â€” BDD + Backend MVP France

**PÃ©riode :** 30 avril 2026 â†’ objectif : backend fonctionnel avec solveur LP  
**Objectif :** Avoir un endpoint `POST /api/v1/optimize` qui rÃ©pond correctement pour un profil jardin France  
**Statut global :** ðŸ”´ Non dÃ©marrÃ©

---

## DÃ©finition du "Done" pour ce sprint

- [x] La BDD dÃ©marre avec `docker compose up -d` sans erreur
- [x] Les migrations Flyway s'exÃ©cutent automatiquement au dÃ©marrage Spring Boot
- [x] `POST /auth/register` + `POST /auth/login` retournent un JWT valide
- [x] `POST /api/v1/gardens` crÃ©e un profil jardin persistÃ© en BDD
- [x] `POST /api/v1/optimize` retourne un plan de plantation JSON valide
- [x] Si surface trop petite â†’ rÃ©ponse 422 avec surface minimale requise
- [x] Tests unitaires du solveur LP passent (â‰¥ 80% couverture)
- [x] Swagger accessible sur http://localhost:8080/swagger-ui.html

---

## TÃ¢ches du Sprint (ordonnÃ©es)

### Bloc 1 â€” Initialisation projet (Jour 1)

| ID | TÃ¢che | Statut | Notes |
|---|---|---|---|
| BDD-01 | CrÃ©er `pom.xml` Maven | â¬œ Ã€ faire | DÃ©pendances : Spring Boot 3.3, JPA, PostgreSQL, Flyway, Security, Commons Math, Lombok, SpringDoc |
| BDD-02 | CrÃ©er `docker-compose.yml` | â¬œ Ã€ faire | PostgreSQL 16 port 5432, pgAdmin port 5050 |
| BE-01 | Initialiser projet Spring Boot | â¬œ Ã€ faire | Groupe: `com.potagerai`, Artefact: `potagerai-backend` |
| BE-02 | Configurer `application.properties` | â¬œ Ã€ faire | Datasource, JPA, JWT, CORS |

### Bloc 2 â€” BDD & EntitÃ©s (Jour 1-2)

| ID | TÃ¢che | Statut | Notes |
|---|---|---|---|
| BDD-03 | Migration `V1__init_schema.sql` | â¬œ Ã€ faire | Tables: users, countries, climate_zones, crops, nutritional_profiles, yield_data, consumption_data, garden_profiles, optimization_results, plot_allocations |
| BDD-04 | Migration `V2__seed_france_data.sql` | â¬œ Ã€ faire | 14 cultures, 4 zones FR, rendements calibrÃ©s France, consommation franÃ§aise statique |
| BDD-05 | 8 entitÃ©s JPA | â¬œ Ã€ faire | Voir Â§4.1 de la spec |
| BDD-06 | EntitÃ© `User` | â¬œ Ã€ faire | id, email, password (BCrypt), createdAt |

### Bloc 3 â€” SÃ©curitÃ© & Auth (Jour 2)

| ID | TÃ¢che | Statut | Notes |
|---|---|---|---|
| BE-03 | `SecurityConfig.java` | â¬œ Ã€ faire | JWT stateless, CORS whitelistÃ© :4200, pas de CSRF (API REST) |
| BE-04 | `JwtTokenProvider.java` | â¬œ Ã€ faire | HS256, expiration 24h, `@Value("${jwt.secret}")` |
| BE-10 | `AuthController.java` | â¬œ Ã€ faire | POST /auth/register, POST /auth/login |
| BE-11 | `AuthService.java` | â¬œ Ã€ faire | BCrypt, `UserDetailsService` |
| BE-12 | DTOs Auth | â¬œ Ã€ faire | `LoginRequest`, `RegisterRequest`, `TokenResponse` |

### Bloc 4 â€” Feature Cultures (Jour 2-3)

| ID | TÃ¢che | Statut | Notes |
|---|---|---|---|
| BE-20 | Repositories JPA | â¬œ Ã€ faire | `CropRepository`, `YieldDataRepository`, `ConsumptionDataRepository` |
| BE-21 | `CropService.java` | â¬œ Ã€ faire | findEligibleByClimateZoneAndCountry(zone, iso) |
| BE-22 | `CropController.java` | â¬œ Ã€ faire | GET /api/v1/crops |
| BE-23 | DTOs Cultures | â¬œ Ã€ faire | `CropDTO`, `CropDetailDTO` |

### Bloc 5 â€” Feature Jardin (Jour 3)

| ID | TÃ¢che | Statut | Notes |
|---|---|---|---|
| BE-30 | `GardenRepository.java` | â¬œ Ã€ faire | findByUserId() |
| BE-31 | `GardenService.java` | â¬œ Ã€ faire | Validation : surface > 0, personnes 1-20, zone valide |
| BE-32 | `GardenController.java` | â¬œ Ã€ faire | POST /gardens, GET /gardens |
| BE-33 | DTOs Jardin | â¬œ Ã€ faire | `GardenProfileRequest`, `GardenProfileResponse` |

### Bloc 6 â€” Solveur LP / CÅ“ur MÃ©tier (Jour 3-4)

| ID | TÃ¢che | Statut | Notes |
|---|---|---|---|
| BE-46 | `ClimateAdjustmentService.java` | â¬œ Ã€ faire | Multiplicateurs : FR-MED=1.2, FR-OCC=1.0, FR-CON=0.85, FR-MON=0.65 |
| BE-40 | `GardenOptimizerService.java` | â¬œ Ã€ faire | CÅ“ur du projet â€” voir Â§5 de la spec |
| BE-41 | Contrainte C1 Surface | â¬œ Ã€ faire | `Î£ x_i â‰¤ S_max` |
| BE-42 | Contrainte C2 Calories | â¬œ Ã€ faire | `Î£ (Y_i Â· x_i Â· Cal_i) â‰¥ householdSize Ã— 2500 Ã— 365` |
| BE-43 | Contrainte C3 Monoculture | â¬œ Ã€ faire | `x_i â‰¤ 0.30 Ã— S_max` |
| BE-44 | Gestion NoFeasibleSolution | â¬œ Ã€ faire | Calcul surface min requise = `CAL_target / max(Y_i Ã— Cal_i)` |
| BE-47 | `OptimizationController.java` | â¬œ Ã€ faire | POST /optimize, GET /optimize/{gardenId}/latest |
| BE-48 | DTOs Optimisation | â¬œ Ã€ faire | `OptimizationRequest`, `OptimizationResultDTO`, `PlotAllocationDTO` |
| BE-05 | `GlobalExceptionHandler.java` | â¬œ Ã€ faire | @ControllerAdvice â€” tous les cas d'erreur |
| BE-06 | Config Swagger/OpenAPI | â¬œ Ã€ faire | `springdoc-openapi-starter-webmvc-ui` |

### Bloc 7 â€” Tests (Jour 4-5)

| ID | TÃ¢che | Statut | Notes |
|---|---|---|---|
| BE-90 | `GardenOptimizerServiceTest` | â¬œ Ã€ faire | Cas : surface suffisante, surface insuffisante, surface limite, 1 culture Ã©ligible |
| BE-91 | `OptimizationControllerIT` | â¬œ Ã€ faire | MockMvc + H2, tester les 3 rÃ©ponses (200, 422, 401) |

---

## ScÃ©narios de test Ã  valider manuellement (Postman/Swagger)

```
# 1. Inscription
POST /api/v1/auth/register
{ "email": "test@potagerai.com", "password": "Test1234!" }
â†’ 201 Created

# 2. Connexion
POST /api/v1/auth/login
{ "email": "test@potagerai.com", "password": "Test1234!" }
â†’ 200 { "accessToken": "eyJ..." }

# 3. CrÃ©er un jardin
POST /api/v1/gardens  [Authorization: Bearer <token>]
{ "totalSurfaceM2": 150, "householdSize": 2, "climateZoneCode": "FR-CON", "cultivationMethod": "OPEN_AIR", "countryIsoCode": "FRA" }
â†’ 201 { "id": 1, ... }

# 4. Optimiser
POST /api/v1/optimize  [Authorization: Bearer <token>]
{ "gardenProfileId": 1 }
â†’ 200 { "allocations": [...], "selfSufficiencyPercent": 87.5 }

# 5. Surface insuffisante
POST /api/v1/gardens avec totalSurfaceM2: 5
puis POST /api/v1/optimize
â†’ 422 { "error": "NO_FEASIBLE_SOLUTION", "requiredSurfaceM2": 280 }
```

---

## Blocages / Risques identifiÃ©s

| Risque | ProbabilitÃ© | Mitigation |
|---|---|---|
| InstabilitÃ© numÃ©rique du SimplexSolver (pivot trop petit) | Moyenne | Ajuster `epsilon=1e-4`, tester avec toutes les 14 cultures |
| Contrainte calorique infaisable sur petites surfaces | Haute | ImplÃ©menter la rÃ©ponse 422 dÃ¨s le dÃ©but |
| Conflit CORS Angular â†” Spring | Faible | Whitelister `http://localhost:4200` dans `SecurityConfig` |
| Version Java incompatible | Faible | Forcer Java 21 dans `pom.xml` avec `<java.version>21</java.version>` |

---

*Sprint 1 â€” PotagerAI â€” DÃ©marrÃ© le 30 avril 2026*
