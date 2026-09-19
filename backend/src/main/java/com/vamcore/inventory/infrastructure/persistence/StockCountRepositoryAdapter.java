package com.vamcore.inventory.infrastructure.persistence;

import com.vamcore.inventory.domain.model.StockCount;
import com.vamcore.inventory.domain.repository.StockCountRepository;
import com.vamcore.inventory.domain.valueobject.Quantity;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class StockCountRepositoryAdapter implements StockCountRepository {

    private final SpringDataStockCountRepository jpaRepository;

    public StockCountRepositoryAdapter(SpringDataStockCountRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public StockCount save(StockCount stockCount) {
        jpaRepository.save(new StockCountJpaEntity(
            stockCount.id(), stockCount.tenantId(), stockCount.productId(), stockCount.warehouseId(),
            stockCount.systemQuantityAtCount().value(), stockCount.countedQuantity().value(),
            stockCount.countedQuantity().unit(), stockCount.status().name(), stockCount.createdAt()
        ));
        return stockCount;
    }

    @Override
    public Optional<StockCount> findById(UUID tenantId, UUID id) {
        return jpaRepository.findByIdAndTenantId(id, tenantId).map(this::toDomain);
    }

    @Override
    public List<StockCount> findAllByTenant(UUID tenantId) {
        return jpaRepository.findAllByTenantId(tenantId).stream().map(this::toDomain).toList();
    }

    private StockCount toDomain(StockCountJpaEntity e) {
        return StockCount.reconstitute(
            e.getId(), e.getTenantId(), e.getProductId(), e.getWarehouseId(),
            Quantity.of(e.getSystemQuantityAtCount(), e.getUnit()), Quantity.of(e.getCountedQuantity(), e.getUnit()),
            StockCount.Status.valueOf(e.getStatus()), e.getCreatedAt()
        );
    }
}
