package com.vamcore.logistics.infrastructure.persistence;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "delivery", uniqueConstraints = @UniqueConstraint(columnNames = {"tenant_id", "delivery_code"}))
public class DeliveryJpaEntity {

    @Id
    private UUID id;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Column(name = "delivery_code", nullable = false)
    private String deliveryCode;

    @Column(name = "customer_name", nullable = false)
    private String customerName;

    @Column(name = "destination_address")
    private String destinationAddress;

    @Column(nullable = false)
    private String status;

    @Column(name = "driver_id")
    private UUID driverId;

    @Column(name = "vehicle_id")
    private UUID vehicleId;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected DeliveryJpaEntity() {
    }

    public DeliveryJpaEntity(UUID id, UUID tenantId, String deliveryCode, String customerName, String destinationAddress,
                              String status, UUID driverId, UUID vehicleId, Instant createdAt) {
        this.id = id;
        this.tenantId = tenantId;
        this.deliveryCode = deliveryCode;
        this.customerName = customerName;
        this.destinationAddress = destinationAddress;
        this.status = status;
        this.driverId = driverId;
        this.vehicleId = vehicleId;
        this.createdAt = createdAt;
    }

    public UUID getId() { return id; }
    public UUID getTenantId() { return tenantId; }
    public String getDeliveryCode() { return deliveryCode; }
    public String getCustomerName() { return customerName; }
    public String getDestinationAddress() { return destinationAddress; }
    public String getStatus() { return status; }
    public UUID getDriverId() { return driverId; }
    public UUID getVehicleId() { return vehicleId; }
    public Instant getCreatedAt() { return createdAt; }
}
