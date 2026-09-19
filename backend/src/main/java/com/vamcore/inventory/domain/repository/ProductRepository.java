package com.vamcore.inventory.domain.repository;

import com.vamcore.inventory.domain.model.Product;
import com.vamcore.inventory.domain.valueobject.SKU;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepository {

    Product save(Product product);

    Optional<Product> findById(UUID tenantId, UUID id);

    Optional<Product> findBySku(UUID tenantId, SKU sku);

    boolean existsBySku(UUID tenantId, SKU sku);

    List<Product> findAllByTenant(UUID tenantId);
}
