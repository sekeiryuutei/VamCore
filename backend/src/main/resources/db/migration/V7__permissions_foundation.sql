-- ============================================================================
-- V7__permissions_foundation.sql
-- Permisos granulares (ver sección 32 del documento de arquitectura: tablas
-- `permission` y `role_permission`).
--
-- Diseño: `role` sigue siendo un string libre (como en user_role, no hay
-- tabla `role` separada todavía) y `permission.code` es la clave natural
-- (más legible en el código Java que un id numérico/UUID arbitrario).
-- ============================================================================

CREATE TABLE permission (
    code         VARCHAR(64) PRIMARY KEY,
    description  VARCHAR(255) NOT NULL
);

CREATE TABLE role_permission (
    role             VARCHAR(64) NOT NULL,
    permission_code  VARCHAR(64) NOT NULL REFERENCES permission(code),
    PRIMARY KEY (role, permission_code)
);

-- ------------------------------------------------------------------
-- Catálogo de permisos
-- ------------------------------------------------------------------
INSERT INTO permission (code, description) VALUES
    ('PRODUCT_MANAGE',     'Crear y editar productos de VamStock'),
    ('WAREHOUSE_MANAGE',   'Crear y editar bodegas'),
    ('INVENTORY_ADJUST',   'Realizar ajustes manuales de stock'),
    ('INVENTORY_TRANSFER', 'Transferir inventario entre bodegas'),
    ('ASSET_MANAGE',       'Registrar, asignar y cambiar el estado de activos'),
    ('ASSET_DISPOSE',      'Dar de baja definitivamente un activo (estado DISPOSED)'),
    ('DELIVERY_MANAGE',    'Crear, asignar y actualizar entregas'),
    ('TENANT_MANAGE',      'Administrar la configuración de la empresa'),
    ('USER_MANAGE',        'Administrar usuarios y roles'),
    ('REPORTING_VIEW',     'Ver reportes y métricas agregadas'),
    ('FILE_MANAGE',        'Subir, descargar y eliminar archivos'),
    ('NOTIFICATION_VIEW',  'Ver notificaciones del sistema');

-- ------------------------------------------------------------------
-- ADMIN: todos los permisos
-- ------------------------------------------------------------------
INSERT INTO role_permission (role, permission_code)
    SELECT 'ADMIN', code FROM permission;

-- ------------------------------------------------------------------
-- USER: operación diaria, SIN las acciones reservadas a administración
-- (ASSET_DISPOSE, TENANT_MANAGE, USER_MANAGE quedan solo para ADMIN)
-- ------------------------------------------------------------------
INSERT INTO role_permission (role, permission_code) VALUES
    ('USER', 'PRODUCT_MANAGE'),
    ('USER', 'WAREHOUSE_MANAGE'),
    ('USER', 'INVENTORY_ADJUST'),
    ('USER', 'INVENTORY_TRANSFER'),
    ('USER', 'ASSET_MANAGE'),
    ('USER', 'DELIVERY_MANAGE'),
    ('USER', 'REPORTING_VIEW'),
    ('USER', 'FILE_MANAGE'),
    ('USER', 'NOTIFICATION_VIEW');
