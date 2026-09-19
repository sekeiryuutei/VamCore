package com.vamcore.inventory.infrastructure.persistence;

import com.vamcore.inventory.domain.model.PurchaseOrder;
import com.vamcore.inventory.domain.repository.PurchaseOrderRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class PurchaseOrderRepositoryAdapter implements PurchaseOrderRepository {

    private final SpringDataPurchaseOrderRepository jpaRepository;

    public PurchaseOrderRepositoryAdapter(SpringDataPurchaseOrderRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public PurchaseOrder save(PurchaseOrder purchaseOrder) {
        jpaRepository.save(new PurchaseOrderJpaEntity(
            purchaseOrder.id(), purchaseOrder.tenantId(), purchaseOrder.code(), purchaseOrder.supplierName(),
            purchaseOrder.productId(), purchaseOrder.warehouseId(), purchaseOrder.quantityOrdered(),
            purchaseOrder.status().name(), purchaseOrder.createdAt()
        ));
        return purchaseOrder;
    }

    @Override
    public Optional<PurchaseOrder> findById(UUID tenantId, UUID id) {
        return jpaRepository.findByIdAndTenantId(id, tenantId).map(this::toDomain);
    }

    @Override
    public List<PurchaseOrder> findAllByTenant(UUID tenantId) {
        return jpaRepository.findAllByTenantId(tenantId).stream().map(this::toDomain).toList();
    }

    private PurchaseOrder toDomain(PurchaseOrderJpaEntity e) {
        return PurchaseOrder.reconstitute(
            e.getId(), e.getTenantId(), e.getCode(), e.getSupplierName(), e.getProductId(), e.getWarehouseId(),
            e.getQuantityOrdered(), PurchaseOrder.Status.valueOf(e.getStatus()), e.getCreatedAt()
        );
    }
}
