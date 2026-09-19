import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { AppUser } from '../../shared/models/user.model';

/** Gestión básica de usuarios del tenant (ver sección 32 del documento de arquitectura, USER_MANAGE). */
@Injectable({ providedIn: 'root' })
export class UserApiService {
  private readonly baseUrl = `${environment.apiBaseUrl}/users`;

  constructor(private http: HttpClient) {}

  list(): Observable<AppUser[]> {
    return this.http.get<AppUser[]>(this.baseUrl);
  }

  updateRoles(userId: string, roles: string[]): Observable<AppUser> {
    return this.http.post<AppUser>(`${this.baseUrl}/${userId}/roles`, { roles });
  }
}
