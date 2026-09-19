package com.vamcore.shared.domain.valueobject;

import java.util.Objects;
import java.util.UUID;

/**
 * Value Object que representa el identificador de un Tenant (empresa cliente).
 *
 * Regla de oro (ver ADR-005 - Multi-tenancy):
 * NUNCA se debe determinar el tenant a partir de un parámetro de request
 * (ej. GET /products?tenantId=123). El TenantId siempre se deriva del
 * contexto autenticado (ver shared.infrastructure.tenant.TenantContext).
 */
public final class TenantId {

    private final UUID value;

    private TenantId(UUID value) {
        this.value = Objects.requireNonNull(value, "tenantId no puede ser null");
    }

    public static TenantId of(UUID value) {
        return new TenantId(value);
    }

    public static TenantId of(String value) {
        return new TenantId(UUID.fromString(value));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TenantId tenantId)) return false;
        return value.equals(tenantId.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
