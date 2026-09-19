#!/usr/bin/env bash
# ============================================================================
# scripts/setup.sh
#
# Deja TODA la plataforma corriendo en Docker con un solo comando:
# PostgreSQL + backend + frontend. Pensado para una máquina nueva que
# solo tiene Docker instalado (no requiere Java, Node ni Maven locales).
# ============================================================================
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$ROOT_DIR"

echo "== VamCore :: setup con Docker =="

if ! command -v docker &> /dev/null; then
  echo "ERROR: Docker no está instalado o no está en el PATH." >&2
  echo "Instálalo desde https://docs.docker.com/get-docker/ y vuelve a correr este script." >&2
  exit 1
fi

if ! docker compose version &> /dev/null; then
  echo "ERROR: 'docker compose' (plugin v2) no está disponible." >&2
  exit 1
fi

if [ ! -f "infrastructure/.env" ]; then
  echo "-> Creando infrastructure/.env a partir de .env.example"
  cp infrastructure/.env.example infrastructure/.env
  echo "   Revisa infrastructure/.env y ajusta VAMCORE_JWT_SECRET antes de producción."
else
  echo "-> infrastructure/.env ya existe, no se sobreescribe"
fi

echo "-> Construyendo y levantando PostgreSQL + backend + frontend (puede tardar la primera vez)"
(cd infrastructure && docker compose up -d --build)

echo ""
echo "-> Esperando a que el backend responda healthy..."
(cd infrastructure && docker compose ps)

# shellcheck disable=SC1091
set -a; source infrastructure/.env; set +a

echo ""
echo "Listo. Servicios disponibles:"
echo "  Frontend:  http://localhost:${VAMCORE_FRONTEND_PORT:-4200}"
echo "  Backend:   http://localhost:${VAMCORE_SERVER_PORT:-8080}/actuator/health"
echo "  Postgres:  localhost:${VAMCORE_DB_PORT:-5432}"
echo ""
echo "Comandos útiles:"
echo "  Ver logs:        ./scripts/docker-logs.sh"
echo "  Detener todo:    ./scripts/docker-down.sh"
echo "  Reiniciar todo:  ./scripts/docker-up.sh"
echo ""
echo "Si en algún momento cambian los nombres de marca, edita /product.config.yml"
echo "y corre ./scripts/rename-product.sh"
