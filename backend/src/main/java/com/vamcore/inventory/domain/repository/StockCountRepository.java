package com.vamcore.inventory.domain.repository;

import com.vamcore.inventory.domain.model.StockCount;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StockCountRepository {

    StockCount save(StockCount stockCount);

    Optional<StockCount> findById(UUID tenantId, UUID id);

    List<StockCount> findAllByTenant(UUID tenantId);
}
