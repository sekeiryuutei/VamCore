package com.vamcore.files.domain.repository;

/**
 * Puerto de salida hacia el almacenamiento físico de archivos (ver
 * sección 38 del documento de arquitectura). El dominio NUNCA sabe si la
 * implementación es disco local, S3 o Azure Blob — solo conoce esta
 * interfaz. La adaptación inicial es {@link
 * com.vamcore.files.infrastructure.storage.LocalFileStorageAdapter}.
 */
public interface FileStoragePort {

    void store(String storageKey, byte[] content);

    byte[] load(String storageKey);

    void delete(String storageKey);
}
