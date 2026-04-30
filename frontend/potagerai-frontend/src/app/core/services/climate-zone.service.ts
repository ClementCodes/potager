import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ClimateZone } from '../models/climate-zone.model';
import { environment } from '../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class ClimateZoneService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiUrl}/climate-zones`;

  findAll(): Observable<ClimateZone[]> {
    return this.http.get<ClimateZone[]>(this.baseUrl);
  }
}
