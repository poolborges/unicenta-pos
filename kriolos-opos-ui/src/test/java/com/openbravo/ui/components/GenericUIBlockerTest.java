package com.openbravo.ui.components;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.awt.Color;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link GenericUIBlocker}.
 * Validates transparency clamping, custom styling, blocking state transitions,
 * and cancel callbacks.
 *
 * @author KriolOS
 * @since 1.0.0
 */
@DisplayName("GenericUIBlocker Screen Blocker Unit Tests")
class GenericUIBlockerTest {

    private static final int CUSTOM_ALPHA = 180;
    private static final float CUSTOM_OPACITY = 0.5f;
    private static final int EXPECTED_HALF_ALPHA = 128; // round(0.5 * 255)
    private static final int FLASH_DURATION_MS = 750;

    @Test
    @DisplayName("Verify default configuration constants")
    void testDefaultConfiguration() {
        GenericUIBlocker blocker = new GenericUIBlocker();

        assertFalse(blocker.isBlocking());
        assertEquals(GenericUIBlocker.DEFAULT_MAX_ALPHA, blocker.getMaxAlpha());
        assertEquals(GenericUIBlocker.DEFAULT_ALPHA_STEP, blocker.getAlphaStep());
        assertEquals(GenericUIBlocker.DEFAULT_SPINNER_COLOR, blocker.getSpinnerColor());
        assertEquals(GenericUIBlocker.DEFAULT_TEXT_COLOR, blocker.getTextColor());
        assertEquals(GenericUIBlocker.DEFAULT_FLASH_COLOR, blocker.getFlashColor());
    }

    @Test
    @DisplayName("Verify transparency clamping and opacity float conversions")
    void testTransparencyClamping() {
        GenericUIBlocker blocker = new GenericUIBlocker();

        blocker.setMaxAlpha(CUSTOM_ALPHA);
        assertEquals(CUSTOM_ALPHA, blocker.getMaxAlpha());

        // Test boundary clamping
        blocker.setMaxAlpha(300);
        assertEquals(255, blocker.getMaxAlpha());

        blocker.setMaxAlpha(-50);
        assertEquals(0, blocker.getMaxAlpha());

        // Float opacity conversion
        blocker.setOpacity(CUSTOM_OPACITY);
        assertEquals(EXPECTED_HALF_ALPHA, blocker.getMaxAlpha());
        assertEquals(EXPECTED_HALF_ALPHA / 255.0f, blocker.getOpacity(), 0.01f);
    }

    @Test
    @DisplayName("Verify background color with custom alpha sets maximum opacity")
    void testBackgroundColorWithAlpha() {
        Color customBg = new Color(30, 40, 50, 200);
        GenericUIBlocker blocker = new GenericUIBlocker(customBg, 140);

        assertEquals(200, blocker.getMaxAlpha());
        assertEquals(new Color(30, 40, 50), blocker.getBackgroundColor());
    }

    @Test
    @DisplayName("Verify blocking state transitions and mode configuration")
    void testBlockingStateTransitions() {
        GenericUIBlocker blocker = new GenericUIBlocker();

        assertFalse(blocker.isBlocking());

        blocker.setBlocking(true, GenericUIBlocker.Mode.SPINNER);
        assertTrue(blocker.isBlocking());

        blocker.setBlocking(true, GenericUIBlocker.Mode.FLASH_ERROR);
        assertTrue(blocker.isBlocking());

        blocker.setBlocking(false);
        assertFalse(blocker.isBlocking());
    }

    @Test
    @DisplayName("Verify custom visual styling colors and timing configuration")
    void testVisualStylingColors() {
        GenericUIBlocker blocker = new GenericUIBlocker();

        blocker.setSpinnerColor(Color.CYAN);
        assertEquals(Color.CYAN, blocker.getSpinnerColor());

        blocker.setTextColor(Color.YELLOW);
        assertEquals(Color.YELLOW, blocker.getTextColor());

        blocker.setHintTextColor(Color.LIGHT_GRAY);
        assertEquals(Color.LIGHT_GRAY, blocker.getHintTextColor());

        blocker.setFlashColor(Color.MAGENTA);
        assertEquals(Color.MAGENTA, blocker.getFlashColor());

        blocker.setFlashDurationMs(FLASH_DURATION_MS);
        assertEquals(FLASH_DURATION_MS, blocker.getFlashDurationMs());
    }

    @Test
    @DisplayName("Verify cancel abort callback assignment")
    void testCancelCallbackAssignment() {
        GenericUIBlocker blocker = new GenericUIBlocker();
        AtomicBoolean aborted = new AtomicBoolean(false);

        blocker.setCancelCallback(() -> aborted.set(true));
        assertDoesNotThrow(() -> blocker.setCancelCallback(null));
    }
}
