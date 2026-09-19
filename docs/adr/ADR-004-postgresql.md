# ADR-004 — Base de datos

**Estado:** Aprobada (propuesta inicial)

## Decisión
PostgreSQL como motor de base de datos, comenzando con una única base física.

## Razón
Los módulos tendrán límites lógicos claros (prefijos de tabla por módulo, ej.
`inventory_*`, `asset_*`, `logistics_*`) aunque compartan la misma instancia física.
Si en el futuro se extraen microservicios, cada uno podrá migrar a su propia base de datos.

## Ver también
Sección 6 del documento de arquitectura.
