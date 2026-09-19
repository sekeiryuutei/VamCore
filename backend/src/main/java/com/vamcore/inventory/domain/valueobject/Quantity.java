package com.vamcore.inventory.domain.valueobject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * Cantidad de inventario con su unidad de medida (ver sección 35 del
 * documento de arquitectura). No todas las cantidades son enteros:
 * pueden ser "10 unidades", "2.5 kg", "3.75 metros".
 *
 * Por simplicidad, esta versión inicial no valida compatibilidad de
 * unidades al operar (eso pertenece a una tabla de conversión de UoM
 * que se agregará cuando el negocio lo requiera).
 */
public final class Quantity {

    private final BigDecimal value;
    private final String unit;

    private Quantity(BigDecimal value, String unit) {
        this.value = value.setScale(4, RoundingMode.HALF_UP);
        this.unit = unit;
    }

    public static Quantity of(BigDecimal value, String unit) {
        Objects.requireNonNull(value, "La cantidad no puede ser null");
        Objects.requireNonNull(unit, "La unidad de medida es obligatoria");
        return new Quantity(value, unit);
    }

    public static Quantity zero(String unit) {
        return of(BigDecimal.ZERO, unit);
    }

    public Quantity add(Quantity other) {
        assertSameUnit(other);
        return new Quantity(this.value.add(other.value), this.unit);
    }

    public Quantity subtract(Quantity other) {
        assertSameUnit(other);
        return new Quantity(this.value.subtract(other.value), this.unit);
    }

    public boolean isNegative() {
        return value.compareTo(BigDecimal.ZERO) < 0;
    }

    public boolean isLessThan(Quantity other) {
        assertSameUnit(other);
        return this.value.compareTo(other.value) < 0;
    }

    private void assertSameUnit(Quantity other) {
        if (!this.unit.equalsIgnoreCase(other.unit)) {
            throw new IllegalArgumentException(
                "No se pueden operar cantidades con unidades distintas: " + this.unit + " vs " + other.unit);
        }
    }

    public BigDecimal value() {
        return value;
    }

    public String unit() {
        return unit;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Quantity quantity)) return false;
        return value.compareTo(quantity.value) == 0 && unit.equalsIgnoreCase(quantity.unit);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value.stripTrailingZeros(), unit.toLowerCase());
    }

    @Override
    public String toString() {
        return value + " " + unit;
    }
}
