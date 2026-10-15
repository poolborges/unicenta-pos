package com.openbravo.ui.components;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.awt.*;

import static org.junit.jupiter.api.Assertions.*;

class ComponentFactoryTest {

    @Test
    @DisplayName("Create label with colon and tooltip")
    void testCreateLabel() {
        JLabel label = ComponentFactory.createLabel("Product Name");
        assertNotNull(label);
        assertEquals("Product Name:", label.getText());
        assertEquals("Product Name", label.getToolTipText());
        assertEquals(SwingConstants.RIGHT, label.getHorizontalAlignment());

        JLabel emptyLabel = ComponentFactory.createLabel(null);
        assertNotNull(emptyLabel);
        assertEquals("", emptyLabel.getText());
    }

    @Test
    @DisplayName("Enforce field size respects minimum touch floor with default font")
    void testEnforceFieldSizeDefaultFont() {
        JTextField field = new JTextField();
        field.setFont(new Font("SansSerif", Font.PLAIN, 12));

        ComponentFactory.enforceFieldSize(field);

        Dimension pref = field.getPreferredSize();
        assertTrue(pref.height >= ComponentFactory.FIELD_HEIGHT,
                "Preferred height should be at least FIELD_HEIGHT (35px), got: " + pref.height);
        assertTrue(pref.width >= ComponentFactory.MIN_WIDTH,
                "Preferred width should be at least MIN_WIDTH (150px), got: " + pref.width);
    }

    @Test
    @DisplayName("Enforce field size scales up dynamically for 16pt touch font")
    void testEnforceFieldSize16pt() {
        JTextField field = new JTextField();
        Font font16 = new Font("SansSerif", Font.PLAIN, 16);
        field.setFont(font16);

        ComponentFactory.enforceFieldSize(field);

        Dimension pref = field.getPreferredSize();
        int expectedMin = ComponentFactory.calculateFontMetricHeight(field);
        assertTrue(pref.height >= expectedMin,
                "Preferred height should scale up to font metric height (" + expectedMin + "), got: " + pref.height);
    }

    @Test
    @DisplayName("Enforce field size scales up dynamically for large 24pt font without truncation")
    void testEnforceFieldSize24pt() {
        JTextField field = new JTextField();
        Font font24 = new Font("SansSerif", Font.PLAIN, 24);
        field.setFont(font24);

        ComponentFactory.enforceFieldSize(field);

        Dimension pref = field.getPreferredSize();
        int fontHeight = field.getFontMetrics(font24).getHeight();
        assertTrue(pref.height >= fontHeight + 10,
                "Preferred height (" + pref.height + ") must exceed font height (" + fontHeight + ") with padding");
        assertTrue(pref.height > ComponentFactory.FIELD_HEIGHT,
                "Preferred height (" + pref.height + ") must expand beyond static FIELD_HEIGHT (35)");
    }

    @Test
    @DisplayName("Enforce footer size scales up dynamically with enlarged font")
    void testEnforceFooterSize() {
        JButton button = new JButton("Confirm Checkout");
        Font font20 = new Font("SansSerif", Font.BOLD, 20);
        button.setFont(font20);

        ComponentFactory.enforceFooterSize(button);

        Dimension pref = button.getPreferredSize();
        assertTrue(pref.height >= ComponentFactory.FOOTER_HEIGHT,
                "Footer height should be at least FOOTER_HEIGHT, got: " + pref.height);
        assertTrue(pref.height >= button.getFontMetrics(font20).getHeight() + 10,
                "Footer height must accommodate 20pt bold font comfortably without truncation");
    }

    @Test
    @DisplayName("Null component safety")
    void testNullComponentHandling() {
        assertDoesNotThrow(() -> ComponentFactory.enforceFieldSize(null));
        assertDoesNotThrow(() -> ComponentFactory.enforceFooterSize(null));
        assertEquals(ComponentFactory.FIELD_HEIGHT, ComponentFactory.calculateFontMetricHeight(null));
    }
}
