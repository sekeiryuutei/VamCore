package com.vamcore.inventory.domain.repository;

import com.vamcore.inventory.domain.model.PurchaseOrder;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PurchaseOrderRepository {

    PurchaseOrder save(PurchaseOrder purchaseOrder);

    Optional<PurchaseOrder> findById(UUID tenantId, UUID id);

    List<PurchaseOrder> findAllByTenant(UUID tenantId);
}
