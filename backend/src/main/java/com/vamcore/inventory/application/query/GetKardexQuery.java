package com.vamcore.inventory.application.query;

import java.util.UUID;

/** warehouseId es opcional: si es null, trae el Kardex del producto en todas las bodegas. */
public record GetKardexQuery(UUID productId, UUID warehouseId) {
}
