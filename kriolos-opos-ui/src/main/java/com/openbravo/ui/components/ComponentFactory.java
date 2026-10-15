package com.openbravo.ui.components;

import javax.swing.*;
import java.awt.*;

/**
 * Component factory responsible for standardizing input dimensions,
 * formatting, and accessible tooltips across POS dialogs.
 *
 * @author KriolOS
 * @since 1.0.0
 */
public final class ComponentFactory {

    public static final int FIELD_HEIGHT = 35;
    public static final int FOOTER_HEIGHT = 45;
    public static final int MIN_WIDTH = 150;

    private ComponentFactory() {
        // Utility class: prevent instantiation
    }

    /**
     * Creates a standardized label with right-alignment and automatic tooltip generation.
     *
     * @param text the label text
     * @return configured JLabel
     */
    public static JLabel createLabel(String text) {
        if (text == null) {
            return new JLabel("");
        }
        String plainText = text.trim();
        if (plainText.endsWith(":")) {
            plainText = plainText.substring(0, plainText.length() - 1).trim();
        }

        JLabel label = new JLabel(plainText + ":");
        label.setToolTipText(plainText);
        label.setHorizontalAlignment(SwingConstants.RIGHT);
        return label;
    }

    /**
     * Enforces the ideal touch height and minimum width on form input components.
     *
     * @param comp the component to size
     */
    public static void enforceFieldSize(Component comp) {
        if (comp == null) return;
        Dimension min = new Dimension(MIN_WIDTH, FIELD_HEIGHT);
        comp.setMinimumSize(min);
        Dimension pref = comp.getPreferredSize();
        comp.setPreferredSize(new Dimension(Math.max(pref.width, MIN_WIDTH), FIELD_HEIGHT));
    }

    /**
     * Enforces enhanced button height for action footer controls.
     *
     * @param comp the component to size
     */
    public static void enforceFooterSize(Component comp) {
        if (comp == null) return;
        comp.setMinimumSize(new Dimension(MIN_WIDTH, FOOTER_HEIGHT));
        Dimension pref = comp.getPreferredSize();
        comp.setPreferredSize(new Dimension(Math.max(pref.width, MIN_WIDTH), FOOTER_HEIGHT));
    }
}
