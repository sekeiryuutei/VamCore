package com.vamcore.notifications.domain.model;

import com.vamcore.notifications.domain.valueobject.NotificationChannel;
import com.vamcore.notifications.domain.valueobject.NotificationStatus;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Notificación generada como reacción a un Domain Event de otro módulo
 * (ver sección 39 del documento de arquitectura: "Domain Event -> Notification
 * Handler -> Notification Service -> Notification Provider"). VamCore no
 * conoce el proveedor externo concreto; solo conoce el puerto
 * {@link com.vamcore.notifications.domain.repository.NotificationSender}.
 */
public class Notification {

    private final UUID id;
    private final UUID tenantId;
    private final NotificationChannel channel;
    private final String recipient;
    private final String subject;
    private final String body;
    private NotificationStatus status;
    private final Instant createdAt;
    private Instant sentAt;

    private Notification(UUID id, UUID tenantId, NotificationChannel channel, String recipient, String subject,
                          String body, NotificationStatus status, Instant createdAt, Instant sentAt) {
        this.id = id;
        this.tenantId = tenantId;
        this.channel = channel;
        this.recipient = recipient;
        this.subject = subject;
        this.body = body;
        this.status = status;
        this.createdAt = createdAt;
        this.sentAt = sentAt;
    }

    public static Notification create(UUID tenantId, NotificationChannel channel, String recipient,
                                       String subject, String body) {
        Objects.requireNonNull(tenantId, "tenantId es obligatorio");
        Objects.requireNonNull(subject, "subject es obligatorio");
        return new Notification(UUID.randomUUID(), tenantId, channel, recipient, subject, body,
            NotificationStatus.PENDING, Instant.now(), null);
    }

    public static Notification reconstitute(UUID id, UUID tenantId, NotificationChannel channel, String recipient,
                                             String subject, String body, NotificationStatus status,
                                             Instant createdAt, Instant sentAt) {
        return new Notification(id, tenantId, channel, recipient, subject, body, status, createdAt, sentAt);
    }

    public void markSent() {
        this.status = NotificationStatus.SENT;
        this.sentAt = Instant.now();
    }

    public void markFailed() {
        this.status = NotificationStatus.FAILED;
    }

    public UUID id() { return id; }
    public UUID tenantId() { return tenantId; }
    public NotificationChannel channel() { return channel; }
    public String recipient() { return recipient; }
    public String subject() { return subject; }
    public String body() { return body; }
    public NotificationStatus status() { return status; }
    public Instant createdAt() { return createdAt; }
    public Instant sentAt() { return sentAt; }
}
