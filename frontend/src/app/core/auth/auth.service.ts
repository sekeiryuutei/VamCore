import { Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { environment } from '../../../environments/environment';
import { AuthSession } from '../../shared/models/auth.model';

const TOKEN_STORAGE_KEY = 'vamcore_access_token';

/**
 * Servicio de autenticación. Llama a POST {apiBaseUrl}/auth/login
 * (ver identity.infrastructure.web.AuthController en el backend).
 */
@Injectable({ providedIn: 'root' })
export class AuthService {
  readonly isAuthenticated = signal<boolean>(!!localStorage.getItem(TOKEN_STORAGE_KEY));

  constructor(private http: HttpClient) {}

  login(email: string, password: string): Observable<AuthSession> {
    return this.http
      .post<AuthSession>(`${environment.apiBaseUrl}/auth/login`, { email, password })
      .pipe(
        tap((session) => {
          localStorage.setItem(TOKEN_STORAGE_KEY, session.accessToken);
          this.isAuthenticated.set(true);
        })
      );
  }

  logout(): void {
    localStorage.removeItem(TOKEN_STORAGE_KEY);
    this.isAuthenticated.set(false);
  }

  get token(): string | null {
    return localStorage.getItem(TOKEN_STORAGE_KEY);
  }

  /**
   * Lee el claim "email" directamente del payload del JWT (sin librería
   * externa: es solo un JSON en base64url). Suficiente para mostrar quién
   * inició sesión en el sidebar; no se usa para autorización real.
   */
  currentUserEmail(): string {
    const token = this.token;
    if (!token) return '';
    try {
      const payload = token.split('.')[1];
      const decoded = atob(payload.replace(/-/g, '+').replace(/_/g, '/'));
      return JSON.parse(decoded).email ?? '';
    } catch {
      return '';
    }
  }

  /** Roles embebidos en el JWT (ver identity.infrastructure.security.JwtTokenProvider). */
  currentUserRoles(): string[] {
    const token = this.token;
    if (!token) return [];
    try {
      const payload = token.split('.')[1];
      const decoded = atob(payload.replace(/-/g, '+').replace(/_/g, '/'));
      return JSON.parse(decoded).roles ?? [];
    } catch {
      return [];
    }
  }

  isAdmin(): boolean {
    return this.currentUserRoles().includes('ADMIN');
  }
}
