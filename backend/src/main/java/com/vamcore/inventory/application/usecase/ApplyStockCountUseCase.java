package com.vamcore.inventory.application.usecase;

import com.vamcore.inventory.application.command.AdjustStockCommand;
import com.vamcore.inventory.domain.model.StockCount;
import com.vamcore.inventory.domain.repository.StockCountRepository;
import com.vamcore.shared.domain.valueobject.TenantId;
import com.vamcore.shared.infrastructure.exception.BusinessException;
import com.vamcore.shared.infrastructure.tenant.TenantContext;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Aplica la varianza de un conteo físico como un movimiento ADJUSTMENT
 * (reutiliza {@link AdjustStockUseCase}, así que queda en el mismo Kardex
 * que cualquier otro ajuste manual). Un conteo en DRAFT no afecta el stock
 * hasta que se aplica explícitamente — así se puede revisar antes de
 * confirmar un faltante/sobrante.
 */
@Service
public class ApplyStockCountUseCase {

    private final StockCountRepository stockCountRepository;
    private final AdjustStockUseCase adjustStockUseCase;

    public ApplyStockCountUseCase(StockCountRepository stockCountRepository, AdjustStockUseCase adjustStockUseCase) {
        this.stockCountRepository = stockCountRepository;
        this.adjustStockUseCase = adjustStockUseCase;
    }

    @Transactional
    public StockCount handle(UUID stockCountId) {
        TenantId tenantId = TenantContext.get();

        StockCount stockCount = stockCountRepository.findById(tenantId.value(), stockCountId)
            .orElseThrow(() -> new BusinessException("STOCK_COUNT_NOT_FOUND", "Conteo no encontrado", HttpStatus.NOT_FOUND));

        BigDecimal variance = stockCount.variance().value();
        if (variance.compareTo(BigDecimal.ZERO) != 0) {
            adjustStockUseCase.handle(new AdjustStockCommand(
                stockCount.productId(), stockCount.warehouseId(), variance,
                "Ajuste por conteo físico " + stockCount.id()
            ));
        }

        stockCount.markApplied();
        return stockCountRepository.save(stockCount);
    }
}
