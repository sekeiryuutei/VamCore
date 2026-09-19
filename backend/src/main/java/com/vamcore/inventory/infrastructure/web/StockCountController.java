package com.vamcore.inventory.infrastructure.web;

import com.vamcore.inventory.application.command.CreateStockCountCommand;
import com.vamcore.inventory.application.dto.CreateStockCountRequest;
import com.vamcore.inventory.application.dto.StockCountResponse;
import com.vamcore.inventory.application.usecase.ApplyStockCountUseCase;
import com.vamcore.inventory.application.usecase.CreateStockCountUseCase;
import com.vamcore.inventory.application.usecase.ListStockCountsUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * API de conteos físicos de inventario (ver sección 56 del documento de
 * arquitectura - MVP: "Inventario físico").
 */
@RestController
@RequestMapping("/api/v1/inventory/stock-counts")
public class StockCountController {

    private final CreateStockCountUseCase createStockCountUseCase;
    private final ListStockCountsUseCase listStockCountsUseCase;
    private final ApplyStockCountUseCase applyStockCountUseCase;

    public StockCountController(CreateStockCountUseCase createStockCountUseCase, ListStockCountsUseCase listStockCountsUseCase,
                                 ApplyStockCountUseCase applyStockCountUseCase) {
        this.createStockCountUseCase = createStockCountUseCase;
        this.listStockCountsUseCase = listStockCountsUseCase;
        this.applyStockCountUseCase = applyStockCountUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('INVENTORY_ADJUST')")
    public StockCountResponse create(@Valid @RequestBody CreateStockCountRequest request) {
        var stockCount = createStockCountUseCase.handle(
            new CreateStockCountCommand(request.productId(), request.warehouseId(), request.countedQuantity())
        );
        return StockCountResponse.from(stockCount);
    }

    @GetMapping
    public List<StockCountResponse> list() {
        return listStockCountsUseCase.handle().stream().map(StockCountResponse::from).toList();
    }

    @PostMapping("/{id}/apply")
    @PreAuthorize("hasAuthority('INVENTORY_ADJUST')")
    public StockCountResponse apply(@PathVariable UUID id) {
        return StockCountResponse.from(applyStockCountUseCase.handle(id));
    }
}
