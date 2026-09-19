package com.vamcore.logistics.domain.event;

import com.vamcore.logistics.domain.valueobject.DeliveryStatus;
import com.vamcore.shared.domain.event.AbstractDomainEvent;

import java.util.UUID;

/**
 * Se publica en cada transición de estado. Cuando newStatus es DELIVERED,
 * otros módulos pueden reaccionar (Audit, Notification, Reporting) sin que
 * Delivery conozca su implementación interna (ver sección 23 del doc de
 * arquitectura).
 */
public class DeliveryStatusChanged extends AbstractDomainEvent {

    private final UUID deliveryId;
    private final DeliveryStatus previousStatus;
    private final DeliveryStatus newStatus;

    public DeliveryStatusChanged(UUID tenantId, UUID deliveryId, DeliveryStatus previousStatus, DeliveryStatus newStatus) {
        super(tenantId);
        this.deliveryId = deliveryId;
        this.previousStatus = previousStatus;
        this.newStatus = newStatus;
    }

    public UUID deliveryId() { return deliveryId; }
    public DeliveryStatus previousStatus() { return previousStatus; }
    public DeliveryStatus newStatus() { return newStatus; }

    @Override
    public String eventType() {
        return "DeliveryStatusChanged";
    }
}
