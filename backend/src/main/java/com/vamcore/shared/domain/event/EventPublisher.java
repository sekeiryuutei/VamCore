package com.vamcore.shared.domain.event;

/**
 * Puerto de salida para publicar eventos de dominio.
 *
 * El dominio y la capa de aplicación dependen únicamente de esta interfaz,
 * nunca de Spring ApplicationEventPublisher, Kafka, RabbitMQ, etc.
 * (ver ADR sección 45 "Estrategia de eventos" del documento de arquitectura).
 *
 * Implementación inicial (Fase 0-1): un adaptador in-process basado en
 * Spring ApplicationEventPublisher (ver infrastructure.messaging en cada módulo
 * o el adaptador compartido en shared.infrastructure).
 *
 * Implementación futura: un adaptador que publique a un Message Broker
 * (Kafka/RabbitMQ) sin que el dominio se entere del cambio.
 */
public interface EventPublisher {

    void publish(DomainEvent event);
}
