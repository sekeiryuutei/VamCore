export interface Delivery {
  id: string;
  deliveryCode: string;
  customerName: string;
  destinationAddress?: string;
  status: string;
  driverId?: string;
  vehicleId?: string;
  createdAt: string;
}

export interface TrackingEvent {
  id: string;
  deliveryId: string;
  status: string;
  notes?: string;
  occurredAt: string;
}

export interface Customer {
  id: string;
  name: string;
  phone?: string;
  defaultAddress?: string;
}

export interface Vehicle {
  id: string;
  plate: string;
  model?: string;
  status: string;
}

export interface Driver {
  id: string;
  fullName: string;
  licenseNumber?: string;
  status: string;
}
