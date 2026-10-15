package com.openbravo.ui.animation;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Animated view transition container supporting smooth directional slide transitions.
 * <p>
 * Replaces instant card switches with mobile-grade horizontal or vertical slides.
 * Ideal for POS catalog category navigation, checkout wizard stages, and modal drawers.
 * Completely constant-driven and Look-and-Feel agnostic.
 * </p>
 *
 * @author KriolOS
 * @since 1.0.0
 */
public class SlidePanel extends JPanel {

    /** Default transition duration in milliseconds. */
    public static final int DEFAULT_TRANSITION_DURATION_MS = 400;

    /** Minimum allowable transition duration in milliseconds. */
    public static final int MIN_TRANSITION_DURATION_MS = 50;

    private static final float PROGRESS_START = 0.0f;
    private static final float PROGRESS_COMPLETE = 1.0f;

    /**
     * Transition slide direction.
     */
    public enum Direction {
        LEFT,
        RIGHT,
        UP,
        DOWN
    }

    private final Map<String, Component> views = new LinkedHashMap<>();
    private Component currentComponent;
    private Component incomingComponent;
    private int durationMs = DEFAULT_TRANSITION_DURATION_MS;
    private NativeAnimator animator;
    private float slideProgress = PROGRESS_START;
    private Direction currentDirection = Direction.LEFT;

    /**
     * Constructs a SlidePanel with default borderless layout.
     */
    public SlidePanel() {
        super(null); // Absolute positioning container during transitions
        setOpaque(false);
    }

    /**
     * Adds a named view to the slider registry.
     *
     * @param name unique view identifier
     * @param component the component to associate with the name
     * @return this SlidePanel instance for fluent chaining
     */
    public SlidePanel addView(String name, Component component) {
        views.put(name, component);
        if (currentComponent == null) {
            currentComponent = component;
            add(component);
            revalidate();
            repaint();
        }
        return this;
    }

    /**
     * Sets the transition animation duration in milliseconds.
     *
     * @param durationMs duration in milliseconds
     * @return this SlidePanel instance
     */
    public SlidePanel setDurationMs(int durationMs) {
        this.durationMs = Math.max(MIN_TRANSITION_DURATION_MS, durationMs);
        return this;
    }

    /**
     * Transitions to the specified named view sliding in the chosen direction.
     *
     * @param name view identifier registered via {@link #addView(String, Component)}
     * @param direction direction of movement (e.g. {@link Direction#LEFT} for next, {@link Direction#RIGHT} for back)
     */
    public void slideTo(String name, Direction direction) {
        Component target = views.get(name);
        if (target != null) {
            slideTo(target, direction);
        }
    }

    /**
     * Transitions directly to the given component sliding in the chosen direction.
     *
     * @param nextComponent incoming component
     * @param direction direction of movement
     */
    public void slideTo(Component nextComponent, Direction direction) {
        if (nextComponent == null || nextComponent == currentComponent) {
            return;
        }

        if (animator != null && animator.isRunning()) {
            animator.stop();
        }

        this.incomingComponent = nextComponent;
        this.currentDirection = direction;
        this.slideProgress = PROGRESS_START;

        // Ensure both components are inside the container
        if (nextComponent.getParent() != this) {
            add(nextComponent);
        }
        if (currentComponent != null && currentComponent.getParent() != this) {
            add(currentComponent);
        }

        int w = getWidth();
        int h = getHeight();

        // Position incoming component at its off-screen starting position
        switch (direction) {
            case LEFT -> incomingComponent.setBounds(w, 0, w, h);
            case RIGHT -> incomingComponent.setBounds(-w, 0, w, h);
            case UP -> incomingComponent.setBounds(0, h, w, h);
            case DOWN -> incomingComponent.setBounds(0, -h, w, h);
        }

        if (currentComponent != null) {
            currentComponent.setBounds(0, 0, w, h);
        }

        animator = NativeAnimator.createWithInterpolator(
                durationMs,
                fraction -> {
                    this.slideProgress = fraction;
                    updateTransitionPositions();
                },
                NativeAnimator.FAST_OUT_SLOW_IN
        );

        animator.addEndCallback(() -> {
            // Transition complete: remove outgoing view and anchor incoming
            if (currentComponent != null) {
                remove(currentComponent);
            }
            currentComponent = incomingComponent;
            incomingComponent = null;
            slideProgress = PROGRESS_COMPLETE;
            if (currentComponent != null) {
                currentComponent.setBounds(0, 0, getWidth(), getHeight());
            }
            revalidate();
            repaint();
        });

        animator.start();
    }

    private void updateTransitionPositions() {
        int w = getWidth();
        int h = getHeight();

        switch (currentDirection) {
            case LEFT -> {
                int offset = (int) (w * slideProgress);
                if (currentComponent != null) {
                    currentComponent.setBounds(-offset, 0, w, h);
                }
                if (incomingComponent != null) {
                    incomingComponent.setBounds(w - offset, 0, w, h);
                }
            }
            case RIGHT -> {
                int offset = (int) (w * slideProgress);
                if (currentComponent != null) {
                    currentComponent.setBounds(offset, 0, w, h);
                }
                if (incomingComponent != null) {
                    incomingComponent.setBounds(-w + offset, 0, w, h);
                }
            }
            case UP -> {
                int offset = (int) (h * slideProgress);
                if (currentComponent != null) {
                    currentComponent.setBounds(0, -offset, w, h);
                }
                if (incomingComponent != null) {
                    incomingComponent.setBounds(0, h - offset, w, h);
                }
            }
            case DOWN -> {
                int offset = (int) (h * slideProgress);
                if (currentComponent != null) {
                    currentComponent.setBounds(0, offset, w, h);
                }
                if (incomingComponent != null) {
                    incomingComponent.setBounds(0, -h + offset, w, h);
                }
            }
        }
        repaint();
    }

    @Override
    public void doLayout() {
        if (animator == null || !animator.isRunning()) {
            if (currentComponent != null) {
                currentComponent.setBounds(0, 0, getWidth(), getHeight());
            }
        }
    }

    /**
     * Returns the currently active visible component.
     *
     * @return active component
     */
    public Component getCurrentComponent() {
        return currentComponent;
    }
}
