package com.vamcore.inventory.infrastructure.persistence;

import com.vamcore.inventory.domain.model.Warehouse;
import com.vamcore.inventory.domain.repository.WarehouseRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class WarehouseRepositoryAdapter implements WarehouseRepository {

    private final SpringDataWarehouseRepository jpaRepository;

    public WarehouseRepositoryAdapter(SpringDataWarehouseRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Warehouse save(Warehouse warehouse) {
        WarehouseJpaEntity entity = new WarehouseJpaEntity(
            warehouse.id(), warehouse.tenantId(), warehouse.branchId(),
            warehouse.name(), warehouse.address(), warehouse.active()
        );
        jpaRepository.save(entity);
        return warehouse;
    }

    @Override
    public Optional<Warehouse> findById(UUID tenantId, UUID id) {
        return jpaRepository.findByIdAndTenantId(id, tenantId).map(this::toDomain);
    }

    @Override
    public List<Warehouse> findAllByTenant(UUID tenantId) {
        return jpaRepository.findAllByTenantId(tenantId).stream()
            .map(this::toDomain)
            .toList();
    }

    private Warehouse toDomain(WarehouseJpaEntity entity) {
        return Warehouse.reconstitute(
            entity.getId(), entity.getTenantId(), entity.getBranchId(),
            entity.getName(), entity.getAddress(), entity.isActive()
        );
    }
}
