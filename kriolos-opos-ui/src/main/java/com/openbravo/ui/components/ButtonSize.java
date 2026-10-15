package com.openbravo.ui.components;

/**
 * Standard touch-ready button size variants for POS action buttons, toolbars, and controls.
 *
 * @author KriolOS
 * @since 1.0.0
 */
public enum ButtonSize {
    MEDIUM(32, 12, 14),
    LARGE(40, 14, 15),
    EXTRA_LARGE(48, 16, 16), // Ideal for Touch / POS
    MASSIVE(56, 20, 18);     // Massive hero/checkout CTA

    public static final int DEFAULT_HEIGHT = 40;    
    public static final int DEFAULT_PADDING = 14;
    public static final int DEFAULT_FONT_SIZE = 16;

    private final int height;
    private final int paddingX;
    private final int fontSize;

    ButtonSize(int height, int paddingX, int fontSize) {
        this.height = height;
        this.paddingX = paddingX;
        this.fontSize = fontSize;
    }

    /**
     * Gets the preferred height of the button variant in pixels.
     *
     * @return the height in pixels
     */
    public int getHeight() {
        return this.height;
    }

    /**
     * Gets the horizontal padding in pixels.
     *
     * @return the horizontal padding in pixels
     */
    public int getPaddingX() {
        return this.paddingX;
    }

    /**
     * Gets the base font size in points.
     *
     * @return the font size in points
     */
    public int getFontSize() {
        return this.fontSize;
    }
}
