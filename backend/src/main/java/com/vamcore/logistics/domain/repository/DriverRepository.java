package com.vamcore.logistics.domain.repository;

import com.vamcore.logistics.domain.model.Driver;

import java.util.List;
import java.util.UUID;

public interface DriverRepository {

    Driver save(Driver driver);

    List<Driver> findAllByTenant(UUID tenantId);
}
