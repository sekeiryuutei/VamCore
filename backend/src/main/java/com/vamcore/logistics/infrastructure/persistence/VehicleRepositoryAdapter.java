package com.vamcore.logistics.infrastructure.persistence;

import com.vamcore.logistics.domain.model.Vehicle;
import com.vamcore.logistics.domain.repository.VehicleRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public class VehicleRepositoryAdapter implements VehicleRepository {

    private final SpringDataVehicleRepository jpaRepository;

    public VehicleRepositoryAdapter(SpringDataVehicleRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Vehicle save(Vehicle vehicle) {
        jpaRepository.save(new VehicleJpaEntity(vehicle.id(), vehicle.tenantId(), vehicle.plate(), vehicle.model(), vehicle.status().name()));
        return vehicle;
    }

    @Override
    public List<Vehicle> findAllByTenant(UUID tenantId) {
        return jpaRepository.findAllByTenantId(tenantId).stream()
            .map(e -> Vehicle.reconstitute(e.getId(), e.getTenantId(), e.getPlate(), e.getModel(), Vehicle.Status.valueOf(e.getStatus())))
            .toList();
    }
}
