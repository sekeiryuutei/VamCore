package com.vamcore.organization.domain.repository;

import com.vamcore.organization.domain.model.Tenant;

import java.util.Optional;
import java.util.UUID;

/**
 * Puerto de salida (repository port) para Tenant.
 * La implementación concreta vive en infrastructure.persistence usando JPA.
 */
public interface TenantRepository {

    Tenant save(Tenant tenant);

    Optional<Tenant> findById(UUID id);

    boolean existsByTaxId(String taxId);
}
