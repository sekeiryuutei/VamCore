# VamCore Frontend

Angular (standalone components) — features con lazy loading para
VamStock (inventory), VamAsset (assets) y VamTrack (logistics).

## Forma recomendada: Docker

Este frontend está pensado para correr dentro de Docker (build de producción
servido con nginx). Ver la guía completa en el
[README principal del repositorio](../README.md).

```bash
cd ../infrastructure
docker compose up -d --build frontend
```

## Modo desarrollo local (con hot-reload, sin Docker)

Requiere Node.js 20+ y Angular CLI 18 (`npm install -g @angular/cli`).

```bash
npm install
npm start
```

Los nombres de marca mostrados en pantalla se leen de
`src/environments/environment.ts`, sincronizado con `/product.config.yml`.
