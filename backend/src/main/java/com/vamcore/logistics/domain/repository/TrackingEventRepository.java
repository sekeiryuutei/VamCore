package com.vamcore.logistics.domain.repository;

import com.vamcore.logistics.domain.model.TrackingEvent;

import java.util.List;
import java.util.UUID;

public interface TrackingEventRepository {

    TrackingEvent save(TrackingEvent event);

    /** GetDeliveryTracking (ver sección 25): recorrido completo, más reciente primero. */
    List<TrackingEvent> findByDelivery(UUID tenantId, UUID deliveryId);
}
