package com.openbravo.ui.components;

import java.awt.Color;

/**
 * Centralized color palette and naming specification for KriolOS POS.
 * Establishes consistent semantic color naming across all UI components, buttons,
 * banners, blockers, and icons.
 *
 * @author KriolOS
 * @since 1.0.0
 */
public final class UIColor {

    // --- Core Semantic Status Palette ---
    public static final Color PRIMARY = new Color(37, 99, 235);    // Royal Blue #2563eb
    public static final Color SUCCESS = new Color(22, 163, 74);    // Emerald Green #16a34a
    public static final Color DANGER = new Color(239, 68, 68);     // Coral Red #ef4444
    public static final Color WARNING = new Color(245, 158, 11);   // Amber Orange #f59e0b
    public static final Color INFO = new Color(59, 130, 246);      // Sky Blue #3b82f6
    public static final Color MUTED = new Color(156, 163, 175);    // Slate Gray #9ca3af

    // --- Neutral Surface & Text Palette ---
    public static final Color DARK = new Color(30, 41, 59);        // Slate Dark #1e293b
    public static final Color LIGHT = new Color(248, 250, 252);    // Off-White #f8fafc

    // --- Standard Semantic Aliases (Ensuring Full Naming Uniformity) ---
    public static final Color ACCENT = PRIMARY;
    public static final Color ERROR = DANGER;

    // --- Component Suffix Aliases (For Backward Compatibility) ---
    public static final Color PRIMARY_COLOR = PRIMARY;
    public static final Color SUCCESS_COLOR = SUCCESS;
    public static final Color DANGER_COLOR = DANGER;
    public static final Color WARNING_COLOR = WARNING;
    public static final Color ACCENT_COLOR = ACCENT;
    public static final Color ERROR_COLOR = ERROR;
    public static final Color INFO_COLOR = INFO;
    public static final Color MUTED_COLOR = MUTED;

    private UIColor() {
        // Utility class: prevent instantiation
    }
}
