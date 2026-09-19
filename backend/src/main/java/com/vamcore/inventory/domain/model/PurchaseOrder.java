package com.vamcore.inventory.domain.model;

import com.vamcore.shared.infrastructure.exception.BusinessException;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Orden de compra a un proveedor (ver sección 53 del documento de
 * arquitectura: Proveedor -> Purchase Order -> Receiving -> Inventory
 * Movement -> Stock). Simplificada a una sola línea de producto por orden
 * para el MVP — una orden multi-línea es una extensión natural de este
 * mismo aggregate cuando el negocio lo requiera.
 */
public class PurchaseOrder {

    public enum Status { DRAFT, CONFIRMED, RECEIVED, CANCELLED }

    private final UUID id;
    private final UUID tenantId;
    private final String code;
    private final String supplierName;
    private final UUID productId;
    private final UUID warehouseId;
    private final BigDecimal quantityOrdered;
    private Status status;
    private final Instant createdAt;

    private PurchaseOrder(UUID id, UUID tenantId, String code, String supplierName, UUID productId, UUID warehouseId,
                           BigDecimal quantityOrdered, Status status, Instant createdAt) {
        this.id = id;
        this.tenantId = tenantId;
        this.code = code;
        this.supplierName = supplierName;
        this.productId = productId;
        this.warehouseId = warehouseId;
        this.quantityOrdered = quantityOrdered;
        this.status = status;
        this.createdAt = createdAt;
    }

    public static PurchaseOrder create(UUID tenantId, String code, String supplierName, UUID productId,
                                        UUID warehouseId, BigDecimal quantityOrdered) {
        Objects.requireNonNull(tenantId, "tenantId es obligatorio");
        Objects.requireNonNull(code, "code es obligatorio");
        Objects.requireNonNull(productId, "productId es obligatorio");
        Objects.requireNonNull(warehouseId, "warehouseId es obligatorio");
        return new PurchaseOrder(UUID.randomUUID(), tenantId, code, supplierName, productId, warehouseId,
            quantityOrdered, Status.DRAFT, Instant.now());
    }

    public static PurchaseOrder reconstitute(UUID id, UUID tenantId, String code, String supplierName, UUID productId,
                                              UUID warehouseId, BigDecimal quantityOrdered, Status status, Instant createdAt) {
        return new PurchaseOrder(id, tenantId, code, supplierName, productId, warehouseId, quantityOrdered, status, createdAt);
    }

    public void confirm() {
        assertStatus(Status.DRAFT, "confirmar");
        this.status = Status.CONFIRMED;
    }

    public void markReceived() {
        assertStatus(Status.CONFIRMED, "recibir");
        this.status = Status.RECEIVED;
    }

    public void cancel() {
        if (this.status == Status.RECEIVED) {
            throw new BusinessException("INVALID_PURCHASE_ORDER_TRANSITION",
                "No se puede cancelar una orden ya recibida", HttpStatus.CONFLICT);
        }
        this.status = Status.CANCELLED;
    }

    private void assertStatus(Status required, String action) {
        if (this.status != required) {
            throw new BusinessException(
                "INVALID_PURCHASE_ORDER_TRANSITION",
                "No se puede " + action + " una orden en estado " + this.status,
                HttpStatus.CONFLICT
            );
        }
    }

    public UUID id() { return id; }
    public UUID tenantId() { return tenantId; }
    public String code() { return code; }
    public String supplierName() { return supplierName; }
    public UUID productId() { return productId; }
    public UUID warehouseId() { return warehouseId; }
    public BigDecimal quantityOrdered() { return quantityOrdered; }
    public Status status() { return status; }
    public Instant createdAt() { return createdAt; }
}
