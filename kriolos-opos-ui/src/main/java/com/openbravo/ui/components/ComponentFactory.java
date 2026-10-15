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
     * Calculates the minimum comfortable component height based on font metrics.
     *
     * @param comp the component to evaluate
     * @return the calculated height in pixels
     */
    public static int calculateFontMetricHeight(Component comp) {
        if (comp == null) {
            return FIELD_HEIGHT;
        }
        Font font = comp.getFont();
        if (font == null) {
            font = UIManager.getFont("TextField.font");
            if (font == null) {
                font = UIManager.getFont("defaultFont");
            }
        }
        if (font != null) {
            FontMetrics fm = comp.getFontMetrics(font);
            if (fm != null) {
                // Font line height + vertical breathing room (insets + border)
                return fm.getHeight() + 14;
            }
        }
        return FIELD_HEIGHT;
    }

    /**
     * Enforces touch-accessible minimum dimensions on form input components while
     * preserving dynamic font-metric height to prevent text truncation across multiple font sizes.
     *
     * @param comp the component to size
     */
    public static void enforceFieldSize(Component comp) {
        if (comp == null) return;
        Dimension pref = comp.getPreferredSize();
        int fontMetricHeight = calculateFontMetricHeight(comp);
        int targetHeight = Math.max(FIELD_HEIGHT, Math.max(pref != null ? pref.height : 0, fontMetricHeight));
        int targetWidth = (pref != null) ? Math.max(pref.width, MIN_WIDTH) : MIN_WIDTH;

        comp.setMinimumSize(new Dimension(MIN_WIDTH, FIELD_HEIGHT));
        comp.setPreferredSize(new Dimension(targetWidth, targetHeight));
    }

    /**
     * Enforces enhanced button height for action footer controls while dynamically
     * expanding to accommodate enlarged fonts.
     *
     * @param comp the component to size
     */
    public static void enforceFooterSize(Component comp) {
        if (comp == null) return;
        Dimension pref = comp.getPreferredSize();
        int fontMetricHeight = calculateFontMetricHeight(comp) + 6;
        int targetHeight = Math.max(FOOTER_HEIGHT, Math.max(pref != null ? pref.height : 0, fontMetricHeight));
        int targetWidth = (pref != null) ? Math.max(pref.width, MIN_WIDTH) : MIN_WIDTH;

        comp.setMinimumSize(new Dimension(MIN_WIDTH, FOOTER_HEIGHT));
        comp.setPreferredSize(new Dimension(targetWidth, targetHeight));
    }
}
