package com.vamcore.assets.domain.valueobject;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Código único de identificación de un activo dentro de un tenant
 * (ver UNIQUE(tenant_id, asset_code), sección 33 del documento de arquitectura).
 */
public final class AssetCode {

    private static final Pattern VALID_FORMAT = Pattern.compile("^[A-Za-z0-9._-]{2,64}$");

    private final String value;

    private AssetCode(String value) {
        this.value = value;
    }

    public static AssetCode of(String value) {
        Objects.requireNonNull(value, "El código de activo no puede ser null");
        String trimmed = value.trim().toUpperCase();
        if (!VALID_FORMAT.matcher(trimmed).matches()) {
            throw new IllegalArgumentException(
                "Código de activo inválido: '" + value + "'. Debe tener 2-64 caracteres alfanuméricos, '.', '_' o '-'.");
        }
        return new AssetCode(trimmed);
    }

    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AssetCode that)) return false;
        return value.equals(that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
