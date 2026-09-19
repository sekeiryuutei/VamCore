#!/usr/bin/env bash
# Sigue los logs de todos los servicios (o de uno solo: ./docker-logs.sh backend)
set -euo pipefail
ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$ROOT_DIR/infrastructure"
docker compose logs -f "$@"
