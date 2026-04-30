# Changelog — PotagerAI

> Toutes les modifications notables sont documentées ici.  
> Format : [Semantic Versioning](https://semver.org/) — `MAJOR.MINOR.PATCH`

---

## [Unreleased] — Sprint 1 en cours

### À venir
- Initialisation projet Spring Boot + Maven
- Schema BDD + migrations Flyway
- Solveur LP Apache Commons Math
- API REST : Auth + Garden + Optimize
- Frontend Angular 18 initial

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
