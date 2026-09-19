package com.vamcore.notifications.domain.repository;

import com.vamcore.notifications.domain.model.Notification;

/**
 * Puerto de salida hacia el proveedor real de notificaciones (ver sección 39
 * del documento de arquitectura). La implementación inicial es un adaptador
 * que solo registra en log (ver infrastructure.messaging.LogNotificationSender);
 * el día que se conecte un proveedor real (SMTP, WhatsApp Business API, SMS),
 * se agrega un nuevo adaptador sin tocar el dominio ni los casos de uso.
 */
public interface NotificationSender {

    /** @return true si el envío fue exitoso. */
    boolean send(Notification notification);
}
