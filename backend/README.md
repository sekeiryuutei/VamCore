# VamCore Backend

Spring Boot (Java 21) — monolito modular con arquitectura hexagonal.

## Forma recomendada: Docker

Este backend está pensado para correr dentro de Docker junto al resto de la
plataforma. Ver la guía completa (paso a paso para una máquina nueva) en el
[README principal del repositorio](../README.md).

```bash
cd ../infrastructure
docker compose up -d --build backend
```

## Modo desarrollo local (sin Docker)

Requiere Java 21 y Maven 3.9+, y PostgreSQL corriendo (puedes levantar solo
esa pieza con `docker compose up -d postgres` desde `infrastructure/`).

```bash
mvn spring-boot:run
```

Ejecutar pruebas:

```bash
mvn test
```
