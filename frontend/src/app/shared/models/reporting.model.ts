export interface PlatformSummary {
  productCount: number;
  warehouseCount: number;
  assetCount: number;
  assetsByStatus: Record<string, number>;
  deliveryCount: number;
  deliveriesByStatus: Record<string, number>;
}

export interface RecentActivity {
  action: string;
  entityType: string;
  entityId: string;
  occurredAt: string;
}
