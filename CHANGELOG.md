# Changelog — PotagerAI

> Toutes les modifications notables sont documentées ici.  
> Format : [Semantic Versioning](https://semver.org/) — `MAJOR.MINOR.PATCH`

---

## [Unreleased] — Sprint 2 à planifier

### À venir (Phase 1 — complétion)
- `docker-compose.yml` + Dockerfile backend/frontend
- Tests frontend Angular (`.spec.ts` services + composants)
- Page historique jardins
- Export PDF du plan

### À venir (Phase 2 — Internationalisation)
- Pipeline ETL FAOSTAT
- Classification Köppen-Geiger (GPS → zone climatique)
- i18n Angular (FR / EN / ES)
- Migration solveur OR-Tools Google GLOP

---

## [0.2.0] — 30 avril 2026 — Sprint 1 complet

### Ajouté — Backend Java / Spring Boot 3.3
- `pom.xml` Maven : Spring Boot 3.3, JPA, PostgreSQL, Flyway, Security, Commons Math 3.6.1, JJWT 0.12.6, Lombok, SpringDoc
- Migrations Flyway : `V1__init_schema.sql` (10 tables), `V2__seed_france_data.sql` (14 cultures, 4 zones FR)
- Entités JPA : `User`, `Country`, `ClimateZone`, `Crop`, `NutritionalProfile`, `YieldData`, `ConsumptionData`, `GardenProfile`, `OptimizationResult`, `PlotAllocation`
- Sécurité JWT stateless HS256 : `JwtTokenProvider`, `JwtAuthenticationFilter`, `SecurityConfig`
- Auth : `POST /api/v1/auth/register`, `POST /api/v1/auth/login` (BCrypt)
- Cultures : `GET /api/v1/crops`, `GET /api/v1/crops/{id}`
- Jardin : `POST /api/v1/gardens`, `GET /api/v1/gardens`, `GET /api/v1/gardens/{id}`
- **Cœur métier** : `GardenOptimizerService` — solveur LP SimplexSolver (C1 surface, C2 calories, C3 anti-monoculture)
- Optimisation : `POST /api/v1/gardens/{id}/optimize` → `OptimizationResultDto` ou 422 surface insuffisante
- `ClimateAdjustmentService` : multiplicateurs FR-MED=1.2, FR-OCC=1.0, FR-CON=0.85, FR-MON=0.65
- `GlobalExceptionHandler` : 400/401/404/422/500 structurés en `ApiError`
- Tests : `GardenOptimizerServiceTest`, `AuthControllerTest`, `CropControllerTest`
- Swagger : accessible sur `http://localhost:8080/swagger-ui.html`

### Ajouté — Frontend Angular 18 SPA
- Angular 18 standalone, SCSS, routing lazy-loaded
- Angular Material 18 (thème indigo-pink), NgRx 18, D3.js v7, Chart.js v4
- Composants : `LoginComponent`, `RegisterComponent`, `GardenFormComponent`, `GardenResultComponent`, `CropListComponent`
- Tous les composants respectent la convention `templateUrl` / `styleUrl` (fichiers `.ts` / `.html` / `.scss` séparés)
- `AuthGuard`, `AuthInterceptor` JWT, `AuthService`, `GardenService`, `CropService`
- Treemap D3.js dans `GardenResultComponent` (surfaces proportionnelles, couleur = score ANDI)
- `inject()` function pour toutes les injections de dépendances

### Décisions
- JWT secret injecté via variable d'environnement `JWT_SECRET` (fallback dev uniquement)
- `.env` dans `.gitignore` — ne jamais committer les secrets de production

---

## [0.1.0] — 30 avril 2026 — Phase de conception

### Ajouté
- `docs/specifications/SPECIFICATION_TECHNIQUE_FONCTIONNELLE.md` v1.0
  - 12 sections couvrant vision, roadmap, specs fonctionnelles, modèle de données, moteur LP, architecture backend/frontend, ETL, API contracts, NFR, instructions IA
  - Section 12 : APIs externes vérifiées (Permapeople, Open Food Facts, FAOSTAT bulk, OR-Tools, Spring Boot 3.3)
- `README.md` : point d'entrée du projet
- `BACKLOG.md` : 60+ tâches organisées par phase et priorité
- `SPRINT_01.md` : planification du premier sprint BDD + Backend
- `CHANGELOG.md` : ce fichier
- `DECISIONS.md` : Architecture Decision Records
- Structure de dossiers professionnelle (`docs/`, `backlog/`, `backend/`, `frontend/`)

### Décisions
- Stack retenu : Java 21 / Spring Boot 3.3 / Angular 18 / PostgreSQL 16
- Phase 1 : MVP France uniquement (données statiques, 4 zones climatiques)
- Solveur Phase 1 : Apache Commons Math 3.6.1 SimplexSolver
- Solveur Phase 2+ : Migration vers Google OR-Tools GLOP
- Déploiement : architecture découplée (API + CDN/Nginx)

---

*PotagerAI Changelog — Maintenu depuis le 30 avril 2026*
