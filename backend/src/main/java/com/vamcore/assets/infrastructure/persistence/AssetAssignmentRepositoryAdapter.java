package com.vamcore.assets.infrastructure.persistence;

import com.vamcore.assets.domain.model.AssetAssignment;
import com.vamcore.assets.domain.repository.AssetAssignmentRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public class AssetAssignmentRepositoryAdapter implements AssetAssignmentRepository {

    private final SpringDataAssetAssignmentRepository jpaRepository;

    public AssetAssignmentRepositoryAdapter(SpringDataAssetAssignmentRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public AssetAssignment save(AssetAssignment assignment) {
        jpaRepository.save(new AssetAssignmentJpaEntity(
            assignment.id(), assignment.tenantId(), assignment.assetId(), assignment.assigneeId(), assignment.assignedAt()
        ));
        return assignment;
    }

    @Override
    public List<AssetAssignment> findByAsset(UUID tenantId, UUID assetId) {
        return jpaRepository.findAllByTenantIdAndAssetIdOrderByAssignedAtDesc(tenantId, assetId).stream()
            .map(e -> AssetAssignment.reconstitute(e.getId(), e.getTenantId(), e.getAssetId(), e.getAssigneeId(), e.getAssignedAt()))
            .toList();
    }
}
