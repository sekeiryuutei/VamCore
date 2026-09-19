package com.vamcore.inventory.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpringDataProductRepository extends JpaRepository<ProductJpaEntity, UUID> {

    Optional<ProductJpaEntity> findByIdAndTenantId(UUID id, UUID tenantId);

    Optional<ProductJpaEntity> findBySkuAndTenantId(String sku, UUID tenantId);

    boolean existsBySkuAndTenantId(String sku, UUID tenantId);

    List<ProductJpaEntity> findAllByTenantId(UUID tenantId);
}
