package com.vamcore.inventory.domain.repository;

import com.vamcore.inventory.domain.model.InventoryMovement;

import java.util.List;
import java.util.UUID;

public interface InventoryMovementRepository {

    InventoryMovement save(InventoryMovement movement);

    /** Kardex: historial completo de movimientos de un producto, más reciente primero. */
    List<InventoryMovement> findByProduct(UUID tenantId, UUID productId);

    List<InventoryMovement> findByProductAndWarehouse(UUID tenantId, UUID productId, UUID warehouseId);
}
