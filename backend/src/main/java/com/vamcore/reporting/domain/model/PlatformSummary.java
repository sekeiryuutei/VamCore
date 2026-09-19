package com.vamcore.reporting.domain.model;

import java.util.Map;

/**
 * Read model agregado entre los tres productos comerciales (ver sección 26
 * del documento de arquitectura - CQRS selectivo). No es un aggregate con
 * reglas de negocio ni se persiste: se construye en cada consulta a partir
 * de los repositorios de dominio de inventory/assets/logistics.
 *
 * Esto es exactamente el tipo de caso donde el propio documento de
 * arquitectura recomienda CQRS: "dashboards" (sección 26).
 */
public record PlatformSummary(
    long productCount,
    long warehouseCount,
    long assetCount,
    Map<String, Long> assetsByStatus,
    long deliveryCount,
    Map<String, Long> deliveriesByStatus
) {
}
