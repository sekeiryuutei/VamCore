import { Component } from '@angular/core';
import { RouterLink, RouterLinkActive, RouterOutlet, Router } from '@angular/router';
import { environment } from '../environments/environment';
import { AuthService } from './core/auth/auth.service';

/**
 * Selector 'vc-root' (prefijo 'vc' = Vam Core, ver angular.json -> prefix).
 *
 * Shell de la aplicación: sidebar oscuro persistente (ver styles.scss
 * .vc-shell) con un acento de color distinto por producto, para que el
 * usuario sepa en qué módulo está de un vistazo sin leer el título.
 * Nombres SIEMPRE desde environment.*, nunca hardcodeados (ver
 * product.config.yml).
 */
@Component({
  selector: 'vc-root',
  standalone: true,
  imports: [RouterOutlet, RouterLink, RouterLinkActive],
  template: `
    @if (auth.isAuthenticated()) {
      <div class="vc-shell">
        <aside class="vc-sidebar">
          <div class="vc-sidebar-brand">
            {{ platformName }} <small>{{ companyName }}</small>
          </div>

          <nav class="vc-sidebar-nav">
            <a routerLink="/resumen" routerLinkActive="active" class="vc-sidebar-link" style="--vc-link-accent: var(--vc-gray)">
              <span class="vc-dot" style="color: var(--vc-gray)"></span>Resumen
            </a>
            <a routerLink="/inventory" routerLinkActive="active" class="vc-sidebar-link" style="--vc-link-accent: var(--vc-blue)">
              <span class="vc-dot" style="color: var(--vc-blue)"></span>{{ productNames.inventory }}
            </a>
            <a routerLink="/assets" routerLinkActive="active" class="vc-sidebar-link" style="--vc-link-accent: var(--vc-violet)">
              <span class="vc-dot" style="color: var(--vc-violet)"></span>{{ productNames.assets }}
            </a>
            <a routerLink="/logistics" routerLinkActive="active" class="vc-sidebar-link" style="--vc-link-accent: var(--vc-cyan)">
              <span class="vc-dot" style="color: var(--vc-cyan)"></span>{{ productNames.logistics }}
            </a>
          </nav>

          <div class="vc-sidebar-footer">
            <span class="vc-user-email">{{ auth.currentUserEmail() }}</span>
            <button (click)="logout()">Cerrar sesión</button>
          </div>
        </aside>

        <main class="vc-content">
          <router-outlet />
        </main>
      </div>
    } @else {
      <router-outlet />
    }
  `,
})
export class AppComponent {
  protected readonly platformName = environment.platformName;
  protected readonly companyName = environment.companyName;
  protected readonly productNames = environment.productNames;

  constructor(protected auth: AuthService, private router: Router) {}

  logout(): void {
    this.auth.logout();
    this.router.navigate(['/login']);
  }
}
