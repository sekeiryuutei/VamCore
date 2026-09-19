# VamCore

**Plataforma empresarial modular** desarrollada por **Codevam** (software a medida),
que integra tres productos comerciales sobre una única base técnica:

| Producto | Módulo técnico | Qué hace |
|---|---|---|
| **VamStock** | `inventory` | Control e inventarios empresariales |
| **VamAsset** | `assets` | Gestión y control de activos fijos |
| **VamTrack** | `logistics` | Logística y seguimiento de entregas |

VamCore se construye como **un monolito modular** (no tres apps separadas), con
arquitectura hexagonal, DDD y eventos de dominio, diseñado para poder evolucionar
a microservicios el día que exista una necesidad real de negocio (no antes).

**Todo el stack corre en Docker** (PostgreSQL + backend + frontend). En una
máquina nueva, con Docker instalado, `./scripts/setup.sh` deja todo funcionando
— ver la sección 3 para el paso a paso completo.

Este documento es la guía de referencia para poner el proyecto a correr y para
entender cómo está organizado. La documentación arquitectónica completa (el
"por qué" de cada decisión) vive en [`docs/`](./docs).

---

## 0. Antes de empezar — parametrización de nombres

Este es el **primer proyecto de la marca**, así que todos los nombres comerciales
(empresa, plataforma, y los tres productos) están centralizados en un solo archivo:

```
product.config.yml
```

**No hardcodees nombres de marca en el código.** Si algún nombre cambia en el
futuro (por ejemplo, VamStock pasa a llamarse de otra forma), el flujo es:

1. Edita `product.config.yml` con el/los nuevos valores.
2. Corre `./scripts/rename-product.sh` desde la raíz del repo.
3. Revisa `git diff` y compila (`cd backend && mvn -q compile`) antes de comitear.

El script propaga el cambio a: `backend/pom.xml`, paquetes Java (si cambia
`java_base_package`), `frontend/src/environments/*`, `frontend/package.json`,
`infrastructure/docker-compose.yml` y la documentación. Ver comentarios dentro
de `product.config.yml` y `scripts/rename-product.sh` para el detalle.

---

## 1. Estructura del repositorio

```
vamcore/
├── product.config.yml         # ← única fuente de verdad de nombres de marca
├── backend/                   # Spring Boot (Java 21) — monolito modular
│   └── src/main/java/com/vamcore/
│       ├── identity/           Autenticación, usuarios, roles (JWT)
│       ├── organization/       Tenant, Branch (multi-empresa / multi-sede)
│       ├── inventory/          VamStock — Fase 2 (completo)
│       ├── assets/             VamAsset — Fase 3 (completo)
│       ├── logistics/          VamTrack — Fase 4 (completo)
│       ├── audit/              Auditoría transversal (escucha domain events)
│       ├── files/              Almacenamiento de archivos — Fase 5 (esqueleto)
│       ├── notifications/      Notificaciones — Fase 5 (esqueleto)
│       ├── reporting/          Reportería / CQRS selectivo — Fase 6 (esqueleto)
│       └── shared/             Kernel compartido: DomainEvent, Money, TenantId...
├── frontend/                   Angular (standalone components)
│   └── src/app/
│       ├── core/                auth, guards, interceptors, services
│       ├── shared/               componentes/pipes/modelos reutilizables
│       ├── features/             inventory, assets, logistics, reports, administration
│       └── layout/
├── docs/
│   ├── adr/                     Architecture Decision Records
│   └── architecture/            Documentos fuente de arquitectura completos
├── infrastructure/
│   ├── docker-compose.yml       Postgres + backend + frontend (todo dockerizado)
│   └── .env.example
└── scripts/
    ├── setup.sh                 Paso 1 en máquina nueva: deja TODO corriendo en Docker
    ├── docker-up.sh             Levanta / reconstruye los contenedores
    ├── docker-down.sh           Detiene los contenedores (--purge borra la BD)
    ├── docker-logs.sh           Sigue los logs de los contenedores
    └── rename-product.sh        Propaga cambios de product.config.yml
```

Cada módulo del backend sigue arquitectura hexagonal:

```
<modulo>/
├── domain/           # Entidades, Value Objects, eventos, puertos (interfaces)
│   ├── model/
│   ├── valueobject/
│   ├── event/
│   └── repository/    (puertos "out")
├── application/      # Casos de uso, Commands, Queries, DTOs
│   ├── command/
│   ├── query/
│   ├── dto/
│   └── usecase/
└── infrastructure/   # Adaptadores concretos (JPA, REST, mensajería)
    ├── persistence/
    ├── web/
    └── messaging/
```

El `domain` nunca importa Spring, JPA ni nada de infraestructura. Ver
[`docs/adr/ADR-003-arquitectura-hexagonal.md`](./docs/adr/ADR-003-arquitectura-hexagonal.md).

---

## 2. Todo corre en Docker

VamCore está **completamente dockerizado**: PostgreSQL, backend y frontend
corren cada uno en su propio contenedor, orquestados con Docker Compose. En una
máquina nueva **no necesitas instalar Java, Node, Maven, Angular CLI ni
PostgreSQL** — solo Docker. (Esas herramientas solo son necesarias si más
adelante quieres desarrollar sin reconstruir contenedores cada vez, ver
sección 3.5 "Modo desarrollo sin Docker").

### 2.1 Requisito único: Docker

| Requisito | Cómo verificarlo | Dónde instalarlo |
|---|---|---|
| **Docker Engine + Docker Compose v2** | `docker --version` y `docker compose version` | [docs.docker.com/get-docker](https://docs.docker.com/get-docker/) (incluye Docker Desktop en Windows/Mac, o Docker Engine en Linux) |
| **Git** | `git --version` | [git-scm.com](https://git-scm.com/downloads) |

Eso es todo. En Linux, asegúrate de que tu usuario pueda ejecutar `docker` sin
`sudo` (`sudo usermod -aG docker $USER`, luego cierra sesión y vuelve a entrar).

---

## 3. Puesta en marcha en una máquina nueva — paso a paso

### Paso 1 — Clonar el repositorio

```bash
git clone <url-del-repo> vamcore
cd vamcore
```

### Paso 2 — Ejecutar el script de setup

```bash
./scripts/setup.sh
```

Este script, de forma automática:

1. Verifica que Docker y Docker Compose estén instalados.
2. Crea `infrastructure/.env` a partir de `infrastructure/.env.example`
   (si ya existe, no lo toca — así no pierdes configuración previa).
3. Construye las imágenes de `backend` y `frontend`, y levanta los tres
   contenedores (`postgres`, `backend`, `frontend`) con `docker compose up -d --build`.
4. Espera a que el backend reporte estado `healthy` (usa el endpoint
   `/actuator/health` internamente) antes de terminar.

La primera vez tarda varios minutos (descarga imágenes base y compila el
backend con Maven dentro del contenedor). Las siguientes veces es mucho más
rápido gracias al cache de capas de Docker.

> **Nota de seguridad:** `infrastructure/.env.example` trae un
> `VAMCORE_JWT_SECRET` de ejemplo. Si vas a exponer el backend fuera de tu
> máquina (staging/producción), abre `infrastructure/.env` y cámbialo por un
> valor propio y secreto antes de levantar los contenedores.

### Paso 3 — Verificar que todo esté corriendo

```bash
cd infrastructure && docker compose ps
```

Deberías ver los tres servicios (`vamcore-postgres`, `vamcore-backend`,
`vamcore-frontend`) en estado `Up` (el backend además marcado `healthy`).

Prueba en el navegador o con `curl`:

```bash
curl http://localhost:8080/actuator/health   # -> {"status":"UP"}
```

Y abre el frontend en tu navegador: **http://localhost:4200** — te
redirige a `/login`. Como todavía no hay pantalla de registro en el
frontend, crea el tenant y el usuario con `curl` primero (ver Paso 4) y
luego inicia sesión con esas credenciales en `/login`; quedarás en el
dashboard de VamStock (`/inventory`), donde puedes crear un producto, una
bodega y recibir stock para probar el circuito completo.

### Paso 4 — Probar el flujo completo de la API

Ver la sección 5 ("Probar la API") más abajo para el flujo `curl` de extremo
a extremo (crear empresa → registrar usuario → login → usar el token).

### Comandos del día a día

| Quiero... | Comando |
|---|---|
| Levantar / reconstruir todo | `./scripts/docker-up.sh` |
| Ver los logs en vivo | `./scripts/docker-logs.sh` (o `./scripts/docker-logs.sh backend` para uno solo) |
| Detener todo (conserva los datos) | `./scripts/docker-down.sh` |
| Detener todo y **borrar la base de datos** | `./scripts/docker-down.sh --purge` |
| Reconstruir solo el backend tras un cambio | `cd infrastructure && docker compose up -d --build backend` |

Al arrancar por primera vez, Flyway crea automáticamente todas las tablas
(`tenant`, `branch`, `app_user`, `user_role`, `audit_log`, `inventory_*`)
usando los scripts en `backend/src/main/resources/db/migration/`. No necesitas
correr ninguna migración a mano.

### 3.5 Modo desarrollo sin Docker (opcional)

Si prefieres iterar en el backend o frontend sin reconstruir la imagen en
cada cambio (hot-reload más rápido), puedes correrlos localmente mientras
sigues usando Docker solo para PostgreSQL:

**Requisitos adicionales para este modo:**
- Java 21 y Maven 3.9+ (`java -version`, `mvn -version`)
- Node.js 20+ y npm, y Angular CLI 18 (`npm install -g @angular/cli`)

```bash
# 1) Solo levanta PostgreSQL en Docker
cd infrastructure && docker compose up -d postgres

# 2) Backend en modo local (recarga con tu IDE o mvn spring-boot:run)
cd ../backend
mvn spring-boot:run
# El backend queda escuchando en http://localhost:8080

# 3) Frontend en modo local, con hot-reload (en otra terminal)
cd ../frontend
npm install
npm start
# El frontend queda en http://localhost:4200 con recarga automática
```

Con este modo, `backend/src/main/resources/application.yml` usa
`localhost:5432` por defecto, así que no necesitas exportar variables
adicionales mientras uses los valores por defecto de `.env.example`.

### Resolución de problemas comunes

| Síntoma | Causa probable / solución |
|---|---|
| `port is already allocated` al hacer `docker compose up` | Otro proceso ya usa el puerto 8080/4200/5432. Cambia el puerto en `infrastructure/.env` (ej. `VAMCORE_SERVER_PORT=8081`) o detén el proceso que lo ocupa. |
| `permission denied` al ejecutar `docker` | Tu usuario no está en el grupo `docker` en Linux. Corre `sudo usermod -aG docker $USER` y vuelve a iniciar sesión. |
| El backend nunca queda `healthy` | Revisa los logs con `./scripts/docker-logs.sh backend`. Lo más común es que Postgres no esté listo aún (espera unos segundos más) o que `VAMCORE_DB_PASSWORD` no coincida entre servicios — revisa `infrastructure/.env`. |
| Cambié `product.config.yml` pero no compila | Corre `./scripts/rename-product.sh`, luego `./scripts/docker-up.sh` para reconstruir las imágenes con el cambio. |
| Quiero empezar de cero (borrar todos los datos) | `./scripts/docker-down.sh --purge` y luego `./scripts/setup.sh` de nuevo. |
| El frontend carga en blanco con error `NG0908` en la consola | Ya corregido en este repo (faltaba declarar el polyfill `zone.js` en `angular.json`). Si ves este error de nuevo tras editar `angular.json`, revisa que `architect.build.options.polyfills` siga incluyendo `["zone.js"]`. |

---

## 4. Probar la API (flujo mínimo)

Con los contenedores corriendo (`./scripts/setup.sh` o `./scripts/docker-up.sh`):

```bash
# 1) Crear un tenant (empresa)
curl -X POST http://localhost:8080/api/v1/tenants \
  -H "Content-Type: application/json" \
  -d '{"name": "Empresa Demo", "taxId": "900123456-7"}'
# -> guarda el "id" de la respuesta, es el tenantId

# 2) Registrar un usuario en ese tenant
curl -X POST http://localhost:8080/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{"tenantId": "<tenantId>", "email": "admin@demo.com", "password": "Sup3rSegura!", "fullName": "Admin Demo"}'

# 3) Login (devuelve un JWT)
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email": "admin@demo.com", "password": "Sup3rSegura!"}'

# 4) Usar el token en rutas protegidas
curl http://localhost:8080/api/v1/algo-protegido \
  -H "Authorization: Bearer <accessToken>"
```

El `tenantId` viaja embebido en el JWT (claim `tid`) y el backend lo usa para
poblar `TenantContext` en cada request — nunca se debe enviar el tenant como
parámetro de la URL (ver ADR-005).

---

## 5. Variables de entorno

La forma recomendada de configurar la plataforma es `infrastructure/.env`
(usado automáticamente por `docker-compose.yml`). Si corres el backend en modo
local sin Docker (sección 3.5), las mismas variables se pueden exportar como
variables de entorno del sistema. Ver valores por defecto en
`backend/src/main/resources/application.yml`.

| Variable | Descripción | Default dev |
|---|---|---|
| `VAMCORE_DB_URL` | URL JDBC de PostgreSQL | `jdbc:postgresql://localhost:5432/vamcore` |
| `VAMCORE_DB_USER` / `VAMCORE_DB_PASSWORD` | Credenciales de BD | `vamcore` / `vamcore` |
| `VAMCORE_SERVER_PORT` | Puerto HTTP del backend | `8080` |
| `VAMCORE_FRONTEND_PORT` | Puerto donde nginx sirve el frontend | `4200` |
| `VAMCORE_DB_PORT` | Puerto expuesto de PostgreSQL en el host | `5432` |
| `VAMCORE_PLATFORM_NAME` | Nombre mostrado en logs/JWT issuer | `VamCore` |
| `VAMCORE_JWT_SECRET` | Secreto de firma JWT — **cámbialo en cada ambiente** | (placeholder inseguro) |
| `VAMCORE_JWT_EXPIRATION_MINUTES` | Minutos de vida del token | `60` |
| `VAMCORE_MAX_FILE_SIZE` | Tamaño máximo por archivo subido (Files) | `10MB` |

---

## 6. Estado actual del proyecto (Fases 0-6 completas)

Según el roadmap del documento de arquitectura (`docs/architecture/documento-arquitectura-inicial.md`,
sección 58), lo ya construido corresponde a **Fases 0 a 6 completas**:

- [x] Estructura del repositorio y arquitectura hexagonal por módulo
- [x] Multi-tenancy (Tenant, Branch) con JWT + `TenantContext`
- [x] Identity (registro, login, roles, **permisos granulares**)
- [x] Auditoría transversal automática vía Domain Events
- [x] Manejo de errores estándar (`ApiError`, códigos de negocio)
- [x] Observabilidad mínima (`correlationId` por request)
- [x] **Todo dockerizado**: Docker Compose con healthchecks (Postgres → backend →
      frontend arrancan en orden y esperan a que el anterior esté listo),
      volumen persistente para archivos, `.dockerignore`, y scripts
      `setup.sh` / `docker-up.sh` / `docker-down.sh` / `docker-logs.sh`
- [x] Frontend rediseñado: sidebar con acento de color por producto, pantalla
      de login, landing "Resumen" (reporting + notificaciones + archivos), y
      un dashboard funcional por cada producto
- [x] **VamStock (inventory) — Fase 2**: productos, bodegas, stock, Kardex,
      transferencias entre bodegas, listados completos, optimistic locking,
      **órdenes de compra con flujo DRAFT→CONFIRMED→RECEIVED** y **conteos
      físicos con cálculo y aplicación de varianza**
- [x] **VamAsset (assets) — Fase 3**: activos con ciclo de vida validado
      (`ACQUIRED → IN_STORAGE → ASSIGNED → IN_USE → IN_MAINTENANCE → RETIRED → DISPOSED`),
      asignación con historial, **mantenimiento como entidad propia con
      historial** (programar/completar), **depreciación lineal** (costo +
      vida útil → valor en libros calculado), dashboard funcional
- [x] **VamTrack (logistics) — Fase 4**: entregas con máquina de estados
      (`CREATED → CONFIRMED → PREPARING → READY → ASSIGNED → DISPATCHED → IN_TRANSIT → DELIVERED`,
      con ramas `FAILED → RETURNED` y `CANCELLED`), tracking histórico,
      **catálogos de Cliente/Vehículo/Conductor** (se asignan eligiendo de
      una lista real, no escribiendo un UUID a mano), dashboard funcional
- [x] **Notifications — Fase 5**: reacciona automáticamente a eventos de los
      3 productos (entrega entregada/fallida, activo dado de baja, stock
      agotado); adaptador de envío intercambiable (hoy solo registra en log)
- [x] **Files — Fase 5**: subida/descarga/borrado de archivos por tenant,
      almacenamiento en disco vía `FileStoragePort` (intercambiable a S3/Azure
      sin tocar el dominio), persistido en un volumen Docker
- [x] **Reporting — Fase 6**: resumen agregado de los 3 productos + feed de
      actividad reciente (CQRS selectivo de solo lectura, sección 26)
- [x] **Permisos granulares** (sección 32): tablas `permission`/`role_permission`,
      catálogo de 12 permisos, resueltos en el login y embebidos en el JWT,
      aplicados con `@PreAuthorize` en cada endpoint de escritura
- [x] **Gestión de usuarios**: listar usuarios del tenant y cambiar sus roles
      desde `/resumen`, visible solo para `ADMIN`

No queda ningún módulo ni sub-entidad planificada del documento de
arquitectura sin implementar al menos en su forma funcional. Lo que queda
fuera de alcance por ser trabajo de escala/integración externa (no de
dominio) se detalla en la sección 13.

---

## 7. Identidad de marca (Codevam)

Los tokens visuales (colores, tipografía) están tomados del manual de marca
de Codevam y viven en `product.config.yml` (sección `brand`) y en
`frontend/src/styles.scss`:

- **Colores:** slate 900 `#0B1220` (fondos oscuros) · azul eléctrico `#2563EB`
  (CTA/acento primario) · cian `#22D3EE` (acento del símbolo) · violeta `#7C3AED`
  (acento secundario) · gris neutro `#94A3B8` (texto secundario) · blanco hueso
  `#F8FAFC` (fondos claros).
- **Tipografía:** Outfit (títulos/wordmark) · Work Sans (cuerpo de texto) ·
  JetBrains Mono (código, datos técnicos, etiquetas).

---

## 8. Convenciones de trabajo

- **Un solo repositorio** para los tres productos mientras sigan siendo un
  monolito modular. No crear repos separados para VamStock/VamAsset/VamTrack
  a menos que se conviertan en microservicios reales y justificados
  (ver `docs/adr/ADR-001-monolito-modular.md`).
- **El dominio no conoce el framework.** Nada de `import org.springframework...`
  ni `import jakarta.persistence...` dentro de `domain/`.
- **Controllers delgados**: solo traducen HTTP ↔ Command/Query, la lógica vive
  en `application/usecase`.
- **Dinero siempre en `BigDecimal` + Value Object `Money`**, nunca `double`.
- **APIs versionadas** bajo `/api/v1/...`, exponiendo siempre DTOs, nunca
  entidades internas.
- **Todo hecho de negocio relevante debería poder reconstruirse históricamente**
  (ver sección 63 del documento de arquitectura) — por eso el uso de Domain
  Events y auditoría desde el día uno.

---

## 9. API de VamStock (inventory) — ejemplo de flujo completo

Con el backend corriendo y un `accessToken` obtenido como en la sección 4:

```bash
TOKEN="<accessToken>"

# 1) Crear un producto
curl -X POST http://localhost:8080/api/v1/inventory/products \
  -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" \
  -d '{"sku":"LAPTOP-LENOVO-01","name":"Laptop Lenovo","unitOfMeasure":"unidad"}'
# -> guarda el "id" como productId

# 2) Crear dos bodegas (Cali y Bogotá)
curl -X POST http://localhost:8080/api/v1/inventory/warehouses \
  -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" \
  -d '{"name":"Bodega Cali"}'
curl -X POST http://localhost:8080/api/v1/inventory/warehouses \
  -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" \
  -d '{"name":"Bodega Bogotá"}'
# -> guarda ambos "id" como warehouseIdCali / warehouseIdBogota

# 3) Recibir 100 unidades en Cali
curl -X POST http://localhost:8080/api/v1/inventory/receipts \
  -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" \
  -d '{"productId":"<productId>","warehouseId":"<warehouseIdCali>","quantity":100}'

# 4) Transferir 20 unidades de Cali a Bogotá (ver sección 62 del doc de arquitectura)
curl -X POST http://localhost:8080/api/v1/inventory/transfers \
  -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" \
  -d '{"productId":"<productId>","sourceWarehouseId":"<warehouseIdCali>","targetWarehouseId":"<warehouseIdBogota>","quantity":20}'

# 5) Consultar el stock del producto en todas sus bodegas
curl http://localhost:8080/api/v1/inventory/products/<productId>/stock -H "Authorization: Bearer $TOKEN"

# 6) Consultar el Kardex completo del producto
curl http://localhost:8080/api/v1/inventory/products/<productId>/kardex -H "Authorization: Bearer $TOKEN"

# 7) Orden de compra: crear -> confirmar -> recibir (actualiza el stock solo)
curl -X POST http://localhost:8080/api/v1/inventory/purchase-orders \
  -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" \
  -d '{"code":"PO-001","supplierName":"Proveedor XYZ","productId":"<productId>","warehouseId":"<warehouseIdCali>","quantityOrdered":50}'
curl -X POST http://localhost:8080/api/v1/inventory/purchase-orders/<poId>/confirm -H "Authorization: Bearer $TOKEN"
curl -X POST http://localhost:8080/api/v1/inventory/purchase-orders/<poId>/receive -H "Authorization: Bearer $TOKEN"

# 8) Conteo físico: registrar -> aplicar varianza (crea un ADJUSTMENT en el Kardex)
curl -X POST http://localhost:8080/api/v1/inventory/stock-counts \
  -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" \
  -d '{"productId":"<productId>","warehouseId":"<warehouseIdCali>","countedQuantity":115}'
curl -X POST http://localhost:8080/api/v1/inventory/stock-counts/<countId>/apply -H "Authorization: Bearer $TOKEN"
```

Notas de diseño relevantes:

- El stock **nunca** puede quedar negativo (`Stock.decrease` / `Stock.adjust` lo validan) —
  ver `docs/architecture/documento-arquitectura-inicial.md` sección 42 (Concurrencia).
- La tabla `inventory_stock` usa una columna `version` (optimistic locking): si dos
  requests concurrentes modifican el mismo saldo, la segunda recibe
  `409 Conflict` con código `STOCK_CONCURRENT_MODIFICATION`.
- Cada operación que afecta el stock queda registrada en `inventory_movement`
  (ledger append-only) y dispara un `DomainEvent` que el módulo `audit` captura
  automáticamente — no hace falta llamar a auditoría manualmente desde inventory.
- Los ajustes manuales (`/inventory/adjustments`) aceptan `quantityDelta` positivo
  (sobrante) o negativo (faltante), típicamente originados en un conteo físico.

---

## 10. API de VamAsset (assets) — ejemplo de flujo completo

```bash
TOKEN="<accessToken>"

# 1) Registrar un activo (costo/vida útil son opcionales — sin ellos no calcula depreciación)
curl -X POST http://localhost:8080/api/v1/assets \
  -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" \
  -d '{"assetCode":"LAPTOP-001","name":"Laptop Lenovo","category":"Cómputo","serialNumber":"SN123","acquisitionCost":4500000,"currency":"COP","usefulLifeMonths":36}'
# -> nace en estado ACQUIRED

# 2) Mover a bodega
curl -X POST http://localhost:8080/api/v1/assets/<assetId>/status \
  -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" \
  -d '{"newStatus":"IN_STORAGE"}'

# 3) Asignar a una persona (avanza automáticamente a ASSIGNED)
curl -X POST http://localhost:8080/api/v1/assets/<assetId>/assign \
  -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" \
  -d '{"assigneeId":"<uuid-de-la-persona>"}'

# 4) Ver historial de asignaciones
curl http://localhost:8080/api/v1/assets/<assetId>/history -H "Authorization: Bearer $TOKEN"

# 5) Consultar depreciación lineal (valor en libros hoy)
curl http://localhost:8080/api/v1/assets/<assetId>/depreciation -H "Authorization: Bearer $TOKEN"

# 6) Mantenimiento: programar (activo debe estar IN_USE) -> completar (vuelve a IN_USE)
curl -X POST http://localhost:8080/api/v1/assets/<assetId>/maintenance/schedule \
  -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" \
  -d '{"notes":"Cambio de batería"}'
curl -X POST http://localhost:8080/api/v1/assets/<assetId>/maintenance/complete -H "Authorization: Bearer $TOKEN"
curl http://localhost:8080/api/v1/assets/<assetId>/maintenance -H "Authorization: Bearer $TOKEN"
```

Transiciones inválidas (ej. `DISPOSED` → `IN_USE`) devuelven `409 Conflict`
con código `INVALID_ASSET_TRANSITION` — ver `Asset.changeStatus` en el backend.

---

## 11. API de VamTrack (logistics) — ejemplo de flujo completo

```bash
TOKEN="<accessToken>"

# 0) Crear un conductor y un vehículo (catálogos, sección 18)
curl -X POST http://localhost:8080/api/v1/logistics/drivers \
  -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" \
  -d '{"fullName":"Carlos Ramírez","licenseNumber":"LIC-001"}'
curl -X POST http://localhost:8080/api/v1/logistics/vehicles \
  -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" \
  -d '{"plate":"ABC123","model":"NPR"}'

# 1) Crear una entrega
curl -X POST http://localhost:8080/api/v1/logistics/deliveries \
  -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" \
  -d '{"deliveryCode":"DEL-001","customerName":"Juan Pérez","destinationAddress":"Cali, Colombia"}'
# -> nace en estado CREATED

# 2) Avanzar la máquina de estados
curl -X POST http://localhost:8080/api/v1/logistics/deliveries/<deliveryId>/status \
  -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" \
  -d '{"newStatus":"CONFIRMED"}'
# repetir con PREPARING, luego READY

# 3) Asignar el conductor/vehículo creados en el paso 0 (avanza automáticamente a ASSIGNED)
curl -X POST http://localhost:8080/api/v1/logistics/deliveries/<deliveryId>/assign \
  -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" \
  -d '{"driverId":"<idDelConductor>","vehicleId":"<idDelVehiculo>"}'

# 4) Ver el recorrido completo (tracking)
curl http://localhost:8080/api/v1/logistics/deliveries/<deliveryId>/tracking -H "Authorization: Bearer $TOKEN"
```

Transiciones inválidas (ej. saltarse `DISPATCHED` sin pasar por `ASSIGNED`)
devuelven `409 Conflict` con código `INVALID_DELIVERY_TRANSITION`.

---

## 12. API de Notifications, Files, Reporting y Permisos

### Notifications (solo lectura — se generan automáticamente)

```bash
curl http://localhost:8080/api/v1/notifications -H "Authorization: Bearer $TOKEN"
```

Se crean solas al: entregar o fallar una entrega (`DELIVERED`/`FAILED`), dar
de baja un activo (`DISPOSED`), o dejar un producto en stock ≤ 0. Por ahora
el "envío" solo escribe en el log del backend (`docker compose logs backend`)
— ver `LogNotificationSender`, diseñado para reemplazarse por un adaptador
SMTP/WhatsApp/SMS real sin tocar el dominio.

### Files

```bash
# Subir un archivo
curl -X POST http://localhost:8080/api/v1/files \
  -H "Authorization: Bearer $TOKEN" -F "file=@/ruta/a/tu/archivo.pdf"

# Listar
curl http://localhost:8080/api/v1/files -H "Authorization: Bearer $TOKEN"

# Descargar
curl http://localhost:8080/api/v1/files/<fileId>/download \
  -H "Authorization: Bearer $TOKEN" -o descargado.pdf

# Borrar
curl -X DELETE http://localhost:8080/api/v1/files/<fileId> -H "Authorization: Bearer $TOKEN"
```

El contenido se guarda en disco dentro del volumen Docker `vamcore_files_data`
(persiste entre reinicios de los contenedores; se borra solo si haces
`docker compose down -v`).

### Reporting

```bash
curl http://localhost:8080/api/v1/reporting/summary -H "Authorization: Bearer $TOKEN"
curl "http://localhost:8080/api/v1/reporting/activity?limit=10" -H "Authorization: Bearer $TOKEN"
```

Todo esto además se ve consolidado en `http://localhost:4200/resumen`
(landing page tras iniciar sesión).

### Permisos granulares

El **primer usuario registrado en cada tenant recibe automáticamente el rol
`ADMIN`** además de `USER` (bootstrap — ver `RegisterUserUseCase`). Los
siguientes usuarios que registres en ese mismo tenant quedan como `USER`.

| Permiso | ADMIN | USER |
|---|---|---|
| `PRODUCT_MANAGE`, `WAREHOUSE_MANAGE`, `INVENTORY_ADJUST`, `INVENTORY_TRANSFER` | ✅ | ✅ |
| `ASSET_MANAGE`, `DELIVERY_MANAGE` | ✅ | ✅ |
| `REPORTING_VIEW`, `FILE_MANAGE`, `NOTIFICATION_VIEW` | ✅ | ✅ |
| `ASSET_DISPOSE` (dar de baja definitivamente) | ✅ | ❌ |
| `TENANT_MANAGE`, `USER_MANAGE` | ✅ | ❌ |

Pruébalo: con un usuario `USER` (no el primero del tenant), intenta pasar un
activo a `DISPOSED` — debería rechazarlo con `403 FORBIDDEN_INSUFFICIENT_PERMISSION`,
mientras que el resto de transiciones (`IN_STORAGE`, `ASSIGNED`, `IN_USE`,
`RETIRED`...) sí funcionan. Ver `ChangeAssetStatusUseCase.assertHasPermission`.

Los permisos se resuelven en el login (tabla `role_permission`, ver
`V7__permissions_foundation.sql`) y viajan como claim `permissions` dentro
del JWT — no se vuelve a consultar la base de datos en cada request.

`USER_MANAGE` habilita `GET /api/v1/users` (listar) y
`POST /api/v1/users/{userId}/roles` (cambiar roles) — solo `ADMIN` los ve,
también disponible en `/resumen` en el frontend.

---

## 13. Lo que queda genuinamente fuera de alcance (y por qué)

Esto **no** es una lista de "trabajo a medias" — es dominio de negocio ya
implementado en su forma funcional (ver sección 6). Lo que queda abajo es
trabajo de **escala e integración con proveedores externos reales**, que
por naturaleza no tiene un punto de "terminado" (siempre se puede ir más
lejos), y que requiere decisiones de negocio que no me corresponden a mí
tomar (qué proveedor de SMS, qué presupuesto de infraestructura, etc.):

- **Notifications** solo escribe en el log — no hay una cuenta SMTP, de
  WhatsApp Business API o de un proveedor de SMS conectada de verdad. El
  adaptador (`NotificationSender`) ya está listo para recibir una
  implementación real sin tocar el dominio.
- **Files** guarda en disco local, no en S3/Azure Blob — funciona
  perfectamente para una sola instancia del backend (que es lo que este
  `docker-compose.yml` levanta), pero no escala automáticamente si algún
  día corres varias instancias del backend en paralelo.
- **Logistics** no tiene GPS/geolocalización en tiempo real ni
  `ProofOfDelivery` (foto/firma en el momento de la entrega) — el catálogo
  de vehículos/conductores y el tracking por eventos sí están, pero
  conectar un GPS real requiere un proveedor de mapas (Google Maps, Mapbox)
  con su propia cuenta y costos.
- **Identity** no tiene recuperación de contraseña por email (porque
  depende de que Notifications tenga un proveedor de correo real
  conectado) ni inicio de sesión con Google/Microsoft.
- **Inventory** no valida FIFO/FEFO ni maneja lotes/seriales todavía a
  nivel de movimiento individual (el campo `TrackingType` existe en el
  producto, pero el Kardex no lo usa aún para diferenciar unidades).

Ninguno de estos puntos es "código faltante que se me olvidó" — son
decisiones que dependen de qué proveedor externo elijas tú, o trabajo de
escalamiento que solo tiene sentido cuando el volumen real lo exija (ver
ADR-001, la misma filosofía que guio toda la arquitectura: no construir
infraestructura antes de que la necesidad sea real).

---

## 14. Siguiente paso sugerido

El dominio de negocio (los 3 productos + los 4 módulos core) está completo
en su forma funcional. El trabajo que sigue es, en orden de impacto:

1. **Conectar un proveedor real de notificaciones** (empieza por email vía
   SMTP, es el más simple) para que `Notifications` deje de ser solo un log.
2. **Pulir la UI**: hoy los 4 dashboards son funcionales y con un sistema de
   diseño coherente, pero no tienen animaciones, estados de carga
   granulares, ni un flujo de onboarding para el primer uso.
3. **Migrar Files a almacenamiento en la nube** el día que se despliegue en
   un entorno con más de una instancia del backend.
4. Cualquier otro ajuste que surja al usarlo en el día a día — para eso
   estoy, avísame qué necesitas cambiar.
