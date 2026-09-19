package com.vamcore.inventory.application.usecase;

import com.vamcore.inventory.application.query.GetStockQuery;
import com.vamcore.inventory.domain.model.Stock;
import com.vamcore.inventory.domain.repository.StockRepository;
import com.vamcore.shared.domain.valueobject.TenantId;
import com.vamcore.shared.infrastructure.tenant.TenantContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/** GetStockDashboard simplificado: stock de un producto en todas sus bodegas (sección 25). */
@Service
public class GetStockUseCase {

    private final StockRepository stockRepository;

    public GetStockUseCase(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
    }

    @Transactional(readOnly = true)
    public List<Stock> handle(GetStockQuery query) {
        TenantId tenantId = TenantContext.get();
        return stockRepository.findAllByProduct(tenantId.value(), query.productId());
    }
}
