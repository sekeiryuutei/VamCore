package com.vamcore.logistics.infrastructure.persistence;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "driver")
public class DriverJpaEntity {

    @Id
    private UUID id;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "license_number")
    private String licenseNumber;

    @Column(nullable = false)
    private String status;

    protected DriverJpaEntity() {
    }

    public DriverJpaEntity(UUID id, UUID tenantId, String fullName, String licenseNumber, String status) {
        this.id = id;
        this.tenantId = tenantId;
        this.fullName = fullName;
        this.licenseNumber = licenseNumber;
        this.status = status;
    }

    public UUID getId() { return id; }
    public UUID getTenantId() { return tenantId; }
    public String getFullName() { return fullName; }
    public String getLicenseNumber() { return licenseNumber; }
    public String getStatus() { return status; }
}
