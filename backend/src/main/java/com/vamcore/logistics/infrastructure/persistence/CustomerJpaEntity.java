package com.vamcore.logistics.infrastructure.persistence;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "customer")
public class CustomerJpaEntity {

    @Id
    private UUID id;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Column(nullable = false)
    private String name;

    private String phone;

    @Column(name = "default_address")
    private String defaultAddress;

    protected CustomerJpaEntity() {
    }

    public CustomerJpaEntity(UUID id, UUID tenantId, String name, String phone, String defaultAddress) {
        this.id = id;
        this.tenantId = tenantId;
        this.name = name;
        this.phone = phone;
        this.defaultAddress = defaultAddress;
    }

    public UUID getId() { return id; }
    public UUID getTenantId() { return tenantId; }
    public String getName() { return name; }
    public String getPhone() { return phone; }
    public String getDefaultAddress() { return defaultAddress; }
}
