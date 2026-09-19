package com.vamcore.inventory.infrastructure.web;

import com.vamcore.inventory.application.command.AdjustStockCommand;
import com.vamcore.inventory.application.command.ReceiveStockCommand;
import com.vamcore.inventory.application.dto.*;
import com.vamcore.inventory.application.query.GetKardexQuery;
import com.vamcore.inventory.application.query.GetStockQuery;
import com.vamcore.inventory.application.usecase.AdjustStockUseCase;
import com.vamcore.inventory.application.usecase.GetKardexUseCase;
import com.vamcore.inventory.application.usecase.GetStockUseCase;
import com.vamcore.inventory.application.usecase.ReceiveStockUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * API de stock y Kardex de VamStock. Ver secciones 13-14 y 25 del
 * documento de arquitectura (GetKardex, GetStockDashboard).
 */
@RestController
@RequestMapping("/api/v1/inventory")
public class StockController {

    private final ReceiveStockUseCase receiveStockUseCase;
    private final AdjustStockUseCase adjustStockUseCase;
    private final GetStockUseCase getStockUseCase;
    private final GetKardexUseCase getKardexUseCase;

    public StockController(ReceiveStockUseCase receiveStockUseCase, AdjustStockUseCase adjustStockUseCase,
                            GetStockUseCase getStockUseCase, GetKardexUseCase getKardexUseCase) {
        this.receiveStockUseCase = receiveStockUseCase;
        this.adjustStockUseCase = adjustStockUseCase;
        this.getStockUseCase = getStockUseCase;
        this.getKardexUseCase = getKardexUseCase;
    }

    @PostMapping("/receipts")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('INVENTORY_ADJUST')")
    public StockResponse receive(@Valid @RequestBody ReceiveStockRequest request) {
        var stock = receiveStockUseCase.handle(new ReceiveStockCommand(
            request.productId(), request.warehouseId(), request.quantity(), request.referenceType(), request.referenceId()
        ));
        return StockResponse.from(stock);
    }

    @PostMapping("/adjustments")
    @PreAuthorize("hasAuthority('INVENTORY_ADJUST')")
    public StockResponse adjust(@Valid @RequestBody AdjustStockRequest request) {
        var stock = adjustStockUseCase.handle(new AdjustStockCommand(
            request.productId(), request.warehouseId(), request.quantityDelta(), request.reason()
        ));
        return StockResponse.from(stock);
    }

    @GetMapping("/products/{productId}/stock")
    public List<StockResponse> getStock(@PathVariable UUID productId) {
        return getStockUseCase.handle(new GetStockQuery(productId)).stream()
            .map(StockResponse::from)
            .toList();
    }

    @GetMapping("/products/{productId}/kardex")
    public List<MovementResponse> getKardex(@PathVariable UUID productId,
                                             @RequestParam(required = false) UUID warehouseId) {
        return getKardexUseCase.handle(new GetKardexQuery(productId, warehouseId)).stream()
            .map(MovementResponse::from)
            .toList();
    }
}
