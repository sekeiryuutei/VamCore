package com.vamcore.logistics.domain.model;

import com.vamcore.logistics.domain.valueobject.DeliveryCode;
import com.vamcore.logistics.domain.valueobject.DeliveryStatus;
import com.vamcore.shared.infrastructure.exception.BusinessException;
import org.springframework.http.HttpStatus;

import java.time.Instant;
import java.util.EnumSet;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static com.vamcore.logistics.domain.valueobject.DeliveryStatus.*;

/**
 * Aggregate root que representa una entrega de VamTrack, con su máquina
 * de estados (ver secciones 19-20 del documento de arquitectura).
 */
public class Delivery {

    private static final Map<DeliveryStatus, EnumSet<DeliveryStatus>> VALID_TRANSITIONS = Map.of(
        CREATED, EnumSet.of(CONFIRMED, CANCELLED),
        CONFIRMED, EnumSet.of(PREPARING, CANCELLED),
        PREPARING, EnumSet.of(READY, CANCELLED),
        READY, EnumSet.of(ASSIGNED),
        ASSIGNED, EnumSet.of(DISPATCHED),
        DISPATCHED, EnumSet.of(IN_TRANSIT),
        IN_TRANSIT, EnumSet.of(DELIVERED, FAILED),
        FAILED, EnumSet.of(RETURNED),
        DELIVERED, EnumSet.noneOf(DeliveryStatus.class),
        RETURNED, EnumSet.noneOf(DeliveryStatus.class),
        CANCELLED, EnumSet.noneOf(DeliveryStatus.class)
    );

    private final UUID id;
    private final UUID tenantId;
    private final DeliveryCode deliveryCode;
    private String customerName;
    private String destinationAddress;
    private DeliveryStatus status;
    private UUID driverId;
    private UUID vehicleId;
    private final Instant createdAt;

    private Delivery(UUID id, UUID tenantId, DeliveryCode deliveryCode, String customerName, String destinationAddress,
                      DeliveryStatus status, UUID driverId, UUID vehicleId, Instant createdAt) {
        this.id = id;
        this.tenantId = tenantId;
        this.deliveryCode = deliveryCode;
        this.customerName = customerName;
        this.destinationAddress = destinationAddress;
        this.status = status;
        this.driverId = driverId;
        this.vehicleId = vehicleId;
        this.createdAt = createdAt;
    }

    public static Delivery create(UUID tenantId, DeliveryCode deliveryCode, String customerName, String destinationAddress) {
        Objects.requireNonNull(tenantId, "tenantId es obligatorio");
        Objects.requireNonNull(deliveryCode, "deliveryCode es obligatorio");
        Objects.requireNonNull(customerName, "customerName es obligatorio");
        return new Delivery(UUID.randomUUID(), tenantId, deliveryCode, customerName, destinationAddress,
            CREATED, null, null, Instant.now());
    }

    public static Delivery reconstitute(UUID id, UUID tenantId, DeliveryCode deliveryCode, String customerName,
                                         String destinationAddress, DeliveryStatus status, UUID driverId,
                                         UUID vehicleId, Instant createdAt) {
        return new Delivery(id, tenantId, deliveryCode, customerName, destinationAddress, status, driverId, vehicleId, createdAt);
    }

    public void changeStatus(DeliveryStatus newStatus) {
        assertTransitionAllowed(newStatus);
        this.status = newStatus;
    }

    /** Asigna conductor/vehículo y avanza READY -> ASSIGNED en una sola operación. */
    public void assignTo(UUID driverId, UUID vehicleId) {
        Objects.requireNonNull(driverId, "driverId es obligatorio");
        assertTransitionAllowed(ASSIGNED);
        this.driverId = driverId;
        this.vehicleId = vehicleId;
        this.status = ASSIGNED;
    }

    private void assertTransitionAllowed(DeliveryStatus newStatus) {
        EnumSet<DeliveryStatus> allowed = VALID_TRANSITIONS.getOrDefault(this.status, EnumSet.noneOf(DeliveryStatus.class));
        if (!allowed.contains(newStatus)) {
            throw new BusinessException(
                "INVALID_DELIVERY_TRANSITION",
                "No se puede pasar la entrega de " + this.status + " a " + newStatus,
                HttpStatus.CONFLICT
            );
        }
    }

    public UUID id() { return id; }
    public UUID tenantId() { return tenantId; }
    public DeliveryCode deliveryCode() { return deliveryCode; }
    public String customerName() { return customerName; }
    public String destinationAddress() { return destinationAddress; }
    public DeliveryStatus status() { return status; }
    public UUID driverId() { return driverId; }
    public UUID vehicleId() { return vehicleId; }
    public Instant createdAt() { return createdAt; }
}
