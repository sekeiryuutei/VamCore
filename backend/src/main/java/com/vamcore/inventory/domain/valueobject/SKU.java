package com.vamcore.inventory.domain.valueobject;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Stock Keeping Unit — identificador comercial único del producto dentro
 * de un tenant (ver ADR de unicidad: UNIQUE(tenant_id, sku), sección 33).
 */
public final class SKU {

    private static final Pattern VALID_FORMAT = Pattern.compile("^[A-Za-z0-9._-]{2,64}$");

    private final String value;

    private SKU(String value) {
        this.value = value;
    }

    public static SKU of(String value) {
        Objects.requireNonNull(value, "El SKU no puede ser null");
        String trimmed = value.trim().toUpperCase();
        if (!VALID_FORMAT.matcher(trimmed).matches()) {
            throw new IllegalArgumentException(
                "SKU inválido: '" + value + "'. Debe tener 2-64 caracteres alfanuméricos, '.', '_' o '-'.");
        }
        return new SKU(trimmed);
    }

    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SKU sku)) return false;
        return value.equals(sku.value);
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
