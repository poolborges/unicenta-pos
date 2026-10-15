package com.openbravo.ui.data.binding;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link Converters}.
 * Validates bi-directional model and presentation transformations,
 * edge cases (null and empty strings), and numeric conversions.
 *
 * @author KriolOS
 * @since 1.0.0
 */
@DisplayName("Data Binding Converters Unit Tests")
class ConvertersTest {

    private static final String EMPTY_STRING = "";
    private static final String INT_STRING = "42";
    private static final Integer INT_VALUE = 42;

    private static final String DOUBLE_STRING = "19.99";
    private static final Double DOUBLE_VALUE = 19.99;

    private static final String FLOAT_STRING = "3.14";
    private static final Float FLOAT_VALUE = 3.14f;

    private static final String LONG_STRING = "9876543210";
    private static final Long LONG_VALUE = 9876543210L;

    private static final String BIG_DECIMAL_STRING = "1250.75";
    private static final BigDecimal BIG_DECIMAL_VALUE = new BigDecimal("1250.75");

    @Test
    @DisplayName("Verify StringToInteger converter")
    void testStringToInteger() {
        Converters.StringToInteger converter = new Converters.StringToInteger();

        assertEquals(INT_VALUE, converter.toModel(INT_STRING));
        assertNull(converter.toModel(EMPTY_STRING));

        assertEquals(INT_STRING, converter.toPresentation(INT_VALUE));
        assertEquals(EMPTY_STRING, converter.toPresentation(null));
    }

    @Test
    @DisplayName("Verify StringToDouble converter")
    void testStringToDouble() {
        Converters.StringToDouble converter = new Converters.StringToDouble();

        assertEquals(DOUBLE_VALUE, converter.toModel(DOUBLE_STRING), 0.0001);
        assertNull(converter.toModel(EMPTY_STRING));

        assertEquals(DOUBLE_STRING, converter.toPresentation(DOUBLE_VALUE));
        assertEquals(EMPTY_STRING, converter.toPresentation(null));
    }

    @Test
    @DisplayName("Verify StringToFloat converter")
    void testStringToFloat() {
        Converters.StringToFloat converter = new Converters.StringToFloat();

        assertEquals(FLOAT_VALUE, converter.toModel(FLOAT_STRING), 0.0001f);
        assertNull(converter.toModel(EMPTY_STRING));

        assertEquals(FLOAT_STRING, converter.toPresentation(FLOAT_VALUE));
        assertEquals(EMPTY_STRING, converter.toPresentation(null));
    }

    @Test
    @DisplayName("Verify StringToLong converter")
    void testStringToLong() {
        Converters.StringToLong converter = new Converters.StringToLong();

        assertEquals(LONG_VALUE, converter.toModel(LONG_STRING));
        assertNull(converter.toModel(EMPTY_STRING));

        assertEquals(LONG_STRING, converter.toPresentation(LONG_VALUE));
        assertEquals(EMPTY_STRING, converter.toPresentation(null));
    }

    @Test
    @DisplayName("Verify StringToBigDecimal converter")
    void testStringToBigDecimal() {
        Converters.StringToBigDecimal converter = new Converters.StringToBigDecimal();

        assertEquals(BIG_DECIMAL_VALUE, converter.toModel(BIG_DECIMAL_STRING));
        assertNull(converter.toModel(EMPTY_STRING));

        assertEquals(BIG_DECIMAL_STRING, converter.toPresentation(BIG_DECIMAL_VALUE));
        assertEquals(EMPTY_STRING, converter.toPresentation(null));
    }
}
