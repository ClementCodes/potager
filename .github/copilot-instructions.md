# PotagerAI — Instructions Copilot (chargé automatiquement)

## Contexte du projet
Application full-stack d'optimisation de jardins potagers vers l'autosuffisance calorique.
- **Auteur :** Clément Dominique — GitHub : ClementCodes
- **Repo :** https://github.com/ClementCodes/potager.git — branche `main`
- **Racine locale :** `c:\Users\cdominique\Documents\Potager\`

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
