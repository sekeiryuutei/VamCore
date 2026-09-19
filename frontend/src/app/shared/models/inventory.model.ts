export interface Product {
  id: string;
  sku: string;
  name: string;
  description?: string;
  category?: string;
  unitOfMeasure: string;
  trackingType: string;
  status: string;
  createdAt: string;
}

export interface Warehouse {
  id: string;
  name: string;
  address?: string;
  branchId?: string;
  active: boolean;
}

export interface Stock {
  productId: string;
  warehouseId: string;
  quantity: number;
  unit: string;
}

export interface PurchaseOrder {
  id: string;
  code: string;
  supplierName?: string;
  productId: string;
  warehouseId: string;
  quantityOrdered: number;
  status: string;
  createdAt: string;
}

export interface StockCount {
  id: string;
  productId: string;
  warehouseId: string;
  systemQuantityAtCount: number;
  countedQuantity: number;
  variance: number;
  status: string;
  createdAt: string;
}
