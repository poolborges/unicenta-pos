package com.openbravo.ui.components;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.awt.*;

import static org.junit.jupiter.api.Assertions.*;

class PaneBuilderTest {

    @Test
    @DisplayName("PaneBuilder creates a functional form with dynamic component height")
    void testDynamicFieldHeight() {
        PaneBuilder builder = new PaneBuilder();
        JTextField field = new JTextField();
        field.setFont(new Font("SansSerif", Font.PLAIN, 20));

        builder.add("Price", field);
        JPanel panel = builder.build();

        assertNotNull(panel);
        Dimension fieldPref = field.getPreferredSize();
        int fontHeight = field.getFontMetrics(field.getFont()).getHeight();

        assertTrue(fieldPref.height >= fontHeight + 10,
                "Field in PaneBuilder must scale dynamically with 20pt font, got: " + fieldPref.height);
    }

    @Test
    @DisplayName("PaneBuilder preserves touch minimum floor on compact fonts")
    void testTouchFloorOnSmallFont() {
        PaneBuilder builder = new PaneBuilder();
        JTextField field = new JTextField();
        field.setFont(new Font("SansSerif", Font.PLAIN, 10));

        builder.add("Code", field);
        builder.build();

        Dimension fieldPref = field.getPreferredSize();
        assertTrue(fieldPref.height >= 35,
                "Small font field must still respect touch floor (35px), got: " + fieldPref.height);
    }
}
