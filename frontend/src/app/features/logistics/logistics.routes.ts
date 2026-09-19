import { Routes } from '@angular/router';
import { LogisticsDashboardComponent } from './pages/logistics-dashboard.component';

/**
 * Rutas del feature 'logistics' (nombre comercial: VamTrack, ver
 * environment.productNames.logistics / product.config.yml).
 */
export const LOGISTICS_ROUTES: Routes = [
  { path: '', component: LogisticsDashboardComponent },
];
