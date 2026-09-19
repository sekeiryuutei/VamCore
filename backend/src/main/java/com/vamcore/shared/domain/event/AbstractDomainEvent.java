package com.vamcore.shared.domain.event;

import java.time.Instant;
import java.util.UUID;

/**
 * Implementación base reutilizable de {@link DomainEvent}.
 * Cada evento concreto (ej. ProductCreated) debe extender esta clase
 * y solo añadir los datos propios del hecho ocurrido.
 */
public abstract class AbstractDomainEvent implements DomainEvent {

    private final UUID eventId;
    private final Instant occurredAt;
    private final UUID tenantId;

    protected AbstractDomainEvent(UUID tenantId) {
        this.eventId = UUID.randomUUID();
        this.occurredAt = Instant.now();
        this.tenantId = tenantId;
    }

    @Override
    public UUID eventId() {
        return eventId;
    }

    @Override
    public Instant occurredAt() {
        return occurredAt;
    }

    @Override
    public UUID tenantId() {
        return tenantId;
    }
}
