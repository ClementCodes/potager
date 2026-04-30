import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { GardenProfile, CreateGardenRequest } from '../models/garden.model';
import { OptimizationResult } from '../models/optimization.model';
import { environment } from '../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class GardenService {
  private readonly baseUrl = `${environment.apiUrl}/gardens`;

  constructor(private http: HttpClient) {}

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
}
