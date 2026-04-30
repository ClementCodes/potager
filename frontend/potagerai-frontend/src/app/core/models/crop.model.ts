export interface NutritionalProfile {
  caloriesPer100g: number;
  proteinsPer100g: number;
  carbsPer100g: number;
  fatsPer100g: number;
  fiberPer100g: number;
  andiScore: number;
}

export interface Crop {
  id: number;
  commonName: string;
  scientificName: string;
  botanicalFamily: string;
  rootDepthCm: number;
  growingDaysMin: number;
  growingDaysMax: number;
  plantSpacingM2: number | null;
  storageMonths: number | null;
  frostSensitive: boolean | null;
  sowingMonthMin: number | null;
  sowingMonthMax: number | null;
  nutritionalProfile: NutritionalProfile | null;
}
