export interface PlotAllocation {
  cropId: number;
  cropName: string;
  botanicalFamily: string | null;
  allocatedSurfaceM2: number;
  estimatedYieldKg: number;
  estimatedCalories: number;
  plantSpacingM2: number | null;
  andiScore: number;
}

export interface OptimizationResult {
  id: number;
  gardenProfileId: number;
  computedAt: string;
  totalCaloriesProduced: number;
  calorieTargetAnnual: number;
  selfSufficiencyPercent: number;
  plotAllocations: PlotAllocation[];
}
