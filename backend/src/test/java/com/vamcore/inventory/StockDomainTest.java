package com.vamcore.inventory;

import com.vamcore.inventory.domain.model.Stock;
import com.vamcore.inventory.domain.valueobject.Quantity;
import com.vamcore.shared.infrastructure.exception.BusinessException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class StockDomainTest {

    private Stock newStock() {
        return Stock.initialize(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), "unidad");
    }

    @Test
    void shouldIncreaseStockOnReceipt() {
        Stock stock = newStock();

        stock.increase(Quantity.of(new BigDecimal("100"), "unidad"));

        assertThat(stock.quantity().value()).isEqualByComparingTo("100");
    }

    @Test
    void shouldDecreaseStockWhenSufficient() {
        Stock stock = newStock();
        stock.increase(Quantity.of(new BigDecimal("100"), "unidad"));

        stock.decrease(Quantity.of(new BigDecimal("30"), "unidad"));

        assertThat(stock.quantity().value()).isEqualByComparingTo("70");
    }

    @Test
    void shouldRejectDecreaseThatWouldGoNegative() {
        // Simula la sección 42 del documento de arquitectura:
        // Stock = 10, dos retiros concurrentes de 8 y 7 no pueden dejar Stock = -5.
        Stock stock = newStock();
        stock.increase(Quantity.of(new BigDecimal("10"), "unidad"));
        stock.decrease(Quantity.of(new BigDecimal("8"), "unidad"));

        assertThatThrownBy(() -> stock.decrease(Quantity.of(new BigDecimal("7"), "unidad")))
            .isInstanceOf(BusinessException.class)
            .satisfies(ex -> assertThat(((BusinessException) ex).code()).isEqualTo("INSUFFICIENT_STOCK"));
    }

    @Test
    void shouldAllowPositiveAndNegativeAdjustmentsWithinBounds() {
        Stock stock = newStock();
        stock.increase(Quantity.of(new BigDecimal("50"), "unidad"));

        stock.adjust(Quantity.of(new BigDecimal("5"), "unidad")); // sobrante encontrado en conteo físico
        assertThat(stock.quantity().value()).isEqualByComparingTo("55");

        stock.adjust(Quantity.of(new BigDecimal("-10"), "unidad")); // faltante encontrado en conteo físico
        assertThat(stock.quantity().value()).isEqualByComparingTo("45");
    }

    @Test
    void shouldRejectAdjustmentThatWouldGoNegative() {
        Stock stock = newStock();
        stock.increase(Quantity.of(new BigDecimal("5"), "unidad"));

        assertThatThrownBy(() -> stock.adjust(Quantity.of(new BigDecimal("-10"), "unidad")))
            .isInstanceOf(BusinessException.class);
    }
}
