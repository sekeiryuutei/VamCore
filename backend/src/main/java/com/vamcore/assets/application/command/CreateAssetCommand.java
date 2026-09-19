package com.vamcore.assets.application.command;

import java.math.BigDecimal;

/** acquisitionCost/usefulLifeMonths son opcionales — sin ellos, el activo no calcula depreciación. */
public record CreateAssetCommand(
    String assetCode, String name, String category, String serialNumber,
    BigDecimal acquisitionCost, String currency, Integer usefulLifeMonths
) {
}
