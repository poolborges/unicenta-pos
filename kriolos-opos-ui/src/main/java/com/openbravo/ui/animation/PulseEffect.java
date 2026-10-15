package com.openbravo.ui.animation;

import javax.swing.*;
import java.awt.*;

/**
 * Pulsing heartbeat visual effect for Swing components.
 * <p>
 * Continuously or once pulses an expanding translucent glow or border halo around
 * a component to draw operator attention to high-priority actions (e.g., "Pay Sale",
 * pending kitchen orders, or urgent system alerts).
 * Fully constant-driven and Look-and-Feel agnostic, adapting automatically to light and dark themes.
 * </p>
 *
 * @author KriolOS
 * @since 1.0.0
 */
public final class PulseEffect {

    /** Default expansion radius in pixels for the pulsing aura halo. */
    public static final int DEFAULT_MAX_EXPANSION = 10;

    /** Minimum supported pulse expansion distance in pixels. */
    public static final int MIN_EXPANSION = 1;

    /** Default cycle duration for a complete heartbeat wave in milliseconds. */
    public static final int DEFAULT_CYCLE_DURATION_MS = 1000;

    /** Minimum allowable pulse animation duration in milliseconds. */
    public static final int MIN_PULSE_DURATION_MS = 100;

    /** Peak alpha transparency multiplier achieved at maximum pulse expansion. */
    public static final float MAX_PULSE_ALPHA = 0.85f;

    /** Corner arc radius for the rounded pulse halo outline in pixels. */
    public static final int HALO_CORNER_RADIUS = 10;

    /** Default fallback halo color when component background is undefined. */
    public static final Color DEFAULT_FALLBACK_HALO_COLOR = new Color(52, 152, 219);

    private static final float ALPHA_CUTOFF_THRESHOLD = 0.01f;
    private static final float EXPANSION_CUTOFF_THRESHOLD = 0.10f;

    private final JComponent component;
    private Color pulseColor;
    private int maxExpansion = DEFAULT_MAX_EXPANSION;
    private float currentExpansion = 0.0f;
    private float currentAlpha = 0.0f;
    private NativeAnimator animator;
    private boolean repeating = false;

    private PulseEffect(JComponent component) {
        this.component = component;
    }

    /**
     * Creates a new PulseEffect controller for the target component.
     *
     * @param component the component to animate
     * @return a new {@link PulseEffect} controller
     */
    public static PulseEffect on(JComponent component) {
        return new PulseEffect(component);
    }

    /**
     * Sets the custom halo color for the pulse glow.
     * If {@code null}, automatically derives the color from the component's background or foreground.
     *
     * @param color the glow color
     * @return this effect instance
     */
    public PulseEffect setPulseColor(Color color) {
        this.pulseColor = color;
        return this;
    }

    /**
     * Sets the maximum expansion distance of the halo in pixels.
     *
     * @param maxExpansion expansion in pixels
     * @return this effect instance
     */
    public PulseEffect setMaxExpansion(int maxExpansion) {
        this.maxExpansion = Math.max(MIN_EXPANSION, maxExpansion);
        return this;
    }

    /**
     * Triggers a single pulse cycle (expand and contract).
     *
     * @param durationMs duration of the pulse in milliseconds
     */
    public void pulseOnce(int durationMs) {
        stop();
        this.repeating = false;
        int duration = Math.max(MIN_PULSE_DURATION_MS, durationMs);
        animator = NativeAnimator.createWithInterpolator(
                duration,
                fraction -> {
                    double wave = Math.sin(fraction * Math.PI);
                    this.currentExpansion = (float) (maxExpansion * wave);
                    this.currentAlpha = (float) (MAX_PULSE_ALPHA * wave);
                    repaintTargetAndParent();
                },
                NativeAnimator.EASE_IN_OUT
        );
        animator.addEndCallback(this::stop);
        animator.start();
    }

    /**
     * Starts a continuous heartbeat pulse loop until {@link #stop()} is invoked.
     *
     * @param cycleDurationMs duration of one complete pulse cycle in milliseconds
     */
    public void start(int cycleDurationMs) {
        stop();
        this.repeating = true;
        int duration = Math.max(MIN_PULSE_DURATION_MS, cycleDurationMs);
        animator = NativeAnimator.createLoop(
                duration,
                fraction -> {
                    double wave = Math.sin(fraction * Math.PI);
                    this.currentExpansion = (float) (maxExpansion * wave);
                    this.currentAlpha = (float) (MAX_PULSE_ALPHA * wave);
                    repaintTargetAndParent();
                },
                NativeAnimator.EASE_IN_OUT
        );
        animator.start();
    }

    /**
     * Stops the active pulse animation and clears any residual halo.
     */
    public void stop() {
        this.repeating = false;
        if (animator != null && animator.isRunning()) {
            animator.stop();
        }
        this.currentExpansion = 0.0f;
        this.currentAlpha = 0.0f;
        repaintTargetAndParent();
    }

    /**
     * Checks if the pulse animation is currently active.
     *
     * @return {@code true} if animating
     */
    public boolean isRunning() {
        return animator != null && animator.isRunning();
    }

    /**
     * Returns the current normalized alpha factor [0.0f, 1.0f].
     *
     * @return current alpha
     */
    public float getCurrentAlpha() {
        return currentAlpha;
    }

    /**
     * Returns the current expansion in pixels.
     *
     * @return current expansion
     */
    public float getCurrentExpansion() {
        return currentExpansion;
    }

    private void repaintTargetAndParent() {
        if (component != null) {
            component.repaint();
            if (component.getParent() != null) {
                component.getParent().repaint();
            }
        }
    }

    /**
     * Paints the pulse glow halo around the component's internal bounds.
     *
     * @param g2 the Graphics2D context
     */
    public void paintHalo(Graphics2D g2) {
        if (component != null) {
            paintHalo(g2, 0, 0, component.getWidth(), component.getHeight());
        }
    }

    /**
     * Paints the pulse glow halo around the specified bounding rectangle.
     * Ideal when a parent container renders the aura around a target child button.
     *
     * @param g2 the Graphics2D context
     * @param x top-left X coordinate of target
     * @param y top-left Y coordinate of target
     * @param width width of target
     * @param height height of target
     */
    public void paintHalo(Graphics2D g2, int x, int y, int width, int height) {
        if (currentAlpha <= ALPHA_CUTOFF_THRESHOLD || currentExpansion <= EXPANSION_CUTOFF_THRESHOLD) {
            return;
        }

        Graphics2D g = (Graphics2D) g2.create();
        AnimationUtils.applyHighQualityRenderingHints(g);

        Color base = pulseColor;
        if (base == null && component != null) {
            base = component.getBackground();
            if (base == null || base.getAlpha() == AnimationUtils.ALPHA_TRANSPARENT) {
                base = component.getForeground();
            }
        }
        if (base == null) {
            base = DEFAULT_FALLBACK_HALO_COLOR;
        }

        int r = base.getRed();
        int gr = base.getGreen();
        int b = base.getBlue();

        // Render multi-layered glowing aura rings for smooth illumination
        int expand = Math.max(1, Math.round(currentExpansion));
        for (int i = expand; i >= 1; i--) {
            float layerRatio = 1.0f - ((float) (i - 1) / (float) expand);
            int layerAlpha = Math.round(currentAlpha * layerRatio * 0.70f * AnimationUtils.ALPHA_OPAQUE);
            if (layerAlpha <= 0) continue;

            g.setColor(new Color(r, gr, b, Math.min(AnimationUtils.ALPHA_OPAQUE, layerAlpha)));
            g.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g.drawRoundRect(
                    x - i,
                    y - i,
                    width + (2 * i),
                    height + (2 * i),
                    HALO_CORNER_RADIUS + (i / 2),
                    HALO_CORNER_RADIUS + (i / 2)
            );
        }

        g.dispose();
    }
}
