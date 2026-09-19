package com.vamcore.inventory.infrastructure.web;

import com.vamcore.inventory.application.command.CreatePurchaseOrderCommand;
import com.vamcore.inventory.application.dto.CreatePurchaseOrderRequest;
import com.vamcore.inventory.application.dto.PurchaseOrderResponse;
import com.vamcore.inventory.application.usecase.CreatePurchaseOrderUseCase;
import com.vamcore.inventory.application.usecase.ListPurchaseOrdersUseCase;
import com.vamcore.inventory.application.usecase.ReceivePurchaseOrderUseCase;
import com.vamcore.inventory.domain.model.PurchaseOrder;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * API de órdenes de compra (ver sección 53 del documento de arquitectura).
 */
@RestController
@RequestMapping("/api/v1/inventory/purchase-orders")
public class PurchaseOrderController {

    private final CreatePurchaseOrderUseCase createPurchaseOrderUseCase;
    private final ListPurchaseOrdersUseCase listPurchaseOrdersUseCase;
    private final ReceivePurchaseOrderUseCase receivePurchaseOrderUseCase;

    public PurchaseOrderController(CreatePurchaseOrderUseCase createPurchaseOrderUseCase,
                                    ListPurchaseOrdersUseCase listPurchaseOrdersUseCase,
                                    ReceivePurchaseOrderUseCase receivePurchaseOrderUseCase) {
        this.createPurchaseOrderUseCase = createPurchaseOrderUseCase;
        this.listPurchaseOrdersUseCase = listPurchaseOrdersUseCase;
        this.receivePurchaseOrderUseCase = receivePurchaseOrderUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('PRODUCT_MANAGE')")
    public PurchaseOrderResponse create(@Valid @RequestBody CreatePurchaseOrderRequest request) {
        PurchaseOrder po = createPurchaseOrderUseCase.handle(new CreatePurchaseOrderCommand(
            request.code(), request.supplierName(), request.productId(), request.warehouseId(), request.quantityOrdered()
        ));
        return PurchaseOrderResponse.from(po);
    }

    @GetMapping
    public List<PurchaseOrderResponse> list() {
        return listPurchaseOrdersUseCase.handle().stream().map(PurchaseOrderResponse::from).toList();
    }

    @PostMapping("/{id}/confirm")
    @PreAuthorize("hasAuthority('PRODUCT_MANAGE')")
    public PurchaseOrderResponse confirm(@PathVariable UUID id) {
        return PurchaseOrderResponse.from(receivePurchaseOrderUseCase.confirm(id));
    }

    @PostMapping("/{id}/receive")
    @PreAuthorize("hasAuthority('INVENTORY_ADJUST')")
    public PurchaseOrderResponse receive(@PathVariable UUID id) {
        return PurchaseOrderResponse.from(receivePurchaseOrderUseCase.receive(id));
    }

    @PostMapping("/{id}/cancel")
    @PreAuthorize("hasAuthority('PRODUCT_MANAGE')")
    public PurchaseOrderResponse cancel(@PathVariable UUID id) {
        return PurchaseOrderResponse.from(receivePurchaseOrderUseCase.cancel(id));
    }
}
