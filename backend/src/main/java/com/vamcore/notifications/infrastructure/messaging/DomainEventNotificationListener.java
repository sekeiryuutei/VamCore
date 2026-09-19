package com.vamcore.notifications.infrastructure.messaging;

import com.vamcore.assets.domain.event.AssetStatusChanged;
import com.vamcore.assets.domain.valueobject.AssetStatus;
import com.vamcore.inventory.domain.event.StockAdjusted;
import com.vamcore.logistics.domain.event.DeliveryStatusChanged;
import com.vamcore.logistics.domain.valueobject.DeliveryStatus;
import com.vamcore.notifications.application.usecase.CreateNotificationUseCase;
import com.vamcore.notifications.domain.valueobject.NotificationChannel;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * Ejemplo de reacción transversal a eventos de dominio (ver sección 39 del
 * documento de arquitectura). Este listener SÍ conoce los eventos concretos
 * de logistics/assets/inventory (a diferencia de audit, que reacciona
 * genéricamente a cualquier DomainEvent) porque necesita construir un
 * mensaje legible para un humano — esa es la dirección correcta de
 * dependencia: los módulos de negocio no conocen `notifications`, pero
 * `notifications` sí puede conocer sus eventos publicados.
 */
@Component
public class DomainEventNotificationListener {

    private final CreateNotificationUseCase createNotificationUseCase;

    public DomainEventNotificationListener(CreateNotificationUseCase createNotificationUseCase) {
        this.createNotificationUseCase = createNotificationUseCase;
    }

    /** Notifica cuando una entrega llega a un estado relevante para el cliente. */
    @Async
    @EventListener
    public void onDeliveryStatusChanged(DeliveryStatusChanged event) {
        if (event.newStatus() != DeliveryStatus.DELIVERED && event.newStatus() != DeliveryStatus.FAILED) {
            return;
        }
        String subject = "Entrega " + event.deliveryId() + " -> " + event.newStatus();
        String body = "La entrega cambió de " + event.previousStatus() + " a " + event.newStatus() + ".";
        createNotificationUseCase.handle(event.tenantId(), NotificationChannel.IN_APP, null, subject, body);
    }

    /** Notifica cuando un activo se da de baja definitivamente. */
    @Async
    @EventListener
    public void onAssetStatusChanged(AssetStatusChanged event) {
        if (event.newStatus() != AssetStatus.DISPOSED) {
            return;
        }
        String subject = "Activo " + event.assetId() + " dado de baja";
        String body = "El activo pasó de " + event.previousStatus() + " a DISPOSED.";
        createNotificationUseCase.handle(event.tenantId(), NotificationChannel.IN_APP, null, subject, body);
    }

    /**
     * Notifica cuando un movimiento deja el stock en cero o menos, análogo
     * al ejemplo "StockBelowMinimum -> EmailNotificationAdapter" de la
     * sección 39 (aquí simplificado a "se agotó" por no tener aún un
     * mínimo configurable por producto).
     */
    @Async
    @EventListener
    public void onStockAdjusted(StockAdjusted event) {
        if (event.resultingStock().signum() > 0) {
            return;
        }
        String subject = "Stock agotado: producto " + event.productId();
        String body = "El producto " + event.productId() + " llegó a " + event.resultingStock()
            + " unidades en la bodega " + event.warehouseId() + ".";
        createNotificationUseCase.handle(event.tenantId(), NotificationChannel.IN_APP, null, subject, body);
    }
}
