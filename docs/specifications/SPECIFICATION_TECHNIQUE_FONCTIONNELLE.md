# Document de Spécification Technique et Fonctionnelle (DSTF)
## Application : PotagerAI — Optimisation Spatiale des Cultures Potagères vers l'Autosuffisance
**Version :** 1.0  
**Date :** 30 avril 2026  
**Destinataire :** Équipe de développement / Agent IA de génération de code  
**Statut :** RÉFÉRENCE — Prêt pour implémentation

---

## TABLE DES MATIÈRES

1. [Vision Produit & Périmètre](#1-vision-produit--périmètre)
2. [Roadmap — Phases de Livraison](#2-roadmap--phases-de-livraison)
3. [Spécifications Fonctionnelles](#3-spécifications-fonctionnelles)
4. [Modèle de Données (Schéma relationnel)](#4-modèle-de-données-schéma-relationnel)
5. [Moteur Mathématique — Programmation Linéaire](#5-moteur-mathématique--programmation-linéaire)
6. [Architecture Backend — Java Spring Boot](#6-architecture-backend--java-spring-boot)
7. [Architecture Frontend — Angular SPA](#7-architecture-frontend--angular-spa)
8. [Sources de Données & Pipelines ETL](#8-sources-de-données--pipelines-etl)
9. [Contrats d'API REST](#9-contrats-dapi-rest)
10. [Exigences Non-Fonctionnelles](#10-exigences-non-fonctionnelles)
11. [Instructions pour l'Agent IA de Génération](#11-instructions-pour-lagent-ia-de-génération)
12. [Annexe — APIs Externes Vérifiées & Alternatives Techniques](#12-annexe--apis-externes-vérifiées--alternatives-techniques)

---

## 1. Vision Produit & Périmètre

### 1.1 Objectif
PotagerAI est un système expert d'aide à la décision agricole. Il permet à un utilisateur de saisir la superficie disponible de son jardin (en m²) et de recevoir un **plan de plantation optimisé** qui :

- **Maximise** le rendement quantitatif (kg/m²) et la satisfaction nutritionnelle
- **Respecte** les habitudes gastronomiques de son pays
- **Garantit** une couverture calorique et micronutritionnelle suffisante pour tendre vers l'autosuffisance alimentaire

### 1.2 Utilisateurs Cibles
| Profil | Usage principal |
|---|---|
| Particulier urbain | Jardin de 20–200 m² |
| Semi-rural / Micro-ferme | Surface de 200–2000 m² |
| Organisation (ONG, école) | Planification collective |

### 1.3 Contraintes Clés
- Surface minimale viable (autosuffisance calorique stricte) : **300–700 m²/personne**
- Surface recommandée (alimentation équilibrée) : **370 m²/personne** (méthode GROW BIOINTENSIVE)
- Surface minimale légumes frais seulement : **120–150 m²/personne**
- Taille du foyer configurable (n personnes)

---

## 2. Roadmap — Phases de Livraison

### Phase 1 — MVP France (Priorité HAUTE)
> **Objectif :** Système fonctionnel, validé sur le territoire français, sans dépendances API mondiales.

| ID | Fonctionnalité | Complexité |
|---|---|---|
| F-01 | Saisie surface + nombre de personnes | Faible |
| F-02 | Sélection zone climatique France (4 zones) | Faible |
| F-03 | Calcul du plan de plantation via solveur LP | Haute |
| F-04 | Affichage cartographique du jardin optimisé | Moyenne |
| F-05 | Dashboard nutritionnel (% besoins couverts) | Moyenne |
| F-06 | Authentification utilisateur (JWT) | Moyenne |

**4 Zones climatiques France (Phase 1) :**
| Code | Zone | Caractéristiques | Impact Semis |
|---|---|---|---|
| `FR-OCC` | Océanique (Ouest/Bretagne) | Hivers doux, étés modérés | Semis précoces possibles |
| `FR-CON` | Semi-océanique/Continental (Nord/Est/IDF) | Hivers rigoureux | Pleine terre après mi-mai |
| `FR-MED` | Méditerranéen (PACA/Languedoc/Corse) | Étés chauds et secs | Pleine terre dès mai |
| `FR-MON` | Montagne (Massif Central/Alpes/Pyrénées) | Saison courte | Pleine terre dès mi-juin |

**Données de consommation France (Phase 1 — données statiques) :**
| Culture | Part des achats | Poids préférence (`P_i`) |
|---|---|---|
| Tomate | 18,4 % | 0.184 |
| Carotte | 11,6 % | 0.116 |
| Courgette | 7,9 % | 0.079 |
| Concombre | ~6 % | 0.060 |
| Pomme de terre | ~15 % | 0.150 |

### Phase 2 — Internationalisation (Priorité MOYENNE)
- Intégration API FAOSTAT (données dynamiques par pays ISO 3166-1 alpha-3)
- Classification Köppen-Geiger à haute résolution (1 km)
- Base GAEZ v4 pour ajustement sol/climat
- Support multilingue (i18n)

### Phase 3 — Intelligence Avancée (Priorité BASSE)
- Successions de cultures temporelles (modèle multi-période)
- Rotation des cultures (contraintes familles botaniques)
- Compagnonnage (base Permapeople, 8700+ plantes)
- Degrés-Jours de Croissance (GDD) pour calendriers phénologiques

---

## 3. Spécifications Fonctionnelles

### 3.1 Parcours Utilisateur Principal

```
[1] Inscription/Connexion
    ↓
[2] Configuration du profil jardin
    • Surface totale (m²)
    • Nombre de personnes
    • Zone climatique (code postal → zone FR-*)
    • Méthode de culture (plein air / serre)
    ↓
[3] Déclenchement du solveur mathématique
    → POST /api/v1/optimize
    ↓
[4] Affichage du plan optimisé
    • Plan 2D interactif du jardin (surfaces proportionnelles)
    • Tableau récapitulatif par culture
    • Dashboard : % besoins caloriques couverts
    • Dashboard : % apport vitaminique couvert
    ↓
[5] Export (PDF / CSV)
```

### 3.2 Table de Référence des Cultures (données initiales DB)

| Catégorie | Culture | Rendement moyen (kg/m²) | Densité (plants/m²) | Kcal/kg |
|---|---|---|---|---|
| Fruits charnus | Tomate | 4.0–15.0 (FR plein air) | 4–6 | 180 |
| Fruits charnus | Concombre | 4.0–12.0 | 4 | 150 |
| Fruits charnus | Courgette | 4.0–7.0 | 1–2 | 170 |
| Racines/Tubercules | Pomme de terre | 2.0–3.5 (FR) | 4–6 | 770 |
| Racines/Tubercules | Carotte | 5.0–10.0 (FR) | 30–50 | 410 |
| Racines/Tubercules | Betterave | 3.0–6.0 | 35–40 | 430 |
| Alliacées | Oignon | 3.0–6.0 | 35–50 | 400 |
| Alliacées | Ail | 0.5–1.5 | 30–35 | 1490 |
| Légumineuses | Haricot nain | 1.0–2.0 | 30–40 | 350 |
| Légumineuses | Pois | 2.0–4.0 | 30–40 | 810 |
| Légumes feuilles | Laitue | 3.0 | 15–25 | 150 |
| Légumes feuilles | Épinard | 2.0–3.0 | 40–50 | 230 |
| Brassicacées | Chou cabus | 5.0–8.0 | 4 | 250 |
| Brassicacées | Brocoli | 1.5–2.0 | 4 | 340 |

**Index ANDI (Aggregate Nutrient Density Index) — priorité micronutritionnelle :**
| Culture | Score ANDI (0–1000) |
|---|---|
| Kale/Chou frisé | 1000 |
| Épinard | 707 |
| Brocoli | 340 |
| Carotte | 240 |
| Tomate | 190 |
| Pomme de terre | 31 |

---

## 4. Modèle de Données (Schéma relationnel)

### 4.1 Entités JPA (Hibernate)

```java
// --- Entité : Country ---
@Entity @Table(name = "countries")
public class Country {
    @Id @Column(length = 3) private String isoCode;     // ISO 3166-1 alpha-3
    private String name;
    private String primaryKoppenZone;                   // ex: "Cfb"
    private String frClimateZone;                       // ex: "FR-CON" (Phase 1)
}

// --- Entité : Crop ---
@Entity @Table(name = "crops")
public class Crop {
    @Id @GeneratedValue private Long id;
    private String commonName;                          // "Tomate"
    private String scientificName;                      // "Solanum lycopersicum"
    private String botanicalFamily;                     // "Solanaceae"
    private Double rootDepthCm;
    private Integer growingDaysMin;
    private Integer growingDaysMax;
}

// --- Entité : NutritionalProfile ---
@Entity @Table(name = "nutritional_profiles")
public class NutritionalProfile {
    @Id @GeneratedValue private Long id;
    @OneToOne @JoinColumn(name = "crop_id") private Crop crop;
    private Double caloriesPer100g;                     // kcal
    private Double proteinsPer100g;                     // g
    private Double carbsPer100g;                        // g
    private Double fatsPer100g;                         // g
    private Integer andiScore;                          // 0–1000
}

// --- Entité : YieldData ---
@Entity @Table(name = "yield_data")
public class YieldData {
    @Id @GeneratedValue private Long id;
    @ManyToOne private Crop crop;
    @ManyToOne private Country country;
    private String climateZoneCode;                     // "FR-CON"
    private Double yieldMinKgPerM2;
    private Double yieldMaxKgPerM2;
    private Double yieldAverageKgPerM2;                 // Valeur utilisée par le solveur
    private String cultivationMethod;                   // "OPEN_AIR" | "GREENHOUSE"
}

// --- Entité : ConsumptionData ---
@Entity @Table(name = "consumption_data")
public class ConsumptionData {
    @Id @GeneratedValue private Long id;
    @ManyToOne private Crop crop;
    @ManyToOne private Country country;
    private Double kgPerCapitaPerYear;
    private Double preferenceWeight;                    // P_i normalisé [0.0–1.0]
    private Integer dataYear;
    private String dataSource;                          // "FAOSTAT" | "STATIC_FR"
}

// --- Entité : GardenProfile ---
@Entity @Table(name = "garden_profiles")
public class GardenProfile {
    @Id @GeneratedValue private Long id;
    @ManyToOne private User user;
    private Double totalSurfaceM2;
    private Integer householdSize;
    private String climateZoneCode;
    private String cultivationMethod;
    private String countryIsoCode;
    private LocalDateTime createdAt;
}

// --- Entité : OptimizationResult ---
@Entity @Table(name = "optimization_results")
public class OptimizationResult {
    @Id @GeneratedValue private Long id;
    @ManyToOne private GardenProfile gardenProfile;
    private LocalDateTime computedAt;
    private Double totalCaloriesProduced;
    private Double selfSufficiencyPercent;
    @OneToMany(mappedBy = "result") private List<PlotAllocation> allocations;
}

// --- Entité : PlotAllocation ---
@Entity @Table(name = "plot_allocations")
public class PlotAllocation {
    @Id @GeneratedValue private Long id;
    @ManyToOne private OptimizationResult result;
    @ManyToOne private Crop crop;
    private Double allocatedSurfaceM2;                  // x_i — variable de décision LP
    private Double estimatedYieldKg;
    private Double estimatedCalories;
}
```

### 4.2 Diagramme de Relations Simplifiées

```
User ──< GardenProfile ──< OptimizationResult ──< PlotAllocation >── Crop
                                                                       │
Country ──< ConsumptionData >── Crop                      NutritionalProfile
Country ──< YieldData >── Crop
```

---

## 5. Moteur Mathématique — Programmation Linéaire

### 5.1 Formulation du Problème

**Variables de décision :** `x_i` = surface (m²) allouée à la culture `i`  
**Nombre de cultures éligibles :** `n`

**Fonction objectif (MAXIMISER) :**
$$Z = \sum_{i=1}^{n} Y_i \cdot P_i \cdot x_i$$

Où :
- `Y_i` = rendement moyen ajusté de la culture `i` (kg/m²), modulé par le facteur climatique
- `P_i` = poids de préférence culturelle de la culture `i` (issu FAOSTAT ou données statiques France)

**Contraintes :**

| # | Description | Formule |
|---|---|---|
| C1 | Surface totale | `Σ x_i ≤ S_max` |
| C2 | Sécurité calorique | `Σ (Y_i · x_i · Cal_i) ≥ CAL_target` |
| C3 | Diversité — plafond monoculture | `x_i ≤ 0.30 · S_max` pour tout `i` |
| C4 | Non-négativité | `x_i ≥ 0` |

**Calcul `CAL_target` :**  
`CAL_target = householdSize × 2500 kcal/jour × 365 jours`

### 5.2 Implémentation — Apache Commons Math 3.6.1

**Dépendance Maven :**
```xml
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-math3</artifactId>
    <version>3.6.1</version>
</dependency>
```

**Pseudocode du Service :**
```java
// GardenOptimizerService.java
public OptimizationResultDTO optimize(GardenProfileDTO profile) {
    List<EligibleCrop> crops = cropSelector.getEligibleCrops(
        profile.getClimateZoneCode(), profile.getCountryIsoCode()
    );
    int n = crops.size();

    // 1. Coefficients fonction objectif : Y_i * P_i
    double[] objCoeffs = new double[n];
    for (int i = 0; i < n; i++) {
        objCoeffs[i] = crops.get(i).getAdjustedYield() * crops.get(i).getPreferenceWeight();
    }
    LinearObjectiveFunction f = new LinearObjectiveFunction(objCoeffs, 0);

    // 2. Construction des contraintes
    Collection<LinearConstraint> constraints = new ArrayList<>();

    // C1 : Surface totale
    double[] surfaceCoeffs = new double[n];
    Arrays.fill(surfaceCoeffs, 1.0);
    constraints.add(new LinearConstraint(surfaceCoeffs, Relationship.LEQ, profile.getTotalSurfaceM2()));

    // C2 : Cible calorique
    double calTarget = profile.getHouseholdSize() * 2500.0 * 365.0;
    double[] calCoeffs = new double[n];
    for (int i = 0; i < n; i++) {
        // Y_i * Cal_i (kcal/kg) — converti en kcal/m²
        calCoeffs[i] = crops.get(i).getAdjustedYield() * crops.get(i).getCaloriesPerKg();
    }
    constraints.add(new LinearConstraint(calCoeffs, Relationship.GEQ, calTarget));

    // C3 : Plafond monoculture 30%
    for (int i = 0; i < n; i++) {
        double[] monoCoeffs = new double[n];
        monoCoeffs[i] = 1.0;
        constraints.add(new LinearConstraint(monoCoeffs, Relationship.LEQ,
            0.30 * profile.getTotalSurfaceM2()));
    }

    // 3. Résolution
    SimplexSolver solver = new SimplexSolver();
    PointValuePair solution = solver.optimize(
        f,
        new LinearConstraintSet(constraints),
        GoalType.MAXIMIZE,
        new NonNegativeConstraint(true)
    );

    return buildResultDTO(solution.getPoint(), crops, profile);
}
```

**Gestion des erreurs critiques :**
- `NoFeasibleSolutionException` → Surface insuffisante : retourner un message avec la surface minimale requise calculée
- `TooManyIterationsException` → Réduire `n` (filtrer les cultures à très faible `P_i < 0.01`)
- Ajuster `epsilon` si rendements < 0.001 (herbes aromatiques)

---

## 6. Architecture Backend — Java Spring Boot

### 6.1 Stack Technique

| Composant | Technologie | Version |
|---|---|---|
| Framework | Spring Boot | 3.3.x |
| Langage | Java | 21 LTS |
| ORM | Hibernate / Spring Data JPA | inclus Boot |
| Solveur LP | Apache Commons Math | 3.6.1 |
| Sécurité | Spring Security + JWT | inclus Boot |
| Base de données prod | PostgreSQL | 16 |
| Base de données test | H2 (in-memory) | inclus Boot |
| Build | Maven | 3.9.x |
| Conteneurisation | Docker | 24.x |

### 6.2 Structure des Packages (Feature-based)

```
com.potagerai/
├── config/
│   ├── SecurityConfig.java          # JWT filter, CORS, CSRF
│   └── JpaConfig.java
├── features/
│   ├── auth/
│   │   ├── AuthController.java
│   │   ├── AuthService.java
│   │   └── dto/  (LoginRequest, TokenResponse)
│   ├── garden/
│   │   ├── GardenController.java    # POST /api/v1/gardens
│   │   ├── GardenService.java
│   │   ├── GardenRepository.java
│   │   └── dto/
│   ├── optimization/
│   │   ├── OptimizationController.java  # POST /api/v1/optimize
│   │   ├── GardenOptimizerService.java  # Solveur LP
│   │   ├── ClimateAdjustmentService.java
│   │   └── dto/  (OptimizationRequest, OptimizationResultDTO)
│   ├── crops/
│   │   ├── CropController.java      # GET /api/v1/crops
│   │   ├── CropService.java
│   │   └── CropRepository.java
│   └── data/
│       ├── DataIngestionService.java  # Pipeline ETL
│       └── FaostatClient.java         # Phase 2
├── domain/                           # Entités JPA
│   ├── Country.java
│   ├── Crop.java
│   ├── NutritionalProfile.java
│   ├── YieldData.java
│   ├── ConsumptionData.java
│   ├── GardenProfile.java
│   ├── OptimizationResult.java
│   └── PlotAllocation.java
└── shared/
    ├── exception/
    │   └── GlobalExceptionHandler.java
    └── security/
        └── JwtTokenProvider.java
```

### 6.3 Configuration `application.properties`

```properties
# Base de données (production)
spring.datasource.url=jdbc:postgresql://localhost:5432/potagerai
spring.datasource.username=${DB_USER}
spring.datasource.password=${DB_PASS}
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=false

# JWT
jwt.secret=${JWT_SECRET}
jwt.expiration-ms=86400000

# CORS (déploiement découplé)
app.cors.allowed-origins=${FRONTEND_URL:http://localhost:4200}
```

---

## 7. Architecture Frontend — Angular SPA

### 7.1 Stack Technique

| Composant | Technologie | Version |
|---|---|---|
| Framework | Angular | 18.x |
| Langage | TypeScript | 5.x |
| UI Components | Angular Material | 18.x |
| Visualisation | Chart.js + ng2-charts | 4.x |
| Cartographie jardin | D3.js | 7.x |
| État global | NgRx (Store) | 18.x |
| HTTP Client | Angular HttpClient + Interceptors | inclus Angular |
| Tests | Karma + Jasmine | inclus Angular |
| Build | Angular CLI | 18.x |

### 7.2 Structure des Modules

```
src/app/
├── core/
│   ├── auth/
│   │   ├── auth.service.ts
│   │   ├── jwt.interceptor.ts
│   │   └── auth.guard.ts
│   ├── models/
│   │   ├── garden-profile.model.ts
│   │   ├── optimization-result.model.ts
│   │   └── crop.model.ts
│   └── services/
│       ├── api.service.ts
│       └── optimization.service.ts
├── features/
│   ├── auth/
│   │   ├── login/
│   │   └── register/
│   ├── garden-config/           # Formulaire de configuration jardin
│   │   ├── garden-config.component.ts
│   │   └── climate-zone-selector.component.ts
│   ├── garden-plan/             # Résultats et visualisation
│   │   ├── garden-map.component.ts      # Plan 2D D3.js
│   │   ├── nutrition-dashboard.component.ts  # Chart.js
│   │   └── crop-table.component.ts
│   └── history/                 # Historique des plans
├── shared/
│   └── components/  (spinner, error-card, etc.)
└── app-routing.module.ts
```

### 7.3 Modèles TypeScript

```typescript
// optimization-result.model.ts
export interface PlotAllocation {
  cropId: number;
  cropName: string;
  botanicalFamily: string;
  allocatedSurfaceM2: number;
  surfacePercent: number;           // Pour le plan 2D
  estimatedYieldKg: number;
  estimatedCalories: number;
  andiScore: number;
}

export interface OptimizationResult {
  id: number;
  computedAt: string;
  totalSurfaceM2: number;
  totalCaloriesProduced: number;
  calorieTargetAnnual: number;
  selfSufficiencyPercent: number;   // Affiché en dashboard
  allocations: PlotAllocation[];
  warnings: string[];               // ex: "Surface insuffisante pour besoins caloriques"
}
```

### 7.4 Visualisation — Plan 2D du Jardin (D3.js)

**Principe :** Treemap proportionnel aux surfaces `x_i`. Chaque cellule affiche :
- Nom de la culture
- Surface allouée (m²)
- Rendement estimé (kg)
- Couleur par famille botanique

```typescript
// garden-map.component.ts — logique D3
buildTreemap(allocations: PlotAllocation[]): void {
  const root = d3.hierarchy({ children: allocations })
    .sum(d => (d as any).allocatedSurfaceM2);
  
  d3.treemap().size([this.width, this.height]).padding(3)(root);
  // Rendu SVG avec labels et tooltips interactifs
}
```

### 7.5 Dashboard Nutritionnel (Chart.js)

**Graphiques à inclure :**
- Doughnut : % besoins caloriques couverts
- Bar chart horizontal : contribution par culture (kcal)
- Radar chart : profil nutritionnel (glucides, protéines, lipides, vitamines)
- Progress bars : vitamines individuelles (A, C, K, Fer…)

---

## 8. Sources de Données & Pipelines ETL

### 8.1 Phase 1 — Données Statiques France (fichiers SQL/JSON)

Charger via `data.sql` au démarrage (Spring Boot `spring.sql.init.mode=always`) :

```sql
-- Données pays France
INSERT INTO countries VALUES ('FRA', 'France', 'Cfb', 'FR-CON');

-- Zones climatiques France (table de référence)
INSERT INTO climate_zones VALUES ('FR-OCC', 'Océanique'), ('FR-CON', 'Continental'), ...;

-- Rendements France (YieldData)
INSERT INTO yield_data (crop_id, country_iso, climate_zone, yield_avg, method)
VALUES (1, 'FRA', 'FR-CON', 10.0, 'OPEN_AIR');  -- Tomate

-- ConsumptionData statiques France
INSERT INTO consumption_data (crop_id, country_iso, kg_per_capita, preference_weight, source)
VALUES (1, 'FRA', 18.5, 0.184, 'STATIC_FR');    -- Tomate 18.4%
```

### 8.2 Phase 2 — API FAOSTAT (dynamique)

**Endpoint FAOSTAT :**
```
GET https://fenixservices.fao.org/faostat/api/v1/en/data/FBS
    ?area=FRA&element=645&item=2918&year=2022&output_type=objects
```

**`FaostatClient.java` (Spring RestClient) :**
```java
@Service
public class FaostatClient {
    private final RestClient restClient;
    
    public List<FaostatRecord> fetchConsumptionData(String isoAlpha2, int year) {
        // items à agréger: 2918 (Vegetables), 2905 (Cereals), 2515 (Starchy Roots)
        // Fallback sur année précédente si données absentes
    }
}
```

**Codes FAO à agréger (éviter l'exclusion tubercules) :**
- `2918` — Légumes (Vegetables)
- `2515` — Racines et tubercules amylacés (dont pomme de terre)
- `2546` — Légumineuses sèches
- `2905` — Céréales (si version étendue)

### 8.3 Phase 3 — Köppen-Geiger & GAEZ

| Source | Usage | Intégration |
|---|---|---|
| Köppen-Geiger (Beck et al.) | Zone climatique depuis GPS | Library Java `kcc` ou appel REST |
| GAEZ v4 (FAO/IIASA) | Facteur d'ajustement rendement par sol | API REST GAEZ ou CSV pré-traité |
| USDA FoodData Central | Profils nutritionnels | API REST ou JSON statique |
| Degree Days API | Calendriers GDD | API REST payante |

---

## 9. Contrats d'API REST

### Base URL : `/api/v1`

### 9.1 Authentification

| Méthode | Endpoint | Description |
|---|---|---|
| `POST` | `/auth/register` | Inscription |
| `POST` | `/auth/login` | Connexion → retourne JWT |
| `POST` | `/auth/refresh` | Rafraîchissement token |

**POST /auth/login — Request Body :**
```json
{ "email": "user@example.com", "password": "..." }
```
**Response 200 :**
```json
{ "accessToken": "eyJ...", "tokenType": "Bearer", "expiresIn": 86400 }
```

### 9.2 Jardins

| Méthode | Endpoint | Description |
|---|---|---|
| `POST` | `/gardens` | Créer un profil jardin |
| `GET` | `/gardens` | Lister mes jardins |
| `GET` | `/gardens/{id}` | Détail d'un jardin |

**POST /gardens — Request Body :**
```json
{
  "totalSurfaceM2": 150.0,
  "householdSize": 2,
  "climateZoneCode": "FR-CON",
  "cultivationMethod": "OPEN_AIR",
  "countryIsoCode": "FRA"
}
```

### 9.3 Optimisation

| Méthode | Endpoint | Description |
|---|---|---|
| `POST` | `/optimize` | Lancer le solveur LP |
| `GET` | `/optimize/{gardenId}/latest` | Dernier résultat |
| `GET` | `/optimize/{resultId}` | Résultat par ID |

**POST /optimize — Request Body :**
```json
{ "gardenProfileId": 42 }
```

**Response 200 :**
```json
{
  "id": 101,
  "computedAt": "2026-04-30T14:23:00",
  "totalSurfaceM2": 150.0,
  "totalCaloriesProduced": 1825000,
  "calorieTargetAnnual": 1825000,
  "selfSufficiencyPercent": 100.0,
  "allocations": [
    {
      "cropId": 1,
      "cropName": "Tomate",
      "botanicalFamily": "Solanaceae",
      "allocatedSurfaceM2": 27.6,
      "surfacePercent": 18.4,
      "estimatedYieldKg": 276.0,
      "estimatedCalories": 49680,
      "andiScore": 190
    }
  ],
  "warnings": []
}
```

**Response 422 (surface insuffisante) :**
```json
{
  "error": "NO_FEASIBLE_SOLUTION",
  "message": "Surface insuffisante. Minimum requis : 280 m² pour 2 personnes.",
  "requiredSurfaceM2": 280.0
}
```

### 9.4 Cultures

| Méthode | Endpoint | Description |
|---|---|---|
| `GET` | `/crops` | Liste toutes les cultures |
| `GET` | `/crops?climateZone=FR-CON` | Filtrer par zone |
| `GET` | `/crops/{id}` | Détail + profil nutritionnel |

---

## 10. Exigences Non-Fonctionnelles

### 10.1 Performance
- Temps de réponse solveur LP : **< 2 secondes** pour `n ≤ 50 cultures`
- API REST : **< 500 ms** pour les requêtes lecture
- Frontend initial load : **< 3 secondes** (lazy loading Angular)

### 10.2 Sécurité (OWASP Top 10)
- Authentification JWT (HS256, expiration 24h)
- HTTPS obligatoire en production
- Paramétrage strict CORS : origins whitelistées uniquement
- Validation des entrées côté serveur (Bean Validation `@NotNull`, `@Min`, `@Max`)
- Protection CSRF via Double Submit Cookie (pour les endpoints non-API)
- Pas d'entités JPA exposées directement (DTO pattern systématique)
- Secrets en variables d'environnement (jamais en clair dans le code)

### 10.3 Disponibilité & Déploiement
- Architecture découplée : Backend (Spring Boot JAR) + Frontend (CDN/Nginx)
- Dockerisation obligatoire (`Dockerfile` + `docker-compose.yml`)
- Health endpoint : `GET /actuator/health`

### 10.4 Maintenabilité
- Couverture de tests unitaires : **≥ 80%** sur `GardenOptimizerService`
- Tests d'intégration sur les endpoints REST (MockMvc)
- Logging structuré (Logback + JSON en production)

---

## 11. Instructions pour l'Agent IA de Génération

> Cette section est destinée à un agent IA qui lirait ce document pour générer le code source.

### Ordre de génération recommandé

**Étape 1 — Setup du projet**
1. Générer le `pom.xml` Maven avec les dépendances listées en §6.1
2. Générer `application.properties` (dev) et `application-prod.properties`
3. Générer `docker-compose.yml` (PostgreSQL + Spring Boot + Angular Nginx)

**Étape 2 — Domaine & Base de données**
1. Générer les 8 entités JPA de la §4.1
2. Générer les interfaces `JpaRepository` pour chaque entité
3. Générer le fichier `data.sql` avec les données initiales France (§8.1)
4. Générer les migrations Flyway (`V1__init_schema.sql`, `V2__seed_france_data.sql`)

**Étape 3 — Backend Feature par Feature**
1. `auth` : `AuthController`, `AuthService`, `JwtTokenProvider`, `SecurityConfig`
2. `crops` : `CropController`, `CropService` (lecture seule)
3. `garden` : `GardenController`, `GardenService` (CRUD profil)
4. `optimization` : `GardenOptimizerService` (LP solver §5.2), `OptimizationController`
5. `GlobalExceptionHandler` : gestion `NoFeasibleSolutionException`, `EntityNotFoundException`

**Étape 4 — Frontend**
1. Générer les modèles TypeScript (§7.3)
2. Générer `AuthService` + `JwtInterceptor` + `AuthGuard`
3. Générer le composant `GardenConfigComponent` (formulaire réactif Angular)
4. Générer `OptimizationService` (appel POST /api/v1/optimize)
5. Générer `GardenMapComponent` (Treemap D3.js proportionnel)
6. Générer `NutritionDashboardComponent` (Chart.js doughnut + bar)

**Étape 5 — Tests**
1. Tests unitaires `GardenOptimizerServiceTest` (JUnit 5 + Mockito)
2. Tests intégration `OptimizationControllerIT` (MockMvc + H2)
3. Tests Angular (Karma/Jasmine sur les services)

### Contraintes impératives pour le générateur

- **Toujours utiliser des DTOs** — ne jamais exposer une entité JPA dans un `@RestController`
- **Valider les entrées** côté backend : `totalSurfaceM2 > 0`, `householdSize >= 1 && <= 20`
- **Ne jamais hard-coder** les secrets JWT ni les credentials DB — utiliser `@Value("${...}")`
- **Le solveur LP** doit être dans sa propre classe de service, injectée, testable unitairement
- **Les coefficients `Y_i`** doivent être récupérés depuis `YieldData` (DB), jamais en dur
- **Les coefficients `P_i`** doivent être récupérés depuis `ConsumptionData` (DB)
- **Phase 1 seulement** : filtrer sur `country.isoCode = 'FRA'` et `dataSource = 'STATIC_FR'`
- L'endpoint `/optimize` doit retourner un **422** (Unprocessable Entity) si `NoFeasibleSolutionException`
- Le frontend doit gérer le **401** (token expiré) et rediriger vers `/login` via intercepteur

### Variables d'environnement requises à documenter dans le README

```
DB_URL       = jdbc:postgresql://localhost:5432/potagerai
DB_USER      = potagerai_user
DB_PASS      = [SECRET]
JWT_SECRET   = [SECRET 256-bit min]
FRONTEND_URL = https://potagerai.example.com
```

---

## 12. Annexe — APIs Externes Vérifiées & Alternatives Techniques

> Section ajoutée suite à une recherche internet approfondie (30/04/2026).

### 12.1 Alternative au Solveur : Google OR-Tools (recommandé Phase 2+)

Apache Commons Math convient pour le MVP, mais **Google OR-Tools** est un solveur LP/MIP de grade industriel, open-source, maintenu par Google, avec un SDK Java natif. Il offre un solveur GLOP (LP pur) et CBC (Mixed Integer) nettement plus performants.

**Avantages vs Commons Math :**
- Solveur GLOP natif, ~100x plus rapide sur les problèmes à 50+ variables
- Gestion native des problèmes infaisables avec diagnostics
- Support Mixed-Integer (utile Phase 3 : variables binaires pour rotation)
- Bien documenté, activement maintenu (dernière maj 18/03/2026)

**Dépendance Maven :**
```xml
<dependency>
    <groupId>com.google.ortools</groupId>
    <artifactId>ortools-java</artifactId>
    <version>9.10.4067</version>
</dependency>
```

**Exemple d'implémentation (alternative à Commons Math) :**
```java
import com.google.ortools.Loader;
import com.google.ortools.linearsolver.MPConstraint;
import com.google.ortools.linearsolver.MPObjective;
import com.google.ortools.linearsolver.MPSolver;
import com.google.ortools.linearsolver.MPVariable;

public OptimizationResultDTO optimizeWithOrTools(GardenProfileDTO profile) {
    Loader.loadNativeLibraries();
    MPSolver solver = MPSolver.createSolver("GLOP");

    int n = crops.size();
    MPVariable[] x = new MPVariable[n];
    for (int i = 0; i < n; i++) {
        x[i] = solver.makeNumVar(0.0, 0.30 * profile.getTotalSurfaceM2(), "x_" + i);
    }

    // C1 : Surface totale
    MPConstraint surfaceCt = solver.makeConstraint(0.0, profile.getTotalSurfaceM2(), "surface");
    for (int i = 0; i < n; i++) surfaceCt.setCoefficient(x[i], 1.0);

    // C2 : Cible calorique
    double calTarget = profile.getHouseholdSize() * 2500.0 * 365.0;
    MPConstraint calCt = solver.makeConstraint(calTarget, Double.POSITIVE_INFINITY, "calories");
    for (int i = 0; i < n; i++) {
        calCt.setCoefficient(x[i], crops.get(i).getAdjustedYield() * crops.get(i).getCaloriesPerKg());
    }

    // Fonction objectif
    MPObjective objective = solver.objective();
    for (int i = 0; i < n; i++) {
        objective.setCoefficient(x[i], crops.get(i).getAdjustedYield() * crops.get(i).getPreferenceWeight());
    }
    objective.setMaximization();

    MPSolver.ResultStatus status = solver.solve();
    if (status != MPSolver.ResultStatus.OPTIMAL && status != MPSolver.ResultStatus.FEASIBLE) {
        throw new NoFeasibleSolutionException("Surface insuffisante");
    }

    // Extraction des résultats x[i].solutionValue()
    return buildResult(x, crops, profile);
}
```

**Stratégie recommandée :** Commons Math en Phase 1 (zéro dépendance native), migration vers OR-Tools en Phase 2 si > 30 cultures éligibles.

---

### 12.2 API Permapeople — Base de Données Plantes & Compagnonnage (Phase 3)

Base open-source de **8500+ profils de plantes** avec données de compagnonnage, zones de rusticité, types de sol.

| Élément | Détail |
|---|---|
| **Endpoint** | `https://permapeople.org/api` |
| **Authentification** | Headers : `x-permapeople-key-id` + `x-permapeople-key-secret` |
| **Licence** | CC BY-SA 4.0 (attribution obligatoire) |
| **Accès** | Demander une clé sur `permapeople.org/api_requests/new` |
| **Limite** | Usage non-commercial gratuit ; commercial sur demande |

**Endpoints utiles :**
```
GET  /api/plants            — Liste paginée (100/page, param: last_id)
GET  /api/plants/{id}       — Détail plante complète
POST /api/search            — Recherche full-text { "q": "tomato" }
```

**Structure de réponse d'une plante :**
```json
{
  "id": 101,
  "scientific_name": "Morus alba",
  "name": "White mulberry",
  "data": [
    { "key": "Family", "value": "Moraceae" },
    { "key": "USDA Hardiness zone", "value": "3-9" },
    { "key": "Soil type", "value": "Light (sandy), medium, heavy (clay)" },
    { "key": "Light requirement", "value": "Full sun, partial sun/shade" },
    { "key": "Water requirement", "value": "Moist" },
    { "key": "Edible parts", "value": "Fruit, inner bark, leaves" }
  ]
}
```

**Entité JPA additionnelle (Phase 3) :**
```java
@Entity @Table(name = "companion_planting")
public class CompanionPlanting {
    @Id @GeneratedValue private Long id;
    @ManyToOne private Crop crop;
    @ManyToOne private Crop companionCrop;
    private String interactionType;  // "BENEFICIAL" | "ANTAGONIST" | "NEUTRAL"
    private String dataSource;       // "PERMAPEOPLE" | "MANUAL"
}
```

---

### 12.3 API Open Food Facts — Données Nutritionnelles (Alternative USDA)

Base **open data** mondiale de produits alimentaires. SDK Spring Boot officiel disponible.

| Élément | Détail |
|---|---|
| **API v2 (production)** | `https://world.openfoodfacts.org/api/v2/` |
| **API v2 (staging/test)** | `https://world.openfoodfacts.net/api/v2/` (auth: `off`/`off`) |
| **Licence** | Open Database License (ODbL) |
| **Rate limits** | 15 req/min/IP (lecture), 10 req/min (recherche) |
| **SDK Java** | `github.com/openfoodfacts/openfoodfacts-java` |
| **SDK Spring Boot** | `github.com/openfoodfacts/openfoodfacts-springboot-starter` |
| **User-Agent requis** | Format: `PotagerAI/1.0 (contact@potagerai.com)` |

**Exemple de requête produit :**
```
GET https://world.openfoodfacts.org/api/v2/product/3274080005003.json
```

**Intégration recommandée :**
```xml
<!-- pom.xml — SDK Spring Boot officiel -->
<dependency>
    <groupId>org.openfoodfacts</groupId>
    <artifactId>openfoodfacts-springboot-starter</artifactId>
    <version>LATEST</version>
</dependency>
```

**Usage pour PotagerAI :** Enrichir les profils nutritionnels (`NutritionalProfile`) avec les données `nutriments` (energy-kcal_100g, proteins_100g, carbohydrates_100g, fat_100g, fiber_100g, vitamins...). Utile comme complément/fallback si les données USDA sont incomplètes pour certains légumes régionaux.

**Important :** Pour un chargement bulk (toutes les cultures), télécharger le dump CSV/JSONL plutôt que requêter l'API :
- CSV complet : `https://static.openfoodfacts.org/data/en.openfoodfacts.org.products.csv`
- JSONL : `https://static.openfoodfacts.org/data/openfoodfacts-products.jsonl.gz`

---

### 12.4 FAOSTAT — Téléchargement Bulk (Phase 2)

Le portail FAOSTAT offre des **téléchargements bulk en CSV** directement, plus fiable qu'une API REST instable.

**URLs de téléchargement direct (Food Balance Sheets) :**
```
ALL DATA       : https://bulks-faostat.fao.org/production/FoodBalanceSheets_E_All_Data.zip         (21.33 MB)
ALL NORMALIZED : https://bulks-faostat.fao.org/production/FoodBalanceSheets_E_All_Data_(Normalized).zip (53.56 MB)
EUROPE         : https://bulks-faostat.fao.org/production/FoodBalanceSheets_E_Europe.zip           (3.94 MB)
AFRICA         : https://bulks-faostat.fao.org/production/FoodBalanceSheets_E_Africa.zip           (3.91 MB)
AMERICAS       : https://bulks-faostat.fao.org/production/FoodBalanceSheets_E_Americas.zip         (3.23 MB)
ASIA           : https://bulks-faostat.fao.org/production/FoodBalanceSheets_E_Asia.zip             (4.48 MB)
OCEANIA        : https://bulks-faostat.fao.org/production/FoodBalanceSheets_E_Oceania.zip          (1.01 MB)
```

**Dernière mise à jour :** 28 octobre 2025  
**Licence :** CC BY 4.0

**Stratégie d'ingestion recommandée (pipeline ETL Spring Batch) :**
1. Télécharger le ZIP Europe + Amériques + Asie à la compilation (Maven plugin)
2. Parser les CSV au démarrage de l'application via un `@EventListener(ApplicationReadyEvent.class)`
3. Filtrer sur `Element Code = 645` (Food supply quantity kg/capita/yr)
4. Stocker en table `consumption_data` avec `dataSource = "FAOSTAT"`
5. Rafraîchir trimestriellement via une tâche `@Scheduled`

**Piège confirmé (recherche) :** Les Item Codes FAOSTAT à agréger obligatoirement :
- `2918` — Vegetables (exclut pommes de terre !)
- `2515` — Starchy Roots (pommes de terre, manioc, ignames)
- `2546` — Pulses (légumineuses sèches : lentilles, haricots secs)
- `2547` — Treenuts (noix, amandes — si espace disponible)

---

### 12.5 Spring Boot 3.3 — Fonctionnalités à Exploiter

Confirmé par la documentation officielle Spring (mai 2024), les nouvelles fonctionnalités de Spring Boot 3.3 directement applicables :

| Fonctionnalité | Application pour PotagerAI |
|---|---|
| **CDS (Class Data Sharing)** | Réduction du temps de démarrage du JAR de 30-50% — critique pour les cold starts en conteneur |
| **Auto-config `JwtAuthenticationConverter`** | Simplifie la sécurité JWT (moins de code custom dans `SecurityConfig`) |
| **Virtual Threads (Java 21)** | Activer `spring.threads.virtual.enabled=true` pour les appels I/O vers FAOSTAT/OpenFoodFacts |
| **SBOM Actuator** | Endpoint `/actuator/sbom` pour la traçabilité des dépendances (compliance) |
| **SSL SNI** | Support natif pour HTTPS multi-domaines sur le serveur embarqué |
| **Docker Compose intégré** | Support Bitnami PostgreSQL image dans `compose.yaml` |

**Configuration `application.properties` enrichie :**
```properties
# Virtual Threads (Java 21 — améliore les appels API externes)
spring.threads.virtual.enabled=true

# CDS pour démarrage rapide
spring.context.class-data-sharing.enabled=true

# Docker Compose automatique en dev
spring.docker.compose.enabled=true
spring.docker.compose.lifecycle-management=start-and-stop
```

---

### 12.6 SimplexSolver — Paramétrage Avancé Confirmé

Recherche confirmée sur la documentation officielle Apache Commons Math 3.6.1 :

**Constructeurs et paramètres de tuning :**
```java
// Configuration recommandée pour PotagerAI
SimplexSolver solver = new SimplexSolver(
    1e-4,    // epsilon : relâché (défaut 1e-6) car nos rendements ont 1-2 décimales
    10,      // maxUlps : comparaisons flottantes (défaut 10, garder)
    1e-8     // cutOff : relevé (défaut 1e-10) car pas de coefficients < 0.001
);

// Limiter les itérations pour éviter les timeouts
PointValuePair solution = solver.optimize(
    f,
    new LinearConstraintSet(constraints),
    GoalType.MAXIMIZE,
    new NonNegativeConstraint(true),
    new MaxIter(10000),                   // Protection timeout
    new SolutionCallback()                // Récupérer la meilleure solution même si non-optimale
    PivotSelectionRule.BLAND              // Évite le cyclage (anti-dégénérescence)
);
```

**`SolutionCallback`** — Récupérer une solution sub-optimale si le solveur atteint `MaxIter` :
```java
SolutionCallback callback = new SolutionCallback();
try {
    solution = solver.optimize(f, constraints, GoalType.MAXIMIZE,
        new NonNegativeConstraint(true), new MaxIter(5000), callback);
} catch (TooManyIterationsException e) {
    // Récupérer la meilleure solution trouvée avant épuisement
    if (callback.isSolutionOptimal()) {
        solution = callback.getSolution();
    } else {
        throw new PartialSolutionException("Solution partielle disponible", callback.getSolution());
    }
}
```

**`PivotSelectionRule.BLAND`** — Obligatoire si les tests révèlent du cyclage (boucles infinies sur certaines configurations de jardin).

---

### 12.7 Concurrents & Inspirations Identifiés

| Produit | Points forts | Lacunes | Leçon pour PotagerAI |
|---|---|---|---|
| **Permapeople Garden Planner** | Plan 2D interactif, 8500+ plantes, open data | Pas de solveur d'optimisation, pas de nutritionnel | Notre valeur ajoutée = le solveur LP |
| **Fryd (fryd.app)** | Calculateur de rendement, calendrier de semis | Pas d'optimisation multi-critères, pas d'autosuffisance | Intégrer un calendrier de semis (Phase 3) |
| **Smart Gardener** | Recommandations par zone USDA | US only, pas de préférence culturelle | Notre avantage = international + données FAO |
| **GrowVeg** | UX excellent, plans par glisser-déposer | Payant, fermé, pas d'optimisation mathématique | Inspirer l'UX Angular du plan 2D |

---

*Document généré le 30 avril 2026 — PotagerAI v1.0 — Référence pour implémentation*  
*Dernière mise à jour : 30 avril 2026 (enrichissement APIs et alternatives techniques)*
