package com.vamcore.assets;

import com.vamcore.assets.domain.model.Asset;
import com.vamcore.assets.domain.valueobject.AssetCode;
import com.vamcore.shared.domain.valueobject.Money;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class AssetDepreciationTest {

    @Test
    void shouldReturnNullWhenNoCostOrUsefulLifeRegistered() {
        Asset asset = Asset.acquire(UUID.randomUUID(), AssetCode.of("ASSET-1"), "Sin depreciar", null, null, null, null);
        assertThat(asset.currentBookValue(Instant.now())).isNull();
    }

    @Test
    void shouldNotDepreciateOnAcquisitionDay() {
        Money cost = Money.of(new BigDecimal("1200.00"), "USD");
        Asset asset = Asset.acquire(UUID.randomUUID(), AssetCode.of("ASSET-2"), "Laptop", "Cómputo", null, cost, 12);

        Money bookValue = asset.currentBookValue(asset.acquiredAt());

        assertThat(bookValue.amount()).isEqualByComparingTo("1200.00");
    }

    @Test
    void shouldDepreciateLinearlyOverTime() {
        Money cost = Money.of(new BigDecimal("1200.00"), "USD");
        Asset asset = Asset.acquire(UUID.randomUUID(), AssetCode.of("ASSET-3"), "Laptop", "Cómputo", null, cost, 12);

        // 6 de 12 meses transcurridos -> la mitad del valor depreciado
        Money bookValue = asset.currentBookValue(asset.acquiredAt().plus(6 * 30L, ChronoUnit.DAYS));

        assertThat(bookValue.amount()).isLessThan(new BigDecimal("1200.00"));
        assertThat(bookValue.amount()).isGreaterThan(BigDecimal.ZERO);
    }

    @Test
    void shouldNeverDepreciateBelowZero() {
        Money cost = Money.of(new BigDecimal("500.00"), "USD");
        Asset asset = Asset.acquire(UUID.randomUUID(), AssetCode.of("ASSET-4"), "Silla", "Mobiliario", null, cost, 6);

        // Muy por encima de la vida útil
        Money bookValue = asset.currentBookValue(asset.acquiredAt().plus(3650, ChronoUnit.DAYS));

        assertThat(bookValue.amount()).isEqualByComparingTo("0.00");
    }
}
