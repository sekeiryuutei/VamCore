package com.vamcore.files.application.usecase;

import com.vamcore.files.domain.model.FileMetadata;
import com.vamcore.files.domain.repository.FileMetadataRepository;
import com.vamcore.shared.domain.valueobject.TenantId;
import com.vamcore.shared.infrastructure.tenant.TenantContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ListFilesUseCase {

    private final FileMetadataRepository fileMetadataRepository;

    public ListFilesUseCase(FileMetadataRepository fileMetadataRepository) {
        this.fileMetadataRepository = fileMetadataRepository;
    }

    @Transactional(readOnly = true)
    public List<FileMetadata> handle() {
        TenantId tenantId = TenantContext.get();
        return fileMetadataRepository.findAllByTenant(tenantId.value());
    }
}
