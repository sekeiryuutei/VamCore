package com.vamcore.inventory.infrastructure.persistence;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "inventory_warehouse")
public class WarehouseJpaEntity {

    @Id
    private UUID id;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Column(name = "branch_id")
    private UUID branchId;

    @Column(nullable = false)
    private String name;

    private String address;

    @Column(nullable = false)
    private boolean active;

    protected WarehouseJpaEntity() {
    }

    public WarehouseJpaEntity(UUID id, UUID tenantId, UUID branchId, String name, String address, boolean active) {
        this.id = id;
        this.tenantId = tenantId;
        this.branchId = branchId;
        this.name = name;
        this.address = address;
        this.active = active;
    }

    public UUID getId() { return id; }
    public UUID getTenantId() { return tenantId; }
    public UUID getBranchId() { return branchId; }
    public String getName() { return name; }
    public String getAddress() { return address; }
    public boolean isActive() { return active; }
}
