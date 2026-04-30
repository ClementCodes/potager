# Backend — PotagerAI (Java Spring Boot)

> Ce dossier contiendra le projet Maven `potagerai-backend`.  
> **Statut :** ⬜ En attente de génération (Sprint 1)

## Structure cible

```
potagerai-backend/
├── pom.xml
├── Dockerfile
├── docker-compose.yml
├── src/
│   ├── main/
│   │   ├── java/com/potagerai/
│   │   │   ├── config/          (SecurityConfig, JpaConfig, SwaggerConfig)
│   │   │   ├── domain/          (8 entités JPA)
│   │   │   ├── features/
│   │   │   │   ├── auth/
│   │   │   │   ├── crops/
│   │   │   │   ├── garden/
│   │   │   │   └── optimization/
│   │   │   └── shared/
│   │   └── resources/
│   │       ├── application.properties
│   │       ├── application-prod.properties
│   │       └── db/migration/    (fichiers Flyway)
│   └── test/
│       └── java/com/potagerai/
```

## Prochaine étape

Voir [Sprint 1](../backlog/SPRINT_01.md) — Bloc 1 : Initialisation projet.
