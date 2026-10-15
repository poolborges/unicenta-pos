package com.openbravo.ui.animation;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;

/**
 * Material Design touch ripple effect for Swing components.
 * <p>
 * Simulates capacitive tactile feedback by expanding a translucent radial wave
 * outward from the exact coordinate of the touch or mouse press event.
 * Optimized for touch POS terminals to confirm physical interaction.
 * Completely self-contained, constant-driven, and Look-and-Feel agnostic.
 * </p>
 *
 * @author KriolOS
 * @since 1.0.0
 */
public final class RippleEffect {

    /** Default duration of the ripple expansion wave in milliseconds. */
    public static final int DEFAULT_DURATION_MS = 450;

    /** Maximum base alpha opacity multiplier applied to the expanding wave. */
    public static final float MAX_RIPPLE_ALPHA = 0.45f;

    /** Semi-transparent white ripple color used over dark component backgrounds. */
    public static final Color RIPPLE_DARK_BG_COLOR = new Color(255, 255, 255, 60);

    /** Semi-transparent black ripple color used over light component backgrounds. */
    public static final Color RIPPLE_LIGHT_BG_COLOR = new Color(0, 0, 0, 40);

    private static final float PROGRESS_START = 0.0f;
    private static final float PROGRESS_END = 1.0f;

    private final JComponent component;
    private Point clickPoint = new Point(0, 0);
    private float progress = PROGRESS_START;
    private float maxRadius = 0.0f;
    private Color rippleColor = null;
    private NativeAnimator animator;
    private boolean active = false;

    private RippleEffect(JComponent component) {
        this.component = component;
        setupListeners();
    }

    /**
     * Attaches a radial ripple effect to the specified Swing component.
     *
     * @param component the target component to receive ripple feedback
     * @return the configured {@link RippleEffect} controller
     */
    public static RippleEffect applyTo(JComponent component) {
        return new RippleEffect(component);
    }

    /**
     * Sets the base color of the expanding ripple wave.
     *
     * @param color the ripple color (alpha is blended automatically during animation)
     * @return this effect instance for fluent chaining
     */
    public RippleEffect setRippleColor(Color color) {
        if (color != null) {
            this.rippleColor = color;
        }
        return this;
    }

    /**
     * Returns the configured ripple wave color.
     *
     * @return current ripple color
     */
    public Color getRippleColor() {
        return rippleColor;
    }

    private void setupListeners() {
        component.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (component.isEnabled()) {
                    trigger(e.getPoint());
                }
            }
        });
    }

    /**
     * Manually triggers a radial ripple wave starting from the given point.
     *
     * @param point center coordinate for the expanding circle
     */
    public void trigger(Point point) {
        if (point == null) {
            point = new Point(component.getWidth() / 2, component.getHeight() / 2);
        }
        this.clickPoint = point;
        this.active = true;

        int w = component.getWidth();
        int h = component.getHeight();
        double d1 = Point.distance(point.x, point.y, 0, 0);
        double d2 = Point.distance(point.x, point.y, w, 0);
        double d3 = Point.distance(point.x, point.y, 0, h);
        double d4 = Point.distance(point.x, point.y, w, h);
        this.maxRadius = (float) Math.max(Math.max(d1, d2), Math.max(d3, d4));

        if (animator != null && animator.isRunning()) {
            animator.stop();
        }

        animator = NativeAnimator.createWithInterpolator(
                DEFAULT_DURATION_MS,
                fraction -> {
                    this.progress = fraction;
                    component.repaint();
                },
                NativeAnimator.FAST_OUT_SLOW_IN
        );
        animator.start();
    }

    /**
     * Renders the active ripple wave onto the provided Graphics2D context.
     * Typically invoked inside the component's {@code paintComponent} method.
     *
     * @param g2 the Graphics2D context to render on
     */
    public void paintRipple(Graphics2D g2) {
        if (!active || progress >= PROGRESS_END || maxRadius <= 0.0f) {
            return;
        }

        Graphics2D g = (Graphics2D) g2.create();
        AnimationUtils.applyHighQualityRenderingHints(g);

        float currentRadius = maxRadius * progress;
        float alpha = Math.max(0.0f, (PROGRESS_END - progress) * MAX_RIPPLE_ALPHA);

        Color effectiveColor = this.rippleColor;
        if (effectiveColor == null) {
            Color bg = AnimationUtils.resolveBackground(component);
            effectiveColor = AnimationUtils.isDark(bg) ? RIPPLE_DARK_BG_COLOR : RIPPLE_LIGHT_BG_COLOR;
        }

        int r = effectiveColor.getRed();
        int gr = effectiveColor.getGreen();
        int b = effectiveColor.getBlue();
        int alphaInt = Math.round(alpha * AnimationUtils.ALPHA_OPAQUE);

        g.setColor(new Color(r, gr, b, alphaInt));
        Shape circle = new Ellipse2D.Float(
                clickPoint.x - currentRadius,
                clickPoint.y - currentRadius,
                currentRadius * 2.0f,
                currentRadius * 2.0f
        );

        // Clip to component shape
        g.clipRect(0, 0, component.getWidth(), component.getHeight());
        g.fill(circle);
        g.dispose();
    }
}
