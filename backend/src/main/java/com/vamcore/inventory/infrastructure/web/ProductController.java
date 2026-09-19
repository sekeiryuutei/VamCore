package com.vamcore.inventory.infrastructure.web;

import com.vamcore.inventory.application.command.CreateProductCommand;
import com.vamcore.inventory.application.dto.CreateProductRequest;
import com.vamcore.inventory.application.dto.ProductResponse;
import com.vamcore.inventory.application.usecase.CreateProductUseCase;
import com.vamcore.inventory.application.usecase.ListProductsUseCase;
import com.vamcore.inventory.domain.model.Product;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * API de catálogo de productos (VamStock). Ver sección 40 del documento
 * de arquitectura: rutas versionadas bajo /api/v1/.
 */
@RestController
@RequestMapping("/api/v1/inventory/products")
public class ProductController {

    private final CreateProductUseCase createProductUseCase;
    private final ListProductsUseCase listProductsUseCase;

    public ProductController(CreateProductUseCase createProductUseCase, ListProductsUseCase listProductsUseCase) {
        this.createProductUseCase = createProductUseCase;
        this.listProductsUseCase = listProductsUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('PRODUCT_MANAGE')")
    public ProductResponse create(@Valid @RequestBody CreateProductRequest request) {
        Product product = createProductUseCase.handle(new CreateProductCommand(
            request.sku(), request.name(), request.description(), request.category(),
            request.unitOfMeasure(), request.trackingType()
        ));
        return ProductResponse.from(product);
    }

    @GetMapping
    public List<ProductResponse> list() {
        return listProductsUseCase.handle().stream()
            .map(ProductResponse::from)
            .toList();
    }
}
