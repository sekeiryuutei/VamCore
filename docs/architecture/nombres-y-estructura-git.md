# IDENTIDAD DEL PRODUCTO Y ESTRUCTURA DEL REPOSITORIO

La plataforma completa se denominará:

**VamCore**

VamCore será la plataforma empresarial principal desarrollada por CodeVam.

Dentro de VamCore existirán inicialmente tres productos/módulos principales:

- **VamStock** → Sistema de control e inventarios empresariales.
- **VamAsset** → Sistema de gestión y control de activos fijos.
- **VamTrack** → Plataforma logística y de seguimiento de entregas.

La arquitectura debe tratar VamCore como una única plataforma modular, no como tres aplicaciones independientes.

## REPOSITORIO GIT

Inicialmente utilizar un único repositorio Git:

```text
vamcore
```

Estructura inicial:

```text
vamcore/
├── backend/
├── frontend/
├── docs/
├── infrastructure/
├── scripts/
└── README.md
```

El backend Spring Boot deberá organizarse por bounded contexts/módulos:

```text
backend/
└── src/
    └── main/
        └── java/
            └── com/
                └── vamcore/
                    ├── identity/
                    ├── organization/
                    ├── inventory/
                    ├── assets/
                    ├── logistics/
                    ├── audit/
                    ├── files/
                    ├── notifications/
                    └── reporting/
```

Los nombres comerciales serán:

```text
VamCore
├── VamStock
├── VamAsset
└── VamTrack
```

No crear repositorios Git independientes para VamStock, VamAsset y VamTrack en esta primera etapa.

La separación futura en repositorios independientes solamente deberá realizarse si un módulo se convierte realmente en un microservicio independiente y existe una justificación arquitectónica para ello.

El código deberá mantener límites de dominio claros para que cualquiera de los tres módulos pueda ser extraído posteriormente sin tener que reconstruir completamente la aplicación.

Toda la documentación arquitectónica, ADR, diagramas, contratos API, decisiones técnicas y documentación del dominio deberá mantenerse versionada dentro del mismo repositorio.

El README principal deberá presentar VamCore como la plataforma y explicar sus tres productos:

**VamStock — Inventory Management**

**VamAsset — Fixed Asset Management**

**VamTrack — Logistics & Delivery Tracking**

La solución utilizará:

- Java
- Spring Boot
- Angular
- PostgreSQL
- Arquitectura Hexagonal
- DDD
- Monolito Modular inicialmente
- Domain Events
- CQRS selectivo
- Docker

Diseñar desde el inicio para que VamCore pueda evolucionar posteriormente hacia una arquitectura distribuida/microservicios sin introducir microservicios prematuramente.