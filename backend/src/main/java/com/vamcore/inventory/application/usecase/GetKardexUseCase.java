package com.vamcore.inventory.application.usecase;

import com.vamcore.inventory.application.query.GetKardexQuery;
import com.vamcore.inventory.domain.model.InventoryMovement;
import com.vamcore.inventory.domain.repository.InventoryMovementRepository;
import com.vamcore.shared.domain.valueobject.TenantId;
import com.vamcore.shared.infrastructure.tenant.TenantContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Consulta el historial de movimientos de un producto (Kardex, ver
 * secciones 13-14 y 25 del documento de arquitectura). Las queries no
 * modifican estado (transacción de solo lectura).
 */
@Service
public class GetKardexUseCase {

    private final InventoryMovementRepository movementRepository;

    public GetKardexUseCase(InventoryMovementRepository movementRepository) {
        this.movementRepository = movementRepository;
    }

    @Transactional(readOnly = true)
    public List<InventoryMovement> handle(GetKardexQuery query) {
        TenantId tenantId = TenantContext.get();
        if (query.warehouseId() != null) {
            return movementRepository.findByProductAndWarehouse(tenantId.value(), query.productId(), query.warehouseId());
        }
        return movementRepository.findByProduct(tenantId.value(), query.productId());
    }
}
