package com.vamcore.assets.domain.model;

import com.vamcore.assets.domain.valueobject.AssetCode;
import com.vamcore.assets.domain.valueobject.AssetStatus;
import com.vamcore.shared.domain.valueobject.Money;
import com.vamcore.shared.infrastructure.exception.BusinessException;
import org.springframework.http.HttpStatus;

import java.time.Instant;
import java.util.EnumSet;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static com.vamcore.assets.domain.valueobject.AssetStatus.*;

/**
 * Aggregate root que representa un bien individualizado de la empresa
 * durante su ciclo de vida (ver secciones 15-17 del documento de
 * arquitectura - Bounded Context Fixed Assets / VamAsset).
 *
 * Un Asset NO es lo mismo que un Product de Inventory: el Asset tiene
 * identidad propia (código, serial, asignación), mientras que Product
 * representa un artículo de catálogo controlado por cantidad.
 */
public class Asset {

    /**
     * Transiciones válidas del ciclo de vida (sección 16). Cualquier
     * transición no listada aquí se rechaza con INVALID_ASSET_TRANSITION,
     * incluyendo explícitamente DISPOSED -> * (un activo dado de baja no
     * puede reactivarse sin una operación de reversión explícita, que esta
     * versión inicial no implementa).
     */
    private static final Map<AssetStatus, EnumSet<AssetStatus>> VALID_TRANSITIONS = Map.of(
        ACQUIRED, EnumSet.of(IN_STORAGE),
        IN_STORAGE, EnumSet.of(ASSIGNED, RETIRED),
        ASSIGNED, EnumSet.of(IN_USE, IN_STORAGE, RETIRED),
        IN_USE, EnumSet.of(IN_MAINTENANCE, IN_STORAGE, RETIRED),
        IN_MAINTENANCE, EnumSet.of(IN_USE, RETIRED),
        RETIRED, EnumSet.of(DISPOSED),
        DISPOSED, EnumSet.noneOf(AssetStatus.class)
    );

    private final UUID id;
    private final UUID tenantId;
    private final AssetCode assetCode;
    private String name;
    private String category;
    private String serialNumber;
    private AssetStatus status;
    private UUID currentAssigneeId;
    private final Instant acquiredAt;
    private final Money acquisitionCost;
    private final Integer usefulLifeMonths;

    private Asset(UUID id, UUID tenantId, AssetCode assetCode, String name, String category, String serialNumber,
                  AssetStatus status, UUID currentAssigneeId, Instant acquiredAt, Money acquisitionCost, Integer usefulLifeMonths) {
        this.id = id;
        this.tenantId = tenantId;
        this.assetCode = assetCode;
        this.name = name;
        this.category = category;
        this.serialNumber = serialNumber;
        this.status = status;
        this.currentAssigneeId = currentAssigneeId;
        this.acquiredAt = acquiredAt;
        this.acquisitionCost = acquisitionCost;
        this.usefulLifeMonths = usefulLifeMonths;
    }

    public static Asset acquire(UUID tenantId, AssetCode assetCode, String name, String category, String serialNumber,
                                 Money acquisitionCost, Integer usefulLifeMonths) {
        Objects.requireNonNull(tenantId, "tenantId es obligatorio");
        Objects.requireNonNull(assetCode, "assetCode es obligatorio");
        Objects.requireNonNull(name, "name es obligatorio");
        return new Asset(UUID.randomUUID(), tenantId, assetCode, name, category, serialNumber, ACQUIRED, null,
            Instant.now(), acquisitionCost, usefulLifeMonths);
    }

    public static Asset reconstitute(UUID id, UUID tenantId, AssetCode assetCode, String name, String category,
                                      String serialNumber, AssetStatus status, UUID currentAssigneeId, Instant acquiredAt,
                                      Money acquisitionCost, Integer usefulLifeMonths) {
        return new Asset(id, tenantId, assetCode, name, category, serialNumber, status, currentAssigneeId,
            acquiredAt, acquisitionCost, usefulLifeMonths);
    }

    /**
     * Cambia el estado del activo, validando que la transición esté permitida.
     * Ver VALID_TRANSITIONS. No usar para asignación (ver {@link #assignTo}),
     * que además de cambiar a ASSIGNED registra el assignee.
     */
    public void changeStatus(AssetStatus newStatus) {
        assertTransitionAllowed(newStatus);
        if (newStatus != ASSIGNED) {
            this.currentAssigneeId = newStatus == IN_STORAGE || newStatus == RETIRED || newStatus == DISPOSED
                ? null
                : this.currentAssigneeId;
        }
        this.status = newStatus;
    }

    public void assignTo(UUID assigneeId) {
        Objects.requireNonNull(assigneeId, "assigneeId es obligatorio");
        assertTransitionAllowed(ASSIGNED);
        this.status = ASSIGNED;
        this.currentAssigneeId = assigneeId;
    }

    private void assertTransitionAllowed(AssetStatus newStatus) {
        EnumSet<AssetStatus> allowed = VALID_TRANSITIONS.getOrDefault(this.status, EnumSet.noneOf(AssetStatus.class));
        if (!allowed.contains(newStatus)) {
            throw new BusinessException(
                "INVALID_ASSET_TRANSITION",
                "No se puede pasar el activo de " + this.status + " a " + newStatus,
                HttpStatus.CONFLICT
            );
        }
    }

    public void rename(String newName) {
        Objects.requireNonNull(newName, "El nombre no puede ser null");
        this.name = newName;
    }

    public UUID id() { return id; }
    public UUID tenantId() { return tenantId; }
    public AssetCode assetCode() { return assetCode; }
    public String name() { return name; }
    public String category() { return category; }
    public String serialNumber() { return serialNumber; }
    public AssetStatus status() { return status; }
    public UUID currentAssigneeId() { return currentAssigneeId; }
    public Instant acquiredAt() { return acquiredAt; }
    public Money acquisitionCost() { return acquisitionCost; }
    public Integer usefulLifeMonths() { return usefulLifeMonths; }

    /**
     * Depreciación lineal (straight-line): valor en libros = costo -
     * (costo / vida útil en meses) * meses transcurridos, nunca por debajo
     * de cero. Devuelve null si no se registró costo o vida útil (no todos
     * los activos requieren depreciación contable, ej. activos de bajo valor).
     */
    public Money currentBookValue(Instant asOf) {
        if (acquisitionCost == null || usefulLifeMonths == null || usefulLifeMonths <= 0) {
            return null;
        }
        long monthsElapsed = java.time.temporal.ChronoUnit.MONTHS.between(
            java.time.YearMonth.from(acquiredAt.atZone(java.time.ZoneOffset.UTC)),
            java.time.YearMonth.from(asOf.atZone(java.time.ZoneOffset.UTC))
        );
        monthsElapsed = Math.max(0, Math.min(monthsElapsed, usefulLifeMonths));

        java.math.BigDecimal monthlyDepreciation = acquisitionCost.amount()
            .divide(java.math.BigDecimal.valueOf(usefulLifeMonths), 4, java.math.RoundingMode.HALF_UP);
        java.math.BigDecimal accumulated = monthlyDepreciation.multiply(java.math.BigDecimal.valueOf(monthsElapsed));
        java.math.BigDecimal bookValue = acquisitionCost.amount().subtract(accumulated);
        if (bookValue.signum() < 0) {
            bookValue = java.math.BigDecimal.ZERO;
        }
        return Money.of(bookValue, acquisitionCost.currency().getCurrencyCode());
    }
}
