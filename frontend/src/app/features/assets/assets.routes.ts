import { Routes } from '@angular/router';
import { AssetsDashboardComponent } from './pages/assets-dashboard.component';

/**
 * Rutas del feature 'assets' (nombre comercial: VamAsset, ver
 * environment.productNames.assets / product.config.yml).
 */
export const ASSETS_ROUTES: Routes = [
  { path: '', component: AssetsDashboardComponent },
];
