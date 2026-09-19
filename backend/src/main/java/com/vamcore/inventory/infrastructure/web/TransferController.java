package com.vamcore.inventory.infrastructure.web;

import com.vamcore.inventory.application.command.TransferInventoryCommand;
import com.vamcore.inventory.application.dto.TransferInventoryRequest;
import com.vamcore.inventory.application.usecase.TransferInventoryUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * API de transferencias entre bodegas (ver ejemplo completo de la
 * sección 62 del documento de arquitectura).
 */
@RestController
@RequestMapping("/api/v1/inventory/transfers")
public class TransferController {

    private final TransferInventoryUseCase transferInventoryUseCase;

    public TransferController(TransferInventoryUseCase transferInventoryUseCase) {
        this.transferInventoryUseCase = transferInventoryUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('INVENTORY_TRANSFER')")
    public void transfer(@Valid @RequestBody TransferInventoryRequest request) {
        transferInventoryUseCase.handle(new TransferInventoryCommand(
            request.productId(), request.sourceWarehouseId(), request.targetWarehouseId(), request.quantity()
        ));
    }
}
