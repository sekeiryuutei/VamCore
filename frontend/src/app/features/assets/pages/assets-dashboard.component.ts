import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AssetApiService } from '../../../core/services/asset-api.service';
import { Asset, Depreciation } from '../../../shared/models/asset.model';
import { environment } from '../../../../environments/environment';

/**
 * Pantalla de VamAsset: crear activo (con costo/vida útil opcionales),
 * ver catálogo, avanzar el ciclo de vida (mantenimiento real, no solo un
 * cambio de estado), asignar, y consultar depreciación del seleccionado.
 */
@Component({
  selector: 'vc-assets-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './assets-dashboard.component.html',
})
export class AssetsDashboardComponent implements OnInit {
  protected readonly productName = environment.productNames.assets;

  assetCode = '';
  name = '';
  category = '';
  serialNumber = '';
  acquisitionCost: number | null = null;
  usefulLifeMonths: number | null = null;
  assigneeId = '';

  assets = signal<Asset[]>([]);
  selectedDepreciation = signal<Depreciation | null>(null);
  message = signal<string | null>(null);
  errorMessage = signal<string | null>(null);

  /** Próximo estado sugerido por cada estado actual (mismo grafo que el backend). */
  private readonly nextStatusSuggestion: Record<string, string> = {
    ACQUIRED: 'IN_STORAGE',
    IN_STORAGE: 'RETIRED',
    ASSIGNED: 'IN_USE',
    RETIRED: 'DISPOSED',
  };

  constructor(private assetApi: AssetApiService) {}

  ngOnInit(): void {
    this.refresh();
  }

  refresh(): void {
    this.assetApi.listAssets().subscribe({ next: (list) => this.assets.set(list) });
  }

  createAsset(): void {
    this.errorMessage.set(null);
    this.assetApi
      .createAsset({
        assetCode: this.assetCode, name: this.name, category: this.category, serialNumber: this.serialNumber,
        acquisitionCost: this.acquisitionCost ?? undefined, currency: 'COP', usefulLifeMonths: this.usefulLifeMonths ?? undefined,
      })
      .subscribe({
        next: (asset) => {
          this.message.set(`Activo creado: ${asset.assetCode}`);
          this.assetCode = '';
          this.name = '';
          this.acquisitionCost = null;
          this.usefulLifeMonths = null;
          this.refresh();
        },
        error: (err) => this.errorMessage.set(err?.error?.message ?? 'No se pudo crear el activo'),
      });
  }

  suggestedNextStatus(asset: Asset): string | null {
    return this.nextStatusSuggestion[asset.status] ?? null;
  }

  assignedCount(): number {
    return this.assets().filter((a) => a.status === 'ASSIGNED' || a.status === 'IN_USE').length;
  }

  disposedCount(): number {
    return this.assets().filter((a) => a.status === 'DISPOSED').length;
  }

  advanceStatus(asset: Asset): void {
    const next = this.suggestedNextStatus(asset);
    if (!next) return;
    this.errorMessage.set(null);
    this.assetApi.changeStatus(asset.id, next).subscribe({
      next: () => {
        this.message.set(`Activo ${asset.assetCode} pasó a ${next}`);
        this.refresh();
      },
      error: (err) => this.errorMessage.set(err?.error?.message ?? 'No se pudo cambiar el estado'),
    });
  }

  scheduleMaintenance(asset: Asset): void {
    this.errorMessage.set(null);
    this.assetApi.scheduleMaintenance(asset.id, 'Mantenimiento programado desde el dashboard').subscribe({
      next: () => {
        this.message.set(`Mantenimiento programado para ${asset.assetCode}`);
        this.refresh();
      },
      error: (err) => this.errorMessage.set(err?.error?.message ?? 'No se pudo programar el mantenimiento'),
    });
  }

  completeMaintenance(asset: Asset): void {
    this.errorMessage.set(null);
    this.assetApi.completeMaintenance(asset.id).subscribe({
      next: () => {
        this.message.set(`Mantenimiento de ${asset.assetCode} completado — vuelve a IN_USE`);
        this.refresh();
      },
      error: (err) => this.errorMessage.set(err?.error?.message ?? 'No se pudo completar el mantenimiento'),
    });
  }

  assign(asset: Asset): void {
    if (!this.assigneeId) {
      this.errorMessage.set('Escribe un ID de persona para asignar (UUID de prueba).');
      return;
    }
    this.errorMessage.set(null);
    this.assetApi.assign(asset.id, this.assigneeId).subscribe({
      next: () => {
        this.message.set(`Activo ${asset.assetCode} asignado`);
        this.refresh();
      },
      error: (err) => this.errorMessage.set(err?.error?.message ?? 'No se pudo asignar el activo'),
    });
  }

  viewDepreciation(asset: Asset): void {
    this.errorMessage.set(null);
    this.assetApi.depreciation(asset.id).subscribe({
      next: (dep) => this.selectedDepreciation.set(dep),
      error: (err) => this.errorMessage.set(err?.error?.message ?? 'No se pudo consultar la depreciación'),
    });
  }
}
