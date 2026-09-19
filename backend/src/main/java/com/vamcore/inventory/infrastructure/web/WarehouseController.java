package com.vamcore.inventory.infrastructure.web;

import com.vamcore.inventory.application.command.CreateWarehouseCommand;
import com.vamcore.inventory.application.dto.CreateWarehouseRequest;
import com.vamcore.inventory.application.dto.WarehouseResponse;
import com.vamcore.inventory.application.usecase.CreateWarehouseUseCase;
import com.vamcore.inventory.application.usecase.ListWarehousesUseCase;
import com.vamcore.inventory.domain.model.Warehouse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/inventory/warehouses")
public class WarehouseController {

    private final CreateWarehouseUseCase createWarehouseUseCase;
    private final ListWarehousesUseCase listWarehousesUseCase;

    public WarehouseController(CreateWarehouseUseCase createWarehouseUseCase, ListWarehousesUseCase listWarehousesUseCase) {
        this.createWarehouseUseCase = createWarehouseUseCase;
        this.listWarehousesUseCase = listWarehousesUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('WAREHOUSE_MANAGE')")
    public WarehouseResponse create(@Valid @RequestBody CreateWarehouseRequest request) {
        Warehouse warehouse = createWarehouseUseCase.handle(
            new CreateWarehouseCommand(request.name(), request.address(), request.branchId())
        );
        return WarehouseResponse.from(warehouse);
    }

    @GetMapping
    public List<WarehouseResponse> list() {
        return listWarehousesUseCase.handle().stream()
            .map(WarehouseResponse::from)
            .toList();
    }
}
