package com.vamcore.shared.domain.event;

import java.time.Instant;
import java.util.UUID;

/**
 * Contrato base para todos los eventos de dominio de la plataforma
 * (VamStock, VamAsset, VamTrack y los módulos core).
 *
 * Un DomainEvent representa un hecho que YA ocurrió (pasado), nunca una
 * intención (eso son los Commands, ver application/command en cada módulo).
 *
 * Ejemplos de implementaciones esperadas en cada bounded context:
 *   inventory  -> ProductCreated, StockAdjusted, InventoryTransferred
 *   assets     -> AssetAcquired, AssetAssigned, AssetDisposed
 *   logistics  -> DeliveryCreated, DeliveryDispatched, DeliveryDelivered
 */
public interface DomainEvent {

    /** Identificador único del evento (para trazabilidad / idempotencia). */
    UUID eventId();

    /** Momento en que ocurrió el hecho de negocio. */
    Instant occurredAt();

    /** Tenant al que pertenece el hecho (multi-tenancy, ver ADR-005). */
    UUID tenantId();

    /** Nombre corto del tipo de evento, usado en audit_log / integraciones. */
    String eventType();
}
