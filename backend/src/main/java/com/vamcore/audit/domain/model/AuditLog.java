package com.vamcore.audit.domain.model;

import java.time.Instant;
import java.util.UUID;

/**
 * Registro de auditoría transversal (ver sección 37 del documento de
 * arquitectura). Se alimenta reaccionando a Domain Events, nunca contamina
 * las reglas de negocio de los demás módulos.
 */
public class AuditLog {

    private final UUID id;
    private final UUID tenantId;
    private final String action;
    private final String entityType;
    private final String entityId;
    private final String details;
    private final Instant occurredAt;

    private AuditLog(UUID id, UUID tenantId, String action, String entityType,
                      String entityId, String details, Instant occurredAt) {
        this.id = id;
        this.tenantId = tenantId;
        this.action = action;
        this.entityType = entityType;
        this.entityId = entityId;
        this.details = details;
        this.occurredAt = occurredAt;
    }

    public static AuditLog record(UUID tenantId, String action, String entityType, String entityId, String details) {
        return new AuditLog(UUID.randomUUID(), tenantId, action, entityType, entityId, details, Instant.now());
    }

    public static AuditLog reconstitute(UUID id, UUID tenantId, String action, String entityType,
                                         String entityId, String details, Instant occurredAt) {
        return new AuditLog(id, tenantId, action, entityType, entityId, details, occurredAt);
    }

    public UUID id() { return id; }
    public UUID tenantId() { return tenantId; }
    public String action() { return action; }
    public String entityType() { return entityType; }
    public String entityId() { return entityId; }
    public String details() { return details; }
    public Instant occurredAt() { return occurredAt; }
}
