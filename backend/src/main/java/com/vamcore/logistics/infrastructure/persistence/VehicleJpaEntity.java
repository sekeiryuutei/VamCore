package com.vamcore.logistics.infrastructure.persistence;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "vehicle")
public class VehicleJpaEntity {

    @Id
    private UUID id;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Column(nullable = false)
    private String plate;

    private String model;

    @Column(nullable = false)
    private String status;

    protected VehicleJpaEntity() {
    }

    public VehicleJpaEntity(UUID id, UUID tenantId, String plate, String model, String status) {
        this.id = id;
        this.tenantId = tenantId;
        this.plate = plate;
        this.model = model;
        this.status = status;
    }

    public UUID getId() { return id; }
    public UUID getTenantId() { return tenantId; }
    public String getPlate() { return plate; }
    public String getModel() { return model; }
    public String getStatus() { return status; }
}
