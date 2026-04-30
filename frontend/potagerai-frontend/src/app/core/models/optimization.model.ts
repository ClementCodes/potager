export interface PlotAllocation {
  cropId: number;
  cropName: string;
  allocatedSurfaceM2: number;
  estimatedYieldKg: number;
  estimatedCalories: number;
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
