#!/usr/bin/env bash
# Detiene todos los contenedores de VamCore.
# Uso: ./scripts/docker-down.sh          -> detiene, conserva los datos
#      ./scripts/docker-down.sh --purge  -> detiene y BORRA el volumen de PostgreSQL
set -euo pipefail
ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$ROOT_DIR/infrastructure"

if [ "${1:-}" = "--purge" ]; then
  echo "Deteniendo contenedores y BORRANDO el volumen de datos de PostgreSQL..."
  docker compose down -v
else
  docker compose down
fi
