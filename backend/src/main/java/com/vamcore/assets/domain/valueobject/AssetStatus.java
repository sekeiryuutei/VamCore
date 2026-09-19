package com.vamcore.assets.domain.valueobject;

/**
 * Ciclo de vida del activo (ver sección 16 del documento de arquitectura):
 *
 *   ACQUIRED -> IN_STORAGE -> ASSIGNED -> IN_USE -> IN_MAINTENANCE -> IN_USE
 *      -> RETIRED -> DISPOSED
 *
 * No cualquier transición es válida (ver Asset.changeStatus). Por ejemplo
 * DISPOSED -> IN_USE está prohibido salvo una operación explícita de
 * reversión autorizada, que esta versión inicial no implementa.
 */
public enum AssetStatus {
    ACQUIRED,
    IN_STORAGE,
    ASSIGNED,
    IN_USE,
    IN_MAINTENANCE,
    RETIRED,
    DISPOSED
}
