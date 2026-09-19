package com.vamcore.notifications.domain.repository;

import com.vamcore.notifications.domain.model.Notification;

import java.util.List;
import java.util.UUID;

public interface NotificationRepository {

    Notification save(Notification notification);

    /** Más recientes primero. */
    List<Notification> findAllByTenant(UUID tenantId);
}
