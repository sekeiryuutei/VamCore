package com.vamcore.logistics.domain.valueobject;

import java.util.Objects;
import java.util.regex.Pattern;

/** Código único de una entrega dentro de un tenant (ver sección 19). */
public final class DeliveryCode {

    private static final Pattern VALID_FORMAT = Pattern.compile("^[A-Za-z0-9._-]{2,64}$");

    private final String value;

    private DeliveryCode(String value) {
        this.value = value;
    }

    public static DeliveryCode of(String value) {
        Objects.requireNonNull(value, "El código de entrega no puede ser null");
        String trimmed = value.trim().toUpperCase();
        if (!VALID_FORMAT.matcher(trimmed).matches()) {
            throw new IllegalArgumentException(
                "Código de entrega inválido: '" + value + "'. Debe tener 2-64 caracteres alfanuméricos, '.', '_' o '-'.");
        }
        return new DeliveryCode(trimmed);
    }

    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DeliveryCode that)) return false;
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
