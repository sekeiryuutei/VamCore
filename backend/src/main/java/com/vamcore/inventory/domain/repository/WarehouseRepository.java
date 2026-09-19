package com.vamcore.inventory.domain.repository;

import com.vamcore.inventory.domain.model.Warehouse;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WarehouseRepository {

    Warehouse save(Warehouse warehouse);

    Optional<Warehouse> findById(UUID tenantId, UUID id);

    List<Warehouse> findAllByTenant(UUID tenantId);
}
