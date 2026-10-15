package com.openbravo.ui.icon;

import java.awt.Color;
import javax.swing.Icon;

/**
 * Standard vector icon catalog for POS operational actions and touch interfaces.
 * Delegates to {@link IconFactory} and {@link IconKey} for agnostic vector resolution and coloring.
 *
 * @author KriolOS
 * @since 1.0.0
 */
public enum PosIcon {

    PAY(IconKey.PAY),
    CUSTOMER(IconKey.CUSTOMER),
    USERS(IconKey.USERS),
    CASH(IconKey.CASH),
    HOLD(IconKey.HOLD),
    PARKED(IconKey.PARKED),
    ORDERS(IconKey.ORDERS),
    CASH_IN(IconKey.CASH_IN),
    CASH_OUT(IconKey.CASH_OUT),
    CLOSE_CASH(IconKey.CLOSE_CASH),
    MINUS(IconKey.MINUS),
    PLUS(IconKey.PLUS),
    EDIT(IconKey.EDIT),
    DELETE(IconKey.DELETE),
    CLEAR(IconKey.CLEAR),
    DISCOUNT(IconKey.DISCOUNT),
    SEARCH(IconKey.SEARCH),
    SETTINGS(IconKey.SETTINGS),
    PRINTER(IconKey.PRINTER),
    LOCK(IconKey.LOCK),
    UNLOCK(IconKey.UNLOCK),
    OK(IconKey.OK),
    CANCEL(IconKey.CANCEL),
    NAV_BACK(IconKey.NAV_BACK),
    NAV_FORWARD(IconKey.NAV_FORWARD),
    BACKSPACE(IconKey.BACKSPACE),
    DATABASE(IconKey.DATABASE);

    private final IconKey iconKey;

    PosIcon(IconKey iconKey) {
        this.iconKey = iconKey;
    }

    public IconKey getIconKey() {
        return iconKey;
    }

    /**
     * Creates a square {@link Icon} for this vector asset at the specified dimension with ambient theming.
     *
     * @param size target width and height in pixels
     * @return vector SVG icon instance
     */
    public Icon asIcon(int size) {
        return IconFactory.getIcon(iconKey, size);
    }

    /**
     * Creates a square {@link Icon} for this vector asset at the specified dimension with an explicit tint color.
     *
     * @param size target width and height in pixels
     * @param color target icon color
     * @return vector SVG icon instance
     */
    public Icon asIcon(int size, Color color) {
        return IconFactory.getIcon(iconKey, size, color);
    }

    /**
     * Creates an {@link Icon} for this vector asset at the specified dimensions.
     *
     * @param width target width in pixels
     * @param height target height in pixels
     * @return vector SVG icon instance
     */
    public Icon asIcon(int width, int height) {
        return IconFactory.getIcon(iconKey, width, height, null);
    }

    /**
     * Creates an {@link Icon} for this vector asset at the specified dimensions with an explicit tint color.
     *
     * @param width target width in pixels
     * @param height target height in pixels
     * @param color target icon color
     * @return vector SVG icon instance
     */
    public Icon asIcon(int width, int height, Color color) {
        return IconFactory.getIcon(iconKey, width, height, color);
    }
}
