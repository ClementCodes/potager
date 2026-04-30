-- =============================================================================
-- V4 : Enrichissement agronomique des cultures
-- Ajoute :
--   - storage_months    : durée de conservation post-récolte (cave/cellier/réfrigéré)
--   - frost_sensitive   : sensibilité au gel (true = gélif, à protéger)
--   - sowing_month_min  : 1er mois de semis pleine terre (1-12)
--   - sowing_month_max  : dernier mois de semis pleine terre (1-12)
-- Sources : INRAE / GNIS / Almanach du Jardinier 2024
-- =============================================================================

ALTER TABLE crops ADD COLUMN storage_months    INTEGER;
ALTER TABLE crops ADD COLUMN frost_sensitive   BOOLEAN;
ALTER TABLE crops ADD COLUMN sowing_month_min  INTEGER;
ALTER TABLE crops ADD COLUMN sowing_month_max  INTEGER;

-- ----------------------------------------------------------------------------
-- Mise à jour des 14 cultures existantes
-- ----------------------------------------------------------------------------
UPDATE crops SET storage_months = 1,  frost_sensitive = TRUE,  sowing_month_min = 4,  sowing_month_max = 6  WHERE common_name = 'Tomate';
UPDATE crops SET storage_months = 1,  frost_sensitive = TRUE,  sowing_month_min = 5,  sowing_month_max = 6  WHERE common_name = 'Concombre';
UPDATE crops SET storage_months = 2,  frost_sensitive = TRUE,  sowing_month_min = 5,  sowing_month_max = 6  WHERE common_name = 'Courgette';
UPDATE crops SET storage_months = 6,  frost_sensitive = TRUE,  sowing_month_min = 3,  sowing_month_max = 5  WHERE common_name = 'Pomme de terre';
UPDATE crops SET storage_months = 6,  frost_sensitive = FALSE, sowing_month_min = 3,  sowing_month_max = 7  WHERE common_name = 'Carotte';
UPDATE crops SET storage_months = 5,  frost_sensitive = FALSE, sowing_month_min = 4,  sowing_month_max = 6  WHERE common_name = 'Betterave';
UPDATE crops SET storage_months = 6,  frost_sensitive = FALSE, sowing_month_min = 2,  sowing_month_max = 4  WHERE common_name = 'Oignon';
UPDATE crops SET storage_months = 9,  frost_sensitive = FALSE, sowing_month_min = 10, sowing_month_max = 11 WHERE common_name = 'Ail';
UPDATE crops SET storage_months = 1,  frost_sensitive = TRUE,  sowing_month_min = 5,  sowing_month_max = 7  WHERE common_name = 'Haricot nain';
UPDATE crops SET storage_months = 1,  frost_sensitive = FALSE, sowing_month_min = 2,  sowing_month_max = 5  WHERE common_name = 'Pois';
UPDATE crops SET storage_months = 1,  frost_sensitive = FALSE, sowing_month_min = 3,  sowing_month_max = 9  WHERE common_name = 'Laitue pommée';
UPDATE crops SET storage_months = 1,  frost_sensitive = FALSE, sowing_month_min = 3,  sowing_month_max = 9  WHERE common_name = 'Épinard';
UPDATE crops SET storage_months = 4,  frost_sensitive = FALSE, sowing_month_min = 3,  sowing_month_max = 7  WHERE common_name = 'Chou cabus';
UPDATE crops SET storage_months = 1,  frost_sensitive = FALSE, sowing_month_min = 4,  sowing_month_max = 7  WHERE common_name = 'Brocoli';
