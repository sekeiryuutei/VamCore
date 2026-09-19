package com.vamcore.notifications.domain.valueobject;

/**
 * Canal de entrega. IN_APP se persiste como registro visible en la
 * plataforma; EMAIL se despacha a través de un NotificationSender externo
 * (ver sección 39: EmailNotificationAdapter, y luego WhatsApp/SMS/Push).
 */
public enum NotificationChannel {
    IN_APP,
    EMAIL
}
