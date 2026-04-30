# PotagerAI 🌱

> Système expert d'optimisation spatiale des cultures potagères vers l'autosuffisance alimentaire.

**Stack :** Java 21 · Spring Boot 3.3 · Angular 18 · PostgreSQL 16  
**Solveur :** Apache Commons Math 3.6.1 (Phase 1) → Google OR-Tools GLOP (Phase 2+)  
**Statut :** ✅ Sprint 1 terminé — Phase 1 MVP France prête à lancer

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

## Lancement rapide

### Prérequis installés
- ✅ Java 21 (Temurin) — `C:\Program Files\Eclipse Adoptium\jdk-21.0.10.7-hotspot`
- ✅ Maven 3.9.8 — `C:\Users\cdominique\AppData\Local\Maven`
- ✅ Node.js 24 + Angular CLI 18
- ⚠️ Docker Desktop — **à installer en admin** : [docker.com](https://www.docker.com/products/docker-desktop/)

### Lancer l'appli (dans 3 terminaux PowerShell)

```powershell
# Toujours faire en premier dans chaque terminal :
$env:Path = [System.Environment]::GetEnvironmentVariable("Path","Machine") + ";" + [System.Environment]::GetEnvironmentVariable("Path","User")
$env:JAVA_HOME = "C:\Program Files\Eclipse Adoptium\jdk-21.0.10.7-hotspot"
```

```powershell
# Terminal 1 — BDD PostgreSQL
cd "c:\Users\cdominique\Documents\Potager\backend"
docker compose up -d
```

```powershell
# Terminal 2 — Backend Spring Boot (port 8080)
cd "c:\Users\cdominique\Documents\Potager\backend\potagerai-backend"
& "$env:LOCALAPPDATA\Maven\bin\mvn.cmd" spring-boot:run
```

```powershell
# Terminal 3 — Frontend Angular (port 4200)
cd "c:\Users\cdominique\Documents\Potager\frontend\potagerai-frontend"
ng serve
```

### Accès
- UI : http://localhost:4200
- API : http://localhost:8080/api/v1
- Swagger : http://localhost:8080/swagger-ui.html

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
