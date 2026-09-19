package com.vamcore.inventory.application.usecase;

import com.vamcore.inventory.application.command.CreateProductCommand;
import com.vamcore.inventory.domain.event.ProductCreated;
import com.vamcore.inventory.domain.model.Product;
import com.vamcore.inventory.domain.repository.ProductRepository;
import com.vamcore.inventory.domain.valueobject.SKU;
import com.vamcore.inventory.domain.valueobject.TrackingType;
import com.vamcore.shared.domain.event.EventPublisher;
import com.vamcore.shared.domain.valueobject.TenantId;
import com.vamcore.shared.infrastructure.exception.BusinessException;
import com.vamcore.shared.infrastructure.tenant.TenantContext;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreateProductUseCase {

    private final ProductRepository productRepository;
    private final EventPublisher eventPublisher;

    public CreateProductUseCase(ProductRepository productRepository, EventPublisher eventPublisher) {
        this.productRepository = productRepository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public Product handle(CreateProductCommand command) {
        TenantId tenantId = TenantContext.get();
        SKU sku = SKU.of(command.sku());

        if (productRepository.existsBySku(tenantId.value(), sku)) {
            throw new BusinessException(
                "PRODUCT_SKU_ALREADY_EXISTS",
                "Ya existe un producto con el SKU '" + sku + "' en este tenant",
                HttpStatus.CONFLICT
            );
        }

        TrackingType trackingType = command.trackingType() != null
            ? TrackingType.valueOf(command.trackingType().toUpperCase())
            : TrackingType.NONE;

        Product product = Product.create(
            tenantId.value(), sku, command.name(), command.description(),
            command.category(), command.unitOfMeasure(), trackingType
        );
        Product saved = productRepository.save(product);

        eventPublisher.publish(new ProductCreated(tenantId.value(), saved.id(), saved.sku().value()));

        return saved;
    }
}
