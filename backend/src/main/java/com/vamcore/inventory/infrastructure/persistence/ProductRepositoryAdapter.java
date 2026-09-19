package com.vamcore.inventory.infrastructure.persistence;

import com.vamcore.inventory.domain.model.Product;
import com.vamcore.inventory.domain.repository.ProductRepository;
import com.vamcore.inventory.domain.valueobject.SKU;
import com.vamcore.inventory.domain.valueobject.TrackingType;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class ProductRepositoryAdapter implements ProductRepository {

    private final SpringDataProductRepository jpaRepository;

    public ProductRepositoryAdapter(SpringDataProductRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Product save(Product product) {
        ProductJpaEntity entity = new ProductJpaEntity(
            product.id(), product.tenantId(), product.sku().value(), product.name(), product.description(),
            product.category(), product.unitOfMeasure(), product.trackingType().name(), product.status().name(), product.createdAt()
        );
        jpaRepository.save(entity);
        return product;
    }

    @Override
    public Optional<Product> findById(UUID tenantId, UUID id) {
        return jpaRepository.findByIdAndTenantId(id, tenantId).map(this::toDomain);
    }

    @Override
    public Optional<Product> findBySku(UUID tenantId, SKU sku) {
        return jpaRepository.findBySkuAndTenantId(sku.value(), tenantId).map(this::toDomain);
    }

    @Override
    public boolean existsBySku(UUID tenantId, SKU sku) {
        return jpaRepository.existsBySkuAndTenantId(sku.value(), tenantId);
    }

    @Override
    public List<Product> findAllByTenant(UUID tenantId) {
        return jpaRepository.findAllByTenantId(tenantId).stream()
            .map(this::toDomain)
            .toList();
    }

    private Product toDomain(ProductJpaEntity entity) {
        return Product.reconstitute(
            entity.getId(), entity.getTenantId(), SKU.of(entity.getSku()), entity.getName(), entity.getDescription(),
            entity.getCategory(), entity.getUnitOfMeasure(), TrackingType.valueOf(entity.getTrackingType()),
            Product.ProductStatus.valueOf(entity.getStatus()), entity.getCreatedAt()
        );
    }
}
