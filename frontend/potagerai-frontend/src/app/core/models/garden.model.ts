export interface GardenProfile {
  id: number;
  totalSurfaceM2: number;
  householdSize: number;
  climateZoneCode: string;
  climateZoneName: string;
  countryIsoCode: string;
  countryName: string;
  cultivationMethod: string;
  createdAt: string;
}

export interface CreateGardenRequest {
  totalSurfaceM2: number;
  householdSize: number;
  climateZoneCode: string;
  countryIsoCode: string;
  cultivationMethod?: string;
}

export interface ClimateZone {
  code: string;
  name: string;
  description: string;
  yieldMultiplier: number;
}

export interface SurfaceEstimate {
  householdSize: number;
  climateZoneCode: string;
  climateZoneName: string;
  estimatedSurfaceM2: number;
  calorieTargetAnnual: number;
}
