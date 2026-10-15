package com.openbravo.ui.components;

import java.awt.Component;
import java.awt.Window;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**
 * Utility helper methods for Swing component hierarchy navigation and parent discovery.
 *
 * @author KriolOS
 * @since 1.0.0
 */
public final class ComponentUtils {

    private ComponentUtils() {
        // Utility class: prevent instantiation
    }

    /**
     * Finds and returns the nearest enclosing {@link JFrame} ancestor for the given component.
     *
     * @param component the source component
     * @return the owning {@link JFrame}, or {@code null} if none is found
     */
    public static JFrame getParentFrame(Component component) {
        if (component == null) {
            return null;
        }
        Window window = SwingUtilities.getWindowAncestor(component);
        if (window instanceof JFrame frame) {
            return frame;
        }
        return null;
    }
}