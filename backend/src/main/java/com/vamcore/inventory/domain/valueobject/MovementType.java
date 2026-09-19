package com.vamcore.inventory.domain.valueobject;

/**
 * Tipos de movimiento del ledger de inventario (ver sección 14 del
 * documento de arquitectura). El sistema debe poder reconstruir la
 * historia completa del inventario a partir de estos movimientos (Kardex).
 */
public enum MovementType {
    RECEIPT,
    SALE,
    ISSUE,
    TRANSFER_OUT,
    TRANSFER_IN,
    ADJUSTMENT,
    RETURN,
    DAMAGE,
    RESERVATION,
    RELEASE
}
