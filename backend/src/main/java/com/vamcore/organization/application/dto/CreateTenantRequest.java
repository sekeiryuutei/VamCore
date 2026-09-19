package com.vamcore.organization.application.dto;

import jakarta.validation.constraints.NotBlank;

/** Request DTO para POST /api/v1/tenants. */
public record CreateTenantRequest(
    @NotBlank(message = "El nombre es obligatorio") String name,
    String taxId
) {
}
