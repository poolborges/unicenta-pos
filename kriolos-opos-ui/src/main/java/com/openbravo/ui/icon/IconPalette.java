package com.openbravo.ui.icon;

import com.openbravo.ui.components.UIColor;
import java.awt.Color;

/**
 * Standard semantic color palette for POS action icons, status badges, and touch feedback.
 * Direct alias for {@link UIColor} constants to ensure project-wide naming uniformity.
 *
 * @author KriolOS
 * @since 1.0.0
 */
public final class IconPalette {

    public static final Color PRIMARY = UIColor.PRIMARY;
    public static final Color SUCCESS = UIColor.SUCCESS;
    public static final Color DANGER = UIColor.DANGER;
    public static final Color WARNING = UIColor.WARNING;
    public static final Color INFO = UIColor.INFO;
    public static final Color MUTED = UIColor.MUTED;

    private IconPalette() {
        // Utility class: prevent instantiation
    }
}
