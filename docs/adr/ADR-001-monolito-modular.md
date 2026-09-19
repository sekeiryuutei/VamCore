# ADR-001 — Arquitectura modular

**Estado:** Aprobada (propuesta inicial)

## Decisión
Utilizar un monolito modular inicialmente, en lugar de microservicios desde el día uno.

## Razón
Los tres dominios (Inventory/VamStock, Assets/VamAsset, Logistics/VamTrack) están en etapa
inicial y no existe información suficiente sobre volumen de usuarios, volumen transaccional,
necesidades reales de escalamiento, límites organizacionales ni necesidades de despliegue
independiente. Introducir microservicios desde el comienzo agregaría complejidad operacional
sin garantizar beneficios.

## Consecuencia
Los módulos deben tener límites estrictos. Por ejemplo, `Inventory` NO puede acceder
directamente a tablas internas de `Assets`. La comunicación entre módulos debe realizarse
mediante interfaces, casos de uso, eventos y contratos explícitos.

## Ver también
Sección 3 y 60 (Riesgo 2 — Acoplamiento entre módulos) del documento de arquitectura.
