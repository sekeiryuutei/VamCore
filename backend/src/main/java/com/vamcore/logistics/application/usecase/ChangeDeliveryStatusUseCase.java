package com.vamcore.logistics.application.usecase;

import com.vamcore.logistics.application.command.ChangeDeliveryStatusCommand;
import com.vamcore.logistics.domain.event.DeliveryStatusChanged;
import com.vamcore.logistics.domain.model.Delivery;
import com.vamcore.logistics.domain.model.TrackingEvent;
import com.vamcore.logistics.domain.repository.DeliveryRepository;
import com.vamcore.logistics.domain.repository.TrackingEventRepository;
import com.vamcore.logistics.domain.valueobject.DeliveryStatus;
import com.vamcore.shared.domain.event.EventPublisher;
import com.vamcore.shared.domain.valueobject.TenantId;
import com.vamcore.shared.infrastructure.exception.BusinessException;
import com.vamcore.shared.infrastructure.tenant.TenantContext;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Aplica una transición de la máquina de estados de la entrega (sección 20).
 * Cada transición queda registrada en el historial de tracking (sección 21)
 * para poder reconstruir el recorrido lógico completo.
 */
@Service
public class ChangeDeliveryStatusUseCase {

    private final DeliveryRepository deliveryRepository;
    private final TrackingEventRepository trackingEventRepository;
    private final EventPublisher eventPublisher;

    public ChangeDeliveryStatusUseCase(DeliveryRepository deliveryRepository,
                                        TrackingEventRepository trackingEventRepository, EventPublisher eventPublisher) {
        this.deliveryRepository = deliveryRepository;
        this.trackingEventRepository = trackingEventRepository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public Delivery handle(ChangeDeliveryStatusCommand command) {
        TenantId tenantId = TenantContext.get();

        Delivery delivery = deliveryRepository.findById(tenantId.value(), command.deliveryId())
            .orElseThrow(() -> new BusinessException("DELIVERY_NOT_FOUND", "Entrega no encontrada", HttpStatus.NOT_FOUND));

        DeliveryStatus previousStatus = delivery.status();
        DeliveryStatus newStatus = DeliveryStatus.valueOf(command.newStatus().toUpperCase());

        delivery.changeStatus(newStatus);
        Delivery saved = deliveryRepository.save(delivery);

        trackingEventRepository.save(TrackingEvent.record(tenantId.value(), saved.id(), newStatus, command.notes()));
        eventPublisher.publish(new DeliveryStatusChanged(tenantId.value(), saved.id(), previousStatus, newStatus));

        return saved;
    }
}
