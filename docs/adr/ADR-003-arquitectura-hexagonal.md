# ADR-003 — Arquitectura Hexagonal

**Estado:** Aprobada (propuesta inicial)

## Decisión
Cada bounded context se organiza en tres capas: `domain`, `application`, `infrastructure`.

## Razón
El dominio no debe conocer Spring MVC, JPA, PostgreSQL, brokers de mensajería, Angular
ni proveedores externos. Esto permite cambiar infraestructura sin modificar las reglas
fundamentales del negocio, y probar el dominio sin levantar el framework.

## Regla de dependencias
```
Infrastructure -> Application -> Domain
```
Nunca al revés: el dominio jamás debe depender de Spring o de PostgreSQL.

## Ver también
Sección 5 y 61 (Principio de dependencias) del documento de arquitectura.
