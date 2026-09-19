package com.vamcore.inventory.application.usecase;

import com.vamcore.inventory.application.command.ReceiveStockCommand;
import com.vamcore.inventory.domain.event.StockAdjusted;
import com.vamcore.inventory.domain.model.InventoryMovement;
import com.vamcore.inventory.domain.model.Product;
import com.vamcore.inventory.domain.model.Stock;
import com.vamcore.inventory.domain.repository.InventoryMovementRepository;
import com.vamcore.inventory.domain.repository.ProductRepository;
import com.vamcore.inventory.domain.repository.StockRepository;
import com.vamcore.inventory.domain.valueobject.MovementType;
import com.vamcore.inventory.domain.valueobject.Quantity;
import com.vamcore.shared.domain.event.EventPublisher;
import com.vamcore.shared.domain.valueobject.TenantId;
import com.vamcore.shared.infrastructure.exception.BusinessException;
import com.vamcore.shared.infrastructure.tenant.TenantContext;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Registra una recepción de mercancía (ver flujo de sección 53:
 * Proveedor -> Purchase Order -> Receiving -> Inventory Movement -> Stock).
 * Esta versión inicial simplifica el flujo omitiendo la orden de compra
 * explícita y registrando directamente el movimiento RECEIPT.
 */
@Service
public class ReceiveStockUseCase {

    private final ProductRepository productRepository;
    private final StockRepository stockRepository;
    private final InventoryMovementRepository movementRepository;
    private final EventPublisher eventPublisher;

    public ReceiveStockUseCase(ProductRepository productRepository, StockRepository stockRepository,
                                InventoryMovementRepository movementRepository, EventPublisher eventPublisher) {
        this.productRepository = productRepository;
        this.stockRepository = stockRepository;
        this.movementRepository = movementRepository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public Stock handle(ReceiveStockCommand command) {
        TenantId tenantId = TenantContext.get();

        Product product = productRepository.findById(tenantId.value(), command.productId())
            .orElseThrow(() -> new BusinessException("PRODUCT_NOT_FOUND", "Producto no encontrado", HttpStatus.NOT_FOUND));

        Stock stock = stockRepository.findByProductAndWarehouse(tenantId.value(), command.productId(), command.warehouseId())
            .orElseGet(() -> Stock.initialize(tenantId.value(), command.productId(), command.warehouseId(), product.unitOfMeasure()));

        Quantity qty = Quantity.of(command.quantity(), product.unitOfMeasure());
        stock.increase(qty);
        Stock saved = stockRepository.save(stock);

        movementRepository.save(InventoryMovement.record(
            tenantId.value(), command.productId(), command.warehouseId(), MovementType.RECEIPT,
            qty, command.referenceType(), command.referenceId(), null
        ));

        eventPublisher.publish(new StockAdjusted(
            tenantId.value(), command.productId(), command.warehouseId(),
            MovementType.RECEIPT, qty.value(), saved.quantity().value()
        ));

        return saved;
    }
}
