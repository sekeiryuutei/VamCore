package com.vamcore.inventory.application.usecase;

import com.vamcore.inventory.application.command.CreateWarehouseCommand;
import com.vamcore.inventory.domain.model.Warehouse;
import com.vamcore.inventory.domain.repository.WarehouseRepository;
import com.vamcore.shared.domain.valueobject.TenantId;
import com.vamcore.shared.infrastructure.tenant.TenantContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreateWarehouseUseCase {

    private final WarehouseRepository warehouseRepository;

    public CreateWarehouseUseCase(WarehouseRepository warehouseRepository) {
        this.warehouseRepository = warehouseRepository;
    }

    @Transactional
    public Warehouse handle(CreateWarehouseCommand command) {
        TenantId tenantId = TenantContext.get();
        Warehouse warehouse = Warehouse.create(tenantId.value(), command.branchId(), command.name(), command.address());
        return warehouseRepository.save(warehouse);
    }
}
