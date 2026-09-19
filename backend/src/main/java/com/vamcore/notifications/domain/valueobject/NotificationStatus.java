package com.vamcore.notifications.domain.valueobject;

/** Estado de envío de una notificación (ver sección 39 del documento de arquitectura). */
public enum NotificationStatus {
    PENDING,
    SENT,
    FAILED
}
