package com.vamcore.inventory.infrastructure.persistence;

import com.vamcore.inventory.domain.model.InventoryMovement;
import com.vamcore.inventory.domain.repository.InventoryMovementRepository;
import com.vamcore.inventory.domain.valueobject.MovementType;
import com.vamcore.inventory.domain.valueobject.Quantity;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public class InventoryMovementRepositoryAdapter implements InventoryMovementRepository {

    private final SpringDataInventoryMovementRepository jpaRepository;

    public InventoryMovementRepositoryAdapter(SpringDataInventoryMovementRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public InventoryMovement save(InventoryMovement movement) {
        InventoryMovementJpaEntity entity = new InventoryMovementJpaEntity(
            movement.id(), movement.tenantId(), movement.productId(), movement.warehouseId(),
            movement.movementType().name(), movement.quantity().value(), movement.quantity().unit(),
            movement.referenceType(), movement.referenceId(), movement.createdBy(), movement.createdAt()
        );
        jpaRepository.save(entity);
        return movement;
    }

    @Override
    public List<InventoryMovement> findByProduct(UUID tenantId, UUID productId) {
        return jpaRepository.findAllByTenantIdAndProductIdOrderByCreatedAtDesc(tenantId, productId).stream()
            .map(this::toDomain)
            .toList();
    }

    @Override
    public List<InventoryMovement> findByProductAndWarehouse(UUID tenantId, UUID productId, UUID warehouseId) {
        return jpaRepository.findAllByTenantIdAndProductIdAndWarehouseIdOrderByCreatedAtDesc(tenantId, productId, warehouseId).stream()
            .map(this::toDomain)
            .toList();
    }

    private InventoryMovement toDomain(InventoryMovementJpaEntity entity) {
        return InventoryMovement.reconstitute(
            entity.getId(), entity.getTenantId(), entity.getProductId(), entity.getWarehouseId(),
            MovementType.valueOf(entity.getMovementType()), Quantity.of(entity.getQuantity(), entity.getUnit()),
            entity.getReferenceType(), entity.getReferenceId(), entity.getCreatedBy(), entity.getCreatedAt()
        );
    }
}
