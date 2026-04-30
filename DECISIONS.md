# Architecture Decision Records (ADR) — PotagerAI

> Chaque décision architecturale importante est documentée ici avec son contexte, les alternatives considérées et la justification.

---

## ADR-001 — Choix du framework Backend : Spring Boot 3.3

**Date :** 30 avril 2026  
**Statut :** ✅ Accepté

**Contexte :** Besoin d'un framework Java robuste pour exposer une API REST, gérer les transactions JPA et intégrer un solveur mathématique.

**Décision :** Spring Boot 3.3 avec Java 21.

**Alternatives considérées :**
- Quarkus : démarrage plus rapide, mais écosystème moins mature pour le calcul scientifique
- Micronaut : moins familier, moins de documentation disponible
- Jakarta EE : trop verbeux pour un projet de cette taille

**Conséquences :** Accès à Spring Security (JWT), Spring Data JPA (Hibernate), Spring Batch (ETL Phase 2), Virtual Threads (Java 21).

---

## ADR-002 — Solveur LP Phase 1 : Apache Commons Math vs OR-Tools

**Date :** 30 avril 2026  
**Statut :** ✅ Accepté (Phase 1), 🔄 À réviser (Phase 2)

**Contexte :** Le cœur de l'application est un solveur de Programmation Linéaire. Deux options identifiées.

**Décision Phase 1 :** Apache Commons Math 3.6.1 `SimplexSolver`.  
**Décision Phase 2 :** Migrer vers Google OR-Tools GLOP.

**Justification Phase 1 :**
- Zéro dépendance native (pas de librairies `.dll`/`.so` à gérer)
- Intégration Maven simple, compatible CI/CD sans configuration spéciale
- Suffisant pour n ≤ 30 cultures (MVP France)

**Justification migration Phase 2 :**
- OR-Tools ~100x plus rapide sur n > 50 variables
- Gère Mixed-Integer Programming (nécessaire pour les variables binaires de rotation des cultures en Phase 3)
- Mieux maintenu, dernière màj Mars 2026

**Risques Commons Math :** instabilités numériques si coefficients < 0.001. Mitigation : ajuster `epsilon=1e-4`, `cutOff=1e-8`.

---

## ADR-003 — Architecture déployée : Découplée (API + SPA séparés)

**Date :** 30 avril 2026  
**Statut :** ✅ Accepté

**Contexte :** Le frontend Angular et le backend Spring Boot peuvent être déployés ensemble (monobloc) ou séparément.

**Décision :** Architecture découplée. Backend = JAR autonome. Frontend = build statique servi par Nginx.

**Justification :**
- Évolutivité : possibilité de scaler le backend indépendamment du frontend
- CI/CD séparés : l'équipe frontend ne dépend pas du cycle de release backend
- Standard industrie pour les applications de cette nature

**Conséquences techniques :**
- CORS obligatoire côté backend (`app.cors.allowed-origins`)
- Authentification par JWT (stateless) — pas de sessions basées sur cookies
- Le frontend doit stocker le JWT dans `localStorage` ou `sessionStorage`

---

## ADR-004 — Base de données : PostgreSQL vs MySQL

**Date :** 30 avril 2026  
**Statut :** ✅ Accepté

**Contexte :** Choix du SGBD de production pour héberger les données agronomiques et les profils utilisateurs.

**Décision :** PostgreSQL 16.

**Justification :**
- Meilleure gestion des types numériques (important pour les calculs de rendements à virgule)
- Support natif JSONB (utile Phase 3 pour stocker des données de compagnonnage complexes)
- Meilleure conformité SQL standard
- Excellent support dans Spring Boot / Hibernate
- Docker image officielle fiable

---

## ADR-005 — Gestion des migrations BDD : Flyway

**Date :** 30 avril 2026  
**Statut :** ✅ Accepté

**Contexte :** La BDD évolue à chaque sprint. Besoin d'un outil pour versionner les changements de schéma.

**Décision :** Flyway (intégré nativement dans Spring Boot).

**Justification :**
- Exécution automatique au démarrage Spring Boot
- Historique des migrations versionné dans la table `flyway_schema_history`
- Pas de configuration manuelle requise
- Compatible avec H2 (tests) et PostgreSQL (prod)

**Convention de nommage :**
- `V1__init_schema.sql` — Création du schéma initial
- `V2__seed_france_data.sql` — Données France Phase 1
- `V3__add_index_performance.sql` — Optimisations
- `V4__...` — Migrations Phase 2 (internationales)

---

## ADR-006 — Authentification : JWT stateless

**Date :** 30 avril 2026  
**Statut :** ✅ Accepté

**Contexte :** L'API REST doit être sécurisée. Deux grandes approches : sessions (stateful) ou JWT (stateless).

**Décision :** JWT HS256, expiration 24h, stocké côté client.

**Justification :**
- Architecture découplée incompatible avec les sessions serveur (ADR-003)
- JWT permet aux frontends mobiles futurs d'utiliser la même API
- Spring Boot 3.3 auto-configure `JwtAuthenticationConverter`

**Sécurité :**
- Secret minimum 256 bits, injecté via variable d'environnement `JWT_SECRET`
- Jamais stocké en dur dans le code source
- Token de rafraîchissement (`/auth/refresh`) pour renouveler sans reconnexion

---

## ADR-007 — Phase 1 : Données statiques France uniquement

**Date :** 30 avril 2026  
**Statut :** ✅ Accepté

**Contexte :** Les données mondiales (FAOSTAT, GAEZ, Köppen) sont complexes à intégrer. Risque de bloquer le développement du MVP.

**Décision :** Phase 1 = données statiques codées en SQL pour la France uniquement.

**Justification :**
- Permet de valider le solveur LP sans dépendances API externes
- Données françaises connues et vérifiables (Agreste, CNRS)
- Le code backend est conçu pour être extensible (le service `CropService` filtre sur `countryIsoCode` et `dataSource`)
- Réduction du time-to-first-working-endpoint

**Convention Phase 1 :**
- `countryIsoCode = 'FRA'`
- `dataSource = 'STATIC_FR'`
- Zones : `FR-OCC`, `FR-CON`, `FR-MED`, `FR-MON`

---

*DECISIONS.md — PotagerAI — Dernière mise à jour : 30 avril 2026*
