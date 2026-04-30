-- =============================================================================
-- V3 — Espacement entre plants (m²/plant)
--
-- Permet de calculer le nombre de plants à semer pour chaque culture.
-- Formule : nb_plants = floor(surface_m2 / plant_spacing_m2)
--
-- Sources : recommandations INRAE / jardinage raisonné (espacement rangées × entre-plants)
-- =============================================================================

ALTER TABLE crops ADD COLUMN plant_spacing_m2 DECIMAL(6,4);

UPDATE crops SET plant_spacing_m2 = 0.1600 WHERE common_name = 'Tomate';           -- 40cm × 40cm
UPDATE crops SET plant_spacing_m2 = 0.2500 WHERE common_name = 'Concombre';        -- 50cm × 50cm
UPDATE crops SET plant_spacing_m2 = 0.5625 WHERE common_name = 'Courgette';        -- 75cm × 75cm
UPDATE crops SET plant_spacing_m2 = 0.1000 WHERE common_name = 'Pomme de terre';   -- 25cm × 40cm
UPDATE crops SET plant_spacing_m2 = 0.0200 WHERE common_name = 'Carotte';          -- 10cm × 20cm
UPDATE crops SET plant_spacing_m2 = 0.0400 WHERE common_name = 'Betterave';        -- 20cm × 20cm
UPDATE crops SET plant_spacing_m2 = 0.0225 WHERE common_name = 'Oignon';           -- 15cm × 15cm
UPDATE crops SET plant_spacing_m2 = 0.0225 WHERE common_name = 'Ail';              -- 15cm × 15cm
UPDATE crops SET plant_spacing_m2 = 0.0400 WHERE common_name = 'Haricot nain';     -- 20cm × 20cm
UPDATE crops SET plant_spacing_m2 = 0.0400 WHERE common_name = 'Pois';             -- 20cm × 20cm
UPDATE crops SET plant_spacing_m2 = 0.0900 WHERE common_name = 'Laitue pommée';    -- 30cm × 30cm
UPDATE crops SET plant_spacing_m2 = 0.0400 WHERE common_name = 'Épinard';          -- 20cm × 20cm
UPDATE crops SET plant_spacing_m2 = 0.3600 WHERE common_name = 'Chou cabus';       -- 60cm × 60cm
UPDATE crops SET plant_spacing_m2 = 0.2025 WHERE common_name = 'Brocoli';          -- 45cm × 45cm
