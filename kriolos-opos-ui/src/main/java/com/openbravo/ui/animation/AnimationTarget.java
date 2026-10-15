package com.openbravo.ui.animation;

/**
 * Lifecycle callback interface for custom component animation targets.
 *
 * @author KriolOS
 * @since 1.0.0
 */
public interface AnimationTarget {

    /**
     * Triggered when the animation lifecycle begins execution.
     */
    void onStart();

    /**
     * Triggered on every clock tick.
     *
     * @param fraction progress percentage ranging from 0.0 to 1.0
     */
    void onTimingEvent(double fraction);

    /**
     * Triggered when the animation completes naturally.
     */
    void onEnd();
}