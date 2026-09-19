import { Component, Input } from '@angular/core';

/**
 * Placeholder para bounded contexts que todavía no tienen UI (VamAsset /
 * VamTrack, ver roadmap Fase 3-4 en el README). Evita que la ruta quede en
 * blanco mientras el módulo se construye.
 */
@Component({
  selector: 'vc-coming-soon',
  standalone: true,
  template: `
    <div class="vc-coming-soon">
      <h1 class="vc-display">{{ productName }}</h1>
      <p>Este módulo todavía está en construcción. Vuelve pronto.</p>
    </div>
  `,
  styles: [`
    .vc-coming-soon {
      padding: 3rem 2rem;
      text-align: center;
      color: var(--vc-color-neutral-gray);
    }
    h1 {
      color: var(--vc-color-slate-900);
    }
  `],
})
export class ComingSoonComponent {
  @Input() productName = '';
}
