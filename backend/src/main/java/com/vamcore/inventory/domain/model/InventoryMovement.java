package com.vamcore.inventory.domain.model;

import com.vamcore.inventory.domain.valueobject.MovementType;
import com.vamcore.inventory.domain.valueobject.Quantity;

import java.time.Instant;
import java.util.UUID;

/**
 * Entrada inmutable del ledger de inventario (ver secciones 13-14 del
 * documento de arquitectura). El stock actual NUNCA se calcula ni se
 * confía como un simple campo `stock = 50`; siempre debe poder
 * reconstruirse a partir del historial de movimientos (Kardex).
 */
public final class InventoryMovement {

    private final UUID id;
    private final UUID tenantId;
    private final UUID productId;
    private final UUID warehouseId;
    private final MovementType movementType;
    private final Quantity quantity;
    private final String referenceType;
    private final String referenceId;
    private final UUID createdBy;
    private final Instant createdAt;

    private InventoryMovement(UUID id, UUID tenantId, UUID productId, UUID warehouseId, MovementType movementType,
                               Quantity quantity, String referenceType, String referenceId, UUID createdBy, Instant createdAt) {
        this.id = id;
        this.tenantId = tenantId;
        this.productId = productId;
        this.warehouseId = warehouseId;
        this.movementType = movementType;
        this.quantity = quantity;
        this.referenceType = referenceType;
        this.referenceId = referenceId;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
    }

    public static InventoryMovement record(UUID tenantId, UUID productId, UUID warehouseId, MovementType movementType,
                                            Quantity quantity, String referenceType, String referenceId, UUID createdBy) {
        return new InventoryMovement(UUID.randomUUID(), tenantId, productId, warehouseId, movementType,
            quantity, referenceType, referenceId, createdBy, Instant.now());
    }

    public static InventoryMovement reconstitute(UUID id, UUID tenantId, UUID productId, UUID warehouseId,
                                                  MovementType movementType, Quantity quantity, String referenceType,
                                                  String referenceId, UUID createdBy, Instant createdAt) {
        return new InventoryMovement(id, tenantId, productId, warehouseId, movementType, quantity,
            referenceType, referenceId, createdBy, createdAt);
    }

    public UUID id() { return id; }
    public UUID tenantId() { return tenantId; }
    public UUID productId() { return productId; }
    public UUID warehouseId() { return warehouseId; }
    public MovementType movementType() { return movementType; }
    public Quantity quantity() { return quantity; }
    public String referenceType() { return referenceType; }
    public String referenceId() { return referenceId; }
    public UUID createdBy() { return createdBy; }
    public Instant createdAt() { return createdAt; }
}
