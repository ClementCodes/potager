import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';

export const routes: Routes = [
  { path: '', redirectTo: '/garden', pathMatch: 'full' },
  {
    path: 'auth',
    loadChildren: () => import('./features/auth/auth.routes').then(m => m.authRoutes)
  },
  {
    path: 'garden',
    canActivate: [authGuard],
    loadChildren: () => import('./features/garden/garden.routes').then(m => m.gardenRoutes)
  },
  {
    path: 'crops',
    canActivate: [authGuard],
    loadChildren: () => import('./features/crops/crops.routes').then(m => m.cropsRoutes)
  },
  { path: '**', redirectTo: '/garden' }
];
