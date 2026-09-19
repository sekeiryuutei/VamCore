package com.vamcore.files.infrastructure.web;

import com.vamcore.files.application.dto.FileMetadataResponse;
import com.vamcore.files.application.usecase.DeleteFileUseCase;
import com.vamcore.files.application.usecase.DownloadFileUseCase;
import com.vamcore.files.application.usecase.ListFilesUseCase;
import com.vamcore.files.application.usecase.UploadFileUseCase;
import com.vamcore.files.domain.model.FileMetadata;
import com.vamcore.shared.infrastructure.exception.BusinessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

/**
 * API de archivos (VamCore core transversal, ver sección 38 del documento
 * de arquitectura). El binario nunca pasa por el cuerpo de una respuesta
 * JSON: se sube como multipart/form-data y se descarga como stream binario.
 */
@RestController
@RequestMapping("/api/v1/files")
public class FileController {

    private final UploadFileUseCase uploadFileUseCase;
    private final ListFilesUseCase listFilesUseCase;
    private final DownloadFileUseCase downloadFileUseCase;
    private final DeleteFileUseCase deleteFileUseCase;

    public FileController(UploadFileUseCase uploadFileUseCase, ListFilesUseCase listFilesUseCase,
                           DownloadFileUseCase downloadFileUseCase, DeleteFileUseCase deleteFileUseCase) {
        this.uploadFileUseCase = uploadFileUseCase;
        this.listFilesUseCase = listFilesUseCase;
        this.downloadFileUseCase = downloadFileUseCase;
        this.deleteFileUseCase = deleteFileUseCase;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('FILE_MANAGE')")
    public FileMetadataResponse upload(@RequestParam("file") MultipartFile file) {
        try {
            FileMetadata metadata = uploadFileUseCase.handle(file.getOriginalFilename(), file.getContentType(), file.getBytes());
            return FileMetadataResponse.from(metadata);
        } catch (IOException e) {
            throw new BusinessException("FILE_READ_ERROR", "No se pudo leer el archivo enviado", HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping
    @PreAuthorize("hasAuthority('FILE_MANAGE')")
    public List<FileMetadataResponse> list() {
        return listFilesUseCase.handle().stream().map(FileMetadataResponse::from).toList();
    }

    @GetMapping("/{fileId}/download")
    @PreAuthorize("hasAuthority('FILE_MANAGE')")
    public ResponseEntity<byte[]> download(@PathVariable UUID fileId) {
        DownloadFileUseCase.FileDownload download = downloadFileUseCase.handle(fileId);
        MediaType mediaType = download.metadata().contentType() != null
            ? MediaType.parseMediaType(download.metadata().contentType())
            : MediaType.APPLICATION_OCTET_STREAM;

        return ResponseEntity.ok()
            .contentType(mediaType)
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + download.metadata().originalFilename() + "\"")
            .body(download.content());
    }

    @DeleteMapping("/{fileId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('FILE_MANAGE')")
    public void delete(@PathVariable UUID fileId) {
        deleteFileUseCase.handle(fileId);
    }
}
