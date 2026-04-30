# Sprint 1 — BDD + Backend MVP France

**Période :** 30 avril 2026 → objectif : backend fonctionnel avec solveur LP  
**Objectif :** Avoir un endpoint `POST /api/v1/optimize` qui répond correctement pour un profil jardin France  
**Statut global :** 🔴 Non démarré

---

## Définition du "Done" pour ce sprint

- [ ] La BDD démarre avec `docker compose up -d` sans erreur
- [ ] Les migrations Flyway s'exécutent automatiquement au démarrage Spring Boot
- [ ] `POST /auth/register` + `POST /auth/login` retournent un JWT valide
- [ ] `POST /api/v1/gardens` crée un profil jardin persisté en BDD
- [ ] `POST /api/v1/optimize` retourne un plan de plantation JSON valide
- [ ] Si surface trop petite → réponse 422 avec surface minimale requise
- [ ] Tests unitaires du solveur LP passent (≥ 80% couverture)
- [ ] Swagger accessible sur http://localhost:8080/swagger-ui.html

---

## Tâches du Sprint (ordonnées)

### Bloc 1 — Initialisation projet (Jour 1)

| ID | Tâche | Statut | Notes |
|---|---|---|---|
| BDD-01 | Créer `pom.xml` Maven | ⬜ À faire | Dépendances : Spring Boot 3.3, JPA, PostgreSQL, Flyway, Security, Commons Math, Lombok, SpringDoc |
| BDD-02 | Créer `docker-compose.yml` | ⬜ À faire | PostgreSQL 16 port 5432, pgAdmin port 5050 |
| BE-01 | Initialiser projet Spring Boot | ⬜ À faire | Groupe: `com.potagerai`, Artefact: `potagerai-backend` |
| BE-02 | Configurer `application.properties` | ⬜ À faire | Datasource, JPA, JWT, CORS |

### Bloc 2 — BDD & Entités (Jour 1-2)

| ID | Tâche | Statut | Notes |
|---|---|---|---|
| BDD-03 | Migration `V1__init_schema.sql` | ⬜ À faire | Tables: users, countries, climate_zones, crops, nutritional_profiles, yield_data, consumption_data, garden_profiles, optimization_results, plot_allocations |
| BDD-04 | Migration `V2__seed_france_data.sql` | ⬜ À faire | 14 cultures, 4 zones FR, rendements calibrés France, consommation française statique |
| BDD-05 | 8 entités JPA | ⬜ À faire | Voir §4.1 de la spec |
| BDD-06 | Entité `User` | ⬜ À faire | id, email, password (BCrypt), createdAt |

### Bloc 3 — Sécurité & Auth (Jour 2)

| ID | Tâche | Statut | Notes |
|---|---|---|---|
| BE-03 | `SecurityConfig.java` | ⬜ À faire | JWT stateless, CORS whitelisté :4200, pas de CSRF (API REST) |
| BE-04 | `JwtTokenProvider.java` | ⬜ À faire | HS256, expiration 24h, `@Value("${jwt.secret}")` |
| BE-10 | `AuthController.java` | ⬜ À faire | POST /auth/register, POST /auth/login |
| BE-11 | `AuthService.java` | ⬜ À faire | BCrypt, `UserDetailsService` |
| BE-12 | DTOs Auth | ⬜ À faire | `LoginRequest`, `RegisterRequest`, `TokenResponse` |

### Bloc 4 — Feature Cultures (Jour 2-3)

| ID | Tâche | Statut | Notes |
|---|---|---|---|
| BE-20 | Repositories JPA | ⬜ À faire | `CropRepository`, `YieldDataRepository`, `ConsumptionDataRepository` |
| BE-21 | `CropService.java` | ⬜ À faire | findEligibleByClimateZoneAndCountry(zone, iso) |
| BE-22 | `CropController.java` | ⬜ À faire | GET /api/v1/crops |
| BE-23 | DTOs Cultures | ⬜ À faire | `CropDTO`, `CropDetailDTO` |

### Bloc 5 — Feature Jardin (Jour 3)

| ID | Tâche | Statut | Notes |
|---|---|---|---|
| BE-30 | `GardenRepository.java` | ⬜ À faire | findByUserId() |
| BE-31 | `GardenService.java` | ⬜ À faire | Validation : surface > 0, personnes 1-20, zone valide |
| BE-32 | `GardenController.java` | ⬜ À faire | POST /gardens, GET /gardens |
| BE-33 | DTOs Jardin | ⬜ À faire | `GardenProfileRequest`, `GardenProfileResponse` |

### Bloc 6 — Solveur LP / Cœur Métier (Jour 3-4)

| ID | Tâche | Statut | Notes |
|---|---|---|---|
| BE-46 | `ClimateAdjustmentService.java` | ⬜ À faire | Multiplicateurs : FR-MED=1.2, FR-OCC=1.0, FR-CON=0.85, FR-MON=0.65 |
| BE-40 | `GardenOptimizerService.java` | ⬜ À faire | Cœur du projet — voir §5 de la spec |
| BE-41 | Contrainte C1 Surface | ⬜ À faire | `Σ x_i ≤ S_max` |
| BE-42 | Contrainte C2 Calories | ⬜ À faire | `Σ (Y_i · x_i · Cal_i) ≥ householdSize × 2500 × 365` |
| BE-43 | Contrainte C3 Monoculture | ⬜ À faire | `x_i ≤ 0.30 × S_max` |
| BE-44 | Gestion NoFeasibleSolution | ⬜ À faire | Calcul surface min requise = `CAL_target / max(Y_i × Cal_i)` |
| BE-47 | `OptimizationController.java` | ⬜ À faire | POST /optimize, GET /optimize/{gardenId}/latest |
| BE-48 | DTOs Optimisation | ⬜ À faire | `OptimizationRequest`, `OptimizationResultDTO`, `PlotAllocationDTO` |
| BE-05 | `GlobalExceptionHandler.java` | ⬜ À faire | @ControllerAdvice — tous les cas d'erreur |
| BE-06 | Config Swagger/OpenAPI | ⬜ À faire | `springdoc-openapi-starter-webmvc-ui` |

### Bloc 7 — Tests (Jour 4-5)

| ID | Tâche | Statut | Notes |
|---|---|---|---|
| BE-90 | `GardenOptimizerServiceTest` | ⬜ À faire | Cas : surface suffisante, surface insuffisante, surface limite, 1 culture éligible |
| BE-91 | `OptimizationControllerIT` | ⬜ À faire | MockMvc + H2, tester les 3 réponses (200, 422, 401) |

---

## Scénarios de test à valider manuellement (Postman/Swagger)

```
# 1. Inscription
POST /api/v1/auth/register
{ "email": "test@potagerai.com", "password": "Test1234!" }
→ 201 Created

# 2. Connexion
POST /api/v1/auth/login
{ "email": "test@potagerai.com", "password": "Test1234!" }
→ 200 { "accessToken": "eyJ..." }

# 3. Créer un jardin
POST /api/v1/gardens  [Authorization: Bearer <token>]
{ "totalSurfaceM2": 150, "householdSize": 2, "climateZoneCode": "FR-CON", "cultivationMethod": "OPEN_AIR", "countryIsoCode": "FRA" }
→ 201 { "id": 1, ... }

# 4. Optimiser
POST /api/v1/optimize  [Authorization: Bearer <token>]
{ "gardenProfileId": 1 }
→ 200 { "allocations": [...], "selfSufficiencyPercent": 87.5 }

# 5. Surface insuffisante
POST /api/v1/gardens avec totalSurfaceM2: 5
puis POST /api/v1/optimize
→ 422 { "error": "NO_FEASIBLE_SOLUTION", "requiredSurfaceM2": 280 }
```

---

## Blocages / Risques identifiés

| Risque | Probabilité | Mitigation |
|---|---|---|
| Instabilité numérique du SimplexSolver (pivot trop petit) | Moyenne | Ajuster `epsilon=1e-4`, tester avec toutes les 14 cultures |
| Contrainte calorique infaisable sur petites surfaces | Haute | Implémenter la réponse 422 dès le début |
| Conflit CORS Angular ↔ Spring | Faible | Whitelister `http://localhost:4200` dans `SecurityConfig` |
| Version Java incompatible | Faible | Forcer Java 21 dans `pom.xml` avec `<java.version>21</java.version>` |

---

*Sprint 1 — PotagerAI — Démarré le 30 avril 2026*
