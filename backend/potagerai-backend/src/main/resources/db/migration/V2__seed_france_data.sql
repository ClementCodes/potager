-- =============================================================================
-- V2 : Seed données initiales — France (Phase 1 MVP)
-- Données statiques, source : Agreste / Kantar Worldpanel 2023 / USDA FoodData Central
-- =============================================================================

-- -----------------------------------------------------------------------------
-- 1. Pays : France
-- -----------------------------------------------------------------------------
INSERT INTO countries (iso_code, name, primary_koppen_zone, fr_climate_zone)
VALUES ('FRA', 'France', 'Cfb', 'FR-CON');

-- -----------------------------------------------------------------------------
-- 2. Zones climatiques françaises (4 macro-zones agronomiques)
--    yield_multiplier appliqué dynamiquement par ClimateAdjustmentService
-- -----------------------------------------------------------------------------
INSERT INTO climate_zones (code, name, description, yield_multiplier) VALUES
    ('FR-OCC', 'Océanique (Ouest & Bretagne)',
     'Hivers doux, étés modérés. Semis précoces possibles dès mars.', 1.00),

    ('FR-CON', 'Semi-océanique & Continental (Nord / Est / IDF)',
     'Hivers frais à rigoureux. Semis pleine terre après mi-mai (Saints de Glace).', 0.85),

    ('FR-MED', 'Méditerranéen (PACA / Languedoc / Corse)',
     'Hivers très doux, étés chauds et secs. Semis pleine terre dès début mai.', 1.20),

    ('FR-MON', 'Montagne (Massif Central / Alpes / Pyrénées)',
     'Saison très courte. Semis pleine terre après mi-juin uniquement.', 0.65);

-- -----------------------------------------------------------------------------
-- 3. Cultures potagères (14 espèces de base)
-- -----------------------------------------------------------------------------
INSERT INTO crops (common_name, scientific_name, botanical_family, root_depth_cm, growing_days_min, growing_days_max) VALUES
    ('Tomate',          'Solanum lycopersicum',         'Solanaceae',      60.0, 70,  100),
    ('Concombre',       'Cucumis sativus',              'Cucurbitaceae',   40.0, 50,   70),
    ('Courgette',       'Cucurbita pepo',               'Cucurbitaceae',   40.0, 50,   65),
    ('Pomme de terre',  'Solanum tuberosum',            'Solanaceae',      45.0, 70,  120),
    ('Carotte',         'Daucus carota',                'Apiaceae',        30.0, 70,   80),
    ('Betterave',       'Beta vulgaris',                'Amaranthaceae',   30.0, 60,   90),
    ('Oignon',          'Allium cepa',                  'Amaryllidaceae',  30.0, 100, 150),
    ('Ail',             'Allium sativum',               'Amaryllidaceae',  25.0, 180, 240),
    ('Haricot nain',    'Phaseolus vulgaris',           'Fabaceae',        30.0, 50,   65),
    ('Pois',            'Pisum sativum',                'Fabaceae',        60.0, 60,   70),
    ('Laitue pommée',   'Lactuca sativa var. capitata', 'Asteraceae',      20.0, 45,   70),
    ('Épinard',         'Spinacia oleracea',            'Amaranthaceae',   25.0, 40,   50),
    ('Chou cabus',      'Brassica oleracea capitata',   'Brassicaceae',    40.0, 90,  120),
    ('Brocoli',         'Brassica oleracea italica',    'Brassicaceae',    40.0, 80,  100);

-- -----------------------------------------------------------------------------
-- 4. Profils nutritionnels
--    Source : USDA FoodData Central (valeurs pour 100g de produit frais)
--    ANDI Source : Dr. Joel Fuhrman — Aggregate Nutrient Density Index
-- -----------------------------------------------------------------------------
INSERT INTO nutritional_profiles (crop_id, calories_per_100g, proteins_per_100g, carbs_per_100g, fats_per_100g, fiber_per_100g, andi_score)
    SELECT id, 18.0, 0.9,  3.9,  0.2, 1.2, 190 FROM crops WHERE common_name = 'Tomate';

INSERT INTO nutritional_profiles (crop_id, calories_per_100g, proteins_per_100g, carbs_per_100g, fats_per_100g, fiber_per_100g, andi_score)
    SELECT id, 15.0, 0.7,  3.6,  0.1, 0.5,  87 FROM crops WHERE common_name = 'Concombre';

INSERT INTO nutritional_profiles (crop_id, calories_per_100g, proteins_per_100g, carbs_per_100g, fats_per_100g, fiber_per_100g, andi_score)
    SELECT id, 17.0, 1.2,  3.1,  0.3, 1.0, 136 FROM crops WHERE common_name = 'Courgette';

INSERT INTO nutritional_profiles (crop_id, calories_per_100g, proteins_per_100g, carbs_per_100g, fats_per_100g, fiber_per_100g, andi_score)
    SELECT id, 77.0, 2.0, 17.5,  0.1, 2.2,  31 FROM crops WHERE common_name = 'Pomme de terre';

INSERT INTO nutritional_profiles (crop_id, calories_per_100g, proteins_per_100g, carbs_per_100g, fats_per_100g, fiber_per_100g, andi_score)
    SELECT id, 41.0, 0.9,  9.6,  0.2, 2.8, 240 FROM crops WHERE common_name = 'Carotte';

INSERT INTO nutritional_profiles (crop_id, calories_per_100g, proteins_per_100g, carbs_per_100g, fats_per_100g, fiber_per_100g, andi_score)
    SELECT id, 43.0, 1.6,  9.6,  0.2, 2.8, 167 FROM crops WHERE common_name = 'Betterave';

INSERT INTO nutritional_profiles (crop_id, calories_per_100g, proteins_per_100g, carbs_per_100g, fats_per_100g, fiber_per_100g, andi_score)
    SELECT id, 40.0, 1.1,  9.3,  0.1, 1.7, 109 FROM crops WHERE common_name = 'Oignon';

INSERT INTO nutritional_profiles (crop_id, calories_per_100g, proteins_per_100g, carbs_per_100g, fats_per_100g, fiber_per_100g, andi_score)
    SELECT id, 149.0, 6.4, 33.1, 0.5, 2.1,  83 FROM crops WHERE common_name = 'Ail';

INSERT INTO nutritional_profiles (crop_id, calories_per_100g, proteins_per_100g, carbs_per_100g, fats_per_100g, fiber_per_100g, andi_score)
    SELECT id, 35.0, 2.4,  6.0,  0.1, 2.1, 340 FROM crops WHERE common_name = 'Haricot nain';

INSERT INTO nutritional_profiles (crop_id, calories_per_100g, proteins_per_100g, carbs_per_100g, fats_per_100g, fiber_per_100g, andi_score)
    SELECT id, 81.0, 5.4, 14.5,  0.4, 5.7, 364 FROM crops WHERE common_name = 'Pois';

INSERT INTO nutritional_profiles (crop_id, calories_per_100g, proteins_per_100g, carbs_per_100g, fats_per_100g, fiber_per_100g, andi_score)
    SELECT id, 15.0, 1.4,  2.9,  0.2, 1.3, 585 FROM crops WHERE common_name = 'Laitue pommée';

INSERT INTO nutritional_profiles (crop_id, calories_per_100g, proteins_per_100g, carbs_per_100g, fats_per_100g, fiber_per_100g, andi_score)
    SELECT id, 23.0, 2.9,  3.6,  0.4, 2.2, 707 FROM crops WHERE common_name = 'Épinard';

INSERT INTO nutritional_profiles (crop_id, calories_per_100g, proteins_per_100g, carbs_per_100g, fats_per_100g, fiber_per_100g, andi_score)
    SELECT id, 25.0, 1.3,  5.8,  0.1, 2.5, 481 FROM crops WHERE common_name = 'Chou cabus';

INSERT INTO nutritional_profiles (crop_id, calories_per_100g, proteins_per_100g, carbs_per_100g, fats_per_100g, fiber_per_100g, andi_score)
    SELECT id, 34.0, 2.8,  6.6,  0.4, 2.6, 340 FROM crops WHERE common_name = 'Brocoli';

-- -----------------------------------------------------------------------------
-- 5. Rendements de base pour la France — référence zone FR-OCC (multiplier = 1.00)
--    ClimateAdjustmentService calcule : yield_adjusted = yield_average × zone.yield_multiplier
--    Valeurs calibrées sur les données agronomiques françaises (Agreste 2023)
-- -----------------------------------------------------------------------------
INSERT INTO yield_data (crop_id, country_iso, climate_zone_code, yield_min_kg_per_m2, yield_max_kg_per_m2, yield_average_kg_per_m2, cultivation_method)
    SELECT id, 'FRA', 'FR-OCC',  8.0, 15.0, 10.00, 'OPEN_AIR' FROM crops WHERE common_name = 'Tomate';

INSERT INTO yield_data (crop_id, country_iso, climate_zone_code, yield_min_kg_per_m2, yield_max_kg_per_m2, yield_average_kg_per_m2, cultivation_method)
    SELECT id, 'FRA', 'FR-OCC',  4.0, 12.0,  7.00, 'OPEN_AIR' FROM crops WHERE common_name = 'Concombre';

INSERT INTO yield_data (crop_id, country_iso, climate_zone_code, yield_min_kg_per_m2, yield_max_kg_per_m2, yield_average_kg_per_m2, cultivation_method)
    SELECT id, 'FRA', 'FR-OCC',  4.0,  7.0,  5.50, 'OPEN_AIR' FROM crops WHERE common_name = 'Courgette';

INSERT INTO yield_data (crop_id, country_iso, climate_zone_code, yield_min_kg_per_m2, yield_max_kg_per_m2, yield_average_kg_per_m2, cultivation_method)
    SELECT id, 'FRA', 'FR-OCC',  2.0,  3.5,  2.50, 'OPEN_AIR' FROM crops WHERE common_name = 'Pomme de terre';

INSERT INTO yield_data (crop_id, country_iso, climate_zone_code, yield_min_kg_per_m2, yield_max_kg_per_m2, yield_average_kg_per_m2, cultivation_method)
    SELECT id, 'FRA', 'FR-OCC',  5.0, 10.0,  7.00, 'OPEN_AIR' FROM crops WHERE common_name = 'Carotte';

INSERT INTO yield_data (crop_id, country_iso, climate_zone_code, yield_min_kg_per_m2, yield_max_kg_per_m2, yield_average_kg_per_m2, cultivation_method)
    SELECT id, 'FRA', 'FR-OCC',  3.0,  6.0,  4.50, 'OPEN_AIR' FROM crops WHERE common_name = 'Betterave';

INSERT INTO yield_data (crop_id, country_iso, climate_zone_code, yield_min_kg_per_m2, yield_max_kg_per_m2, yield_average_kg_per_m2, cultivation_method)
    SELECT id, 'FRA', 'FR-OCC',  3.0,  6.0,  4.00, 'OPEN_AIR' FROM crops WHERE common_name = 'Oignon';

INSERT INTO yield_data (crop_id, country_iso, climate_zone_code, yield_min_kg_per_m2, yield_max_kg_per_m2, yield_average_kg_per_m2, cultivation_method)
    SELECT id, 'FRA', 'FR-OCC',  0.5,  1.5,  1.00, 'OPEN_AIR' FROM crops WHERE common_name = 'Ail';

INSERT INTO yield_data (crop_id, country_iso, climate_zone_code, yield_min_kg_per_m2, yield_max_kg_per_m2, yield_average_kg_per_m2, cultivation_method)
    SELECT id, 'FRA', 'FR-OCC',  1.0,  2.0,  1.50, 'OPEN_AIR' FROM crops WHERE common_name = 'Haricot nain';

INSERT INTO yield_data (crop_id, country_iso, climate_zone_code, yield_min_kg_per_m2, yield_max_kg_per_m2, yield_average_kg_per_m2, cultivation_method)
    SELECT id, 'FRA', 'FR-OCC',  2.0,  4.0,  3.00, 'OPEN_AIR' FROM crops WHERE common_name = 'Pois';

INSERT INTO yield_data (crop_id, country_iso, climate_zone_code, yield_min_kg_per_m2, yield_max_kg_per_m2, yield_average_kg_per_m2, cultivation_method)
    SELECT id, 'FRA', 'FR-OCC',  2.5,  3.5,  3.00, 'OPEN_AIR' FROM crops WHERE common_name = 'Laitue pommée';

INSERT INTO yield_data (crop_id, country_iso, climate_zone_code, yield_min_kg_per_m2, yield_max_kg_per_m2, yield_average_kg_per_m2, cultivation_method)
    SELECT id, 'FRA', 'FR-OCC',  2.0,  3.0,  2.50, 'OPEN_AIR' FROM crops WHERE common_name = 'Épinard';

INSERT INTO yield_data (crop_id, country_iso, climate_zone_code, yield_min_kg_per_m2, yield_max_kg_per_m2, yield_average_kg_per_m2, cultivation_method)
    SELECT id, 'FRA', 'FR-OCC',  5.0,  8.0,  6.50, 'OPEN_AIR' FROM crops WHERE common_name = 'Chou cabus';

INSERT INTO yield_data (crop_id, country_iso, climate_zone_code, yield_min_kg_per_m2, yield_max_kg_per_m2, yield_average_kg_per_m2, cultivation_method)
    SELECT id, 'FRA', 'FR-OCC',  1.5,  2.0,  1.75, 'OPEN_AIR' FROM crops WHERE common_name = 'Brocoli';

-- -----------------------------------------------------------------------------
-- 6. Préférences de consommation — France
--    Source : Kantar Worldpanel Shopper 2023 + FranceAgriMer
--    preference_weight = part relative dans les achats de légumes français
-- -----------------------------------------------------------------------------
INSERT INTO consumption_data (crop_id, country_iso, kg_per_capita_per_year, preference_weight, data_year, data_source)
    SELECT id, 'FRA', 18.5, 0.184, 2023, 'STATIC_FR' FROM crops WHERE common_name = 'Tomate';

INSERT INTO consumption_data (crop_id, country_iso, kg_per_capita_per_year, preference_weight, data_year, data_source)
    SELECT id, 'FRA',  6.0, 0.060, 2023, 'STATIC_FR' FROM crops WHERE common_name = 'Concombre';

INSERT INTO consumption_data (crop_id, country_iso, kg_per_capita_per_year, preference_weight, data_year, data_source)
    SELECT id, 'FRA',  7.9, 0.079, 2023, 'STATIC_FR' FROM crops WHERE common_name = 'Courgette';

INSERT INTO consumption_data (crop_id, country_iso, kg_per_capita_per_year, preference_weight, data_year, data_source)
    SELECT id, 'FRA', 15.0, 0.150, 2023, 'STATIC_FR' FROM crops WHERE common_name = 'Pomme de terre';

INSERT INTO consumption_data (crop_id, country_iso, kg_per_capita_per_year, preference_weight, data_year, data_source)
    SELECT id, 'FRA', 11.6, 0.116, 2023, 'STATIC_FR' FROM crops WHERE common_name = 'Carotte';

INSERT INTO consumption_data (crop_id, country_iso, kg_per_capita_per_year, preference_weight, data_year, data_source)
    SELECT id, 'FRA',  3.0, 0.030, 2023, 'STATIC_FR' FROM crops WHERE common_name = 'Betterave';

INSERT INTO consumption_data (crop_id, country_iso, kg_per_capita_per_year, preference_weight, data_year, data_source)
    SELECT id, 'FRA',  8.0, 0.080, 2023, 'STATIC_FR' FROM crops WHERE common_name = 'Oignon';

INSERT INTO consumption_data (crop_id, country_iso, kg_per_capita_per_year, preference_weight, data_year, data_source)
    SELECT id, 'FRA',  2.5, 0.025, 2023, 'STATIC_FR' FROM crops WHERE common_name = 'Ail';

INSERT INTO consumption_data (crop_id, country_iso, kg_per_capita_per_year, preference_weight, data_year, data_source)
    SELECT id, 'FRA',  4.0, 0.040, 2023, 'STATIC_FR' FROM crops WHERE common_name = 'Haricot nain';

INSERT INTO consumption_data (crop_id, country_iso, kg_per_capita_per_year, preference_weight, data_year, data_source)
    SELECT id, 'FRA',  3.5, 0.035, 2023, 'STATIC_FR' FROM crops WHERE common_name = 'Pois';

INSERT INTO consumption_data (crop_id, country_iso, kg_per_capita_per_year, preference_weight, data_year, data_source)
    SELECT id, 'FRA',  5.0, 0.050, 2023, 'STATIC_FR' FROM crops WHERE common_name = 'Laitue pommée';

INSERT INTO consumption_data (crop_id, country_iso, kg_per_capita_per_year, preference_weight, data_year, data_source)
    SELECT id, 'FRA',  3.0, 0.030, 2023, 'STATIC_FR' FROM crops WHERE common_name = 'Épinard';

INSERT INTO consumption_data (crop_id, country_iso, kg_per_capita_per_year, preference_weight, data_year, data_source)
    SELECT id, 'FRA',  6.0, 0.060, 2023, 'STATIC_FR' FROM crops WHERE common_name = 'Chou cabus';

INSERT INTO consumption_data (crop_id, country_iso, kg_per_capita_per_year, preference_weight, data_year, data_source)
    SELECT id, 'FRA',  4.0, 0.040, 2023, 'STATIC_FR' FROM crops WHERE common_name = 'Brocoli';
