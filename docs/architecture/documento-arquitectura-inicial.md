# DOCUMENTO DE ARQUITECTURA INICIAL

## Plataforma Empresarial de Inventarios, Activos Fijos y Logística

**Versión:** 1.0  
**Estado:** Propuesta arquitectónica inicial  
**Fecha:** Septiembre de 2026  
**Arquitectura objetivo:** Monolito modular evolutivo hacia microservicios  
**Enfoques:** DDD + Arquitectura Hexagonal + Event-Driven + CQRS selectivo  
**Backend:** Java + Spring Boot  
**Frontend:** Angular  
**Base de datos:** PostgreSQL  

---

# 1. RESUMEN EJECUTIVO

La plataforma tiene como objetivo proporcionar una solución empresarial integral para gestionar tres áreas principales:

1. Control e inventarios empresariales.
2. Gestión y control de activos fijos.
3. Gestión logística y seguimiento de entregas.

La plataforma será diseñada desde el inicio como un producto **multiempresa, multisede, modular y escalable**.

No se desarrollarán tres aplicaciones completamente independientes. Se construirá una plataforma común sobre la cual se implementarán diferentes dominios de negocio.

La arquitectura inicial será un:

> **Monolito modular con límites de dominio explícitos y arquitectura hexagonal.**

Esto permite comenzar con una infraestructura relativamente sencilla, reduciendo costos y complejidad operacional, pero manteniendo los límites necesarios para posteriormente extraer determinados módulos como microservicios.

La arquitectura deberá evitar dos extremos:

**Aplicación monolítica tradicional**

```text
Controller
   ↓
Service
   ↓
Repository
   ↓
Database
```

y:

**Microservicios prematuros**

```text
20 microservicios
20 bases de datos
Kafka
Kubernetes
API Gateway
Service Mesh
...
```

sin que exista una necesidad real.

La estrategia propuesta es:

```text
                PLATAFORMA EMPRESARIAL
                         │
        ┌────────────────┼────────────────┐
        │                │                │
   INVENTARIOS       ACTIVOS FIJOS    LOGÍSTICA
        │                │                │
        └────────────────┼────────────────┘
                         │
                  CAPACIDADES CORE
                         │
        ┌────────────────┼─────────────────┐
        │                │                 │
     Identity         Audit             Files
     Tenancy       Notifications      Configuration
```

---

# 2. OBJETIVOS

## 2.1 Objetivo principal

Construir una plataforma empresarial comercializable que permita administrar operaciones de inventario, activos y logística de diferentes organizaciones desde una misma solución.

## 2.2 Objetivos técnicos

La solución deberá:

- Ser multi-tenant.
- Soportar múltiples empresas.
- Soportar múltiples sedes por empresa.
- Soportar múltiples usuarios.
- Tener autorización granular.
- Mantener trazabilidad.
- Ser extensible.
- Permitir integración con terceros.
- Tener APIs públicas/versionadas.
- Ser testeable.
- Ser observable.
- Poder crecer horizontalmente.
- Poder evolucionar a microservicios.

---

# 3. PRINCIPIOS ARQUITECTÓNICOS

## ADR-001 — Arquitectura modular

**Decisión:** Utilizar un monolito modular inicialmente.

### Razón

Los tres dominios todavía estarán en una etapa inicial y no existe información suficiente sobre:

- volumen de usuarios;
- volumen transaccional;
- necesidades reales de escalamiento;
- límites organizacionales;
- necesidades de despliegue independiente.

Por lo tanto, introducir microservicios desde el comienzo agregaría complejidad operacional sin garantizar beneficios.

### Consecuencia

Los módulos deben tener límites estrictos.

Por ejemplo:

```text
Inventory
    ↓
NO puede acceder directamente a tablas internas de Assets.
```

La comunicación debe realizarse mediante:

- interfaces;
- casos de uso;
- eventos;
- contratos explícitos.

---

# 4. ADR-002 — Domain-Driven Design

Se utilizará DDD para identificar y separar los diferentes dominios del negocio.

Los principales bounded contexts serán:

```text
Identity & Access
Organization
Inventory
Fixed Assets
Logistics
Notification
File Management
Audit
Reporting
```

No todos necesariamente se convertirán en microservicios.

Un bounded context representa principalmente un **límite de responsabilidad y significado del negocio**.

---

# 5. ADR-003 — Arquitectura Hexagonal

Cada dominio utilizará:

```text
             ┌─────────────────────┐
             │       DOMAIN        │
             │                     │
             │ Entities            │
             │ Aggregates          │
             │ Value Objects       │
             │ Domain Services     │
             │ Domain Events       │
             └──────────┬──────────┘
                        │
             ┌──────────┴──────────┐
             │    APPLICATION      │
             │                     │
             │ Use Cases           │
             │ Commands            │
             │ Queries             │
             └──────────┬──────────┘
                        │
          ┌─────────────┴─────────────┐
          │                           │
     ADAPTERS IN                ADAPTERS OUT
          │                           │
     REST API                    PostgreSQL
     Web                    Email Provider
     Mobile                  Message Broker
                              File Storage
```

El dominio no conocerá:

- Spring MVC;
- JPA;
- PostgreSQL;
- RabbitMQ;
- Kafka;
- Angular;
- proveedores externos.

Esto permite cambiar infraestructura sin modificar las reglas fundamentales del negocio.

---

# 6. ADR-004 — Base de datos

**Decisión:** PostgreSQL.

Se utilizará inicialmente una base de datos PostgreSQL.

La aplicación podrá comenzar utilizando una única base de datos física.

Los módulos tendrán límites lógicos claros.

Ejemplo:

```text
inventory_product
inventory_stock
inventory_movement

asset_asset
asset_assignment
asset_maintenance

logistics_order
logistics_delivery
logistics_tracking
```

Posteriormente, si se extraen microservicios, cada servicio podrá migrar hacia su propia base de datos.

---

# 7. ADR-005 — Multi-tenancy

La plataforma será multi-tenant desde el principio.

Modelo inicial:

```text
Tenant
 │
 ├── Users
 ├── Roles
 ├── Permissions
 ├── Branches
 │      ├── Warehouses
 │      ├── Assets
 │      └── Operations
 │
 ├── Inventory
 ├── Assets
 └── Logistics
```

Cada registro perteneciente a una organización deberá estar asociado directa o indirectamente con un `tenant_id`.

Ejemplo:

```text
Product
 ├── id
 ├── tenant_id
 ├── sku
 ├── name
 └── status
```

El backend determinará el tenant a partir del contexto autenticado.

Nunca deberá confiar en:

```http
GET /products?tenantId=123
```

para determinar el acceso.

El tenant será derivado del usuario autenticado.

---

# 8. BOUNDED CONTEXT MAP

## 8.1 Vista general

```text
                    ┌──────────────────┐
                    │ Identity &       │
                    │ Access           │
                    └────────┬─────────┘
                             │
                             ▼
                    ┌──────────────────┐
                    │ Organization     │
                    │ & Tenancy        │
                    └────────┬─────────┘
                             │
          ┌──────────────────┼──────────────────┐
          │                  │                  │
          ▼                  ▼                  ▼
   ┌─────────────┐    ┌──────────────┐   ┌──────────────┐
   │  Inventory  │    │ Fixed Assets │   │  Logistics   │
   └──────┬──────┘    └──────┬───────┘   └──────┬───────┘
          │                  │                  │
          └──────────────────┼──────────────────┘
                             │
             ┌───────────────┼───────────────┐
             ▼               ▼               ▼
        ┌──────────┐   ┌────────────┐   ┌──────────────┐
        │  Audit   │   │  Files     │   │Notification  │
        └──────────┘   └────────────┘   └──────────────┘
```

---

# 9. BOUNDED CONTEXT: IDENTITY & ACCESS

Responsabilidad:

Administrar identidad y autorización.

## Entidades principales

```text
User
Role
Permission
Session
Credential
```

## Responsabilidades

- autenticación;
- autorización;
- contraseñas;
- sesiones;
- roles;
- permisos;
- recuperación;
- bloqueo;
- activación/desactivación.

## Ejemplo

```text
User
 ├── Roles
 │     ├── ADMIN
 │     └── INVENTORY_MANAGER
 │
 └── Tenant
```

---

# 10. BOUNDED CONTEXT: ORGANIZATION

Responsabilidad:

Representar la estructura empresarial.

## Entidades

```text
Tenant
Organization
Branch
Department
Location
```

Ejemplo:

```text
Empresa ABC
│
├── Cali
│   ├── Administración
│   ├── Bodega principal
│   └── Operaciones
│
└── Bogotá
    ├── Administración
    └── Bodega
```

Este contexto NO debe contener lógica de inventario.

---

# 11. BOUNDED CONTEXT: INVENTORY

Responsabilidad:

Controlar existencias físicas y movimientos de inventario.

## Agregados principales

```text
Product
Inventory
InventoryTransfer
StockCount
```

## Entidades

```text
Product
Category
Warehouse
StorageLocation
Stock
InventoryMovement
Batch
SerialNumber
PurchaseOrder
Receiving
Transfer
StockCount
```

## Value Objects

```text
SKU
Barcode
Money
Quantity
SerialNumber
BatchNumber
Address
```

---

# 12. AGREGADO PRODUCT

Conceptualmente:

```text
Product
│
├── ProductId
├── SKU
├── Name
├── Description
├── Category
├── UnitOfMeasure
├── ProductType
├── TrackingType
└── InventoryRules
```

TrackingType:

```text
NONE
BATCH
SERIALIZED
```

Esto permite determinar cómo se controla físicamente el producto.

---

# 13. AGREGADO INVENTORY

El inventario no deberá depender únicamente de un campo:

```text
stock = 50
```

Debe existir un historial de movimientos.

```text
Inventory
    │
    └── Stock
          │
          └── InventoryMovement
```

Ejemplo:

```text
Ingreso +100
Salida -20
Transferencia -10
Ajuste +5
----------------
Stock = 75
```

Esto permite construir el Kardex.

---

# 14. LEDGER DE INVENTARIO

El movimiento de inventario funcionará conceptualmente como un ledger.

```text
InventoryMovement

id
tenant_id
product_id
location_id
movement_type
quantity
reference_type
reference_id
created_by
created_at
```

Tipos:

```text
RECEIPT
SALE
ISSUE
TRANSFER_OUT
TRANSFER_IN
ADJUSTMENT
RETURN
DAMAGE
RESERVATION
RELEASE
```

El sistema deberá poder reconstruir la historia del inventario.

---

# 15. BOUNDED CONTEXT: FIXED ASSETS

Responsabilidad:

Administrar activos de propiedad o uso empresarial durante su ciclo de vida.

## Agregado principal

```text
Asset
```

## Entidades

```text
Asset
AssetCategory
AssetAssignment
AssetTransfer
Maintenance
Depreciation
Disposal
```

## Value Objects

```text
AssetCode
SerialNumber
Money
DepreciationValue
UsefulLife
```

---

# 16. CICLO DE VIDA DEL ACTIVO

```text
ACQUIRED
   ↓
IN_STORAGE
   ↓
ASSIGNED
   ↓
IN_USE
   ↓
IN_MAINTENANCE
   ↓
IN_USE
   ↓
RETIRED
   ↓
DISPOSED
```

No cualquier transición será válida.

Por ejemplo:

```text
DISPOSED → IN_USE
```

deberá estar prohibido salvo que exista una operación explícita de reversión autorizada.

---

# 17. RELACIÓN INVENTORY ↔ ASSETS

No considerar:

```text
Product = Asset
```

Son conceptos diferentes.

Ejemplo:

Inventario:

```text
Laptop Lenovo
Cantidad: 10
```

Activos:

```text
Laptop #001
Serial ABC123
Asignada a Juan

Laptop #002
Serial ABC124
Asignada a María
```

La compra/recepción puede eventualmente producir activos, pero debe existir una operación explícita que cree o registre el activo.

Esto evita acoplar artificialmente ambos dominios.

---

# 18. BOUNDED CONTEXT: LOGISTICS

Responsabilidad:

Administrar pedidos, entregas y trazabilidad logística.

## Agregados

```text
Order
Delivery
Route
Vehicle
```

## Entidades

```text
Customer
Address
Package
Driver
TrackingEvent
ProofOfDelivery
```

---

# 19. AGREGADO DELIVERY

```text
Delivery
│
├── DeliveryId
├── DeliveryCode
├── OrderReference
├── Origin
├── Destination
├── Customer
├── DeliveryWindow
├── Priority
├── Status
├── Driver
├── Vehicle
└── Packages
```

---

# 20. DELIVERY STATE MACHINE

Estados:

```text
CREATED
    ↓
CONFIRMED
    ↓
PREPARING
    ↓
READY
    ↓
ASSIGNED
    ↓
DISPATCHED
    ↓
IN_TRANSIT
    ↓
DELIVERED
```

Flujos alternativos:

```text
IN_TRANSIT
    ↓
FAILED
    ↓
RETURNED
```

o:

```text
CREATED
    ↓
CANCELLED
```

---

# 21. TRACKING

El estado actual no reemplaza el historial.

Debe existir:

```text
Delivery
    │
    └── TrackingEvents
```

Ejemplo:

```text
09:00 CREATED
09:15 ASSIGNED
10:00 DISPATCHED
10:30 IN_TRANSIT
11:20 ARRIVED
11:25 DELIVERED
```

El sistema podrá reconstruir el recorrido lógico de la entrega.

---

# 22. DOMAIN EVENTS

Los eventos representan hechos que ya ocurrieron.

Ejemplos:

```text
ProductCreated
StockAdjusted
InventoryTransferred

AssetAcquired
AssetAssigned
AssetTransferred
AssetMaintenanceScheduled
AssetDisposed

OrderCreated
DeliveryCreated
DeliveryAssigned
DeliveryDispatched
DeliveryDelivered
DeliveryFailed
```

---

# 23. EVENTO DELIVERY DELIVERED

Cuando ocurre:

```text
DeliveryDelivered
```

otros componentes pueden reaccionar:

```text
             DeliveryDelivered
                     │
        ┌────────────┼─────────────┐
        ▼            ▼             ▼
     Audit      Notification     Reporting
        │            │             │
        ▼            ▼             ▼
     Registro      Email       Métricas
```

Delivery no necesita conocer la implementación interna de cada componente.

---

# 24. COMMANDS

Los Commands representan intención.

Ejemplos:

```text
CreateProduct
AdjustStock
TransferInventory
PerformStockCount

CreateAsset
AssignAsset
TransferAsset
ScheduleMaintenance
DisposeAsset

CreateDelivery
AssignDelivery
DispatchDelivery
ConfirmDelivery
FailDelivery
```

Un command normalmente produce una operación de negocio.

---

# 25. QUERIES

Las Queries representan consultas.

Ejemplos:

```text
GetProduct
GetInventory
GetKardex
GetStockDashboard

GetAsset
GetAssetHistory
GetDepreciationReport

GetDelivery
GetDeliveryTracking
GetLogisticsDashboard
```

Las consultas no deberían modificar el estado del sistema.

---

# 26. CQRS

CQRS será utilizado de forma selectiva.

No se separará físicamente la base de datos desde el inicio.

Conceptualmente:

```text
             API
              │
       ┌──────┴──────┐
       ▼             ▼
   COMMAND         QUERY
       │             │
       ▼             ▼
   Use Case      Read Model
       │             │
       ▼             ▼
   Domain       Projection
```

Ejemplos donde puede aportar valor:

- dashboards;
- Kardex;
- tracking;
- reportes;
- métricas;
- consultas históricas complejas.

---

# 27. C4 — CONTEXT DIAGRAM

Vista de alto nivel:

```text
                    ┌────────────────────┐
                    │     Usuarios       │
                    │                    │
                    │ Administradores    │
                    │ Operadores         │
                    │ Conductores        │
                    │ Supervisores       │
                    └─────────┬──────────┘
                              │
                              ▼
                 ┌─────────────────────────┐
                 │                         │
                 │  PLATAFORMA EMPRESARIAL │
                 │                         │
                 │ Inventory               │
                 │ Fixed Assets            │
                 │ Logistics               │
                 │                         │
                 └───────────┬─────────────┘
                             │
              ┌──────────────┼───────────────┐
              ▼              ▼               ▼
          ERP/SIESA      Maps/GPS       Email/WhatsApp
```

---

# 28. C4 — CONTAINER DIAGRAM

```text
┌───────────────────────────────────────────────┐
│                FRONTEND ANGULAR               │
│                                               │
│ Inventory │ Assets │ Logistics │ Admin       │
└───────────────────────┬───────────────────────┘
                        │ HTTPS
                        ▼
┌───────────────────────────────────────────────┐
│              BACKEND SPRING BOOT              │
│                                               │
│ Identity                                      │
│ Organization                                  │
│ Inventory                                     │
│ Fixed Assets                                  │
│ Logistics                                     │
│ Notifications                                 │
│ Files                                         │
│ Audit                                         │
│ Reporting                                     │
└───────────────────────┬───────────────────────┘
                        │
             ┌──────────┴───────────┐
             ▼                      ▼
       ┌─────────────┐       ┌──────────────┐
       │ PostgreSQL  │       │ File Storage │
       └─────────────┘       └──────────────┘
```

Posteriormente:

```text
Backend
   │
   ├── Message Broker
   │
   ├── Cache
   │
   └── External APIs
```

---

# 29. C4 — COMPONENTE DE INVENTARIO

```text
Inventory API
     │
     ▼
Inventory Application
     │
 ┌───┼───────────────────┐
 ▼   ▼                   ▼
Product   Stock        Transfer
UseCases  UseCases     UseCases
     │
     ▼
Inventory Domain
     │
 ┌───┼──────────┐
 ▼   ▼          ▼
Product Stock Movement
     │
     ▼
Repository Ports
     │
     ▼
PostgreSQL Adapter
```

---

# 30. ESTRUCTURA DEL BACKEND

La estructura propuesta:

```text
src/main/java/com/codevam/platform/

├── identity/
│   ├── domain/
│   ├── application/
│   └── infrastructure/
│
├── organization/
│   ├── domain/
│   ├── application/
│   └── infrastructure/
│
├── inventory/
│   ├── domain/
│   │   ├── model/
│   │   ├── valueobject/
│   │   ├── event/
│   │   └── repository/
│   │
│   ├── application/
│   │   ├── command/
│   │   ├── query/
│   │   ├── dto/
│   │   └── usecase/
│   │
│   └── infrastructure/
│       ├── persistence/
│       ├── web/
│       └── messaging/
│
├── assets/
│   ├── domain/
│   ├── application/
│   └── infrastructure/
│
├── logistics/
│   ├── domain/
│   ├── application/
│   └── infrastructure/
│
├── audit/
├── notification/
├── files/
└── reporting/
```

---

# 31. FRONTEND

Angular:

```text
src/app/

├── core/
│   ├── auth/
│   ├── guards/
│   ├── interceptors/
│   └── services/
│
├── shared/
│   ├── components/
│   ├── directives/
│   ├── pipes/
│   └── models/
│
├── features/
│   ├── inventory/
│   ├── assets/
│   ├── logistics/
│   ├── reports/
│   └── administration/
│
└── layout/
```

Cada feature deberá ser independiente y utilizar lazy loading cuando sea apropiado.

---

# 32. MODELO DE DATOS INICIAL

Entidades core:

```text
tenant
organization
branch
user
role
permission
user_role
role_permission
```

Inventario:

```text
product
product_category
warehouse
storage_location
stock
inventory_movement
inventory_transfer
inventory_transfer_item
batch
serial_number
purchase_order
receiving
stock_count
stock_count_item
```

Activos:

```text
asset
asset_category
asset_assignment
asset_transfer
asset_maintenance
asset_depreciation
asset_disposal
```

Logística:

```text
customer
customer_address
order
order_item
delivery
delivery_package
route
route_stop
vehicle
driver
tracking_event
proof_of_delivery
```

Transversales:

```text
audit_log
file_metadata
notification
system_configuration
domain_event
```

---

# 33. REGLAS DE INTEGRIDAD

La base de datos deberá ayudar a proteger las reglas.

Ejemplos:

```text
UNIQUE(tenant_id, sku)

UNIQUE(tenant_id, asset_code)

UNIQUE(tenant_id, serial_number)
```

cuando el dominio lo requiera.

No depender exclusivamente de validaciones Java para garantizar unicidad.

---

# 34. MANEJO DE DINERO

Nunca utilizar:

```java
double price;
```

Para valores monetarios utilizar:

```text
BigDecimal
```

y un Value Object:

```text
Money
```

Ejemplo:

```text
Money
├── amount
└── currency
```

---

# 35. MANEJO DE CANTIDADES

Las cantidades de inventario no deben tratarse indiscriminadamente como enteros.

Dependiendo del producto podrían existir:

```text
10 unidades
2.5 kg
3.75 metros
```

Por lo tanto:

```text
Quantity
```

deberá encapsular:

- valor;
- unidad;
- reglas de precisión.

---

# 36. SEGURIDAD Y AUTORIZACIÓN

La autorización se realizará en backend.

Flujo:

```text
Request
   ↓
Authentication
   ↓
Tenant Context
   ↓
Permission Check
   ↓
Application Use Case
   ↓
Domain
```

Ejemplo:

```text
POST /api/v1/inventory/adjustments
```

requiere:

```text
INVENTORY_ADJUST
```

No basta con ocultar el botón en Angular.

---

# 37. AUDITORÍA

Las operaciones críticas deberán producir auditoría.

Ejemplo:

```text
User: 123
Action: STOCK_ADJUSTED
Entity: Product
EntityId: 789
Before: 100
After: 95
Timestamp: ...
```

La auditoría será transversal y no deberá contaminar las reglas de dominio.

Puede utilizar eventos:

```text
Domain Event
     ↓
Audit Handler
     ↓
AuditLog
```

---

# 38. ARCHIVOS

No almacenar archivos binarios directamente en PostgreSQL salvo que exista una razón concreta.

Arquitectura:

```text
Application
    ↓
FileStoragePort
    ↓
LocalStorageAdapter

posteriormente:

S3Adapter
AzureBlobAdapter
```

El dominio no sabrá dónde se almacena físicamente el archivo.

---

# 39. NOTIFICACIONES

Arquitectura:

```text
Domain Event
      ↓
Notification Handler
      ↓
Notification Service
      ↓
Notification Provider
```

Ejemplo:

```text
StockBelowMinimum
        ↓
EmailNotificationAdapter
```

Posteriormente:

```text
WhatsAppAdapter
SMSAdapter
PushNotificationAdapter
```

---

# 40. API VERSIONING

Las APIs comenzarán con:

```text
/api/v1/
```

Ejemplo:

```text
GET    /api/v1/products
POST   /api/v1/products
GET    /api/v1/products/{id}
PUT    /api/v1/products/{id}
DELETE /api/v1/products/{id}
```

Las entidades internas nunca deberán exponerse directamente.

Utilizar:

```text
Request DTO
Response DTO
```

---

# 41. MANEJO DE ERRORES

Todas las APIs deberán utilizar un formato consistente.

Ejemplo conceptual:

```json
{
  "timestamp": "...",
  "status": 409,
  "code": "INSUFFICIENT_STOCK",
  "message": "Insufficient stock available",
  "traceId": "..."
}
```

Los errores de negocio deberán tener códigos identificables.

Ejemplos:

```text
PRODUCT_NOT_FOUND
INSUFFICIENT_STOCK
INVALID_TRANSFER_STATE
ASSET_ALREADY_ASSIGNED
INVALID_DELIVERY_TRANSITION
TENANT_ACCESS_DENIED
```

---

# 42. CONCURRENCIA

Especial atención en inventario.

Problema:

```text
Stock = 10

Usuario A → retira 8
Usuario B → retira 7
```

No permitir terminar con:

```text
Stock = -5
```

La solución deberá utilizar mecanismos adecuados como:

- transacciones;
- optimistic locking;
- restricciones;
- operaciones atómicas;
- validación dentro de la transacción.

---

# 43. IDEMPOTENCIA

Especialmente importante para:

- pagos futuros;
- integraciones;
- webhooks;
- entregas;
- eventos;
- recepción de datos externos.

Ejemplo:

```text
POST /deliveries/{id}/confirm
Idempotency-Key: XYZ
```

Si la misma solicitud llega dos veces, no deberá generar dos operaciones.

---

# 44. OBSERVABILIDAD

Desde el inicio preparar:

```text
Logs
Metrics
Tracing
Health Checks
Correlation ID
```

Cada request importante debería poder rastrearse:

```text
Request
 ↓
Controller
 ↓
Use Case
 ↓
Repository
 ↓
Database
```

utilizando un `traceId` o `correlationId`.

---

# 45. ESTRATEGIA DE EVENTOS

Inicialmente:

```text
Domain Event
       ↓
In-process Event Handler
```

Posteriormente:

```text
Domain Event
       ↓
Message Broker
       ↓
Consumers
```

La aplicación deberá encapsular la infraestructura mediante interfaces.

Por ejemplo:

```text
EventPublisher
```

y no:

```text
KafkaTemplate
```

directamente dentro del dominio.

---

# 46. ESTRATEGIA DE ESCALABILIDAD

Escalamiento horizontal futuro:

```text
                Load Balancer
                     │
        ┌────────────┼────────────┐
        ▼            ▼            ▼
    Backend 1    Backend 2    Backend 3
        │            │            │
        └────────────┼────────────┘
                     ▼
                PostgreSQL
```

La aplicación deberá ser lo más stateless posible.

Las sesiones no deberán depender de memoria local del servidor.

---

# 47. CACHE

No introducir Redis desde el primer día sin necesidad.

Podrá utilizarse posteriormente para:

- catálogos;
- permisos;
- sesiones;
- consultas frecuentes;
- rate limiting;
- información temporal.

La caché nunca debe convertirse en la fuente principal de verdad para datos transaccionales críticos.

---

# 48. EXTRACCIÓN FUTURA A MICROSERVICIOS

Cuando exista una necesidad real:

```text
MONOLITO MODULAR
       │
       ▼
Extraer Inventory
       │
       ▼
Inventory Service
```

Ejemplo futuro:

```text
                 API Gateway
                     │
        ┌────────────┼─────────────┐
        ▼            ▼             ▼
   Inventory      Assets       Logistics
   Service        Service        Service
      │              │              │
      ▼              ▼              ▼
Inventory DB     Assets DB     Logistics DB
```

Los módulos deberán poder separarse porque sus límites ya fueron definidos desde el inicio.

---

# 49. CRITERIOS PARA EXTRAER UN MICROSERVICIO

No se extraerá un módulo hasta demostrar:

### 1. Necesidad de escalamiento independiente

Ejemplo:

Logística recibe millones de eventos de tracking.

### 2. Necesidad de despliegue independiente

Ejemplo:

Logística necesita actualizarse diariamente mientras Inventory se actualiza semanalmente.

### 3. Independencia del dominio

Debe poseer una responsabilidad suficientemente clara.

### 4. Volumen

Debe existir una carga que justifique la separación.

### 5. Equipo

Debe existir capacidad para mantener el servicio.

### 6. Beneficio operacional

El beneficio debe superar el costo de:

- monitoreo;
- despliegue;
- redes;
- seguridad;
- observabilidad;
- comunicación;
- consistencia distribuida.

---

# 50. INTEGRACIONES

Las integraciones externas utilizarán adapters.

Ejemplo:

```text
Inventory
    │
    ▼
ERPIntegrationPort
    │
    ▼
SiesaAdapter
```

El dominio nunca deberá depender directamente de SIESA.

Posteriormente:

```text
SAPAdapter
SiesaAdapter
OracleERPAdapter
CustomERPAdapter
```

podrán coexistir.

---

# 51. C4 — DEPLOYMENT FUTURO

Inicial:

```text
Internet
   │
   ▼
Reverse Proxy
   │
   ├── Angular
   │
   └── Spring Boot
          │
          ▼
      PostgreSQL
```

Escalable:

```text
Internet
   │
   ▼
Load Balancer
   │
 ┌─┴───────────┐
 ▼             ▼
Backend 1   Backend 2
 │             │
 └──────┬──────┘
        ▼
 PostgreSQL
        │
        ├── Redis
        │
        ├── Broker
        │
        └── Object Storage
```

---

# 52. MODELO DE DOMINIO GENERAL

Vista simplificada:

```text
                         TENANT
                           │
             ┌─────────────┼─────────────┐
             │             │             │
          USERS          BRANCHES     CONFIGURATION
                           │
             ┌─────────────┼─────────────┐
             │             │             │
          INVENTORY      ASSETS       LOGISTICS
             │             │             │
          Products       Assets       Orders
             │             │             │
           Stock       Assignments    Deliveries
             │             │             │
         Movements     Maintenance    Tracking
             │             │             │
        Transfers      Depreciation   Proof
```

---

# 53. FLUJO EMPRESARIAL DE INVENTARIO

Ejemplo:

```text
Proveedor
   ↓
Purchase Order
   ↓
Receiving
   ↓
Inventory Movement
   ↓
Stock
   ↓
Transfer
   ↓
Warehouse
   ↓
Issue
   ↓
Customer / Internal Area
```

---

# 54. FLUJO EMPRESARIAL DE ACTIVOS

```text
Compra
  ↓
Recepción
  ↓
Activo creado
  ↓
Almacenamiento
  ↓
Asignación
  ↓
Uso
  ↓
Mantenimiento
  ↓
Reasignación
  ↓
Depreciación
  ↓
Baja
```

---

# 55. FLUJO EMPRESARIAL LOGÍSTICO

```text
Order
  ↓
Preparation
  ↓
Delivery
  ↓
Route
  ↓
Driver
  ↓
Dispatch
  ↓
Tracking
  ↓
Destination
  ↓
Proof of Delivery
  ↓
Delivered
```

---

# 56. MVP

El MVP NO debe intentar implementar absolutamente todo.

## MVP Core

### Plataforma

- Tenant.
- Empresa.
- Sedes.
- Usuarios.
- Roles.
- Permisos.
- Login.
- Auditoría.

### Inventario

- Productos.
- Categorías.
- Bodegas.
- Ubicaciones.
- Stock.
- Movimientos.
- Transferencias.
- Kardex.
- Inventario físico.

### Activos

- Activos.
- Categorías.
- Asignaciones.
- Traslados.
- Historial.
- Estados.

### Logística

- Clientes.
- Direcciones.
- Pedidos.
- Entregas.
- Asignación.
- Estados.
- Tracking.
- Evidencia básica.

---

# 57. FUNCIONALIDADES POSTERIORES

Después del MVP:

## Inventario

- Lotes.
- Vencimientos.
- FEFO.
- RFID.
- códigos QR.
- compras avanzadas.
- proveedores.
- forecasting.
- reposición automática.

## Activos

- depreciación avanzada.
- mantenimiento preventivo.
- contratos.
- garantías.
- documentos.
- firma digital.
- códigos QR.

## Logística

- aplicación de conductor.
- GPS.
- geolocalización.
- optimización de rutas.
- ETA.
- notificaciones al cliente.
- WhatsApp.
- prueba avanzada de entrega.

---

# 58. ROADMAP

## FASE 0 — Arquitectura

```text
DDD
C4
ADR
ERD
API contracts
Project structure
```

## FASE 1 — Core

```text
Authentication
Users
Tenants
Roles
Permissions
Branches
Audit
```

## FASE 2 — Inventory

```text
Products
Categories
Warehouses
Locations
Stock
Movements
Transfers
Counts
Reports
```

## FASE 3 — Assets

```text
Assets
Assignments
Transfers
Maintenance
Depreciation
Disposal
```

## FASE 4 — Logistics

```text
Customers
Orders
Deliveries
Routes
Drivers
Vehicles
Tracking
Proof of Delivery
```

## FASE 5 — Cross-cutting

```text
Notifications
Files
Reports
Integrations
```

## FASE 6 — Enterprise

```text
Observability
Caching
Message Broker
Advanced Reporting
```

## FASE 7 — Scale

```text
Performance
Horizontal scaling
Service extraction
Microservices where justified
```

---

# 59. ADR — RESUMEN DE DECISIONES

| ID | Decisión | Estado |
|---|---|---|
| ADR-001 | Monolito modular inicial | Aprobada propuesta |
| ADR-002 | DDD | Aprobada propuesta |
| ADR-003 | Arquitectura Hexagonal | Aprobada propuesta |
| ADR-004 | PostgreSQL | Aprobada propuesta |
| ADR-005 | Multi-tenancy | Aprobada propuesta |
| ADR-006 | REST API | Aprobada propuesta |
| ADR-007 | Angular | Aprobada propuesta |
| ADR-008 | Spring Boot | Aprobada propuesta |
| ADR-009 | Eventos de dominio | Aprobada propuesta |
| ADR-010 | CQRS selectivo | Aprobada propuesta |
| ADR-011 | Microservicios posteriores | Aprobada propuesta |
| ADR-012 | Docker | Aprobada propuesta |
| ADR-013 | Message Broker posteriormente | Pendiente |
| ADR-014 | Redis posteriormente | Pendiente |
| ADR-015 | Kubernetes posteriormente | Pendiente |

---

# 60. RIESGOS ARQUITECTÓNICOS

## Riesgo 1 — Sobrearquitectura

Implementar demasiada infraestructura demasiado pronto.

### Mitigación

Comenzar con monolito modular.

---

## Riesgo 2 — Acoplamiento entre módulos

Ejemplo:

```text
InventoryService
   ↓
AssetRepository
```

Esto debe evitarse.

---

## Riesgo 3 — Base de datos compartida sin límites

Aunque inicialmente exista una misma PostgreSQL, cada módulo debe respetar sus límites.

---

## Riesgo 4 — Lógica empresarial en controllers

Los controllers deberán ser delgados.

```text
Controller
   ↓
Use Case
   ↓
Domain
```

---

## Riesgo 5 — Frontend controlando seguridad

El frontend nunca será la autoridad de seguridad.

---

## Riesgo 6 — Eventos mal utilizados

No todo debe convertirse en evento.

Un evento debe utilizarse cuando exista una razón real de desacoplamiento, integración o procesamiento asíncrono.

---

# 61. PRINCIPIO DE DEPENDENCIAS

Regla:

```text
Infrastructure
      ↓
Application
      ↓
Domain
```

Nunca:

```text
Domain
      ↓
Spring
      ↓
PostgreSQL
```

El dominio debe permanecer independiente.

---

# 62. EJEMPLO DE FLUJO COMPLETO

Caso:

> Un administrador transfiere 20 unidades de Cali a Bogotá.

Flujo conceptual:

```text
POST /inventory/transfers
             │
             ▼
InventoryTransferController
             │
             ▼
CreateTransferUseCase
             │
             ▼
InventoryTransfer Aggregate
             │
             ▼
Business Rules
             │
             ▼
TransferCreated
             │
             ▼
Repository Port
             │
             ▼
PostgreSQL Adapter
             │
             ▼
Audit Event
             │
             ▼
AuditLog
```

Posteriormente:

```text
TransferApproved
      ↓
TransferDispatched
      ↓
TransferReceived
```

Cada etapa produce su trazabilidad.

---

# 63. PRINCIPIO DE TRAZABILIDAD

Los tres dominios comparten una característica fundamental:

> Una operación empresarial importante debe poder reconstruirse históricamente.

Inventario:

```text
¿De dónde salió este producto?
¿Quién lo movió?
¿Cuándo?
¿Dónde estaba?
```

Activo:

```text
¿Quién tuvo este activo?
¿Dónde estuvo?
¿Cuándo se transfirió?
¿Qué mantenimiento tuvo?
```

Logística:

```text
¿Quién transportó el pedido?
¿Dónde estuvo?
¿Cuándo salió?
¿Cuándo llegó?
¿Quién recibió?
```

Por esto la plataforma debe priorizar **historial y eventos**, no únicamente estados actuales.

---

# 64. DECISIONES PENDIENTES ANTES DE PROGRAMAR

Antes de comenzar la implementación se deberán definir:

### Seguridad

- JWT vs OAuth2/OIDC.
- proveedor de identidad;
- MFA futuro;
- política de contraseñas.

### Multi-tenancy

- estrategia exacta de aislamiento;
- tenant por usuario;
- usuarios pertenecientes a múltiples empresas;
- permisos por sede.

### Inventario

- valoración de inventario;
- FIFO/FEFO;
- manejo contable;
- reservas;
- unidades de medida.

### Activos

- reglas contables;
- depreciación;
- integración contable.

### Logística

- proveedor de mapas;
- GPS;
- optimización;
- aplicación móvil/PWA.

### Infraestructura

- cloud;
- proveedor de almacenamiento;
- proveedor de correo;
- broker;
- estrategia de backups.

---

# 65. DECISIÓN ARQUITECTÓNICA CENTRAL

La decisión más importante del proyecto es:

> **Diseñar para escalar sin implementar complejidad innecesaria desde el primer día.**

Por ello:

```text
                 AHORA
                   │
                   ▼
        MONOLITO MODULAR
                   │
        ┌──────────┼──────────┐
        ▼          ▼          ▼
    Inventory    Assets    Logistics
        │          │          │
        └──────────┼──────────┘
                   │
              Domain Events
                   │
                   ▼
              Integraciones
```

Y posteriormente:

```text
              FUTURO
                │
                ▼
           API Gateway
                │
       ┌────────┼─────────┐
       ▼        ▼         ▼
 Inventory   Assets   Logistics
 Service     Service    Service
    │           │          │
    ▼           ▼          ▼
   DB          DB         DB
```

La transición será posible porque los límites se habrán definido desde el primer día.

---

# 66. CRITERIO DE ÉXITO ARQUITECTÓNICO

La arquitectura será considerada exitosa si:

1. Un desarrollador puede entender dónde colocar una nueva funcionalidad.
2. Un cambio en Inventory no rompe Assets.
3. Un cambio en Logistics no obliga a modificar el dominio de Inventory.
4. Las reglas empresariales pueden probarse sin levantar Spring.
5. Se pueden cambiar proveedores externos sin modificar el dominio.
6. Se puede agregar una aplicación móvil utilizando la misma API.
7. Se puede agregar una nueva empresa sin modificar el código.
8. Se puede extraer posteriormente un bounded context como microservicio.
9. Se pueden auditar las operaciones críticas.
10. El sistema puede crecer horizontalmente.

---

# 67. SIGUIENTE PASO

No comenzar todavía con microservicios.

No comenzar todavía con Kubernetes.

No comenzar todavía con Kafka.

No comenzar todavía con optimización prematura.

El siguiente paso técnico será construir:

```text
FASE 0
│
├── Project skeleton
├── Architecture
├── Domain boundaries
├── Database foundation
├── Identity
├── Tenant
├── User
├── Role
├── Permission
└── Audit
```

Una vez terminada la Fase 0, se podrá comenzar Inventory.

---

# 68. CONCLUSIÓN

La plataforma debe entenderse como un producto empresarial y no simplemente como una colección de CRUDs.

La arquitectura propuesta establece:

```text
DDD
 +
Hexagonal
 +
Modular Monolith
 +
Domain Events
 +
Selective CQRS
 +
Multi-tenancy
 +
API First
 +
Observability
 +
Automated Testing
```

con una evolución controlada hacia:

```text
Microservices
 +
Message Broker
 +
Distributed Systems
 +
Horizontal Scaling
```

cuando las necesidades reales del negocio lo justifiquen.

La primera meta no es construir la arquitectura técnicamente más compleja.

La primera meta es construir una **base empresarial sólida que permita crecer sin tener que destruir y reconstruir el producto**.