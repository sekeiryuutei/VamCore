import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { AuthService } from '../auth/auth.service';

/**
 * Añade "Authorization: Bearer <token>" a cada request saliente.
 * El tenant NO se envía manualmente: viaja embebido en el JWT y el
 * backend lo extrae en JwtAuthenticationFilter (ver ADR-005 Multi-tenancy).
 */
export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const auth = inject(AuthService);
  const token = auth.token;

  if (!token) {
    return next(req);
  }

  const cloned = req.clone({
    setHeaders: { Authorization: `Bearer ${token}` },
  });
  return next(cloned);
};
