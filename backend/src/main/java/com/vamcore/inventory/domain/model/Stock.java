package com.vamcore.inventory.domain.model;

import com.vamcore.inventory.domain.valueobject.Quantity;
import com.vamcore.shared.infrastructure.exception.BusinessException;
import org.springframework.http.HttpStatus;

import java.util.Objects;
import java.util.UUID;

/**
 * Aggregate root que representa la cantidad actual de un producto en una
 * bodega específica. Es una PROYECCIÓN del ledger de {@link InventoryMovement}
 * (ver sección 13), mantenida como saldo para consultas rápidas, pero el
 * historial de movimientos sigue siendo la fuente de verdad para auditoría.
 *
 * Regla crítica (sección 42 - Concurrencia): el stock NUNCA debe quedar
 * negativo. La persistencia (infrastructure.persistence) debe usar
 * optimistic locking (columna `version`) para evitar condiciones de carrera
 * entre dos operaciones concurrentes sobre el mismo Stock.
 */
public class Stock {

    private final UUID id;
    private final UUID tenantId;
    private final UUID productId;
    private final UUID warehouseId;
    private Quantity quantity;
    private long version;

    private Stock(UUID id, UUID tenantId, UUID productId, UUID warehouseId, Quantity quantity, long version) {
        this.id = id;
        this.tenantId = tenantId;
        this.productId = productId;
        this.warehouseId = warehouseId;
        this.quantity = quantity;
        this.version = version;
    }

    public static Stock initialize(UUID tenantId, UUID productId, UUID warehouseId, String unitOfMeasure) {
        Objects.requireNonNull(tenantId, "tenantId es obligatorio");
        Objects.requireNonNull(productId, "productId es obligatorio");
        Objects.requireNonNull(warehouseId, "warehouseId es obligatorio");
        return new Stock(UUID.randomUUID(), tenantId, productId, warehouseId, Quantity.zero(unitOfMeasure), 0L);
    }

    public static Stock reconstitute(UUID id, UUID tenantId, UUID productId, UUID warehouseId,
                                      Quantity quantity, long version) {
        return new Stock(id, tenantId, productId, warehouseId, quantity, version);
    }

    /** RECEIPT / TRANSFER_IN / RETURN: incrementa el stock. */
    public void increase(Quantity qty) {
        this.quantity = this.quantity.add(qty);
    }

    /** SALE / ISSUE / TRANSFER_OUT / DAMAGE: decrementa el stock, validando disponibilidad. */
    public void decrease(Quantity qty) {
        Quantity resulting = this.quantity.subtract(qty);
        assertNotNegative(resulting);
        this.quantity = resulting;
    }

    /** ADJUSTMENT: aplica un delta que puede ser positivo o negativo (ya viene con signo). */
    public void adjust(Quantity signedDelta) {
        Quantity resulting = this.quantity.add(signedDelta);
        assertNotNegative(resulting);
        this.quantity = resulting;
    }

    private void assertNotNegative(Quantity resulting) {
        if (resulting.isNegative()) {
            throw new BusinessException(
                "INSUFFICIENT_STOCK",
                "La operación dejaría el stock en negativo para el producto " + productId + " en la bodega " + warehouseId,
                HttpStatus.CONFLICT
            );
        }
    }

    public UUID id() { return id; }
    public UUID tenantId() { return tenantId; }
    public UUID productId() { return productId; }
    public UUID warehouseId() { return warehouseId; }
    public Quantity quantity() { return quantity; }
    public long version() { return version; }
}
