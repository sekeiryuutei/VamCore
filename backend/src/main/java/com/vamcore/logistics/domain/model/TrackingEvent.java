package com.vamcore.logistics.domain.model;

import com.vamcore.logistics.domain.valueobject.DeliveryStatus;

import java.time.Instant;
import java.util.UUID;

/**
 * Evento de tracking inmutable (ver sección 21 del documento de
 * arquitectura). El estado actual de la Delivery NO reemplaza el
 * historial: el sistema debe poder reconstruir el recorrido lógico
 * completo de la entrega a partir de estos eventos.
 */
public final class TrackingEvent {

    private final UUID id;
    private final UUID tenantId;
    private final UUID deliveryId;
    private final DeliveryStatus status;
    private final String notes;
    private final Instant occurredAt;

    private TrackingEvent(UUID id, UUID tenantId, UUID deliveryId, DeliveryStatus status, String notes, Instant occurredAt) {
        this.id = id;
        this.tenantId = tenantId;
        this.deliveryId = deliveryId;
        this.status = status;
        this.notes = notes;
        this.occurredAt = occurredAt;
    }

    public static TrackingEvent record(UUID tenantId, UUID deliveryId, DeliveryStatus status, String notes) {
        return new TrackingEvent(UUID.randomUUID(), tenantId, deliveryId, status, notes, Instant.now());
    }

    public static TrackingEvent reconstitute(UUID id, UUID tenantId, UUID deliveryId, DeliveryStatus status,
                                              String notes, Instant occurredAt) {
        return new TrackingEvent(id, tenantId, deliveryId, status, notes, occurredAt);
    }

    public UUID id() { return id; }
    public UUID tenantId() { return tenantId; }
    public UUID deliveryId() { return deliveryId; }
    public DeliveryStatus status() { return status; }
    public String notes() { return notes; }
    public Instant occurredAt() { return occurredAt; }
}
