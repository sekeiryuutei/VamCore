package com.vamcore.logistics.application.usecase;

import com.vamcore.logistics.application.command.CreateDeliveryCommand;
import com.vamcore.logistics.domain.event.DeliveryCreated;
import com.vamcore.logistics.domain.model.Delivery;
import com.vamcore.logistics.domain.model.TrackingEvent;
import com.vamcore.logistics.domain.repository.DeliveryRepository;
import com.vamcore.logistics.domain.repository.TrackingEventRepository;
import com.vamcore.logistics.domain.valueobject.DeliveryCode;
import com.vamcore.shared.domain.event.EventPublisher;
import com.vamcore.shared.domain.valueobject.TenantId;
import com.vamcore.shared.infrastructure.tenant.TenantContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Crea una nueva entrega (ver flujo de sección 55: Order -> Preparation ->
 * Delivery). Esta versión inicial simplifica omitiendo el Order explícito
 * y creando la Delivery directamente en estado CREATED.
 */
@Service
public class CreateDeliveryUseCase {

    private final DeliveryRepository deliveryRepository;
    private final TrackingEventRepository trackingEventRepository;
    private final EventPublisher eventPublisher;

    public CreateDeliveryUseCase(DeliveryRepository deliveryRepository, TrackingEventRepository trackingEventRepository,
                                  EventPublisher eventPublisher) {
        this.deliveryRepository = deliveryRepository;
        this.trackingEventRepository = trackingEventRepository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public Delivery handle(CreateDeliveryCommand command) {
        TenantId tenantId = TenantContext.get();
        DeliveryCode deliveryCode = DeliveryCode.of(command.deliveryCode());

        Delivery delivery = Delivery.create(tenantId.value(), deliveryCode, command.customerName(), command.destinationAddress());
        Delivery saved = deliveryRepository.save(delivery);

        trackingEventRepository.save(TrackingEvent.record(tenantId.value(), saved.id(), saved.status(), "Entrega creada"));
        eventPublisher.publish(new DeliveryCreated(tenantId.value(), saved.id(), saved.deliveryCode().value()));

        return saved;
    }
}
