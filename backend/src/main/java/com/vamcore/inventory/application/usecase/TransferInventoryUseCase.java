package com.vamcore.inventory.application.usecase;

import com.vamcore.inventory.application.command.TransferInventoryCommand;
import com.vamcore.inventory.domain.event.InventoryTransferred;
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

import java.util.UUID;

/**
 * Transferencia de inventario entre dos bodegas del mismo tenant
 * (ver ejemplo completo de la sección 62 del documento de arquitectura:
 * "Un administrador transfiere 20 unidades de Cali a Bogotá").
 *
 * Genera dos movimientos ligados (TRANSFER_OUT en origen, TRANSFER_IN en
 * destino) dentro de la misma transacción, para que el Kardex de ambas
 * bodegas quede consistente.
 */
@Service
public class TransferInventoryUseCase {

    private final ProductRepository productRepository;
    private final StockRepository stockRepository;
    private final InventoryMovementRepository movementRepository;
    private final EventPublisher eventPublisher;

    public TransferInventoryUseCase(ProductRepository productRepository, StockRepository stockRepository,
                                     InventoryMovementRepository movementRepository, EventPublisher eventPublisher) {
        this.productRepository = productRepository;
        this.stockRepository = stockRepository;
        this.movementRepository = movementRepository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public void handle(TransferInventoryCommand command) {
        TenantId tenantId = TenantContext.get();

        if (command.sourceWarehouseId().equals(command.targetWarehouseId())) {
            throw new BusinessException(
                "INVALID_TRANSFER_STATE",
                "La bodega origen y destino no pueden ser la misma",
                HttpStatus.BAD_REQUEST
            );
        }

        Product product = productRepository.findById(tenantId.value(), command.productId())
            .orElseThrow(() -> new BusinessException("PRODUCT_NOT_FOUND", "Producto no encontrado", HttpStatus.NOT_FOUND));

        Stock sourceStock = stockRepository.findByProductAndWarehouse(tenantId.value(), command.productId(), command.sourceWarehouseId())
            .orElseThrow(() -> new BusinessException(
                "INSUFFICIENT_STOCK", "No hay stock registrado en la bodega origen", HttpStatus.CONFLICT));

        Quantity qty = Quantity.of(command.quantity(), product.unitOfMeasure());

        // 1) Salida de la bodega origen (valida disponibilidad, ver Stock.decrease)
        sourceStock.decrease(qty);
        stockRepository.save(sourceStock);
        movementRepository.save(InventoryMovement.record(
            tenantId.value(), command.productId(), command.sourceWarehouseId(), MovementType.TRANSFER_OUT,
            qty, "INVENTORY_TRANSFER", null, null
        ));

        // 2) Entrada en la bodega destino
        Stock targetStock = stockRepository.findByProductAndWarehouse(tenantId.value(), command.productId(), command.targetWarehouseId())
            .orElseGet(() -> Stock.initialize(tenantId.value(), command.productId(), command.targetWarehouseId(), product.unitOfMeasure()));
        targetStock.increase(qty);
        stockRepository.save(targetStock);
        movementRepository.save(InventoryMovement.record(
            tenantId.value(), command.productId(), command.targetWarehouseId(), MovementType.TRANSFER_IN,
            qty, "INVENTORY_TRANSFER", null, null
        ));

        eventPublisher.publish(new InventoryTransferred(
            tenantId.value(), command.productId(), command.sourceWarehouseId(), command.targetWarehouseId(), qty.value()
        ));
    }
}
