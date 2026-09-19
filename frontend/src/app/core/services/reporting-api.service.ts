import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { PlatformSummary, RecentActivity } from '../../shared/models/reporting.model';

@Injectable({ providedIn: 'root' })
export class ReportingApiService {
  private readonly baseUrl = `${environment.apiBaseUrl}/reporting`;

  constructor(private http: HttpClient) {}

  getSummary(): Observable<PlatformSummary> {
    return this.http.get<PlatformSummary>(`${this.baseUrl}/summary`);
  }

  getRecentActivity(limit = 10): Observable<RecentActivity[]> {
    return this.http.get<RecentActivity[]>(`${this.baseUrl}/activity`, { params: { limit } });
  }
}
