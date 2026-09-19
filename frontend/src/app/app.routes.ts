import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';
import { LoginComponent } from './features/auth/login.component';
import { SummaryDashboardComponent } from './features/summary/pages/summary-dashboard.component';

/**
 * Rutas raíz. Cada feature (inventory/VamStock, assets/VamAsset,
 * logistics/VamTrack) se carga con lazy loading (ver sección 31 del
 * documento de arquitectura - Frontend). '/resumen' es la landing page
 * tras iniciar sesión, agregando los módulos core transversales
 * (Reporting, Notifications, Files) que no son productos comerciales.
 *
 * Los nombres comerciales mostrados en el menú deben leerse de
 * environment.productNames, NO hardcodearse en las plantillas.
 */
export const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: 'resumen', canActivate: [authGuard], component: SummaryDashboardComponent },
  {
    path: 'inventory',
    canActivate: [authGuard],
    loadChildren: () => import('./features/inventory/inventory.routes').then((m) => m.INVENTORY_ROUTES),
  },
  {
    path: 'assets',
    canActivate: [authGuard],
    loadChildren: () => import('./features/assets/assets.routes').then((m) => m.ASSETS_ROUTES),
  },
  {
    path: 'logistics',
    canActivate: [authGuard],
    loadChildren: () => import('./features/logistics/logistics.routes').then((m) => m.LOGISTICS_ROUTES),
  },
  { path: '', redirectTo: 'resumen', pathMatch: 'full' },
];
