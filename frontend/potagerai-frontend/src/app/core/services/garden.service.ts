import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { GardenProfile, CreateGardenRequest, SurfaceEstimate } from '../models/garden.model';
import { OptimizationResult } from '../models/optimization.model';
import { environment } from '../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class GardenService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiUrl}/gardens`;

  create(request: CreateGardenRequest): Observable<GardenProfile> {
    return this.http.post<GardenProfile>(this.baseUrl, request);
  }

  findAll(): Observable<GardenProfile[]> {
    return this.http.get<GardenProfile[]>(this.baseUrl);
  }

  findById(id: number): Observable<GardenProfile> {
    return this.http.get<GardenProfile>(`${this.baseUrl}/${id}`);
  }

  optimize(id: number): Observable<OptimizationResult> {
    return this.http.post<OptimizationResult>(`${this.baseUrl}/${id}/optimize`, {});
  }

  /** Récupère le dernier résultat d'optimisation persisté pour un jardin (sans recalcul). */
  getLatestOptimization(gardenId: number): Observable<OptimizationResult> {
    return this.http.get<OptimizationResult>(`${this.baseUrl}/${gardenId}/optimization`);
  }

  /**
   * Estime la surface nécessaire pour l'autosuffisance calorique (endpoint public).
   * Appelé avant la création du jardin pour orienter l'utilisateur.
   */
  estimateSurface(householdSize: number, climateZoneCode: string): Observable<SurfaceEstimate> {
    return this.http.get<SurfaceEstimate>(
      `${environment.apiUrl}/v1/surface-estimate`,
      { params: { householdSize: householdSize.toString(), climateZoneCode } }
    );
  }
}
