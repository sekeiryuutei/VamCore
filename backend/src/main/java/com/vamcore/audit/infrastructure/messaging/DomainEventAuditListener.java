package com.vamcore.audit.infrastructure.messaging;

import com.vamcore.audit.domain.model.AuditLog;
import com.vamcore.audit.domain.repository.AuditLogRepository;
import com.vamcore.shared.domain.event.DomainEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * Ejemplo de reacción transversal a eventos de dominio (ver sección 23 y 37
 * del documento de arquitectura: "Domain Event -> Audit Handler -> AuditLog").
 *
 * Cualquier módulo (inventory, assets, logistics, identity, organization)
 * puede publicar un DomainEvent y este listener queda automáticamente
 * suscrito, sin que el módulo emisor conozca la existencia de auditoría.
 */
@Component
public class DomainEventAuditListener {

    private final AuditLogRepository auditLogRepository;

    public DomainEventAuditListener(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    @Async
    @EventListener
    public void onDomainEvent(DomainEvent event) {
        AuditLog log = AuditLog.record(
            event.tenantId(),
            event.eventType(),
            event.getClass().getSimpleName(),
            event.eventId().toString(),
            null
        );
        auditLogRepository.save(log);
    }
}
