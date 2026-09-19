# ADR-002 — Domain-Driven Design

**Estado:** Aprobada (propuesta inicial)

## Decisión
Usar DDD para identificar y separar los dominios del negocio en bounded contexts:
Identity & Access, Organization, Inventory, Fixed Assets, Logistics, Notification,
File Management, Audit, Reporting.

## Razón
Un bounded context representa un límite de responsabilidad y significado del negocio,
no necesariamente un futuro microservicio. Permite razonar sobre el dominio de forma
independiente del framework técnico.

## Ver también
Sección 4 y 8 (Bounded Context Map) del documento de arquitectura.
