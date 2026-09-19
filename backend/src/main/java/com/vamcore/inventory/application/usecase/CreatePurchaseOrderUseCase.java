package com.vamcore.inventory.application.usecase;

import com.vamcore.inventory.application.command.CreatePurchaseOrderCommand;
import com.vamcore.inventory.domain.model.PurchaseOrder;
import com.vamcore.inventory.domain.repository.PurchaseOrderRepository;
import com.vamcore.shared.domain.valueobject.TenantId;
import com.vamcore.shared.infrastructure.tenant.TenantContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreatePurchaseOrderUseCase {

    private final PurchaseOrderRepository purchaseOrderRepository;

    public CreatePurchaseOrderUseCase(PurchaseOrderRepository purchaseOrderRepository) {
        this.purchaseOrderRepository = purchaseOrderRepository;
    }

    @Transactional
    public PurchaseOrder handle(CreatePurchaseOrderCommand command) {
        TenantId tenantId = TenantContext.get();
        PurchaseOrder po = PurchaseOrder.create(
            tenantId.value(), command.code(), command.supplierName(), command.productId(),
            command.warehouseId(), command.quantityOrdered()
        );
        return purchaseOrderRepository.save(po);
    }
}
