import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Customer, Delivery, Driver, TrackingEvent, Vehicle } from '../../shared/models/logistics.model';

@Injectable({ providedIn: 'root' })
export class LogisticsApiService {
  private readonly baseUrl = `${environment.apiBaseUrl}/logistics`;
  private readonly deliveriesUrl = `${this.baseUrl}/deliveries`;

  constructor(private http: HttpClient) {}

  createDelivery(payload: { deliveryCode: string; customerName: string; destinationAddress?: string }): Observable<Delivery> {
    return this.http.post<Delivery>(this.deliveriesUrl, payload);
  }

  listDeliveries(): Observable<Delivery[]> {
    return this.http.get<Delivery[]>(this.deliveriesUrl);
  }

  changeStatus(deliveryId: string, newStatus: string, notes?: string): Observable<Delivery> {
    return this.http.post<Delivery>(`${this.deliveriesUrl}/${deliveryId}/status`, { newStatus, notes });
  }

  assign(deliveryId: string, driverId: string, vehicleId?: string): Observable<Delivery> {
    return this.http.post<Delivery>(`${this.deliveriesUrl}/${deliveryId}/assign`, { driverId, vehicleId });
  }

  tracking(deliveryId: string): Observable<TrackingEvent[]> {
    return this.http.get<TrackingEvent[]>(`${this.deliveriesUrl}/${deliveryId}/tracking`);
  }

  // --- Catálogos: Customer, Vehicle, Driver (sección 18) ---

  createCustomer(payload: { name: string; phone?: string; defaultAddress?: string }): Observable<Customer> {
    return this.http.post<Customer>(`${this.baseUrl}/customers`, payload);
  }

  listCustomers(): Observable<Customer[]> {
    return this.http.get<Customer[]>(`${this.baseUrl}/customers`);
  }

  createVehicle(payload: { plate: string; model?: string }): Observable<Vehicle> {
    return this.http.post<Vehicle>(`${this.baseUrl}/vehicles`, payload);
  }

  listVehicles(): Observable<Vehicle[]> {
    return this.http.get<Vehicle[]>(`${this.baseUrl}/vehicles`);
  }

  createDriver(payload: { fullName: string; licenseNumber?: string }): Observable<Driver> {
    return this.http.post<Driver>(`${this.baseUrl}/drivers`, payload);
  }

  listDrivers(): Observable<Driver[]> {
    return this.http.get<Driver[]>(`${this.baseUrl}/drivers`);
  }
}
