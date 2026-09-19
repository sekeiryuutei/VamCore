package com.vamcore.files.infrastructure.storage;

import com.vamcore.files.domain.repository.FileStoragePort;
import com.vamcore.shared.infrastructure.exception.BusinessException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Implementación inicial de {@link FileStoragePort}: guarda los archivos en
 * disco local, bajo el directorio configurado en
 * `vamcore.files.storage-path` (por defecto /app/storage dentro del
 * contenedor, montado como volumen Docker para persistir entre reinicios —
 * ver infrastructure/docker-compose.yml).
 *
 * Migración futura a S3/Azure Blob (ver sección 38 del documento de
 * arquitectura): crear un nuevo `@Component` que implemente FileStoragePort
 * (ej. S3FileStorageAdapter) y quitar/perfilar este bean. Ni el dominio ni
 * los casos de uso cambian.
 */
@Component
public class LocalFileStorageAdapter implements FileStoragePort {

    private final Path basePath;

    public LocalFileStorageAdapter(@Value("${vamcore.files.storage-path:/app/storage}") String storagePath) {
        this.basePath = Paths.get(storagePath);
        try {
            Files.createDirectories(this.basePath);
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo crear el directorio de almacenamiento: " + storagePath, e);
        }
    }

    @Override
    public void store(String storageKey, byte[] content) {
        try {
            Path target = resolve(storageKey);
            Files.createDirectories(target.getParent());
            Files.write(target, content);
        } catch (IOException e) {
            throw new BusinessException("FILE_STORAGE_ERROR", "No se pudo guardar el archivo", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public byte[] load(String storageKey) {
        try {
            return Files.readAllBytes(resolve(storageKey));
        } catch (IOException e) {
            throw new BusinessException("FILE_STORAGE_ERROR", "No se pudo leer el archivo", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public void delete(String storageKey) {
        try {
            Files.deleteIfExists(resolve(storageKey));
        } catch (IOException e) {
            throw new BusinessException("FILE_STORAGE_ERROR", "No se pudo borrar el archivo", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Resuelve la ruta física, verificando que quede dentro de basePath
     * (evita path traversal a partir de un storageKey manipulado).
     */
    private Path resolve(String storageKey) {
        Path resolved = basePath.resolve(storageKey).normalize();
        if (!resolved.startsWith(basePath)) {
            throw new BusinessException("INVALID_FILE_KEY", "Clave de almacenamiento inválida", HttpStatus.BAD_REQUEST);
        }
        return resolved;
    }
}
