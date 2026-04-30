# PotagerAI 🌱

> Système expert d'optimisation spatiale des cultures potagères vers l'autosuffisance alimentaire.

**Stack :** Java 21 · Spring Boot 3.3 · Angular 18 · PostgreSQL 16  
**Solveur :** Apache Commons Math 3.6.1 (Phase 1) → Google OR-Tools GLOP (Phase 2+)  
**Statut :** 🟡 En cours de développement — Phase 1 MVP France

---

## Navigation rapide

| Document | Description |
|---|---|
| [Spécification technique complète](docs/specifications/SPECIFICATION_TECHNIQUE_FONCTIONNELLE.md) | DSTF v1.0 — référence principale |
| [Backlog produit](backlog/BACKLOG.md) | Toutes les tâches par phase et priorité |
| [Sprint en cours](backlog/SPRINT_01.md) | Tâches actives du sprint 1 |
| [Historique](CHANGELOG.md) | Journal des versions et modifications |
| [Décisions d'architecture](DECISIONS.md) | ADR — Architecture Decision Records |

---

## Structure du projet

```
PotagerAI/
├── README.md                        ← Vous êtes ici
├── CHANGELOG.md                     ← Historique des versions
├── DECISIONS.md                     ← Décisions d'architecture (ADR)
│
├── docs/
│   ├── specifications/
│   │   └── SPECIFICATION_TECHNIQUE_FONCTIONNELLE.md
│   ├── architecture/
│   │   └── ARCHITECTURE.md
│   └── api/
│       └── API_CONTRACTS.md
│
├── backlog/
│   ├── BACKLOG.md                   ← Product backlog complet
│   ├── SPRINT_01.md                 ← Sprint 1 (BDD + Backend MVP)
│   └── DONE.md                      ← Tâches terminées archivées
│
├── backend/                         ← Projet Java Spring Boot (Maven)
│   └── potagerai-backend/
│
└── frontend/                        ← Projet Angular
    └── potagerai-frontend/
```

---

## Lancement rapide (quand le code sera généré)

### Prérequis
- Java 21 JDK
- Node.js 20 LTS + npm
- Docker Desktop (pour PostgreSQL en dev)
- Maven 3.9+
- Angular CLI 18 (`npm install -g @angular/cli@18`)

### Backend
```bash
cd backend/potagerai-backend
docker compose up -d          # Démarre PostgreSQL
mvn spring-boot:run           # Démarre l'API sur :8080
```

### Frontend
```bash
cd frontend/potagerai-frontend
npm install
ng serve                      # Démarre l'UI sur :4200
```

### Accès
- API : http://localhost:8080/api/v1
- UI  : http://localhost:4200
- Swagger : http://localhost:8080/swagger-ui.html
- Health : http://localhost:8080/actuator/health

---

## Roadmap des phases

```
Phase 1 ── MVP France (en cours)
 └── BDD SQL + seed France
 └── Backend Spring Boot + Solveur LP
 └── Frontend Angular + Plan 2D + Dashboard

Phase 2 ── Internationalisation
 └── API FAOSTAT (données dynamiques par pays)
 └── Köppen-Geiger géolocalisation
 └── Migration Google OR-Tools

Phase 3 ── Intelligence avancée
 └── Successions de cultures (multi-période)
 └── Rotation & compagnonnage (API Permapeople)
 └── Calendriers phénologiques (GDD)
```

---

*PotagerAI — Démarré le 30 avril 2026*
