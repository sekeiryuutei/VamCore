package com.vamcore.organization.application.dto;

import com.vamcore.organization.domain.model.Tenant;

import java.time.Instant;
import java.util.UUID;

/** Response DTO — las entidades internas nunca se exponen directamente (sección 40). */
public record TenantResponse(UUID id, String name, String taxId, String status, Instant createdAt) {

    public static TenantResponse from(Tenant tenant) {
        return new TenantResponse(tenant.id(), tenant.name(), tenant.taxId(), tenant.status().name(), tenant.createdAt());
    }
}
