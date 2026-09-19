package com.vamcore.shared.infrastructure.messaging;

import com.vamcore.shared.domain.event.DomainEvent;
import com.vamcore.shared.domain.event.EventPublisher;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

/**
 * Implementación inicial (Fase 0-1) del puerto EventPublisher.
 *
 * Publica los eventos de dominio en memoria usando el mecanismo de eventos
 * de Spring. Cuando exista una necesidad real de desacoplar procesos o
 * escalar horizontalmente, este adaptador se reemplaza por uno que publique
 * a un Message Broker (Kafka/RabbitMQ) SIN que el dominio ni la capa de
 * aplicación se enteren del cambio (ver sección 45 del doc de arquitectura).
 */
@Component
public class InProcessEventPublisher implements EventPublisher {

    private final ApplicationEventPublisher springPublisher;

    public InProcessEventPublisher(ApplicationEventPublisher springPublisher) {
        this.springPublisher = springPublisher;
    }

    @Override
    public void publish(DomainEvent event) {
        springPublisher.publishEvent(event);
    }
}
