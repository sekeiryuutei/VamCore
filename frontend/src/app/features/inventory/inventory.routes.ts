import { Routes } from '@angular/router';
import { InventoryDashboardComponent } from './pages/inventory-dashboard.component';

/**
 * Rutas del feature 'inventory' (nombre comercial: VamStock, ver
 * environment.productNames.inventory / product.config.yml).
 */
export const INVENTORY_ROUTES: Routes = [
  { path: '', component: InventoryDashboardComponent },
];
