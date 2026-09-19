package com.vamcore.inventory.application.usecase;

import com.vamcore.inventory.application.command.ReceiveStockCommand;
import com.vamcore.inventory.domain.model.PurchaseOrder;
import com.vamcore.inventory.domain.repository.PurchaseOrderRepository;
import com.vamcore.shared.domain.valueobject.TenantId;
import com.vamcore.shared.infrastructure.exception.BusinessException;
import com.vamcore.shared.infrastructure.tenant.TenantContext;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Confirma y recibe una orden de compra (ver flujo de sección 53:
 * Purchase Order -> Receiving -> Inventory Movement -> Stock). Recibir
 * una orden reutiliza {@link ReceiveStockUseCase} para que el movimiento
 * de inventario, el Kardex y los eventos de dominio sean exactamente los
 * mismos que una recepción manual — la orden de compra es solo el
 * "por qué" formal detrás de esa recepción.
 */
@Service
public class ReceivePurchaseOrderUseCase {

    private final PurchaseOrderRepository purchaseOrderRepository;
    private final ReceiveStockUseCase receiveStockUseCase;

    public ReceivePurchaseOrderUseCase(PurchaseOrderRepository purchaseOrderRepository, ReceiveStockUseCase receiveStockUseCase) {
        this.purchaseOrderRepository = purchaseOrderRepository;
        this.receiveStockUseCase = receiveStockUseCase;
    }

    @Transactional
    public PurchaseOrder confirm(UUID purchaseOrderId) {
        TenantId tenantId = TenantContext.get();
        PurchaseOrder po = findOrThrow(tenantId.value(), purchaseOrderId);
        po.confirm();
        return purchaseOrderRepository.save(po);
    }

    @Transactional
    public PurchaseOrder receive(UUID purchaseOrderId) {
        TenantId tenantId = TenantContext.get();
        PurchaseOrder po = findOrThrow(tenantId.value(), purchaseOrderId);

        receiveStockUseCase.handle(new ReceiveStockCommand(
            po.productId(), po.warehouseId(), po.quantityOrdered(), "PURCHASE_ORDER", po.code()
        ));

        po.markReceived();
        return purchaseOrderRepository.save(po);
    }

    @Transactional
    public PurchaseOrder cancel(UUID purchaseOrderId) {
        TenantId tenantId = TenantContext.get();
        PurchaseOrder po = findOrThrow(tenantId.value(), purchaseOrderId);
        po.cancel();
        return purchaseOrderRepository.save(po);
    }

    private PurchaseOrder findOrThrow(UUID tenantId, UUID id) {
        return purchaseOrderRepository.findById(tenantId, id)
            .orElseThrow(() -> new BusinessException("PURCHASE_ORDER_NOT_FOUND", "Orden de compra no encontrada", HttpStatus.NOT_FOUND));
    }
}
