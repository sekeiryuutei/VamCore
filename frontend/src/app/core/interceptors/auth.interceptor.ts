import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';
import { AuthService } from '../auth/auth.service';

/**
 * Añade "Authorization: Bearer <token>" a cada request saliente.
 * El tenant NO se envía manualmente: viaja embebido en el JWT y el
 * backend lo extrae en JwtAuthenticationFilter (ver ADR-005 Multi-tenancy).
 *
 * Si el backend responde 401 (UNAUTHENTICATED — token vencido o inválido,
 * por ejemplo si el permission system cambió después de que se emitió el
 * token), se limpia la sesión local y se manda a /login automáticamente
 * en vez de dejar al usuario viendo errores sin salida.
 */
export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const auth = inject(AuthService);
  const router = inject(Router);
  const token = auth.token;

  const cloned = token ? req.clone({ setHeaders: { Authorization: `Bearer ${token}` } }) : req;

  return next(cloned).pipe(
    catchError((err) => {
      if (err?.status === 401) {
        auth.logout();
        router.navigate(['/login']);
      }
      return throwError(() => err);
    })
  );
};
