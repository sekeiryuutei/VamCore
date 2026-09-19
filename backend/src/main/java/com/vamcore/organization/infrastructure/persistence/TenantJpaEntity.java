package com.vamcore.organization.infrastructure.persistence;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

/**
 * Entidad de persistencia (adaptador de infraestructura). El dominio
 * (com.vamcore.organization.domain.model.Tenant) NO conoce JPA ni esta clase.
 */
@Entity
@Table(name = "tenant")
public class TenantJpaEntity {

    @Id
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(name = "tax_id", unique = true)
    private String taxId;

    @Column(nullable = false)
    private String status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected TenantJpaEntity() {
        // requerido por JPA
    }

    public TenantJpaEntity(UUID id, String name, String taxId, String status, Instant createdAt) {
        this.id = id;
        this.name = name;
        this.taxId = taxId;
        this.status = status;
        this.createdAt = createdAt;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getTaxId() {
        return taxId;
    }

    public String getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
