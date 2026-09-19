#!/usr/bin/env bash
# Levanta (o reconstruye) todos los contenedores de VamCore.
set -euo pipefail
ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$ROOT_DIR/infrastructure"

if [ ! -f ".env" ]; then
  echo "No existe infrastructure/.env, ejecuta primero ./scripts/setup.sh" >&2
  exit 1
fi

docker compose up -d --build
docker compose ps
