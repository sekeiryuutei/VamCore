package com.vamcore.files.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpringDataFileMetadataRepository extends JpaRepository<FileMetadataJpaEntity, UUID> {

    Optional<FileMetadataJpaEntity> findByIdAndTenantId(UUID id, UUID tenantId);

    List<FileMetadataJpaEntity> findAllByTenantIdOrderByCreatedAtDesc(UUID tenantId);

    void deleteByIdAndTenantId(UUID id, UUID tenantId);
}
