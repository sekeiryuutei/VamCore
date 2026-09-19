package com.vamcore.files.domain.repository;

import com.vamcore.files.domain.model.FileMetadata;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FileMetadataRepository {

    FileMetadata save(FileMetadata metadata);

    Optional<FileMetadata> findById(UUID tenantId, UUID id);

    List<FileMetadata> findAllByTenant(UUID tenantId);

    void deleteById(UUID tenantId, UUID id);
}
