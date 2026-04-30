-- =============================================================================
-- V1 : Initialisation du schéma PotagerAI
-- =============================================================================

-- -----------------------------------------------------------------------------
-- USERS
-- -----------------------------------------------------------------------------
CREATE TABLE users (
    id          BIGSERIAL    PRIMARY KEY,
    email       VARCHAR(255) NOT NULL UNIQUE,
    password    VARCHAR(255) NOT NULL,
    created_at  TIMESTAMP    NOT NULL DEFAULT NOW()
);

-- -----------------------------------------------------------------------------
-- COUNTRIES
-- -----------------------------------------------------------------------------
CREATE TABLE countries (
    iso_code            CHAR(3)     PRIMARY KEY,  -- ISO 3166-1 alpha-3
    name                VARCHAR(100) NOT NULL,
    primary_koppen_zone VARCHAR(10),
    fr_climate_zone     VARCHAR(10)
);

-- -----------------------------------------------------------------------------
-- CLIMATE_ZONES
-- -----------------------------------------------------------------------------
CREATE TABLE climate_zones (
    code             VARCHAR(10)    PRIMARY KEY,
    name             VARCHAR(100)   NOT NULL,
    description      TEXT,
    yield_multiplier DECIMAL(4, 2)  NOT NULL DEFAULT 1.00
);

-- -----------------------------------------------------------------------------
-- CROPS
-- -----------------------------------------------------------------------------
CREATE TABLE crops (
    id               BIGSERIAL    PRIMARY KEY,
    common_name      VARCHAR(100) NOT NULL,
    scientific_name  VARCHAR(150),
    botanical_family VARCHAR(100),
    root_depth_cm    DECIMAL(6, 1),
    growing_days_min INTEGER,
    growing_days_max INTEGER
);

-- -----------------------------------------------------------------------------
-- NUTRITIONAL_PROFILES
-- -----------------------------------------------------------------------------
CREATE TABLE nutritional_profiles (
    id                BIGSERIAL     PRIMARY KEY,
    crop_id           BIGINT        NOT NULL REFERENCES crops(id),
    calories_per_100g DECIMAL(8, 2),   -- kcal
    proteins_per_100g DECIMAL(8, 2),   -- g
    carbs_per_100g    DECIMAL(8, 2),   -- g
    fats_per_100g     DECIMAL(8, 2),   -- g
    fiber_per_100g    DECIMAL(8, 2),   -- g
    andi_score        INTEGER,          -- 0 à 1000
    UNIQUE (crop_id)
);

-- -----------------------------------------------------------------------------
-- YIELD_DATA
-- Stocke le rendement de base (zone FR-OCC, multiplicateur = 1.0).
-- Le ClimateAdjustmentService applique dynamiquement : yield_average × zone.yield_multiplier
-- -----------------------------------------------------------------------------
CREATE TABLE yield_data (
    id                      BIGSERIAL     PRIMARY KEY,
    crop_id                 BIGINT        NOT NULL REFERENCES crops(id),
    country_iso             CHAR(3)       NOT NULL REFERENCES countries(iso_code),
    climate_zone_code       VARCHAR(10)   REFERENCES climate_zones(code),
    yield_min_kg_per_m2     DECIMAL(8, 3),
    yield_max_kg_per_m2     DECIMAL(8, 3),
    yield_average_kg_per_m2 DECIMAL(8, 3) NOT NULL,
    cultivation_method      VARCHAR(20)   NOT NULL DEFAULT 'OPEN_AIR'  -- OPEN_AIR | GREENHOUSE
);

-- -----------------------------------------------------------------------------
-- CONSUMPTION_DATA
-- -----------------------------------------------------------------------------
CREATE TABLE consumption_data (
    id                      BIGSERIAL     PRIMARY KEY,
    crop_id                 BIGINT        NOT NULL REFERENCES crops(id),
    country_iso             CHAR(3)       NOT NULL REFERENCES countries(iso_code),
    kg_per_capita_per_year  DECIMAL(10, 3),
    preference_weight       DECIMAL(6, 4) NOT NULL,  -- P_i normalisé [0.0–1.0]
    data_year               INTEGER,
    data_source             VARCHAR(50)   NOT NULL DEFAULT 'STATIC_FR'  -- STATIC_FR | FAOSTAT
);

-- -----------------------------------------------------------------------------
-- GARDEN_PROFILES
-- -----------------------------------------------------------------------------
CREATE TABLE garden_profiles (
    id                  BIGSERIAL     PRIMARY KEY,
    user_id             BIGINT        NOT NULL REFERENCES users(id),
    total_surface_m2    DECIMAL(10, 2) NOT NULL,
    household_size      INTEGER       NOT NULL DEFAULT 1,
    climate_zone_code   VARCHAR(10)   REFERENCES climate_zones(code),
    cultivation_method  VARCHAR(20)   NOT NULL DEFAULT 'OPEN_AIR',
    country_iso_code    CHAR(3)       REFERENCES countries(iso_code),
    created_at          TIMESTAMP     NOT NULL DEFAULT NOW()
);

-- -----------------------------------------------------------------------------
-- OPTIMIZATION_RESULTS
-- -----------------------------------------------------------------------------
CREATE TABLE optimization_results (
    id                       BIGSERIAL      PRIMARY KEY,
    garden_profile_id        BIGINT         NOT NULL REFERENCES garden_profiles(id),
    computed_at              TIMESTAMP      NOT NULL DEFAULT NOW(),
    total_calories_produced  DECIMAL(15, 2),
    calorie_target_annual    DECIMAL(15, 2),
    self_sufficiency_percent DECIMAL(5, 2)
);

-- -----------------------------------------------------------------------------
-- PLOT_ALLOCATIONS  (résultats détaillés par culture — variables x_i du solveur LP)
-- -----------------------------------------------------------------------------
CREATE TABLE plot_allocations (
    id                    BIGSERIAL      PRIMARY KEY,
    result_id             BIGINT         NOT NULL REFERENCES optimization_results(id),
    crop_id               BIGINT         NOT NULL REFERENCES crops(id),
    allocated_surface_m2  DECIMAL(10, 4) NOT NULL,  -- x_i
    estimated_yield_kg    DECIMAL(10, 3),
    estimated_calories    DECIMAL(15, 2)
);

-- =============================================================================
-- INDEX pour les performances de requêtes
-- =============================================================================
CREATE INDEX idx_yield_data_crop_country_zone  ON yield_data(crop_id, country_iso, climate_zone_code);
CREATE INDEX idx_consumption_data_country_src  ON consumption_data(country_iso, data_source);
CREATE INDEX idx_garden_profiles_user          ON garden_profiles(user_id);
CREATE INDEX idx_plot_allocations_result       ON plot_allocations(result_id);
CREATE INDEX idx_nutritional_profiles_crop     ON nutritional_profiles(crop_id);
