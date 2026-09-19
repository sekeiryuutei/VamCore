# ADR-005 — Multi-tenancy

**Estado:** Aprobada (propuesta inicial)

## Decisión
La plataforma es multi-tenant desde el principio. Todo registro perteneciente a una
organización debe estar asociado directa o indirectamente con un `tenant_id`.

## Regla crítica de seguridad
El backend determina el tenant a partir del contexto autenticado (claim del JWT).
**Nunca** se debe confiar en un `tenantId` recibido como query param o en el body de
la request para decidir qué datos mostrar o modificar.

## Implementación
Ver `shared.infrastructure.tenant.TenantContext` y
`identity.infrastructure.security.JwtAuthenticationFilter` en el backend.

## Ver también
Sección 7 del documento de arquitectura.
