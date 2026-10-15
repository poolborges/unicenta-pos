package com.openbravo.ui.animation;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import org.jdesktop.core.animation.timing.Animator;
import org.jdesktop.core.animation.timing.TimingTargetAdapter;

/**
 * Tactical error shake feedback effect for Swing components.
 * <p>
 * Simulates an assertive horizontal shake motion using a damped sine wave
 * coupled with a temporary tactile validation border highlight to immediately
 * alert the operator to invalid inputs, scanner read errors, or validation failures.
 * Fully constant-driven and Look-and-Feel agnostic.
 * </p>
 *
 * @author KriolOS
 * @since 1.0.0
 */
public final class ShakeEffect {

    /** Default duration of the oscillation in milliseconds. */
    public static final int DEFAULT_SHAKE_DURATION_MS = 380;

    /** Maximum horizontal pixel displacement at initial oscillation peak. */
    public static final int DEFAULT_MAX_OFFSET_PX = 10;

    /** Number of complete sinusoidal cycles executed during the animation. */
    public static final int DEFAULT_TOTAL_CYCLES = 4;

    /** Highlight border color indicating invalid state during shake. */
    public static final Color SHAKE_ERROR_COLOR = new Color(231, 76, 60);

    /** Border line thickness during error shake highlight. */
    public static final int SHAKE_BORDER_THICKNESS = 2;

    private ShakeEffect() {
        // Utility class: prevent instantiation
    }

    /**
     * Shakes the given component horizontally with an authentic damped oscillation
     * and a temporary error outline highlight.
     *
     * @param comp the component to shake
     */
    public static void play(final Component comp) {
        play(comp, DEFAULT_SHAKE_DURATION_MS, DEFAULT_MAX_OFFSET_PX);
    }

    /**
     * Shakes the given component with custom duration and displacement amplitude.
     *
     * @param comp the component to shake
     * @param durationMs total animation duration in milliseconds
     * @param maxOffsetPx maximum horizontal pixel offset
     */
    public static void play(final Component comp, int durationMs, int maxOffsetPx) {
        if (comp == null) return;

        final Point originalLocation = comp.getLocation();
        final int duration = Math.max(100, durationMs);
        final int maxOffset = Math.max(2, maxOffsetPx);

        // Apply temporary tactile red border highlight if it is a JComponent
        Border originalBorder = null;
        if (comp instanceof JComponent jcomp) {
            originalBorder = jcomp.getBorder();
            jcomp.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(SHAKE_ERROR_COLOR, SHAKE_BORDER_THICKNESS),
                    BorderFactory.createEmptyBorder(2, 4, 2, 4)
            ));
        }

        final Border borderToRestore = originalBorder;

        NativeAnimator animator = new NativeAnimator(duration, new TimingTargetAdapter() {
            @Override
            public void timingEvent(Animator source, double fraction) {
                double angle = fraction * Math.PI * 2.0 * DEFAULT_TOTAL_CYCLES;
                int currentOffset = (int) Math.round(Math.sin(angle) * maxOffset * (1.0 - fraction));

                comp.setLocation(originalLocation.x + currentOffset, originalLocation.y);
                comp.repaint();
                if (comp.getParent() != null) {
                    comp.getParent().repaint(
                            originalLocation.x - maxOffset,
                            originalLocation.y,
                            comp.getWidth() + (2 * maxOffset),
                            comp.getHeight()
                    );
                }
            }

            @Override
            public void end(Animator source) {
                comp.setLocation(originalLocation);
                if (comp instanceof JComponent jcomp) {
                    jcomp.setBorder(borderToRestore);
                }
                comp.repaint();
                if (comp.getParent() != null) {
                    comp.getParent().repaint(
                            originalLocation.x - maxOffset,
                            originalLocation.y,
                            comp.getWidth() + (2 * maxOffset),
                            comp.getHeight()
                    );
                }
            }
        });

        animator.start();
    }
}
