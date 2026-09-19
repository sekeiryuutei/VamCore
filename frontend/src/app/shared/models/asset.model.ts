export interface Asset {
  id: string;
  assetCode: string;
  name: string;
  category?: string;
  serialNumber?: string;
  status: string;
  currentAssigneeId?: string;
  acquiredAt: string;
  acquisitionCost?: number;
  currency?: string;
  usefulLifeMonths?: number;
}

export interface AssetAssignment {
  id: string;
  assetId: string;
  assigneeId: string;
  assignedAt: string;
}

export interface Maintenance {
  id: string;
  assetId: string;
  scheduledAt: string;
  completedAt?: string;
  notes?: string;
  status: string;
}

export interface Depreciation {
  assetId: string;
  acquisitionCost?: number;
  currency?: string;
  usefulLifeMonths?: number;
  currentBookValue?: number;
  asOf: string;
}
