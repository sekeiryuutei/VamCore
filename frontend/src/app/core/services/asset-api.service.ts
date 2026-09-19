import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Asset, AssetAssignment, Depreciation, Maintenance } from '../../shared/models/asset.model';

@Injectable({ providedIn: 'root' })
export class AssetApiService {
  private readonly baseUrl = `${environment.apiBaseUrl}/assets`;

  constructor(private http: HttpClient) {}

  createAsset(payload: {
    assetCode: string; name: string; category?: string; serialNumber?: string;
    acquisitionCost?: number; currency?: string; usefulLifeMonths?: number;
  }): Observable<Asset> {
    return this.http.post<Asset>(this.baseUrl, payload);
  }

  listAssets(): Observable<Asset[]> {
    return this.http.get<Asset[]>(this.baseUrl);
  }

  changeStatus(assetId: string, newStatus: string): Observable<Asset> {
    return this.http.post<Asset>(`${this.baseUrl}/${assetId}/status`, { newStatus });
  }

  assign(assetId: string, assigneeId: string): Observable<Asset> {
    return this.http.post<Asset>(`${this.baseUrl}/${assetId}/assign`, { assigneeId });
  }

  history(assetId: string): Observable<AssetAssignment[]> {
    return this.http.get<AssetAssignment[]>(`${this.baseUrl}/${assetId}/history`);
  }

  depreciation(assetId: string): Observable<Depreciation> {
    return this.http.get<Depreciation>(`${this.baseUrl}/${assetId}/depreciation`);
  }

  scheduleMaintenance(assetId: string, notes?: string): Observable<Maintenance> {
    return this.http.post<Maintenance>(`${this.baseUrl}/${assetId}/maintenance/schedule`, { notes });
  }

  completeMaintenance(assetId: string): Observable<Maintenance> {
    return this.http.post<Maintenance>(`${this.baseUrl}/${assetId}/maintenance/complete`, {});
  }

  maintenanceHistory(assetId: string): Observable<Maintenance[]> {
    return this.http.get<Maintenance[]>(`${this.baseUrl}/${assetId}/maintenance`);
  }
}
