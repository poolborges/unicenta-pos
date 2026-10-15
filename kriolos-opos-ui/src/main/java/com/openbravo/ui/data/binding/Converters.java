package com.openbravo.ui.data.binding;

import java.math.BigDecimal;

/**
 * Standard implementations of {@link Converter} for common Java types.
 *
 * @author KriolOS
 * @since 1.0.0
 */
public final class Converters {

    private Converters() {
        // Utility class constructor
    }

    public static class StringToInteger implements Converter<String, Integer> {
        @Override public Integer toModel(String p) { return p.isEmpty() ? null : Integer.parseInt(p); }
        @Override public String toPresentation(Integer m) { return m != null ? String.valueOf(m) : ""; }
    }

    public static class StringToDouble implements Converter<String, Double> {
        @Override public Double toModel(String p) { return p.isEmpty() ? null : Double.parseDouble(p); }
        @Override public String toPresentation(Double m) { return m != null ? String.valueOf(m) : ""; }
    }

    public static class StringToFloat implements Converter<String, Float> {
        @Override public Float toModel(String p) { return p.isEmpty() ? null : Float.parseFloat(p); }
        @Override public String toPresentation(Float m) { return m != null ? String.valueOf(m) : ""; }
    }

    public static class StringToLong implements Converter<String, Long> {
        @Override public Long toModel(String p) { return p.isEmpty() ? null : Long.parseLong(p); }
        @Override public String toPresentation(Long m) { return m != null ? String.valueOf(m) : ""; }
    }

    public static class StringToBigDecimal implements Converter<String, BigDecimal> {
        @Override public BigDecimal toModel(String p) { return p.isEmpty() ? null : new BigDecimal(p); }
        @Override public String toPresentation(BigDecimal m) { return m != null ? m.toPlainString() : ""; }
    }
}
