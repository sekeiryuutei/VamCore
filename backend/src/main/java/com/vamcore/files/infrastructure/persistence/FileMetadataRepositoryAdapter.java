package com.vamcore.files.infrastructure.persistence;

import com.vamcore.files.domain.model.FileMetadata;
import com.vamcore.files.domain.repository.FileMetadataRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class FileMetadataRepositoryAdapter implements FileMetadataRepository {

    private final SpringDataFileMetadataRepository jpaRepository;

    public FileMetadataRepositoryAdapter(SpringDataFileMetadataRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public FileMetadata save(FileMetadata metadata) {
        jpaRepository.save(new FileMetadataJpaEntity(
            metadata.id(), metadata.tenantId(), metadata.originalFilename(), metadata.contentType(),
            metadata.sizeBytes(), metadata.storageKey(), metadata.uploadedBy(), metadata.createdAt()
        ));
        return metadata;
    }

    @Override
    public Optional<FileMetadata> findById(UUID tenantId, UUID id) {
        return jpaRepository.findByIdAndTenantId(id, tenantId).map(this::toDomain);
    }

    @Override
    public List<FileMetadata> findAllByTenant(UUID tenantId) {
        return jpaRepository.findAllByTenantIdOrderByCreatedAtDesc(tenantId).stream()
            .map(this::toDomain)
            .toList();
    }

    @Override
    public void deleteById(UUID tenantId, UUID id) {
        jpaRepository.deleteByIdAndTenantId(id, tenantId);
    }

    private FileMetadata toDomain(FileMetadataJpaEntity entity) {
        return FileMetadata.reconstitute(
            entity.getId(), entity.getTenantId(), entity.getOriginalFilename(), entity.getContentType(),
            entity.getSizeBytes(), entity.getStorageKey(), entity.getUploadedBy(), entity.getCreatedAt()
        );
    }
}
