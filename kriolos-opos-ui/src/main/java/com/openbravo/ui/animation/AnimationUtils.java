package com.openbravo.ui.animation;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;

/**
 * Self-contained graphical and mathematical utility suite for the animation package.
 * <p>
 * Provides Look-and-Feel agnostic color luminance detection, contrast adaptation,
 * alpha blending, rendering hints configuration, and Bézier trajectory calculations.
 * Ensures that the {@code com.openbravo.ui.animation} package remains entirely
 * self-contained with zero external UI module dependencies and zero magic numbers.
 * </p>
 *
 * @author KriolOS
 * @since 1.0.0
 */
public final class AnimationUtils {

    // --- Luminance Weights (ITU-R BT.709 Standard) ---
    public static final double LUMINANCE_RED_WEIGHT   = 0.2126;
    public static final double LUMINANCE_GREEN_WEIGHT = 0.7152;
    public static final double LUMINANCE_BLUE_WEIGHT  = 0.0722;
    public static final double LUMINANCE_THRESHOLD_DARK = 0.50;

    // --- Color Component Normalization ---
    public static final double RGB_MAX_VALUE = 255.0;
    public static final int ALPHA_OPAQUE = 255;
    public static final int ALPHA_TRANSPARENT = 0;

    // --- Default Contrast Colors ---
    public static final Color CONTRAST_LIGHT_FOREGROUND = Color.WHITE;
    public static final Color CONTRAST_DARK_FOREGROUND  = new Color(33, 37, 41);

    // --- Highlight Tint Offsets ---
    public static final int HIGHLIGHT_DARK_TINT_OFFSET  = 45;
    public static final int HIGHLIGHT_LIGHT_SHADE_OFFSET = 35;
    public static final Color DEFAULT_FALLBACK_HIGHLIGHT = new Color(225, 230, 240);

    // --- Look & Feel Property Keys ---
    public static final String UI_KEY_PANEL_BACKGROUND  = "Panel.background";
    public static final String UI_KEY_BUTTON_BACKGROUND = "Button.background";
    public static final String UI_KEY_BUTTON_FOREGROUND = "Button.foreground";

    private AnimationUtils() {
        // Utility class: prevent instantiation
    }

    /**
     * Calculates the perceived relative luminance of a color based on the ITU-R BT.709 standard.
     *
     * @param color the color to analyze
     * @return perceived luminance in range [0.0, 1.0] (0.0 = black, 1.0 = white)
     */
    public static double getLuminance(Color color) {
        if (color == null) {
            return 1.0;
        }
        return (LUMINANCE_RED_WEIGHT * color.getRed()
                + LUMINANCE_GREEN_WEIGHT * color.getGreen()
                + LUMINANCE_BLUE_WEIGHT * color.getBlue()) / RGB_MAX_VALUE;
    }

    /**
     * Determines whether the specified color represents a dark tone (luminance &lt; 0.5).
     *
     * @param color the color to inspect
     * @return {@code true} if dark, {@code false} if light
     */
    public static boolean isDark(Color color) {
        return getLuminance(color) < LUMINANCE_THRESHOLD_DARK;
    }

    /**
     * Returns a high-contrast text or accent color (pure white or dark slate)
     * suitable for overlaying against the given background.
     *
     * @param background the underlying background color
     * @return {@link #CONTRAST_LIGHT_FOREGROUND} for dark backgrounds, or {@link #CONTRAST_DARK_FOREGROUND} for light
     */
    public static Color getContrastingColor(Color background) {
        return isDark(background) ? CONTRAST_LIGHT_FOREGROUND : CONTRAST_DARK_FOREGROUND;
    }

    /**
     * Produces an appropriate subtle tactile highlight color for pressed or active states,
     * dynamically computed according to the background brightness without magic values.
     *
     * @param base the base background color
     * @return lighter tint if base is dark, or darker shade if base is light
     */
    public static Color getHighlightColor(Color base) {
        if (base == null) {
            return DEFAULT_FALLBACK_HIGHLIGHT;
        }
        if (isDark(base)) {
            return new Color(
                    Math.min(255, base.getRed() + HIGHLIGHT_DARK_TINT_OFFSET),
                    Math.min(255, base.getGreen() + HIGHLIGHT_DARK_TINT_OFFSET),
                    Math.min(255, base.getBlue() + HIGHLIGHT_DARK_TINT_OFFSET)
            );
        } else {
            return new Color(
                    Math.max(0, base.getRed() - HIGHLIGHT_LIGHT_SHADE_OFFSET),
                    Math.max(0, base.getGreen() - HIGHLIGHT_LIGHT_SHADE_OFFSET),
                    Math.max(0, base.getBlue() - HIGHLIGHT_LIGHT_SHADE_OFFSET)
            );
        }
    }

    /**
     * Creates a new {@link Color} sharing the same RGB components as the input,
     * but with a modified normalized alpha channel.
     *
     * @param color original color
     * @param alpha opacity normalized between 0.0f (transparent) and 1.0f (opaque)
     * @return color with new alpha
     */
    public static Color withAlpha(Color color, float alpha) {
        if (color == null) {
            return new Color(0, 0, 0, (int) (clamp(alpha, 0.0f, 1.0f) * ALPHA_OPAQUE));
        }
        int clampedAlpha = Math.round(clamp(alpha, 0.0f, 1.0f) * ALPHA_OPAQUE);
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), clampedAlpha);
    }

    /**
     * Creates a new {@link Color} sharing the same RGB components with an integer alpha [0, 255].
     *
     * @param color original color
     * @param alpha integer opacity between 0 and 255
     * @return color with new alpha
     */
    public static Color withAlpha(Color color, int alpha) {
        int clamped = Math.max(ALPHA_TRANSPARENT, Math.min(ALPHA_OPAQUE, alpha));
        if (color == null) {
            return new Color(0, 0, 0, clamped);
        }
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), clamped);
    }

    /**
     * Linearly blends two colors by the given ratio.
     *
     * @param c1 starting color
     * @param c2 target color
     * @param ratio blend factor from 0.0 (c1) to 1.0 (c2)
     * @return interpolated color
     */
    public static Color blend(Color c1, Color c2, float ratio) {
        if (c1 == null && c2 == null) return Color.BLACK;
        if (c1 == null) return c2;
        if (c2 == null) return c1;

        float r = clamp(ratio, 0.0f, 1.0f);
        float invR = 1.0f - r;

        int red = Math.round(c1.getRed() * invR + c2.getRed() * r);
        int green = Math.round(c1.getGreen() * invR + c2.getGreen() * r);
        int blue = Math.round(c1.getBlue() * invR + c2.getBlue() * r);
        int alpha = Math.round(c1.getAlpha() * invR + c2.getAlpha() * r);

        return new Color(red, green, blue, alpha);
    }

    /**
     * Applies standard high-quality rendering hints (anti-aliasing, bicubic interpolation, text subpixel)
     * to a {@link Graphics2D} pipeline.
     *
     * @param g2 the Graphics2D context to configure
     */
    public static void applyHighQualityRenderingHints(Graphics2D g2) {
        if (g2 == null) return;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
    }

    /**
     * Climbs up the component hierarchy to locate an effective opaque background color.
     * If no parent has an opaque background, falls back to the current Look-and-Feel default.
     *
     * @param component the component to inspect
     * @return resolved background color (never {@code null})
     */
    public static Color resolveBackground(Component component) {
        Component current = component;
        while (current != null) {
            Color bg = current.getBackground();
            if (bg != null && bg.getAlpha() > ALPHA_TRANSPARENT && current.isOpaque()) {
                return bg;
            }
            current = current.getParent();
        }

        Color lafBg = UIManager.getColor(UI_KEY_PANEL_BACKGROUND);
        return (lafBg != null) ? lafBg : SystemColor.control;
    }

    /**
     * Evaluates a 2D quadratic Bézier curve at step {@code t} using Bernstein polynomials:
     * {@code B(t) = (1-t)^2 * P0 + 2*(1-t)*t * P_ctrl + t^2 * P1}.
     *
     * @param p0 start point
     * @param pCtrl elevated control point
     * @param p1 destination point
     * @param t progress fraction [0.0, 1.0]
     * @return calculated point on the parabolic path
     */
    public static Point2D.Float evaluateQuadraticBezier(Point p0, Point pCtrl, Point p1, float t) {
        float invT = 1.0f - t;
        float x = (invT * invT * p0.x) + (2.0f * invT * t * pCtrl.x) + (t * t * p1.x);
        float y = (invT * invT * p0.y) + (2.0f * invT * t * pCtrl.y) + (t * t * p1.y);
        return new Point2D.Float(x, y);
    }

    /**
     * Clamps a floating-point value within a [min, max] range.
     *
     * @param val input value
     * @param min minimum bound
     * @param max maximum bound
     * @return clamped value
     */
    public static float clamp(float val, float min, float max) {
        return Math.max(min, Math.min(max, val));
    }
}
