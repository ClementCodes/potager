import { Routes } from '@angular/router';

export const gardenRoutes: Routes = [
  {
    path: '',
    loadComponent: () => import('./garden-form/garden-form.component').then(m => m.GardenFormComponent)
  },
  {
    path: ':id/result',
    loadComponent: () => import('./garden-result/garden-result.component').then(m => m.GardenResultComponent)
  }
];
