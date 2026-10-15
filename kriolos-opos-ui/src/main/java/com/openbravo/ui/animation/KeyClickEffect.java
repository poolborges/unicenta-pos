package com.openbravo.ui.animation;

import java.awt.Color;
import java.awt.Component;

/**
 * Tactile key-press feedback effect for touch terminals and physical keyboards.
 * <p>
 * Briefly flashes a contrasting highlight over the target component's background
 * and smoothly restores it using an Ease-Out animation curve.
 * Automatically adapts highlight tone to active Look-and-Feel brightness (light vs dark mode).
 * </p>
 *
 * @author KriolOS
 * @since 1.0.0
 */
public final class KeyClickEffect {

    private KeyClickEffect() {
        // Utility class: prevent instantiation
    }

    /**
     * Plays a 150ms tactile color flash effect on the target component.
     *
     * @param targetComponent the component receiving the click animation
     */
    public static void play(final Component targetComponent) {
        if (targetComponent == null) {
            return;
        }

        final Color originalBackground = AnimationUtils.resolveBackground(targetComponent);
        final Color highlightColor = AnimationUtils.getHighlightColor(originalBackground);

        PropertySetter<Component, Color> colorSetter = PropertySetter.getTarget(
                targetComponent,
                Component::setBackground,
                highlightColor,
                originalBackground
        );

        NativeAnimator animator = NativeAnimator.createWithInterpolator(
                150,
                colorSetter,
                NativeAnimator.EASE_OUT
        );
        animator.start();
    }
}
