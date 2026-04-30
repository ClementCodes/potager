import { Routes } from '@angular/router';

export const cropsRoutes: Routes = [
  {
    path: '',
    loadComponent: () => import('./crop-list/crop-list.component').then(m => m.CropListComponent)
  }
];
