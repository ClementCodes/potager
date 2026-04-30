-- =============================================================================
-- V5 : Enrichissement de la base de cultures (+12 espèces stratégiques)
-- Couvre les manques identifiés :
--   - Cultures haute énergie stockables (autosuffisance hivernale) :
--       Patate douce, Topinambour, Courge butternut, Maïs doux, Fève
--   - Cultures populaires françaises manquantes :
--       Poireau, Radis, Aubergine, Poivron, Chou-fleur, Bette, Mâche
--   - Nouvelles familles botaniques (diversité agronomique) :
--       Poaceae (Maïs), Convolvulaceae (Patate douce), Caprifoliaceae (Mâche)
--
-- Sources :
--   - Nutrition  : USDA FoodData Central
--   - ANDI       : Dr. Joel Fuhrman, Aggregate Nutrient Density Index
--   - Rendements : Agreste 2023, INRAE Productions Végétales
--   - Conso FR   : FranceAgriMer / Kantar Worldpanel 2023
--   - Espacement : GNIS / INRAE / Almanach du Jardinier
-- =============================================================================

-- ----------------------------------------------------------------------------
-- 1. Insertion des 12 nouvelles cultures
-- ----------------------------------------------------------------------------
INSERT INTO crops (common_name, scientific_name, botanical_family, root_depth_cm, growing_days_min, growing_days_max, plant_spacing_m2, storage_months, frost_sensitive, sowing_month_min, sowing_month_max) VALUES
    ('Poireau',         'Allium porrum',                 'Amaryllidaceae',   30.0,  120, 180, 0.0500, 5, FALSE, 2,  8),
    ('Radis',           'Raphanus sativus',              'Brassicaceae',     15.0,   25,  35, 0.0025, 1, FALSE, 3,  9),
    ('Aubergine',       'Solanum melongena',             'Solanaceae',       50.0,  100, 130, 0.4500, 1, TRUE,  5,  6),
    ('Poivron',         'Capsicum annuum',               'Solanaceae',       40.0,  100, 130, 0.2500, 1, TRUE,  5,  6),
    ('Courge butternut','Cucurbita moschata',            'Cucurbitaceae',    50.0,  100, 130, 1.0000, 6, TRUE,  5,  6),
    ('Patate douce',    'Ipomoea batatas',               'Convolvulaceae',   30.0,  120, 150, 0.4000, 5, TRUE,  5,  6),
    ('Topinambour',     'Helianthus tuberosus',          'Asteraceae',       40.0,  150, 180, 0.2500, 5, FALSE, 3,  4),
    ('Mâche',           'Valerianella locusta',          'Caprifoliaceae',   15.0,   60,  90, 0.0064, 1, FALSE, 8, 10),
    ('Bette à carde',   'Beta vulgaris subsp. cicla',    'Amaranthaceae',    30.0,   60,  80, 0.1600, 1, FALSE, 4,  7),
    ('Fève',            'Vicia faba',                    'Fabaceae',         60.0,  120, 150, 0.0400, 1, FALSE, 10, 3),
    ('Maïs doux',       'Zea mays var. saccharata',      'Poaceae',          50.0,   90, 110, 0.1600, 1, TRUE,  5,  6),
    ('Chou-fleur',      'Brassica oleracea var. botrytis','Brassicaceae',    40.0,   90, 120, 0.3600, 1, FALSE, 4,  7);

-- ----------------------------------------------------------------------------
-- 2. Profils nutritionnels (USDA FoodData Central, 100g cru)
-- ----------------------------------------------------------------------------
INSERT INTO nutritional_profiles (crop_id, calories_per_100g, proteins_per_100g, carbs_per_100g, fats_per_100g, fiber_per_100g, andi_score)
    SELECT id, 61.0,  1.5, 14.2, 0.3, 1.8,  73 FROM crops WHERE common_name = 'Poireau';
INSERT INTO nutritional_profiles (crop_id, calories_per_100g, proteins_per_100g, carbs_per_100g, fats_per_100g, fiber_per_100g, andi_score)
    SELECT id, 16.0,  0.7,  3.4, 0.1, 1.6, 502 FROM crops WHERE common_name = 'Radis';
INSERT INTO nutritional_profiles (crop_id, calories_per_100g, proteins_per_100g, carbs_per_100g, fats_per_100g, fiber_per_100g, andi_score)
    SELECT id, 25.0,  1.0,  5.9, 0.2, 3.0,  36 FROM crops WHERE common_name = 'Aubergine';
INSERT INTO nutritional_profiles (crop_id, calories_per_100g, proteins_per_100g, carbs_per_100g, fats_per_100g, fiber_per_100g, andi_score)
    SELECT id, 31.0,  1.0,  6.0, 0.3, 2.1, 265 FROM crops WHERE common_name = 'Poivron';
INSERT INTO nutritional_profiles (crop_id, calories_per_100g, proteins_per_100g, carbs_per_100g, fats_per_100g, fiber_per_100g, andi_score)
    SELECT id, 45.0,  1.0, 11.7, 0.1, 2.0, 281 FROM crops WHERE common_name = 'Courge butternut';
INSERT INTO nutritional_profiles (crop_id, calories_per_100g, proteins_per_100g, carbs_per_100g, fats_per_100g, fiber_per_100g, andi_score)
    SELECT id, 86.0,  1.6, 20.1, 0.1, 3.0, 181 FROM crops WHERE common_name = 'Patate douce';
INSERT INTO nutritional_profiles (crop_id, calories_per_100g, proteins_per_100g, carbs_per_100g, fats_per_100g, fiber_per_100g, andi_score)
    SELECT id, 73.0,  2.0, 17.4, 0.0, 1.6,  56 FROM crops WHERE common_name = 'Topinambour';
INSERT INTO nutritional_profiles (crop_id, calories_per_100g, proteins_per_100g, carbs_per_100g, fats_per_100g, fiber_per_100g, andi_score)
    SELECT id, 21.0,  2.0,  3.6, 0.4, 1.9, 489 FROM crops WHERE common_name = 'Mâche';
INSERT INTO nutritional_profiles (crop_id, calories_per_100g, proteins_per_100g, carbs_per_100g, fats_per_100g, fiber_per_100g, andi_score)
    SELECT id, 19.0,  1.8,  3.7, 0.2, 1.6, 670 FROM crops WHERE common_name = 'Bette à carde';
INSERT INTO nutritional_profiles (crop_id, calories_per_100g, proteins_per_100g, carbs_per_100g, fats_per_100g, fiber_per_100g, andi_score)
    SELECT id, 88.0,  7.6, 17.6, 0.4, 5.0, 118 FROM crops WHERE common_name = 'Fève';
INSERT INTO nutritional_profiles (crop_id, calories_per_100g, proteins_per_100g, carbs_per_100g, fats_per_100g, fiber_per_100g, andi_score)
    SELECT id, 86.0,  3.3, 19.0, 1.4, 2.7,  22 FROM crops WHERE common_name = 'Maïs doux';
INSERT INTO nutritional_profiles (crop_id, calories_per_100g, proteins_per_100g, carbs_per_100g, fats_per_100g, fiber_per_100g, andi_score)
    SELECT id, 25.0,  1.9,  5.0, 0.3, 2.0, 295 FROM crops WHERE common_name = 'Chou-fleur';

-- ----------------------------------------------------------------------------
-- 3. Rendements de base FR-OCC (Agreste 2023 / INRAE)
-- ----------------------------------------------------------------------------
INSERT INTO yield_data (crop_id, country_iso, climate_zone_code, yield_min_kg_per_m2, yield_max_kg_per_m2, yield_average_kg_per_m2, cultivation_method)
    SELECT id, 'FRA', 'FR-OCC', 2.0, 4.0, 3.00, 'OPEN_AIR' FROM crops WHERE common_name = 'Poireau';
INSERT INTO yield_data (crop_id, country_iso, climate_zone_code, yield_min_kg_per_m2, yield_max_kg_per_m2, yield_average_kg_per_m2, cultivation_method)
    SELECT id, 'FRA', 'FR-OCC', 1.5, 3.0, 2.00, 'OPEN_AIR' FROM crops WHERE common_name = 'Radis';
INSERT INTO yield_data (crop_id, country_iso, climate_zone_code, yield_min_kg_per_m2, yield_max_kg_per_m2, yield_average_kg_per_m2, cultivation_method)
    SELECT id, 'FRA', 'FR-OCC', 3.0, 6.0, 4.50, 'OPEN_AIR' FROM crops WHERE common_name = 'Aubergine';
INSERT INTO yield_data (crop_id, country_iso, climate_zone_code, yield_min_kg_per_m2, yield_max_kg_per_m2, yield_average_kg_per_m2, cultivation_method)
    SELECT id, 'FRA', 'FR-OCC', 2.5, 5.0, 3.50, 'OPEN_AIR' FROM crops WHERE common_name = 'Poivron';
INSERT INTO yield_data (crop_id, country_iso, climate_zone_code, yield_min_kg_per_m2, yield_max_kg_per_m2, yield_average_kg_per_m2, cultivation_method)
    SELECT id, 'FRA', 'FR-OCC', 2.5, 5.0, 3.50, 'OPEN_AIR' FROM crops WHERE common_name = 'Courge butternut';
INSERT INTO yield_data (crop_id, country_iso, climate_zone_code, yield_min_kg_per_m2, yield_max_kg_per_m2, yield_average_kg_per_m2, cultivation_method)
    SELECT id, 'FRA', 'FR-OCC', 1.5, 3.5, 2.50, 'OPEN_AIR' FROM crops WHERE common_name = 'Patate douce';
INSERT INTO yield_data (crop_id, country_iso, climate_zone_code, yield_min_kg_per_m2, yield_max_kg_per_m2, yield_average_kg_per_m2, cultivation_method)
    SELECT id, 'FRA', 'FR-OCC', 3.0, 6.0, 4.00, 'OPEN_AIR' FROM crops WHERE common_name = 'Topinambour';
INSERT INTO yield_data (crop_id, country_iso, climate_zone_code, yield_min_kg_per_m2, yield_max_kg_per_m2, yield_average_kg_per_m2, cultivation_method)
    SELECT id, 'FRA', 'FR-OCC', 1.0, 2.0, 1.50, 'OPEN_AIR' FROM crops WHERE common_name = 'Mâche';
INSERT INTO yield_data (crop_id, country_iso, climate_zone_code, yield_min_kg_per_m2, yield_max_kg_per_m2, yield_average_kg_per_m2, cultivation_method)
    SELECT id, 'FRA', 'FR-OCC', 3.0, 6.0, 4.00, 'OPEN_AIR' FROM crops WHERE common_name = 'Bette à carde';
INSERT INTO yield_data (crop_id, country_iso, climate_zone_code, yield_min_kg_per_m2, yield_max_kg_per_m2, yield_average_kg_per_m2, cultivation_method)
    SELECT id, 'FRA', 'FR-OCC', 1.0, 2.5, 1.50, 'OPEN_AIR' FROM crops WHERE common_name = 'Fève';
INSERT INTO yield_data (crop_id, country_iso, climate_zone_code, yield_min_kg_per_m2, yield_max_kg_per_m2, yield_average_kg_per_m2, cultivation_method)
    SELECT id, 'FRA', 'FR-OCC', 1.0, 2.5, 1.75, 'OPEN_AIR' FROM crops WHERE common_name = 'Maïs doux';
INSERT INTO yield_data (crop_id, country_iso, climate_zone_code, yield_min_kg_per_m2, yield_max_kg_per_m2, yield_average_kg_per_m2, cultivation_method)
    SELECT id, 'FRA', 'FR-OCC', 3.0, 6.0, 4.50, 'OPEN_AIR' FROM crops WHERE common_name = 'Chou-fleur';

-- ----------------------------------------------------------------------------
-- 4. Préférences de consommation FR (FranceAgriMer / Kantar 2023)
-- ----------------------------------------------------------------------------
INSERT INTO consumption_data (crop_id, country_iso, kg_per_capita_per_year, preference_weight, data_year, data_source)
    SELECT id, 'FRA', 5.5, 0.055, 2023, 'STATIC_FR' FROM crops WHERE common_name = 'Poireau';
INSERT INTO consumption_data (crop_id, country_iso, kg_per_capita_per_year, preference_weight, data_year, data_source)
    SELECT id, 'FRA', 2.0, 0.020, 2023, 'STATIC_FR' FROM crops WHERE common_name = 'Radis';
INSERT INTO consumption_data (crop_id, country_iso, kg_per_capita_per_year, preference_weight, data_year, data_source)
    SELECT id, 'FRA', 3.0, 0.030, 2023, 'STATIC_FR' FROM crops WHERE common_name = 'Aubergine';
INSERT INTO consumption_data (crop_id, country_iso, kg_per_capita_per_year, preference_weight, data_year, data_source)
    SELECT id, 'FRA', 3.5, 0.035, 2023, 'STATIC_FR' FROM crops WHERE common_name = 'Poivron';
INSERT INTO consumption_data (crop_id, country_iso, kg_per_capita_per_year, preference_weight, data_year, data_source)
    SELECT id, 'FRA', 2.5, 0.025, 2023, 'STATIC_FR' FROM crops WHERE common_name = 'Courge butternut';
INSERT INTO consumption_data (crop_id, country_iso, kg_per_capita_per_year, preference_weight, data_year, data_source)
    SELECT id, 'FRA', 1.0, 0.010, 2023, 'STATIC_FR' FROM crops WHERE common_name = 'Patate douce';
INSERT INTO consumption_data (crop_id, country_iso, kg_per_capita_per_year, preference_weight, data_year, data_source)
    SELECT id, 'FRA', 0.4, 0.005, 2023, 'STATIC_FR' FROM crops WHERE common_name = 'Topinambour';
INSERT INTO consumption_data (crop_id, country_iso, kg_per_capita_per_year, preference_weight, data_year, data_source)
    SELECT id, 'FRA', 1.5, 0.015, 2023, 'STATIC_FR' FROM crops WHERE common_name = 'Mâche';
INSERT INTO consumption_data (crop_id, country_iso, kg_per_capita_per_year, preference_weight, data_year, data_source)
    SELECT id, 'FRA', 1.0, 0.010, 2023, 'STATIC_FR' FROM crops WHERE common_name = 'Bette à carde';
INSERT INTO consumption_data (crop_id, country_iso, kg_per_capita_per_year, preference_weight, data_year, data_source)
    SELECT id, 'FRA', 1.0, 0.010, 2023, 'STATIC_FR' FROM crops WHERE common_name = 'Fève';
INSERT INTO consumption_data (crop_id, country_iso, kg_per_capita_per_year, preference_weight, data_year, data_source)
    SELECT id, 'FRA', 1.5, 0.015, 2023, 'STATIC_FR' FROM crops WHERE common_name = 'Maïs doux';
INSERT INTO consumption_data (crop_id, country_iso, kg_per_capita_per_year, preference_weight, data_year, data_source)
    SELECT id, 'FRA', 3.0, 0.030, 2023, 'STATIC_FR' FROM crops WHERE common_name = 'Chou-fleur';
