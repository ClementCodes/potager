import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Crop } from '../models/crop.model';
import { environment } from '../../../environments/environment';

export type Season = 'PRINTEMPS' | 'ETE' | 'AUTOMNE' | 'HIVER' | 'TOUTE_ANNEE';

@Injectable({ providedIn: 'root' })
export class CropService {
  private readonly baseUrl = `${environment.apiUrl}/crops`;

  constructor(private http: HttpClient) {}

  findAll(): Observable<Crop[]> {
    return this.http.get<Crop[]>(this.baseUrl);
  }

  findBySeason(season: Season): Observable<Crop[]> {
    return this.http.get<Crop[]>(this.baseUrl, { params: { season } });
  }

  findById(id: number): Observable<Crop> {
    return this.http.get<Crop>(`${this.baseUrl}/${id}`);
  }
}
