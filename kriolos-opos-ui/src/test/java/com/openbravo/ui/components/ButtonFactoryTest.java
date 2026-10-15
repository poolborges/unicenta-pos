package com.openbravo.ui.components;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.JButton;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link ButtonFactory} and {@link ButtonSize}.
 *
 * @author KriolOS
 * @since 1.0.0
 */
class ButtonFactoryTest {

    @Test
    @DisplayName("createButton creates touch button with appropriate dimensions and sizing")
    void testCreateButton() {
        JButton btn = ButtonFactory.createButton(ButtonSize.EXTRA_LARGE);
        assertNotNull(btn);
        assertEquals(48, btn.getPreferredSize().height);
        assertFalse(btn.isFocusable());
        assertFalse(btn.isFocusPainted());
    }

    @Test
    @DisplayName("createActionButton sets text, fontIcon, mnemonic, and annotated tooltip")
    void testCreateActionButton() {
        AtomicBoolean clicked = new AtomicBoolean(false);
        JButton btn = ButtonFactory.createActionButton(
                "Delete",
                "[DEL]",
                ButtonSize.EXTRA_LARGE,
                KeyEvent.VK_R,
                "Delete item",
                e -> clicked.set(true)
        );

        assertNotNull(btn);
        assertTrue(btn.getText().contains("[DEL]"));
        assertTrue(btn.getText().contains("Delete"));
        assertEquals(KeyEvent.VK_R, btn.getMnemonic());
        assertNotNull(btn.getToolTipText());
        assertTrue(btn.getToolTipText().contains("Alt+R"));
        assertEquals(48, btn.getPreferredSize().height);

        btn.doClick();
        assertTrue(clicked.get());
    }

    @Test
    @DisplayName("createSuccessButton creates emerald green button with massive size")
    void testCreateSuccessButton() {
        JButton btn = ButtonFactory.createSuccessButton(
                "PAY",
                "[PAY]",
                ButtonSize.MASSIVE,
                KeyEvent.VK_P,
                "Pay order",
                null
        );

        assertNotNull(btn);
        assertEquals(56, btn.getPreferredSize().height);
        assertEquals(ButtonFactory.SUCCESS_COLOR, btn.getBackground());
        assertEquals(Color.WHITE, btn.getForeground());
        assertTrue(btn.getText().contains("[PAY]"));
    }

    @Test
    @DisplayName("createDangerButton creates red button with appropriate color")
    void testCreateDangerButton() {
        JButton btn = ButtonFactory.createDangerButton(
                "Close Cash",
                "[LOCK]",
                ButtonSize.EXTRA_LARGE,
                KeyEvent.VK_F,
                "Close",
                null
        );

        assertNotNull(btn);
        assertEquals(ButtonFactory.DANGER_COLOR, btn.getBackground());
        assertEquals(Color.WHITE, btn.getForeground());
    }

    @Test
    @DisplayName("formatButtonText handles null, empty, and non-empty glyphs")
    void testFormatButtonText() {
        assertEquals("[USER]  Customer", ButtonFactory.formatButtonText("Customer", "[USER]"));
        assertEquals("Customer", ButtonFactory.formatButtonText("Customer", (String) null));
        assertEquals("Customer", ButtonFactory.formatButtonText("Customer", ""));
        assertEquals("[USER]", ButtonFactory.formatButtonText(null, "[USER]"));
    }
}
