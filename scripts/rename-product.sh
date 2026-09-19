#!/usr/bin/env bash
# ============================================================================
# scripts/rename-product.sh
#
# Propaga los nombres definidos en /product.config.yml a todo el repositorio:
# backend (Java, pom.xml, application.yml), frontend (environments,
# package.json), docs, docker-compose, README.
#
# CÓMO USARLO:
#   1. Edita /product.config.yml con los nuevos nombres.
#   2. Corre:  ./scripts/rename-product.sh
#   3. Revisa `git diff` antes de hacer commit.
#   4. Si cambiaste `platform.java_base_package`, compila para verificar:
#      cd backend && mvn -q compile
#
# El script compara los valores NUEVOS (en product.config.yml) contra los
# valores ANTERIORES guardados en scripts/.product-snapshot.env, reemplaza
# solo lo que efectivamente cambió, y actualiza el snapshot al finalizar.
# ============================================================================
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$ROOT_DIR"

CONFIG_FILE="product.config.yml"
SNAPSHOT_FILE="scripts/.product-snapshot.env"

if [ ! -f "$CONFIG_FILE" ]; then
  echo "ERROR: no se encontró $CONFIG_FILE en la raíz del repo." >&2
  exit 1
fi

# --------------------------------------------------------------------------
# Extraer valores NUEVOS de product.config.yml (parser simple basado en grep,
# suficiente porque el archivo mantiene una estructura plana conocida).
# --------------------------------------------------------------------------
yaml_value() {
  # $1 = clave literal tal como aparece en el yml (ej: "  name:")
  grep -m1 "$1" "$CONFIG_FILE" | sed -E 's/^[^:]+:\s*"?([^"#]*)"?.*/\1/' | sed -E 's/[[:space:]]+$//'
}

NEW_COMPANY_NAME="$(yaml_value '  name: "')"
NEW_PLATFORM_NAME="$(grep -A1 '^platform:' "$CONFIG_FILE" | yaml_value '  name:')"
NEW_PLATFORM_SLUG="$(grep -A2 '^platform:' "$CONFIG_FILE" | yaml_value '  slug:')"
NEW_JAVA_BASE_PACKAGE="$(grep -A3 '^platform:' "$CONFIG_FILE" | yaml_value '  java_base_package:')"

NEW_INVENTORY_DISPLAY_NAME="$(awk '/key: "inventory"/{f=1} f&&/display_name:/{print;exit}' "$CONFIG_FILE" | sed -E 's/^[^:]+:\s*"?([^"#]*)"?.*/\1/')"
NEW_INVENTORY_SLUG="$(awk '/key: "inventory"/{f=1} f&&/slug:/{print;exit}' "$CONFIG_FILE" | sed -E 's/^[^:]+:\s*"?([^"#]*)"?.*/\1/')"
NEW_ASSETS_DISPLAY_NAME="$(awk '/key: "assets"/{f=1} f&&/display_name:/{print;exit}' "$CONFIG_FILE" | sed -E 's/^[^:]+:\s*"?([^"#]*)"?.*/\1/')"
NEW_ASSETS_SLUG="$(awk '/key: "assets"/{f=1} f&&/slug:/{print;exit}' "$CONFIG_FILE" | sed -E 's/^[^:]+:\s*"?([^"#]*)"?.*/\1/')"
NEW_LOGISTICS_DISPLAY_NAME="$(awk '/key: "logistics"/{f=1} f&&/display_name:/{print;exit}' "$CONFIG_FILE" | sed -E 's/^[^:]+:\s*"?([^"#]*)"?.*/\1/')"
NEW_LOGISTICS_SLUG="$(awk '/key: "logistics"/{f=1} f&&/slug:/{print;exit}' "$CONFIG_FILE" | sed -E 's/^[^:]+:\s*"?([^"#]*)"?.*/\1/')"

# --------------------------------------------------------------------------
# Cargar valores ANTERIORES del snapshot
# --------------------------------------------------------------------------
# shellcheck disable=SC1090
source "$SNAPSHOT_FILE"

# --------------------------------------------------------------------------
# Reemplazo de texto en todo el repo, excluyendo directorios generados
# --------------------------------------------------------------------------
EXCLUDES=(-not -path '*/.git/*' -not -path '*/node_modules/*' -not -path '*/target/*' -not -path '*/dist/*' -not -path '*/.angular/*')

replace_text() {
  local old="$1" new="$2"
  if [ -z "$old" ] || [ -z "$new" ] || [ "$old" = "$new" ]; then
    return 0
  fi
  echo "  - Reemplazando '$old' -> '$new'"
  # Usamos find + sed (portable en Linux/macOS con GNU sed; en macOS con BSD sed
  # cambia `sed -i` por `sed -i ''`).
  find . -type f "${EXCLUDES[@]}" -print0 | xargs -0 grep -lZ "$old" 2>/dev/null | \
    xargs -0 -r sed -i "s|$old|$new|g"
}

echo "== VamCore :: propagando nombres de product.config.yml =="

replace_text "$COMPANY_NAME" "$NEW_COMPANY_NAME"
replace_text "$PLATFORM_NAME" "$NEW_PLATFORM_NAME"
replace_text "$PLATFORM_SLUG" "$NEW_PLATFORM_SLUG"
replace_text "$INVENTORY_DISPLAY_NAME" "$NEW_INVENTORY_DISPLAY_NAME"
replace_text "$INVENTORY_SLUG" "$NEW_INVENTORY_SLUG"
replace_text "$ASSETS_DISPLAY_NAME" "$NEW_ASSETS_DISPLAY_NAME"
replace_text "$ASSETS_SLUG" "$NEW_ASSETS_SLUG"
replace_text "$LOGISTICS_DISPLAY_NAME" "$NEW_LOGISTICS_DISPLAY_NAME"
replace_text "$LOGISTICS_SLUG" "$NEW_LOGISTICS_SLUG"

# --------------------------------------------------------------------------
# Caso especial: cambio de paquete Java base (mueve directorios físicamente)
# --------------------------------------------------------------------------
if [ -n "$NEW_JAVA_BASE_PACKAGE" ] && [ "$JAVA_BASE_PACKAGE" != "$NEW_JAVA_BASE_PACKAGE" ]; then
  echo "  - Moviendo paquete Java '$JAVA_BASE_PACKAGE' -> '$NEW_JAVA_BASE_PACKAGE'"
  OLD_PATH="backend/src/main/java/$(echo "$JAVA_BASE_PACKAGE" | tr '.' '/')"
  NEW_PATH="backend/src/main/java/$(echo "$NEW_JAVA_BASE_PACKAGE" | tr '.' '/')"
  OLD_TEST_PATH="backend/src/test/java/$(echo "$JAVA_BASE_PACKAGE" | tr '.' '/')"
  NEW_TEST_PATH="backend/src/test/java/$(echo "$NEW_JAVA_BASE_PACKAGE" | tr '.' '/')"

  mkdir -p "$(dirname "$NEW_PATH")"
  mv "$OLD_PATH" "$NEW_PATH"
  if [ -d "$OLD_TEST_PATH" ]; then
    mkdir -p "$(dirname "$NEW_TEST_PATH")"
    mv "$OLD_TEST_PATH" "$NEW_TEST_PATH"
  fi

  replace_text "$JAVA_BASE_PACKAGE" "$NEW_JAVA_BASE_PACKAGE"
fi

# --------------------------------------------------------------------------
# Actualizar el snapshot con los valores nuevos
# --------------------------------------------------------------------------
cat > "$SNAPSHOT_FILE" << EOF
# Ver comentario en la cabecera de rename-product.sh. NO editar a mano.
COMPANY_NAME=$NEW_COMPANY_NAME
PLATFORM_NAME=$NEW_PLATFORM_NAME
PLATFORM_SLUG=$NEW_PLATFORM_SLUG
JAVA_BASE_PACKAGE=$NEW_JAVA_BASE_PACKAGE
INVENTORY_DISPLAY_NAME=$NEW_INVENTORY_DISPLAY_NAME
INVENTORY_SLUG=$NEW_INVENTORY_SLUG
ASSETS_DISPLAY_NAME=$NEW_ASSETS_DISPLAY_NAME
ASSETS_SLUG=$NEW_ASSETS_SLUG
LOGISTICS_DISPLAY_NAME=$NEW_LOGISTICS_DISPLAY_NAME
LOGISTICS_SLUG=$NEW_LOGISTICS_SLUG
EOF

echo ""
echo "Listo. Revisa 'git diff' y compila el backend antes de hacer commit:"
echo "  cd backend && mvn -q compile"
