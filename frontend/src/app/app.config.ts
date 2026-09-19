import { ApplicationConfig, provideZoneChangeDetection } from '@angular/core';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { provideHttpClient, withInterceptors } from '@angular/common/http';

import { routes } from './app.routes';
import { authInterceptor } from './core/interceptors/auth.interceptor';

/**
 * Configuración raíz de la aplicación VamCore (standalone components,
 * sin NgModules). Ver product.config.yml para nombres de marca.
 *
 * withComponentInputBinding() permite que los `data` de una ruta (ej.
 * { productName: ... }) se asignen automáticamente a un @Input() del
 * mismo nombre en el componente de esa ruta (ver ComingSoonComponent).
 */
export const appConfig: ApplicationConfig = {
  providers: [
    provideZoneChangeDetection({ eventCoalescing: true }),
    provideRouter(routes, withComponentInputBinding()),
    provideHttpClient(withInterceptors([authInterceptor])),
  ],
};
