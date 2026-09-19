package com.vamcore.logistics.application.usecase;

import com.vamcore.logistics.domain.model.TrackingEvent;
import com.vamcore.logistics.domain.repository.TrackingEventRepository;
import com.vamcore.shared.domain.valueobject.TenantId;
import com.vamcore.shared.infrastructure.tenant.TenantContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/** GetDeliveryTracking (ver sección 25 del documento de arquitectura). */
@Service
public class GetDeliveryTrackingUseCase {

    private final TrackingEventRepository trackingEventRepository;

    public GetDeliveryTrackingUseCase(TrackingEventRepository trackingEventRepository) {
        this.trackingEventRepository = trackingEventRepository;
    }

    @Transactional(readOnly = true)
    public List<TrackingEvent> handle(UUID deliveryId) {
        TenantId tenantId = TenantContext.get();
        return trackingEventRepository.findByDelivery(tenantId.value(), deliveryId);
    }
}
