package com.vamcore.logistics.domain.event;

import com.vamcore.shared.domain.event.AbstractDomainEvent;

import java.util.UUID;

public class DeliveryCreated extends AbstractDomainEvent {

    private final UUID deliveryId;
    private final String deliveryCode;

    public DeliveryCreated(UUID tenantId, UUID deliveryId, String deliveryCode) {
        super(tenantId);
        this.deliveryId = deliveryId;
        this.deliveryCode = deliveryCode;
    }

    public UUID deliveryId() { return deliveryId; }
    public String deliveryCode() { return deliveryCode; }

    @Override
    public String eventType() {
        return "DeliveryCreated";
    }
}
