package com.vamcore.inventory.application.usecase;

import com.vamcore.inventory.domain.model.StockCount;
import com.vamcore.inventory.domain.repository.StockCountRepository;
import com.vamcore.shared.domain.valueobject.TenantId;
import com.vamcore.shared.infrastructure.tenant.TenantContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ListStockCountsUseCase {

    private final StockCountRepository stockCountRepository;

    public ListStockCountsUseCase(StockCountRepository stockCountRepository) {
        this.stockCountRepository = stockCountRepository;
    }

    @Transactional(readOnly = true)
    public List<StockCount> handle() {
        TenantId tenantId = TenantContext.get();
        return stockCountRepository.findAllByTenant(tenantId.value());
    }
}
