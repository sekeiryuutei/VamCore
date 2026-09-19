package com.vamcore.logistics.application.usecase;

import com.vamcore.logistics.application.command.AssignDeliveryCommand;
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

@Service
public class AssignDeliveryUseCase {

    private final DeliveryRepository deliveryRepository;
    private final TrackingEventRepository trackingEventRepository;
    private final EventPublisher eventPublisher;

    public AssignDeliveryUseCase(DeliveryRepository deliveryRepository,
                                  TrackingEventRepository trackingEventRepository, EventPublisher eventPublisher) {
        this.deliveryRepository = deliveryRepository;
        this.trackingEventRepository = trackingEventRepository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public Delivery handle(AssignDeliveryCommand command) {
        TenantId tenantId = TenantContext.get();

        Delivery delivery = deliveryRepository.findById(tenantId.value(), command.deliveryId())
            .orElseThrow(() -> new BusinessException("DELIVERY_NOT_FOUND", "Entrega no encontrada", HttpStatus.NOT_FOUND));

        DeliveryStatus previousStatus = delivery.status();
        delivery.assignTo(command.driverId(), command.vehicleId());
        Delivery saved = deliveryRepository.save(delivery);

        trackingEventRepository.save(TrackingEvent.record(tenantId.value(), saved.id(), DeliveryStatus.ASSIGNED, "Conductor asignado"));
        eventPublisher.publish(new DeliveryStatusChanged(tenantId.value(), saved.id(), previousStatus, DeliveryStatus.ASSIGNED));

        return saved;
    }
}
