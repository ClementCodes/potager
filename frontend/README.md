# Frontend — PotagerAI (Angular 18)

> Ce dossier contiendra le projet Angular `potagerai-frontend`.  
> **Statut :** ⬜ En attente de génération (après Sprint 1 Backend)

## Structure cible

```
potagerai-frontend/
├── package.json
├── angular.json
├── Dockerfile
├── nginx.conf
└── src/
    └── app/
        ├── core/
        │   ├── auth/            (AuthService, JwtInterceptor, AuthGuard)
        │   ├── models/          (TypeScript interfaces)
        │   └── services/        (OptimizationService, GardenService)
        ├── features/
        │   ├── auth/            (Login, Register)
        │   ├── garden-config/   (Formulaire de configuration)
        │   ├── garden-plan/     (Plan 2D D3.js + Dashboard Chart.js)
        │   └── history/
        └── shared/
```

## Prochaine étape

Voir [Backlog](../backlog/BACKLOG.md) — Section Frontend (FE-*), après complétion du Sprint 1 Backend.
