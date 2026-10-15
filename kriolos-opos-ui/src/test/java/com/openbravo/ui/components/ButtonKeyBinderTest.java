package com.openbravo.ui.components;

import java.awt.event.KeyEvent;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link ButtonKeyBinder}.
 *
 * @author KriolOS
 * @since 1.0.0
 */
class ButtonKeyBinderTest {

    @Test
    @DisplayName("bindKey handles null button without throwing exception")
    void testBindKeyNullButton() {
        assertDoesNotThrow(() -> ButtonKeyBinder.bindKey(null, KeyEvent.VK_ENTER, "testAction"));
    }

    @Test
    @DisplayName("bindKey binds KeyStroke to JButton on EDT")
    void testBindKey() throws Exception {
        JButton button = new JButton("Test");
        AtomicBoolean clicked = new AtomicBoolean(false);
        button.addActionListener(e -> clicked.set(true));

        ButtonKeyBinder.bindKey(button, KeyEvent.VK_F1, "f1Action");

        // Wait for EDT execution
        SwingUtilities.invokeAndWait(() -> {
            assertFalse(button.isFocusable());
            KeyStroke stroke = KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0);
            Object actionKey = button.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).get(stroke);
            assertEquals("f1Action", actionKey);
            assertNotNull(button.getActionMap().get("f1Action"));
        });
    }

    @Test
    @DisplayName("bindNumpadKey delegates to bindKey")
    void testBindNumpadKey() throws Exception {
        JButton button = new JButton("Numpad");
        ButtonKeyBinder.bindNumpadKey(button, KeyEvent.VK_NUMPAD1, "num1Action");

        SwingUtilities.invokeAndWait(() -> {
            KeyStroke stroke = KeyStroke.getKeyStroke(KeyEvent.VK_NUMPAD1, 0);
            Object actionKey = button.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).get(stroke);
            assertEquals("num1Action", actionKey);
        });
    }
}
