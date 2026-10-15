package com.openbravo.ui.animation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.swing.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link SlidePanel}.
 *
 * @author KriolOS
 */
@DisplayName("SlidePanel Unit Tests")
class SlidePanelTest {

    @Test
    @DisplayName("Verify view registration and initial component activation")
    void testViewRegistration() {
        SlidePanel slidePanel = new SlidePanel();

        JPanel view1 = new JPanel();
        JPanel view2 = new JPanel();

        slidePanel.addView("v1", view1);
        slidePanel.addView("v2", view2);

        assertSame(view1, slidePanel.getCurrentComponent(), "First added view should be current active component");
    }

    @Test
    @DisplayName("Verify duration boundary enforcement")
    void testDurationBoundary() {
        SlidePanel slidePanel = new SlidePanel();
        slidePanel.setDurationMs(10); // below minimum 50ms

        // Should clamp to MIN_TRANSITION_DURATION_MS
        // We verify that invoking slide with clamped duration does not throw
        JPanel v1 = new JPanel();
        JPanel v2 = new JPanel();
        slidePanel.addView("v1", v1);
        slidePanel.addView("v2", v2);

        assertDoesNotThrow(() -> slidePanel.slideTo("v2", SlidePanel.Direction.LEFT));
    }
}
