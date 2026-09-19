# Architecture Decision Records (ADR)

Este directorio contiene las decisiones arquitectónicas clave de VamCore,
extraídas del "Documento de Arquitectura Inicial — Plataforma Empresarial CodeVam".

| ID | Decisión | Estado |
|---|---|---|
| [ADR-001](./ADR-001-monolito-modular.md) | Monolito modular inicial | Aprobada propuesta |
| [ADR-002](./ADR-002-ddd.md) | Domain-Driven Design | Aprobada propuesta |
| [ADR-003](./ADR-003-arquitectura-hexagonal.md) | Arquitectura Hexagonal | Aprobada propuesta |
| [ADR-004](./ADR-004-postgresql.md) | PostgreSQL | Aprobada propuesta |
| [ADR-005](./ADR-005-multitenancy.md) | Multi-tenancy | Aprobada propuesta |

Decisiones pendientes (ver sección 64 del documento de arquitectura completo en
`docs/architecture/`): JWT vs OAuth2/OIDC, estrategia exacta de aislamiento
multi-tenant, valoración de inventario (FIFO/FEFO), reglas contables de
depreciación, proveedor de mapas/GPS, proveedor de cloud/almacenamiento/broker.

Cuando se tome una nueva decisión arquitectónica relevante, agregar un archivo
`ADR-0XX-titulo.md` siguiendo el mismo formato (Estado / Decisión / Razón /
Consecuencia / Ver también) y añadirlo a esta tabla.
