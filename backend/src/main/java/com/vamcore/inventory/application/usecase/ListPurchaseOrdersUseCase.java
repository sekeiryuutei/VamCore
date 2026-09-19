package com.vamcore.inventory.application.usecase;

import com.vamcore.inventory.domain.model.PurchaseOrder;
import com.vamcore.inventory.domain.repository.PurchaseOrderRepository;
import com.vamcore.shared.domain.valueobject.TenantId;
import com.vamcore.shared.infrastructure.tenant.TenantContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ListPurchaseOrdersUseCase {

    private final PurchaseOrderRepository purchaseOrderRepository;

    public ListPurchaseOrdersUseCase(PurchaseOrderRepository purchaseOrderRepository) {
        this.purchaseOrderRepository = purchaseOrderRepository;
    }

    @Transactional(readOnly = true)
    public List<PurchaseOrder> handle() {
        TenantId tenantId = TenantContext.get();
        return purchaseOrderRepository.findAllByTenant(tenantId.value());
    }
}
