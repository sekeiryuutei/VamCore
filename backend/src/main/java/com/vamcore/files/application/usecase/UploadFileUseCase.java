package com.vamcore.files.application.usecase;

import com.vamcore.files.domain.model.FileMetadata;
import com.vamcore.files.domain.repository.FileMetadataRepository;
import com.vamcore.files.domain.repository.FileStoragePort;
import com.vamcore.shared.domain.valueobject.TenantId;
import com.vamcore.shared.infrastructure.tenant.TenantContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Sube un archivo (ver sección 38 del documento de arquitectura): el
 * contenido va al {@link FileStoragePort}, los metadatos a PostgreSQL.
 * El `storageKey` incluye el tenantId como prefijo para que, aunque el
 * almacenamiento físico sea compartido, los archivos de un tenant nunca
 * puedan colisionar ni ser adivinados a partir de los de otro.
 */
@Service
public class UploadFileUseCase {

    private final FileMetadataRepository fileMetadataRepository;
    private final FileStoragePort fileStoragePort;

    public UploadFileUseCase(FileMetadataRepository fileMetadataRepository, FileStoragePort fileStoragePort) {
        this.fileMetadataRepository = fileMetadataRepository;
        this.fileStoragePort = fileStoragePort;
    }

    @Transactional
    public FileMetadata handle(String originalFilename, String contentType, byte[] content) {
        TenantId tenantId = TenantContext.get();
        String storageKey = tenantId.value() + "/" + UUID.randomUUID() + "-" + sanitize(originalFilename);

        fileStoragePort.store(storageKey, content);

        FileMetadata metadata = FileMetadata.create(
            tenantId.value(), originalFilename, contentType, content.length, storageKey, null
        );
        return fileMetadataRepository.save(metadata);
    }

    private String sanitize(String filename) {
        return filename == null ? "archivo" : filename.replaceAll("[^A-Za-z0-9._-]", "_");
    }
}
