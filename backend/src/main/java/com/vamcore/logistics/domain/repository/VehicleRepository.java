package com.vamcore.logistics.domain.repository;

import com.vamcore.logistics.domain.model.Vehicle;

import java.util.List;
import java.util.UUID;

public interface VehicleRepository {

    Vehicle save(Vehicle vehicle);

    List<Vehicle> findAllByTenant(UUID tenantId);
}
