package com.openbravo.ui.animation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link RippleButton}.
 *
 * @author KriolOS
 */
@DisplayName("RippleButton Unit Tests")
class RippleButtonTest {

    @Test
    @DisplayName("Verify corner radius and ripple attachment")
    void testCornerRadiusAndRipple() {
        RippleButton btn = new RippleButton("Test Action");

        assertEquals(RippleButton.DEFAULT_CORNER_RADIUS, btn.getCornerRadius());
        assertNotNull(btn.getRipple(), "RippleEffect controller should be attached automatically");

        btn.setCornerRadius(16);
        assertEquals(16, btn.getCornerRadius());

        btn.setCornerRadius(-5);
        assertEquals(0, btn.getCornerRadius(), "Negative radius should clamp to zero");
    }
}
