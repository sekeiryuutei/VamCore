package com.vamcore.inventory.infrastructure.persistence;

import com.vamcore.inventory.domain.model.Stock;
import com.vamcore.inventory.domain.repository.StockRepository;
import com.vamcore.inventory.domain.valueobject.Quantity;
import com.vamcore.shared.infrastructure.exception.BusinessException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Adaptador de Stock. Traduce OptimisticLockingFailureException (lanzada
 * por Hibernate cuando dos transacciones concurrentes modifican el mismo
 * registro) a un error de negocio legible (ver sección 42 - Concurrencia).
 */
@Repository
public class StockRepositoryAdapter implements StockRepository {

    private final SpringDataStockRepository jpaRepository;

    public StockRepositoryAdapter(SpringDataStockRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Stock save(Stock stock) {
        StockJpaEntity entity = new StockJpaEntity(
            stock.id(), stock.tenantId(), stock.productId(), stock.warehouseId(),
            stock.quantity().value(), stock.quantity().unit(), stock.version()
        );
        try {
            StockJpaEntity saved = jpaRepository.save(entity);
            return Stock.reconstitute(
                saved.getId(), saved.getTenantId(), saved.getProductId(), saved.getWarehouseId(),
                Quantity.of(saved.getQuantity(), saved.getUnit()), saved.getVersion()
            );
        } catch (OptimisticLockingFailureException ex) {
            throw new BusinessException(
                "STOCK_CONCURRENT_MODIFICATION",
                "El stock fue modificado por otra operación al mismo tiempo. Vuelve a intentar.",
                HttpStatus.CONFLICT
            );
        }
    }

    @Override
    public Optional<Stock> findByProductAndWarehouse(UUID tenantId, UUID productId, UUID warehouseId) {
        return jpaRepository.findByTenantIdAndProductIdAndWarehouseId(tenantId, productId, warehouseId)
            .map(this::toDomain);
    }

    @Override
    public List<Stock> findAllByProduct(UUID tenantId, UUID productId) {
        return jpaRepository.findAllByTenantIdAndProductId(tenantId, productId).stream()
            .map(this::toDomain)
            .toList();
    }

    private Stock toDomain(StockJpaEntity entity) {
        return Stock.reconstitute(
            entity.getId(), entity.getTenantId(), entity.getProductId(), entity.getWarehouseId(),
            Quantity.of(entity.getQuantity(), entity.getUnit()), entity.getVersion()
        );
    }
}
