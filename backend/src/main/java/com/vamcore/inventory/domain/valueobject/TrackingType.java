package com.vamcore.inventory.domain.valueobject;

/**
 * Determina cómo se controla físicamente un producto (ver sección 12
 * del documento de arquitectura).
 */
public enum TrackingType {
    NONE,
    BATCH,
    SERIALIZED
}
