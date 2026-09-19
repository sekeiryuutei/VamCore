package com.vamcore.assets.infrastructure.persistence;

import com.vamcore.assets.domain.model.Maintenance;
import com.vamcore.assets.domain.repository.MaintenanceRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class MaintenanceRepositoryAdapter implements MaintenanceRepository {

    private final SpringDataMaintenanceRepository jpaRepository;

    public MaintenanceRepositoryAdapter(SpringDataMaintenanceRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Maintenance save(Maintenance maintenance) {
        jpaRepository.save(new MaintenanceJpaEntity(
            maintenance.id(), maintenance.tenantId(), maintenance.assetId(), maintenance.scheduledAt(),
            maintenance.completedAt(), maintenance.notes(), maintenance.status().name()
        ));
        return maintenance;
    }

    @Override
    public Optional<Maintenance> findScheduledByAsset(UUID tenantId, UUID assetId) {
        return jpaRepository.findFirstByTenantIdAndAssetIdAndStatusOrderByScheduledAtDesc(tenantId, assetId, "SCHEDULED")
            .map(this::toDomain);
    }

    @Override
    public List<Maintenance> findByAsset(UUID tenantId, UUID assetId) {
        return jpaRepository.findAllByTenantIdAndAssetIdOrderByScheduledAtDesc(tenantId, assetId).stream()
            .map(this::toDomain)
            .toList();
    }

    private Maintenance toDomain(MaintenanceJpaEntity e) {
        return Maintenance.reconstitute(
            e.getId(), e.getTenantId(), e.getAssetId(), e.getScheduledAt(), e.getCompletedAt(),
            e.getNotes(), Maintenance.Status.valueOf(e.getStatus())
        );
    }
}
