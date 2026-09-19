package com.vamcore.inventory.domain.model;

import com.vamcore.inventory.domain.valueobject.Quantity;
import com.vamcore.shared.infrastructure.exception.BusinessException;
import org.springframework.http.HttpStatus;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Conteo físico de inventario (ver sección 56 del documento de
 * arquitectura - MVP de Inventario: "Inventario físico"). Registra la
 * cantidad contada contra la cantidad del sistema en el momento del
 * conteo; la diferencia (varianza) se aplica como un ADJUSTMENT solo
 * cuando se confirma explícitamente (ver ApplyStockCountUseCase), nunca
 * automáticamente al crear el conteo.
 */
public class StockCount {

    public enum Status { DRAFT, APPLIED }

    private final UUID id;
    private final UUID tenantId;
    private final UUID productId;
    private final UUID warehouseId;
    private final Quantity systemQuantityAtCount;
    private final Quantity countedQuantity;
    private Status status;
    private final Instant createdAt;

    private StockCount(UUID id, UUID tenantId, UUID productId, UUID warehouseId, Quantity systemQuantityAtCount,
                        Quantity countedQuantity, Status status, Instant createdAt) {
        this.id = id;
        this.tenantId = tenantId;
        this.productId = productId;
        this.warehouseId = warehouseId;
        this.systemQuantityAtCount = systemQuantityAtCount;
        this.countedQuantity = countedQuantity;
        this.status = status;
        this.createdAt = createdAt;
    }

    public static StockCount create(UUID tenantId, UUID productId, UUID warehouseId,
                                     Quantity systemQuantityAtCount, Quantity countedQuantity) {
        Objects.requireNonNull(tenantId, "tenantId es obligatorio");
        return new StockCount(UUID.randomUUID(), tenantId, productId, warehouseId,
            systemQuantityAtCount, countedQuantity, Status.DRAFT, Instant.now());
    }

    public static StockCount reconstitute(UUID id, UUID tenantId, UUID productId, UUID warehouseId,
                                           Quantity systemQuantityAtCount, Quantity countedQuantity,
                                           Status status, Instant createdAt) {
        return new StockCount(id, tenantId, productId, warehouseId, systemQuantityAtCount, countedQuantity, status, createdAt);
    }

    /** Positivo = sobrante, negativo = faltante. */
    public Quantity variance() {
        return countedQuantity.subtract(systemQuantityAtCount);
    }

    public void markApplied() {
        if (this.status == Status.APPLIED) {
            throw new BusinessException("STOCK_COUNT_ALREADY_APPLIED",
                "Este conteo ya fue aplicado anteriormente", HttpStatus.CONFLICT);
        }
        this.status = Status.APPLIED;
    }

    public UUID id() { return id; }
    public UUID tenantId() { return tenantId; }
    public UUID productId() { return productId; }
    public UUID warehouseId() { return warehouseId; }
    public Quantity systemQuantityAtCount() { return systemQuantityAtCount; }
    public Quantity countedQuantity() { return countedQuantity; }
    public Status status() { return status; }
    public Instant createdAt() { return createdAt; }
}
