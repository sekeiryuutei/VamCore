import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { LogisticsApiService } from '../../../core/services/logistics-api.service';
import { Customer, Delivery, Driver, Vehicle } from '../../../shared/models/logistics.model';
import { environment } from '../../../../environments/environment';

/**
 * Pantalla de VamTrack: crear entrega, avanzar la máquina de estados
 * (sección 20), asignar conductor/vehículo eligiéndolos de catálogos reales
 * (sección 18) en vez de escribir un UUID a mano.
 */
@Component({
  selector: 'vc-logistics-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './logistics-dashboard.component.html',
})
export class LogisticsDashboardComponent implements OnInit {
  protected readonly productName = environment.productNames.logistics;

  deliveryCode = '';
  customerName = '';
  destinationAddress = '';
  selectedDriverId = '';
  selectedVehicleId = '';

  newCustomerName = '';
  newVehiclePlate = '';
  newDriverName = '';

  deliveries = signal<Delivery[]>([]);
  customers = signal<Customer[]>([]);
  vehicles = signal<Vehicle[]>([]);
  drivers = signal<Driver[]>([]);
  message = signal<string | null>(null);
  errorMessage = signal<string | null>(null);

  /** Siguiente paso "feliz" sugerido por cada estado (mismo grafo que el backend). */
  private readonly nextStatusSuggestion: Record<string, string> = {
    CREATED: 'CONFIRMED',
    CONFIRMED: 'PREPARING',
    PREPARING: 'READY',
    DISPATCHED: 'IN_TRANSIT',
    IN_TRANSIT: 'DELIVERED',
  };

  constructor(private logisticsApi: LogisticsApiService) {}

  ngOnInit(): void {
    this.refresh();
  }

  refresh(): void {
    this.logisticsApi.listDeliveries().subscribe({ next: (list) => this.deliveries.set(list) });
    this.logisticsApi.listCustomers().subscribe({ next: (list) => this.customers.set(list) });
    this.logisticsApi.listVehicles().subscribe({ next: (list) => this.vehicles.set(list) });
    this.logisticsApi.listDrivers().subscribe({ next: (list) => this.drivers.set(list) });
  }

  createDelivery(): void {
    this.errorMessage.set(null);
    this.logisticsApi
      .createDelivery({ deliveryCode: this.deliveryCode, customerName: this.customerName, destinationAddress: this.destinationAddress })
      .subscribe({
        next: (delivery) => {
          this.message.set(`Entrega creada: ${delivery.deliveryCode}`);
          this.deliveryCode = '';
          this.customerName = '';
          this.refresh();
        },
        error: (err) => this.errorMessage.set(err?.error?.message ?? 'No se pudo crear la entrega'),
      });
  }

  suggestedNextStatus(delivery: Delivery): string | null {
    return this.nextStatusSuggestion[delivery.status] ?? null;
  }

  inTransitCount(): number {
    return this.deliveries().filter((d) => d.status === 'IN_TRANSIT').length;
  }

  deliveredCount(): number {
    return this.deliveries().filter((d) => d.status === 'DELIVERED').length;
  }

  advanceStatus(delivery: Delivery): void {
    const next = this.suggestedNextStatus(delivery);
    if (!next) return;
    this.errorMessage.set(null);
    this.logisticsApi.changeStatus(delivery.id, next).subscribe({
      next: () => {
        this.message.set(`Entrega ${delivery.deliveryCode} pasó a ${next}`);
        this.refresh();
      },
      error: (err) => this.errorMessage.set(err?.error?.message ?? 'No se pudo cambiar el estado'),
    });
  }

  assignDriver(delivery: Delivery): void {
    if (!this.selectedDriverId) {
      this.errorMessage.set('Elige un conductor en el panel de la izquierda antes de asignar.');
      return;
    }
    this.errorMessage.set(null);
    this.logisticsApi.assign(delivery.id, this.selectedDriverId, this.selectedVehicleId || undefined).subscribe({
      next: () => {
        this.message.set(`Entrega ${delivery.deliveryCode} asignada`);
        this.refresh();
      },
      error: (err) => this.errorMessage.set(err?.error?.message ?? 'No se pudo asignar la entrega'),
    });
  }

  createCustomer(): void {
    this.logisticsApi.createCustomer({ name: this.newCustomerName }).subscribe({
      next: () => { this.newCustomerName = ''; this.refresh(); },
      error: (err) => this.errorMessage.set(err?.error?.message ?? 'No se pudo crear el cliente'),
    });
  }

  createVehicle(): void {
    this.logisticsApi.createVehicle({ plate: this.newVehiclePlate }).subscribe({
      next: () => { this.newVehiclePlate = ''; this.refresh(); },
      error: (err) => this.errorMessage.set(err?.error?.message ?? 'No se pudo crear el vehículo'),
    });
  }

  createDriver(): void {
    this.logisticsApi.createDriver({ fullName: this.newDriverName }).subscribe({
      next: () => { this.newDriverName = ''; this.refresh(); },
      error: (err) => this.errorMessage.set(err?.error?.message ?? 'No se pudo crear el conductor'),
    });
  }
}
