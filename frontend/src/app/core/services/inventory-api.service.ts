import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Product, PurchaseOrder, Stock, StockCount, Warehouse } from '../../shared/models/inventory.model';

/**
 * Cliente HTTP del bounded context Inventory (VamStock). El interceptor
 * `authInterceptor` añade el JWT automáticamente a cada request.
 */
@Injectable({ providedIn: 'root' })
export class InventoryApiService {
  private readonly baseUrl = `${environment.apiBaseUrl}/inventory`;

  constructor(private http: HttpClient) {}

  createProduct(payload: { sku: string; name: string; unitOfMeasure: string }): Observable<Product> {
    return this.http.post<Product>(`${this.baseUrl}/products`, payload);
  }

  listProducts(): Observable<Product[]> {
    return this.http.get<Product[]>(`${this.baseUrl}/products`);
  }

  createWarehouse(payload: { name: string }): Observable<Warehouse> {
    return this.http.post<Warehouse>(`${this.baseUrl}/warehouses`, payload);
  }

  listWarehouses(): Observable<Warehouse[]> {
    return this.http.get<Warehouse[]>(`${this.baseUrl}/warehouses`);
  }

  receiveStock(payload: { productId: string; warehouseId: string; quantity: number }): Observable<Stock> {
    return this.http.post<Stock>(`${this.baseUrl}/receipts`, payload);
  }

  getStock(productId: string): Observable<Stock[]> {
    return this.http.get<Stock[]>(`${this.baseUrl}/products/${productId}/stock`);
  }

  // --- Purchase Orders (sección 53 del documento de arquitectura) ---

  createPurchaseOrder(payload: { code: string; supplierName?: string; productId: string; warehouseId: string; quantityOrdered: number }): Observable<PurchaseOrder> {
    return this.http.post<PurchaseOrder>(`${this.baseUrl}/purchase-orders`, payload);
  }

  listPurchaseOrders(): Observable<PurchaseOrder[]> {
    return this.http.get<PurchaseOrder[]>(`${this.baseUrl}/purchase-orders`);
  }

  confirmPurchaseOrder(id: string): Observable<PurchaseOrder> {
    return this.http.post<PurchaseOrder>(`${this.baseUrl}/purchase-orders/${id}/confirm`, {});
  }

  receivePurchaseOrder(id: string): Observable<PurchaseOrder> {
    return this.http.post<PurchaseOrder>(`${this.baseUrl}/purchase-orders/${id}/receive`, {});
  }

  cancelPurchaseOrder(id: string): Observable<PurchaseOrder> {
    return this.http.post<PurchaseOrder>(`${this.baseUrl}/purchase-orders/${id}/cancel`, {});
  }

  // --- Stock Counts (conteo físico, sección 56) ---

  createStockCount(payload: { productId: string; warehouseId: string; countedQuantity: number }): Observable<StockCount> {
    return this.http.post<StockCount>(`${this.baseUrl}/stock-counts`, payload);
  }

  listStockCounts(): Observable<StockCount[]> {
    return this.http.get<StockCount[]>(`${this.baseUrl}/stock-counts`);
  }

  applyStockCount(id: string): Observable<StockCount> {
    return this.http.post<StockCount>(`${this.baseUrl}/stock-counts/${id}/apply`, {});
  }
}
