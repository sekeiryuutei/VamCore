package com.vamcore.notifications.infrastructure.messaging;

import com.vamcore.notifications.domain.model.Notification;
import com.vamcore.notifications.domain.repository.NotificationSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Implementación inicial del puerto {@link NotificationSender}: registra la
 * notificación en el log de la aplicación en vez de despacharla a un
 * proveedor externo real. Es intencional — VamCore aún no tiene credenciales
 * de SMTP/WhatsApp/SMS configuradas (ver sección 39 del documento de
 * arquitectura).
 *
 * Para conectar un proveedor real: crear un nuevo `@Component` que implemente
 * NotificationSender (ej. SmtpEmailNotificationSender) y marcarlo
 * `@Primary`, o eliminar este bean vía un profile. Ni el dominio ni los
 * casos de uso cambian.
 */
@Component
public class LogNotificationSender implements NotificationSender {

    private static final Logger log = LoggerFactory.getLogger(LogNotificationSender.class);

    @Override
    public boolean send(Notification notification) {
        log.info(
            "[NOTIFICATION -> {}] tenant={} to={} subject=\"{}\" body=\"{}\"",
            notification.channel(), notification.tenantId(), notification.recipient(),
            notification.subject(), notification.body()
        );
        return true;
    }
}
