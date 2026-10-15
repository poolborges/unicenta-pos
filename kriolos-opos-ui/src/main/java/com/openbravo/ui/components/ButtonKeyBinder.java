package com.openbravo.ui.components;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

/**
 * Thread-safe utility to bind keyboard shortcuts to Swing {@link JButton} components
 * without blocking the Event Dispatch Thread (EDT).
 *
 * @author KriolOS
 * @since 1.0.0
 */
public class ButtonKeyBinder {

    private ButtonKeyBinder() {
        // Utility class: prevent instantiation
    }

    /**
     * Binds a key stroke to trigger button action safely on the Event Dispatch Thread.
     *
     * @param button the target button
     * @param keyCode the KeyEvent key code
     * @param actionName the unique action identifier
     */
    public static void bindKey(final JButton button, final int keyCode, final String actionName) {
        if (button == null) {
            return;
        }

        SwingUtilities.invokeLater(() -> {
            button.setFocusable(false);

            KeyStroke stroke = KeyStroke.getKeyStroke(keyCode, 0);

            // 1. Safe cleanup of existing Swing input and action maps
            button.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).remove(stroke);
            button.getActionMap().remove(actionName);

            // 2. Register the new shortcut
            button.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(stroke, actionName);
            button.getActionMap().put(actionName, new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (button.isEnabled() && button.isVisible()) {
                        button.doClick();
                    }
                }
            });

            // 3. Smooth repaint of the parent container
            JComponent parent = (JComponent) button.getParent();
            if (parent != null) {
                parent.revalidate();
                parent.repaint();
            }
        });
    }

    /**
     * Legacy alias for {@link #bindKey(JButton, int, String)}.
     *
     * @param button the target button
     * @param keyCode the KeyEvent key code
     * @param actionName the unique action identifier
     */
    public static void bindNumpadKey(final JButton button, final int keyCode, final String actionName) {
        bindKey(button, keyCode, actionName);
    }
}
