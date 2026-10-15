package com.openbravo.ui.animation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link RollingNumberLabel}.
 *
 * @author KriolOS
 */
@DisplayName("RollingNumberLabel Unit Tests")
class RollingNumberLabelTest {

    @Test
    @DisplayName("Verify default initialization values and formatting")
    void testInitialization() {
        RollingNumberLabel label = new RollingNumberLabel(1500.50, "CVE ", "");

        assertEquals(1500.50, label.getValue(), 0.001);
        assertEquals(1500.50, label.getTargetValue(), 0.001);
        assertTrue(label.getText().startsWith("CVE "));
        assertTrue(label.getText().contains("1,500.50") || label.getText().contains("1.500,50"));
    }

    @Test
    @DisplayName("Verify setValueImmediate sets target and updates text synchronously")
    void testSetValueImmediate() {
        RollingNumberLabel label = new RollingNumberLabel(0.0);
        label.setPrefix("$");
        label.setValueImmediate(250.75);

        assertEquals(250.75, label.getValue(), 0.001);
        assertEquals(250.75, label.getTargetValue(), 0.001);
        assertTrue(label.getText().contains("250.75") || label.getText().contains("250,75"));
    }

    @Test
    @DisplayName("Verify rollTo updates target value correctly")
    void testRollToTarget() {
        RollingNumberLabel label = new RollingNumberLabel(100.0);
        label.rollTo(500.0);

        assertEquals(500.0, label.getTargetValue(), 0.001);
    }

    @Test
    @DisplayName("Verify BigDecimal overload support")
    void testBigDecimalOverload() {
        RollingNumberLabel label = new RollingNumberLabel(0.0);
        label.rollTo(new BigDecimal("1234.56"));

        assertEquals(1234.56, label.getTargetValue(), 0.001);
    }

    @Test
    @DisplayName("Verify decimal precision changes")
    void testDecimalPrecision() {
        RollingNumberLabel label = new RollingNumberLabel(100.0);
        label.setDecimalPlaces(0);
        label.setValueImmediate(100.0);

        assertFalse(label.getText().contains(".00"));
        assertFalse(label.getText().contains(",00"));
    }
}
