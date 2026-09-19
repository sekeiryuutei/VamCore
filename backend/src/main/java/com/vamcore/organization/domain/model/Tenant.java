package com.vamcore.organization.domain.model;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Aggregate root que representa una empresa cliente de la plataforma
 * (ver sección 7 y 10 del documento de arquitectura - ADR-005 Multi-tenancy
 * y Bounded Context Organization).
 *
 * Todo dato perteneciente a una organización debe estar asociado directa
 * o indirectamente a un Tenant.
 */
public class Tenant {

    private final UUID id;
    private String name;
    private String taxId;
    private TenantStatus status;
    private final Instant createdAt;

    private Tenant(UUID id, String name, String taxId, TenantStatus status, Instant createdAt) {
        this.id = id;
        this.name = name;
        this.taxId = taxId;
        this.status = status;
        this.createdAt = createdAt;
    }

    public static Tenant create(String name, String taxId) {
        Objects.requireNonNull(name, "El nombre del tenant es obligatorio");
        return new Tenant(UUID.randomUUID(), name, taxId, TenantStatus.ACTIVE, Instant.now());
    }

    public static Tenant reconstitute(UUID id, String name, String taxId, TenantStatus status, Instant createdAt) {
        return new Tenant(id, name, taxId, status, createdAt);
    }

    public void suspend() {
        if (this.status == TenantStatus.SUSPENDED) {
            return;
        }
        this.status = TenantStatus.SUSPENDED;
    }

    public void reactivate() {
        this.status = TenantStatus.ACTIVE;
    }

    public void rename(String newName) {
        Objects.requireNonNull(newName, "El nombre no puede ser null");
        this.name = newName;
    }

    public UUID id() {
        return id;
    }

    public String name() {
        return name;
    }

    public String taxId() {
        return taxId;
    }

    public TenantStatus status() {
        return status;
    }

    public Instant createdAt() {
        return createdAt;
    }

    public enum TenantStatus {
        ACTIVE, SUSPENDED
    }
}
