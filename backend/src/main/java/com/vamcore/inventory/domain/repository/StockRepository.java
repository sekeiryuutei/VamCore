package com.vamcore.inventory.domain.repository;

import com.vamcore.inventory.domain.model.Stock;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StockRepository {

    /**
     * Guarda el stock. La implementación debe usar optimistic locking
     * (columna `version`) y lanzar una excepción de conflicto si otra
     * transacción concurrente ya modificó el mismo registro
     * (ver sección 42 - Concurrencia).
     */
    Stock save(Stock stock);

    Optional<Stock> findByProductAndWarehouse(UUID tenantId, UUID productId, UUID warehouseId);

    List<Stock> findAllByProduct(UUID tenantId, UUID productId);
}
