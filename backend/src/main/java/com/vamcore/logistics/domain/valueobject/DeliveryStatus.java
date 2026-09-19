package com.vamcore.logistics.domain.valueobject;

/**
 * Estados de la máquina de estados de una entrega (ver sección 20 del
 * documento de arquitectura):
 *
 *   CREATED -> CONFIRMED -> PREPARING -> READY -> ASSIGNED -> DISPATCHED
 *      -> IN_TRANSIT -> DELIVERED
 *
 * Flujos alternativos: IN_TRANSIT -> FAILED -> RETURNED, o CREATED -> CANCELLED.
 */
public enum DeliveryStatus {
    CREATED,
    CONFIRMED,
    PREPARING,
    READY,
    ASSIGNED,
    DISPATCHED,
    IN_TRANSIT,
    DELIVERED,
    FAILED,
    RETURNED,
    CANCELLED
}
