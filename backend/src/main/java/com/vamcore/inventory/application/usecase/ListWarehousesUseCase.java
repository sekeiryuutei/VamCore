package com.vamcore.inventory.application.usecase;

import com.vamcore.inventory.domain.model.Warehouse;
import com.vamcore.inventory.domain.repository.WarehouseRepository;
import com.vamcore.shared.domain.valueobject.TenantId;
import com.vamcore.shared.infrastructure.tenant.TenantContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ListWarehousesUseCase {

    private final WarehouseRepository warehouseRepository;

    public ListWarehousesUseCase(WarehouseRepository warehouseRepository) {
        this.warehouseRepository = warehouseRepository;
    }

    @Transactional(readOnly = true)
    public List<Warehouse> handle() {
        TenantId tenantId = TenantContext.get();
        return warehouseRepository.findAllByTenant(tenantId.value());
    }
}
