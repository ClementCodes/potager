# PotagerAI — Instructions Copilot (chargé automatiquement)

## Contexte du projet
Application full-stack d'optimisation de jardins potagers vers l'autosuffisance calorique.
- **Auteur :** Clément Dominique — GitHub : ClementCodes
- **Repo :** https://github.com/ClementCodes/potager.git — branche `main`
- **Racine locale :** `c:\Users\cdominique\Documents\Potager\`
- **Dernier commit :** `230379c`

---

## Stack technique

| Couche | Technologie |
|---|---|
| Backend | Java 21, Spring Boot 3.3, Maven 3.9.8 |
| Base de données | H2 (dev, fichier `./potagerai-dev.mv.db`, mode PostgreSQL) — PostgreSQL 16 via Docker (prod) |
| Flyway | 5 migrations appliquées (V1→V5) |
| Sécurité | JWT HS256 (JJWT 0.12.6), BCrypt, Spring Security stateless |
| Solveur | Apache Commons Math 3.6.1 SimplexSolver, `MONOCULTURE_MAX_RATIO=0.30`, 2500 kcal/pers/jour |
| Frontend | Angular 18 standalone, SCSS, Angular Material 18, NgRx 18 |
| Visualisation | D3.js v7 (treemap), Chart.js v4 |
| Tests backend | JUnit 5, Mockito, MockMvc, H2 in-memory |
| Tests frontend | Jasmine, HttpClientTestingModule |

---

## État du projet — Sprint 2 en cours

### Backend (`backend/potagerai-backend/`) ✅
- Package racine : `com.potagerai`
- Architecture : `controller/` → `service/` → `domain/` → `dto/`

#### Endpoints
| Méthode | URL | Auth | Description |
|---|---|---|---|
| POST | `/api/auth/register` | public | Inscription |
| POST | `/api/auth/login` | public | Connexion → JWT |
| GET | `/api/crops` | JWT | Liste toutes les cultures |
| GET | `/api/crops?season=ETE` | JWT | Filtre par saison (`PRINTEMPS`, `ETE`, `AUTOMNE`, `HIVER`, `TOUTE_ANNEE`) |
| GET | `/api/crops/{id}` | JWT | Détail culture |
| GET | `/api/climate-zones` | public | Zones climatiques |
| GET | `/api/v1/surface-estimate?householdSize=4&climateZoneCode=FR-OCC` | public | Estime la surface nécessaire |
| POST | `/api/gardens` | JWT | Créer un jardin |
| GET | `/api/gardens` | JWT | Lister les jardins |
| GET | `/api/gardens/{id}` | JWT | Détail jardin |
| POST | `/api/gardens/{id}/optimize` | JWT | Lance l'optimisation LP (body optionnel: `{selectedCropIds:[1,3]}`) |
| GET | `/api/gardens/{id}/optimization` | JWT | Dernière optimisation (sans recalcul) |
| GET | `/api/gardens/{id}/optimizations` | JWT | Historique des optimisations |

- Swagger : `http://localhost:8080/swagger-ui.html`

#### Migrations Flyway
- **V1** : schéma de base
- **V2** : seed 14 cultures France
- **V3** : `plant_spacing_m2`
- **V4** : `storage_months`, `frost_sensitive`, `sowing_month_min/max` (14 cultures)
- **V5** : 12 nouvelles cultures (26 au total) — Poireau, Radis, Aubergine, Poivron, Courge, Patate douce, Topinambour, Mâche, Bette, Fève, Maïs, Chou-fleur

#### DTOs notables
- `CropDto` : tous les champs agronomiques + nutritionnels
- `SurfaceEstimateDto(householdSize, climateZoneCode, climateZoneName, estimatedSurfaceM2, calorieTargetAnnual)`
- `OptimizeRequestDto(List<Long> selectedCropIds)` — optionnel, si vide → toutes les cultures
- `Season` enum : `PRINTEMPS(3,5)`, `ETE(6,8)`, `AUTOMNE(9,11)`, `HIVER(12,2,wrapsAround)`, `TOUTE_ANNEE`
- `PlotAllocationDto` : inclut `plantSpacingM2`, `botanicalFamily`

### Frontend (`frontend/potagerai-frontend/`) ✅
- Composants standalone (`templateUrl` + `styleUrl`, injection via `inject()`) :
  - `LoginComponent` → `/auth/login`
  - `RegisterComponent` → `/auth/register`
  - `GardenFormComponent` → `/garden` — formulaire en **3 étapes** :
    1. Foyer (householdSize + climateZoneCode) → auto-fetch estimation surface
    2. Surface (pré-remplie avec l'estimation, feedback vert/orange)
    3. Cultures (sélecteur saison + grille de chips cliquables, 🌡️/📦 badges)
  - `GardenResultComponent` → `/garden/:id/result` — treemap D3, KPIs, plan PvZ avec lanes par culture
  - `CropListComponent` → `/crops`
- Services : `AuthService`, `GardenService`, `CropService`, `ClimateZoneService`
- Guard : `AuthGuard` (redirige `/auth/login` si non connecté)
- Interceptor : `AuthInterceptor` (injecte `Authorization: Bearer`)

#### Modèles TypeScript
```typescript
// garden.model.ts
GardenProfile { id, totalSurfaceM2, householdSize, climateZoneCode, climateZoneName,
                countryIsoCode, countryName, cultivationMethod, createdAt }
CreateGardenRequest { totalSurfaceM2, householdSize, climateZoneCode, countryIsoCode, cultivationMethod? }
SurfaceEstimate { householdSize, climateZoneCode, climateZoneName,
                  estimatedSurfaceM2, calorieTargetAnnual }

// crop.model.ts
Crop { id, commonName, scientificName, botanicalFamily, rootDepthCm,
       growingDaysMin, growingDaysMax, plantSpacingM2, storageMonths,
       frostSensitive, sowingMonthMin, sowingMonthMax, nutritionalProfile }

// optimization.model.ts
PlotAllocation { cropId, cropName, botanicalFamily, allocatedSurfaceM2,
                 estimatedYieldKg, estimatedCalories, plantSpacingM2, andiScore }
```

#### Services Angular
- `CropService.findBySeason(season: Season)` → `GET /api/crops?season=`
- `GardenService.estimateSurface(householdSize, climateZoneCode)` → `GET /api/v1/surface-estimate`
- `GardenService.optimize(id, selectedCropIds?)` → `POST /api/gardens/{id}/optimize` avec body optionnel

### Infrastructure
- `backend/docker-compose.yml` : PostgreSQL 16 (port 5432) + pgAdmin (port 5050)
- `.env.example` : variables d'environnement à configurer
- Profile `dev-h2` : H2 fichier, pas besoin de Docker pour développer

---

## Conventions de code

### Angular 18
- Tous les composants : **standalone**, `templateUrl` + `styleUrl` (jamais inline)
- Injection de dépendances : `private service = inject(Service)` (jamais dans le constructeur)
- Syntaxe control flow : `@if` / `@for` / `@else` (pas `*ngIf` / `*ngFor`)
- `@else` avec plusieurs nodes → wrapper `<ng-container>`

### Java Spring Boot
- DTOs : Java Records (`record MyDto(...)`)
- Validation : annotations Bean Validation sur tous les DTOs
- Erreurs : `GlobalExceptionHandler` avec `ApiError` uniforme
- Logs : `@Slf4j`, `log.error` uniquement sur les 500

---

## Prérequis locaux installés

| Outil | Version | Chemin |
|---|---|---|
| Java | OpenJDK 21 (Temurin) | `C:\Program Files\Eclipse Adoptium\jdk-21.0.10.7-hotspot` |
| Maven | 3.9.8 | `C:\Users\cdominique\AppData\Local\Maven` |
| Node.js | 24.x | dans PATH |
| Angular CLI | 18 | global npm |
| Docker Desktop | ⚠️ À installer en admin | [docker.com](https://www.docker.com/products/docker-desktop/) |

---

## Commandes pour lancer l'appli

```powershell
# Rafraîchir PATH + JAVA_HOME (à faire une fois par terminal)
$env:Path = [System.Environment]::GetEnvironmentVariable("Path","Machine") + ";" + [System.Environment]::GetEnvironmentVariable("Path","User")
$env:JAVA_HOME = "C:\Program Files\Eclipse Adoptium\jdk-21.0.10.7-hotspot"

# Backend (H2, pas besoin de Docker)
cd "c:\Users\cdominique\Documents\Potager\backend\potagerai-backend"
& "$env:LOCALAPPDATA\Maven\bin\mvn.cmd" spring-boot:run "-Dspring-boot.run.profiles=dev-h2"

# Si H2 locked (port ou fichier occupé)
Get-Process java | Stop-Process -Force

# Frontend
cd "c:\Users\cdominique\Documents\Potager\frontend\potagerai-frontend"
ng serve
```

---

## Prochaines étapes (Sprint 2 — restant)

- [ ] Page historique des jardins (`/gardens`)
- [ ] Tests frontend `ng test` (specs AuthService + GardenService)
- [ ] `docker compose` incluant backend + frontend (déploiement tout-en-un)
- [ ] Phase 2 : internationalisation, ETL FAOSTAT, Köppen-Geiger


---

## Stack technique

| Couche | Technologie |
|---|---|
| Backend | Java 21, Spring Boot 3.3, Maven 3.9.8 |
| Base de données | PostgreSQL 16 (via Docker), Flyway migrations |
| Sécurité | JWT HS256 (JJWT 0.12.6), BCrypt, Spring Security stateless |
| Solveur | Apache Commons Math 3.6.1 SimplexSolver (Phase 1) |
| Frontend | Angular 18 standalone, SCSS, Angular Material 18, NgRx 18 |
| Visualisation | D3.js v7 (treemap), Chart.js v4 |
| Tests backend | JUnit 5, Mockito, MockMvc, H2 in-memory |
| Tests frontend | Jasmine, HttpClientTestingModule |

---

## État du projet — Sprint 1 terminé ✅

### Backend (`backend/potagerai-backend/`) ✅
- Package racine : `com.potagerai`
- Architecture : `controller/` → `service/` → `domain/` → `dto/`
- Endpoints principaux :
  - `POST /api/v1/auth/register` + `/auth/login`
  - `GET /api/v1/crops`, `GET /api/v1/crops/{id}`
  - `POST /api/v1/gardens`, `GET /api/v1/gardens`, `GET /api/v1/gardens/{id}`
  - `POST /api/v1/gardens/{id}/optimize` → retourne `OptimizationResultDto` ou 422
- Swagger : `http://localhost:8080/swagger-ui.html`

### Frontend (`frontend/potagerai-frontend/`) ✅
- Composants (tous avec fichiers `.ts` + `.html` + `.scss` séparés) :
  - `LoginComponent`, `RegisterComponent` → `/auth/login`, `/auth/register`
  - `GardenFormComponent` → `/garden` (créé jardin + lance optimisation)
  - `GardenResultComponent` → `/garden/:id/result` (treemap D3, KPIs, tableau)
  - `CropListComponent` → `/crops`
- Services : `AuthService`, `GardenService`, `CropService`
- Guard : `AuthGuard` (redirige `/auth/login` si non connecté)
- Interceptor : `AuthInterceptor` (injecte `Authorization: Bearer`)

### Infrastructure ✅
- `backend/docker-compose.yml` : PostgreSQL 16 (port 5432) + pgAdmin (port 5050)
- `.env.example` : variables d'environnement à configurer
- `.gitignore` inclut `.env` et `application-prod.properties`

---

## Modèles de données clés (TypeScript + Java)

```
Crop { id, commonName, scientificName, botanicalFamily, rootDepthCm,
       growingDaysMin, growingDaysMax, nutritionalProfile: NutritionalProfile }
NutritionalProfile { caloriesPer100g, proteinsPer100g, carbsPer100g,
                     fatsPer100g, fiberPer100g, andiScore }
GardenProfile { id, totalSurfaceM2, householdSize, climateZoneCode,
                climateZoneName, countryIsoCode, countryName, cultivationMethod }
CreateGardenRequest { totalSurfaceM2, householdSize, climateZoneCode: string,
                      countryIsoCode: string, cultivationMethod? }
OptimizationResult { id, gardenProfileId, computedAt, totalCaloriesProduced,
                     calorieTargetAnnual, selfSufficiencyPercent, plotAllocations }
PlotAllocation { cropId, cropName, allocatedSurfaceM2, estimatedYieldKg,
                 estimatedCalories, andiScore }
```

---

## Conventions de code

### Angular 18
- Tous les composants : **standalone**, `templateUrl` + `styleUrl` (jamais inline)
- Injection de dépendances : `private service = inject(Service)` (jamais dans le constructeur)
- Syntaxe control flow : `@if` / `@for` / `@else` (pas `*ngIf` / `*ngFor`)
- `@else` avec plusieurs nodes → wrapper `<ng-container>`

### Java Spring Boot
- DTOs : Java Records (`record MyDto(...)`)
- Validation : annotations Bean Validation sur tous les DTOs
- Erreurs : `GlobalExceptionHandler` avec `ApiError` uniforme
- Logs : `@Slf4j`, `log.error` uniquement sur les 500

---

## Prérequis locaux installés

| Outil | Version | Chemin |
|---|---|---|
| Java | OpenJDK 21 (Temurin) | `C:\Program Files\Eclipse Adoptium\jdk-21.0.10.7-hotspot` |
| Maven | 3.9.8 | `C:\Users\cdominique\AppData\Local\Maven` |
| Node.js | 24.x | dans PATH |
| Angular CLI | 18 | global npm |
| Docker Desktop | ⚠️ À installer en admin | [docker.com](https://www.docker.com/products/docker-desktop/) |

---

## Commandes pour lancer l'appli (après Docker installé)

```powershell
# Rafraîchir PATH + JAVA_HOME (à faire une fois par terminal)
$env:Path = [System.Environment]::GetEnvironmentVariable("Path","Machine") + ";" + [System.Environment]::GetEnvironmentVariable("Path","User")
$env:JAVA_HOME = "C:\Program Files\Eclipse Adoptium\jdk-21.0.10.7-hotspot"

# Terminal 1 — BDD
cd "c:\Users\cdominique\Documents\Potager\backend"
docker compose up -d

# Terminal 2 — Backend
cd "c:\Users\cdominique\Documents\Potager\backend\potagerai-backend"
& "$env:LOCALAPPDATA\Maven\bin\mvn.cmd" spring-boot:run

# Terminal 3 — Frontend
cd "c:\Users\cdominique\Documents\Potager\frontend\potagerai-frontend"
ng serve
```

---

## Prochaines étapes (Sprint 2)

- [ ] Installer Docker Desktop (admin requis) → lancer l'appli complète
- [ ] Tests frontend `ng test` (specs AuthService + GardenService déjà créées)
- [ ] Page historique des jardins
- [ ] `docker compose` incluant backend + frontend (déploiement tout-en-un)
- [ ] Phase 2 : internationalisation, ETL FAOSTAT, Köppen-Geiger
