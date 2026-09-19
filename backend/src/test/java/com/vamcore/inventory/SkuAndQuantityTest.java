package com.vamcore.inventory;

import com.vamcore.inventory.domain.valueobject.Quantity;
import com.vamcore.inventory.domain.valueobject.SKU;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SkuAndQuantityTest {

    @Test
    void shouldNormalizeSkuToUpperCase() {
        SKU sku = SKU.of("abc-123");
        assertThat(sku.value()).isEqualTo("ABC-123");
    }

    @Test
    void shouldRejectInvalidSku() {
        assertThatThrownBy(() -> SKU.of("a")).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> SKU.of("has spaces")).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldNotAllowOperatingQuantitiesWithDifferentUnits() {
        Quantity kg = Quantity.of(new BigDecimal("5"), "kg");
        Quantity units = Quantity.of(new BigDecimal("5"), "unidad");

        assertThatThrownBy(() -> kg.add(units)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldSupportFractionalQuantities() {
        Quantity q = Quantity.of(new BigDecimal("2.5"), "kg").add(Quantity.of(new BigDecimal("1.25"), "kg"));
        assertThat(q.value()).isEqualByComparingTo("3.75");
    }
}
