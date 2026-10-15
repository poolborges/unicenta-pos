package com.openbravo.ui.animation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.awt.geom.Point2D;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link AnimationUtils}.
 * Verifies luminance math, contrast color calculation, alpha blending, and Bézier curves.
 *
 * @author KriolOS
 */
@DisplayName("AnimationUtils Mathematical & Color Utility Tests")
class AnimationUtilsTest {

    @Test
    @DisplayName("Verify luminance calculations for standard color spaces")
    void testLuminanceCalculations() {
        assertEquals(0.0, AnimationUtils.getLuminance(Color.BLACK), 0.001);
        assertEquals(1.0, AnimationUtils.getLuminance(Color.WHITE), 0.001);

        double redLum = AnimationUtils.getLuminance(Color.RED);
        double greenLum = AnimationUtils.getLuminance(Color.GREEN);
        double blueLum = AnimationUtils.getLuminance(Color.BLUE);

        assertTrue(greenLum > redLum, "Green should have higher perceived luminance than Red");
        assertTrue(redLum > blueLum, "Red should have higher perceived luminance than Blue");
    }

    @Test
    @DisplayName("Verify isDark correctly classifies luminance thresholds")
    void testIsDark() {
        assertTrue(AnimationUtils.isDark(Color.BLACK));
        assertTrue(AnimationUtils.isDark(new Color(20, 20, 20)));
        assertTrue(AnimationUtils.isDark(new Color(15, 23, 42))); // Dark navy

        assertFalse(AnimationUtils.isDark(Color.WHITE));
        assertFalse(AnimationUtils.isDark(new Color(245, 247, 250)));
        assertFalse(AnimationUtils.isDark(Color.YELLOW));
    }

    @Test
    @DisplayName("Verify contrasting text color selection")
    void testContrastingColor() {
        assertEquals(AnimationUtils.CONTRAST_LIGHT_FOREGROUND, AnimationUtils.getContrastingColor(Color.BLACK));
        assertEquals(AnimationUtils.CONTRAST_LIGHT_FOREGROUND, AnimationUtils.getContrastingColor(new Color(30, 30, 40)));

        assertEquals(AnimationUtils.CONTRAST_DARK_FOREGROUND, AnimationUtils.getContrastingColor(Color.WHITE));
        assertEquals(AnimationUtils.CONTRAST_DARK_FOREGROUND, AnimationUtils.getContrastingColor(new Color(240, 240, 240)));
    }

    @Test
    @DisplayName("Verify alpha channel modification and clamping")
    void testWithAlpha() {
        Color base = new Color(100, 150, 200, 255);

        Color semiTransparent = AnimationUtils.withAlpha(base, 0.5f);
        assertEquals(100, semiTransparent.getRed());
        assertEquals(150, semiTransparent.getGreen());
        assertEquals(200, semiTransparent.getBlue());
        assertEquals(128, semiTransparent.getAlpha());

        // Test boundary clamping
        Color fullyTransparent = AnimationUtils.withAlpha(base, -0.5f);
        assertEquals(0, fullyTransparent.getAlpha());

        Color fullyOpaque = AnimationUtils.withAlpha(base, 1.5f);
        assertEquals(255, fullyOpaque.getAlpha());
    }

    @Test
    @DisplayName("Verify linear color blending interpolation")
    void testColorBlending() {
        Color c1 = new Color(0, 0, 0, 0);
        Color c2 = new Color(100, 100, 100, 100);

        Color midpoint = AnimationUtils.blend(c1, c2, 0.5f);
        assertEquals(50, midpoint.getRed());
        assertEquals(50, midpoint.getGreen());
        assertEquals(50, midpoint.getBlue());
        assertEquals(50, midpoint.getAlpha());

        Color startPoint = AnimationUtils.blend(c1, c2, 0.0f);
        assertEquals(c1, startPoint);

        Color endPoint = AnimationUtils.blend(c1, c2, 1.0f);
        assertEquals(c2, endPoint);
    }

    @Test
    @DisplayName("Verify quadratic Bézier trajectory evaluation")
    void testEvaluateQuadraticBezier() {
        Point p0 = new Point(0, 0);
        Point pCtrl = new Point(50, 100);
        Point p1 = new Point(100, 0);

        // At t=0, point must match p0
        Point2D.Float start = AnimationUtils.evaluateQuadraticBezier(p0, pCtrl, p1, 0.0f);
        assertEquals(0.0f, start.x, 0.01f);
        assertEquals(0.0f, start.y, 0.01f);

        // At t=1, point must match p1
        Point2D.Float end = AnimationUtils.evaluateQuadraticBezier(p0, pCtrl, p1, 1.0f);
        assertEquals(100.0f, end.x, 0.01f);
        assertEquals(0.0f, end.y, 0.01f);

        // At t=0.5, apex x must be 50, apex y must be 50
        // Formula: 0.25*0 + 2*0.25*100 + 0.25*0 = 50
        Point2D.Float mid = AnimationUtils.evaluateQuadraticBezier(p0, pCtrl, p1, 0.5f);
        assertEquals(50.0f, mid.x, 0.01f);
        assertEquals(50.0f, mid.y, 0.01f);
    }

    @Test
    @DisplayName("Verify value clamping helper")
    void testClamp() {
        assertEquals(5.0f, AnimationUtils.clamp(5.0f, 0.0f, 10.0f));
        assertEquals(0.0f, AnimationUtils.clamp(-2.0f, 0.0f, 10.0f));
        assertEquals(10.0f, AnimationUtils.clamp(15.0f, 0.0f, 10.0f));
    }
}
