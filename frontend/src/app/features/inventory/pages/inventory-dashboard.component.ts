import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { InventoryApiService } from '../../../core/services/inventory-api.service';
import { Product, PurchaseOrder, Stock, StockCount, Warehouse } from '../../../shared/models/inventory.model';
import { environment } from '../../../../environments/environment';

/**
 * Pantalla de VamStock: catálogo, bodegas, stock, órdenes de compra
 * (sección 53) y conteos físicos (sección 56).
 */
@Component({
  selector: 'vc-inventory-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './inventory-dashboard.component.html',
})
export class InventoryDashboardComponent implements OnInit {
  protected readonly productName = environment.productNames.inventory;

  sku = '';
  name = '';
  unitOfMeasure = 'unidad';
  warehouseName = 'Bodega principal';
  receiveQuantity = 0;

  poCode = '';
  poSupplier = '';
  poQuantity = 0;

  countQuantity = 0;

  currentProductId = signal<string | null>(null);
  currentWarehouseId = signal<string | null>(null);
  stock = signal<Stock[]>([]);
  products = signal<Product[]>([]);
  warehouses = signal<Warehouse[]>([]);
  purchaseOrders = signal<PurchaseOrder[]>([]);
  stockCounts = signal<StockCount[]>([]);
  message = signal<string | null>(null);
  errorMessage = signal<string | null>(null);

  constructor(private inventoryApi: InventoryApiService) {}

  ngOnInit(): void {
    this.refreshCatalogs();
  }

  refreshCatalogs(): void {
    this.inventoryApi.listProducts().subscribe({ next: (list) => this.products.set(list) });
    this.inventoryApi.listWarehouses().subscribe({ next: (list) => this.warehouses.set(list) });
    this.inventoryApi.listPurchaseOrders().subscribe({ next: (list) => this.purchaseOrders.set(list) });
    this.inventoryApi.listStockCounts().subscribe({ next: (list) => this.stockCounts.set(list) });
  }

  selectProduct(product: Product): void {
    this.currentProductId.set(product.id);
    this.refreshStock();
  }

  selectWarehouse(warehouse: Warehouse): void {
    this.currentWarehouseId.set(warehouse.id);
  }

  selectedStockTotal(): string {
    const rows = this.stock();
    if (rows.length === 0) return '—';
    const total = rows.reduce((sum, r) => sum + Number(r.quantity), 0);
    return `${total} ${rows[0].unit}`;
  }

  productSku(productId: string): string {
    return this.products().find((p) => p.id === productId)?.sku ?? productId.slice(0, 8);
  }

  createProduct(): void {
    this.errorMessage.set(null);
    this.inventoryApi.createProduct({ sku: this.sku, name: this.name, unitOfMeasure: this.unitOfMeasure }).subscribe({
      next: (product) => {
        this.currentProductId.set(product.id);
        this.message.set(`Producto creado: ${product.sku}`);
        this.sku = '';
        this.name = '';
        this.refreshCatalogs();
      },
      error: (err) => this.errorMessage.set(err?.error?.message ?? 'No se pudo crear el producto'),
    });
  }

  createWarehouse(): void {
    this.errorMessage.set(null);
    this.inventoryApi.createWarehouse({ name: this.warehouseName }).subscribe({
      next: (warehouse) => {
        this.currentWarehouseId.set(warehouse.id);
        this.message.set(`Bodega creada: ${warehouse.name}`);
        this.refreshCatalogs();
      },
      error: (err) => this.errorMessage.set(err?.error?.message ?? 'No se pudo crear la bodega'),
    });
  }

  receiveStock(): void {
    const productId = this.currentProductId();
    const warehouseId = this.currentWarehouseId();
    if (!productId || !warehouseId) {
      this.errorMessage.set('Primero selecciona un producto y una bodega en las tablas.');
      return;
    }
    this.errorMessage.set(null);
    this.inventoryApi.receiveStock({ productId, warehouseId, quantity: this.receiveQuantity }).subscribe({
      next: () => {
        this.message.set('Stock recibido correctamente');
        this.refreshStock();
      },
      error: (err) => this.errorMessage.set(err?.error?.message ?? 'No se pudo registrar la recepción'),
    });
  }

  refreshStock(): void {
    const productId = this.currentProductId();
    if (!productId) return;
    this.inventoryApi.getStock(productId).subscribe({
      next: (stock) => this.stock.set(stock),
      error: (err) => this.errorMessage.set(err?.error?.message ?? 'No se pudo consultar el stock'),
    });
  }

  createPurchaseOrder(): void {
    const productId = this.currentProductId();
    const warehouseId = this.currentWarehouseId();
    if (!productId || !warehouseId) {
      this.errorMessage.set('Selecciona un producto y una bodega antes de crear la orden.');
      return;
    }
    this.errorMessage.set(null);
    this.inventoryApi
      .createPurchaseOrder({ code: this.poCode, supplierName: this.poSupplier, productId, warehouseId, quantityOrdered: this.poQuantity })
      .subscribe({
        next: () => {
          this.message.set(`Orden de compra creada: ${this.poCode}`);
          this.poCode = '';
          this.poSupplier = '';
          this.refreshCatalogs();
        },
        error: (err) => this.errorMessage.set(err?.error?.message ?? 'No se pudo crear la orden de compra'),
      });
  }

  confirmPurchaseOrder(po: PurchaseOrder): void {
    this.inventoryApi.confirmPurchaseOrder(po.id).subscribe({
      next: () => this.refreshCatalogs(),
      error: (err) => this.errorMessage.set(err?.error?.message ?? 'No se pudo confirmar la orden'),
    });
  }

  receivePurchaseOrder(po: PurchaseOrder): void {
    this.inventoryApi.receivePurchaseOrder(po.id).subscribe({
      next: () => {
        this.message.set(`Orden ${po.code} recibida — stock actualizado`);
        this.refreshCatalogs();
      },
      error: (err) => this.errorMessage.set(err?.error?.message ?? 'No se pudo recibir la orden'),
    });
  }

  cancelPurchaseOrder(po: PurchaseOrder): void {
    this.inventoryApi.cancelPurchaseOrder(po.id).subscribe({
      next: () => this.refreshCatalogs(),
      error: (err) => this.errorMessage.set(err?.error?.message ?? 'No se pudo cancelar la orden'),
    });
  }

  createStockCount(): void {
    const productId = this.currentProductId();
    const warehouseId = this.currentWarehouseId();
    if (!productId || !warehouseId) {
      this.errorMessage.set('Selecciona un producto y una bodega antes de registrar el conteo.');
      return;
    }
    this.errorMessage.set(null);
    this.inventoryApi.createStockCount({ productId, warehouseId, countedQuantity: this.countQuantity }).subscribe({
      next: () => {
        this.message.set('Conteo registrado — revisa la varianza antes de aplicarlo');
        this.refreshCatalogs();
      },
      error: (err) => this.errorMessage.set(err?.error?.message ?? 'No se pudo registrar el conteo'),
    });
  }

  applyStockCount(count: StockCount): void {
    this.inventoryApi.applyStockCount(count.id).subscribe({
      next: () => {
        this.message.set('Varianza aplicada al stock');
        this.refreshCatalogs();
        this.refreshStock();
      },
      error: (err) => this.errorMessage.set(err?.error?.message ?? 'No se pudo aplicar el conteo'),
    });
  }
}
