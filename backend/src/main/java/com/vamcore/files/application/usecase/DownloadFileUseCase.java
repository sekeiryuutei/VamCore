package com.vamcore.files.application.usecase;

import com.vamcore.files.domain.model.FileMetadata;
import com.vamcore.files.domain.repository.FileMetadataRepository;
import com.vamcore.files.domain.repository.FileStoragePort;
import com.vamcore.shared.domain.valueobject.TenantId;
import com.vamcore.shared.infrastructure.exception.BusinessException;
import com.vamcore.shared.infrastructure.tenant.TenantContext;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class DownloadFileUseCase {

    private final FileMetadataRepository fileMetadataRepository;
    private final FileStoragePort fileStoragePort;

    public DownloadFileUseCase(FileMetadataRepository fileMetadataRepository, FileStoragePort fileStoragePort) {
        this.fileMetadataRepository = fileMetadataRepository;
        this.fileStoragePort = fileStoragePort;
    }

    public record FileDownload(FileMetadata metadata, byte[] content) {
    }

    @Transactional(readOnly = true)
    public FileDownload handle(UUID fileId) {
        TenantId tenantId = TenantContext.get();
        FileMetadata metadata = fileMetadataRepository.findById(tenantId.value(), fileId)
            .orElseThrow(() -> new BusinessException("FILE_NOT_FOUND", "Archivo no encontrado", HttpStatus.NOT_FOUND));
        byte[] content = fileStoragePort.load(metadata.storageKey());
        return new FileDownload(metadata, content);
    }
}
