# Product Backlog — PotagerAI

> Toutes les tâches du projet, organisées par phase et priorité.  
> Légende : 🔴 Critique · 🟠 Haute · 🟡 Moyenne · 🟢 Basse

---

## PHASE 1 — MVP France

### 🗄️ BDD — Base de Données

| ID | Priorité | Tâche | Statut |
|---|---|---|---|
| BDD-01 | 🔴 | Créer `pom.xml` Maven avec toutes les dépendances (Spring Boot, JPA, PostgreSQL, Commons Math, Flyway, Security, Lombok) | ⬜ À faire |
| BDD-02 | 🔴 | Créer `docker-compose.yml` avec PostgreSQL 16 + pgAdmin | ⬜ À faire |
| BDD-03 | 🔴 | Écrire migration Flyway `V1__init_schema.sql` — création de toutes les tables | ⬜ À faire |
| BDD-04 | 🔴 | Écrire migration Flyway `V2__seed_france_data.sql` — données cultures + rendements + consommation France | ⬜ À faire |
| BDD-05 | 🟠 | Créer les 8 entités JPA annotées (`Country`, `Crop`, `NutritionalProfile`, `YieldData`, `ConsumptionData`, `GardenProfile`, `OptimizationResult`, `PlotAllocation`) | ⬜ À faire |
| BDD-06 | 🟠 | Créer entité `User` + table `users` pour l'authentification | ⬜ À faire |
| BDD-07 | 🟡 | Créer table `climate_zones` (référentiel des 4 zones françaises) | ⬜ À faire |
| BDD-08 | 🟡 | Seed : peupler 14 cultures de base avec profils nutritionnels et scores ANDI | ⬜ À faire |
| BDD-09 | 🟢 | Script de réinitialisation rapide BDD pour les tests | ⬜ À faire |

### ⚙️ Backend — Java Spring Boot

**Config & Infra**

| ID | Priorité | Tâche | Statut |
|---|---|---|---|
| BE-01 | 🔴 | Initialiser le projet Spring Boot avec Spring Initializr (Java 21, Maven) | ⬜ À faire |
| BE-02 | 🔴 | Configurer `application.properties` (dev) et `application-prod.properties` | ⬜ À faire |
| BE-03 | 🟠 | Configurer `SecurityConfig.java` : JWT filter, CORS, désactivation CSRF pour API REST | ⬜ À faire |
| BE-04 | 🟠 | Créer `JwtTokenProvider.java` : génération + validation du token JWT HS256 | ⬜ À faire |
| BE-05 | 🟠 | Créer `GlobalExceptionHandler.java` : gérer `NoFeasibleSolutionException`, `EntityNotFoundException`, validation errors | ⬜ À faire |
| BE-06 | 🟡 | Configurer Swagger/OpenAPI (`springdoc-openapi`) pour la doc auto des endpoints | ⬜ À faire |
| BE-07 | 🟡 | Créer `Dockerfile` pour le backend | ⬜ À faire |

**Feature : Authentification**

| ID | Priorité | Tâche | Statut |
|---|---|---|---|
| BE-10 | 🔴 | Créer `AuthController.java` : endpoints `/auth/register`, `/auth/login`, `/auth/refresh` | ⬜ À faire |
| BE-11 | 🔴 | Créer `AuthService.java` : logique d'inscription + connexion + hachage mot de passe (BCrypt) | ⬜ À faire |
| BE-12 | 🟠 | DTOs : `LoginRequest`, `RegisterRequest`, `TokenResponse` | ⬜ À faire |

**Feature : Cultures (Crops)**

| ID | Priorité | Tâche | Statut |
|---|---|---|---|
| BE-20 | 🔴 | Créer `CropRepository.java` + `YieldDataRepository.java` + `ConsumptionDataRepository.java` | ⬜ À faire |
| BE-21 | 🟠 | Créer `CropService.java` : liste des cultures éligibles selon zone climatique + pays | ⬜ À faire |
| BE-22 | 🟠 | Créer `CropController.java` : `GET /api/v1/crops`, `GET /api/v1/crops/{id}`, `GET /api/v1/crops?climateZone=FR-CON` | ⬜ À faire |
| BE-23 | 🟡 | DTOs : `CropDTO`, `CropDetailDTO` (avec `NutritionalProfileDTO`) | ⬜ À faire |

**Feature : Jardin**

| ID | Priorité | Tâche | Statut |
|---|---|---|---|
| BE-30 | 🔴 | Créer `GardenRepository.java` | ⬜ À faire |
| BE-31 | 🔴 | Créer `GardenService.java` : CRUD profil jardin + validation des entrées (`0 < surface ≤ 50000`, `1 ≤ personnes ≤ 20`) | ⬜ À faire |
| BE-32 | 🔴 | Créer `GardenController.java` : `POST /api/v1/gardens`, `GET /api/v1/gardens`, `GET /api/v1/gardens/{id}` | ⬜ À faire |
| BE-33 | 🟠 | DTOs : `GardenProfileRequest`, `GardenProfileResponse` | ⬜ À faire |

**Feature : Optimisation (Cœur Métier)**

| ID | Priorité | Tâche | Statut |
|---|---|---|---|
| BE-40 | 🔴 | Créer `GardenOptimizerService.java` : implémentation du solveur LP Apache Commons Math | ⬜ À faire |
| BE-41 | 🔴 | Implémenter C1 : contrainte surface totale `Σ x_i ≤ S_max` | ⬜ À faire |
| BE-42 | 🔴 | Implémenter C2 : contrainte calorique `Σ (Y_i · x_i · Cal_i) ≥ CAL_target` | ⬜ À faire |
| BE-43 | 🔴 | Implémenter C3 : plafond monoculture `x_i ≤ 0.30 · S_max` | ⬜ À faire |
| BE-44 | 🟠 | Gérer `NoFeasibleSolutionException` → calculer la surface minimale requise et la retourner dans la réponse 422 | ⬜ À faire |
| BE-45 | 🟠 | Gérer `TooManyIterationsException` → réduire `n` et recalculer (filtrer cultures à `P_i < 0.01`) | ⬜ À faire |
| BE-46 | 🔴 | Créer `ClimateAdjustmentService.java` : appliquer le coefficient de rendement selon la zone climatique FR-* | ⬜ À faire |
| BE-47 | 🔴 | Créer `OptimizationController.java` : `POST /api/v1/optimize`, `GET /api/v1/optimize/{gardenId}/latest` | ⬜ À faire |
| BE-48 | 🟠 | DTOs : `OptimizationRequest`, `OptimizationResultDTO`, `PlotAllocationDTO` | ⬜ À faire |
| BE-49 | 🟡 | Calculer et stocker le `selfSufficiencyPercent` dans `OptimizationResult` | ⬜ À faire |

**Tests Backend**

| ID | Priorité | Tâche | Statut |
|---|---|---|---|
| BE-90 | 🟠 | Tests unitaires `GardenOptimizerServiceTest` (JUnit 5 + Mockito) — couverture ≥ 80% | ⬜ À faire |
| BE-91 | 🟠 | Tests intégration `OptimizationControllerIT` (MockMvc + H2 in-memory) | ⬜ À faire |
| BE-92 | 🟡 | Tests `AuthControllerIT` : register + login + token validation | ⬜ À faire |
| BE-93 | 🟡 | Tests `GardenControllerIT` : CRUD complet | ⬜ À faire |

---

### 🖥️ Frontend — Angular 18

**Config & Infra**

| ID | Priorité | Tâche | Statut |
|---|---|---|---|
| FE-01 | 🔴 | Initialiser le projet Angular 18 (`ng new potagerai-frontend --style=scss --routing`) | ⬜ À faire |
| FE-02 | 🔴 | Installer les dépendances : Angular Material 18, Chart.js, ng2-charts, D3.js, NgRx 18 | ⬜ À faire |
| FE-03 | 🟠 | Configurer `environment.ts` et `environment.prod.ts` (API base URL) | ⬜ À faire |
| FE-04 | 🟠 | Créer `JwtInterceptor` : injecter `Authorization: Bearer <token>` sur chaque requête | ⬜ À faire |
| FE-05 | 🟠 | Créer `AuthGuard` : rediriger vers `/login` si non authentifié | ⬜ À faire |
| FE-06 | 🟠 | Créer `ErrorInterceptor` : gérer 401 (redirect login) et 422 (afficher erreur surface) | ⬜ À faire |
| FE-07 | 🟡 | Créer `Dockerfile` + config Nginx pour le frontend | ⬜ À faire |

**Modèles TypeScript**

| ID | Priorité | Tâche | Statut |
|---|---|---|---|
| FE-10 | 🔴 | Créer `garden-profile.model.ts` | ⬜ À faire |
| FE-11 | 🔴 | Créer `optimization-result.model.ts` (avec `PlotAllocation`) | ⬜ À faire |
| FE-12 | 🟠 | Créer `crop.model.ts` | ⬜ À faire |
| FE-13 | 🟠 | Créer `auth.model.ts` (`LoginRequest`, `TokenResponse`) | ⬜ À faire |

**Services Angular**

| ID | Priorité | Tâche | Statut |
|---|---|---|---|
| FE-20 | 🔴 | Créer `AuthService` : login, register, logout, stockage token localStorage | ⬜ À faire |
| FE-21 | 🔴 | Créer `OptimizationService` : POST `/optimize`, GET résultat | ⬜ À faire |
| FE-22 | 🟠 | Créer `GardenService` : CRUD profil jardin | ⬜ À faire |
| FE-23 | 🟠 | Créer `CropService` : GET liste cultures | ⬜ À faire |

**Composants UI**

| ID | Priorité | Tâche | Statut |
|---|---|---|---|
| FE-30 | 🔴 | Page `LoginComponent` + `RegisterComponent` (Angular Material forms) | ⬜ À faire |
| FE-31 | 🔴 | Page `GardenConfigComponent` : formulaire réactif (surface, personnes, zone, méthode) | ⬜ À faire |
| FE-32 | 🔴 | Composant `GardenMapComponent` : plan 2D Treemap D3.js proportionnel aux surfaces `x_i` | ⬜ À faire |
| FE-33 | 🔴 | Composant `NutritionDashboardComponent` : doughnut % calories, bar chart par culture | ⬜ À faire |
| FE-34 | 🟠 | Composant `CropTableComponent` : tableau récap des cultures allouées (surface, rendement, kcal) | ⬜ À faire |
| FE-35 | 🟠 | Page `HistoryComponent` : liste des plans générés | ⬜ À faire |
| FE-36 | 🟡 | Composant `WarningBannerComponent` : afficher les avertissements du solveur | ⬜ À faire |
| FE-37 | 🟡 | Bouton d'export PDF (via `jsPDF` ou `print CSS`) | ⬜ À faire |

**Tests Frontend**

| ID | Priorité | Tâche | Statut |
|---|---|---|---|
| FE-90 | 🟠 | Tests unitaires `OptimizationService.spec.ts` (HttpClientTestingModule) | ⬜ À faire |
| FE-91 | 🟠 | Tests unitaires `AuthService.spec.ts` | ⬜ À faire |
| FE-92 | 🟡 | Tests composants `GardenConfigComponent.spec.ts` | ⬜ À faire |

---

## PHASE 2 — Internationalisation

| ID | Priorité | Tâche | Statut |
|---|---|---|---|
| P2-01 | 🟡 | Pipeline ETL Spring Batch : téléchargement et parsing des CSV FAOSTAT | ⬜ À faire |
| P2-02 | 🟡 | Créer `FaostatClient.java` avec RestClient | ⬜ À faire |
| P2-03 | 🟡 | Intégrer la classification Köppen-Geiger (coordonnées GPS → zone climatique) | ⬜ À faire |
| P2-04 | 🟡 | Intégrer la base GAEZ v4 (ajustement rendements par type de sol) | ⬜ À faire |
| P2-05 | 🟡 | Support i18n Angular (français, anglais, espagnol minimum) | ⬜ À faire |
| P2-06 | 🟢 | Migration solveur Commons Math → Google OR-Tools GLOP | ⬜ À faire |

## PHASE 3 — Intelligence Avancée

| ID | Priorité | Tâche | Statut |
|---|---|---|---|
| P3-01 | 🟢 | Intégrer API Permapeople (compagnonnage, données plantes enrichies) | ⬜ À faire |
| P3-02 | 🟢 | Modèle multi-période (successions de cultures `x_{i,t,p}`) | ⬜ À faire |
| P3-03 | 🟢 | Rotation des cultures (contraintes familles botaniques) | ⬜ À faire |
| P3-04 | 🟢 | Calendriers phénologiques via Growing Degree Days (GDD) | ⬜ À faire |
| P3-05 | 🟢 | Visualisation calendrier saisonnier Angular (timeline par parcelle) | ⬜ À faire |

---

*Dernière mise à jour : 30 avril 2026*
