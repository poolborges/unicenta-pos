package com.openbravo.ui.animation;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 * Animated rolling number label for real-time totals, prices, and quantities.
 * <p>
 * Instead of jumping abruptly between values, the display smoothly interpolates
 * between numeric states using timing framework easing curves.
 * Ideal for POS ticket subtotals, cash drawer change calculations, and loyalty points.
 * Fully configurable, free of magic values, and Look-and-Feel agnostic.
 * </p>
 *
 * @author KriolOS
 * @since 1.0.0
 */
public class RollingNumberLabel extends JLabel {

    /** Default duration of the rolling interpolation animation in milliseconds. */
    public static final int DEFAULT_ANIMATION_DURATION_MS = 500;

    /** Minimum supported animation duration in milliseconds. */
    public static final int MIN_ANIMATION_DURATION_MS = 50;

    /** Default decimal places for currency representations. */
    public static final int DEFAULT_DECIMAL_PLACES = 2;

    /** Base integer number format pattern. */
    public static final String BASE_NUMBER_PATTERN = "#,##0";

    private static final String DECIMAL_SEPARATOR = ".";
    private static final String ZERO_DIGIT = "0";
    private static final String EMPTY_STRING = "";

    private double currentValue = 0.0;
    private double targetValue = 0.0;
    private int decimalPlaces = DEFAULT_DECIMAL_PLACES;
    private String prefix = EMPTY_STRING;
    private String suffix = EMPTY_STRING;
    private int animationDurationMs = DEFAULT_ANIMATION_DURATION_MS;
    private NativeAnimator animator;
    private DecimalFormat formatter;

    /**
     * Constructs a rolling number label initialized at zero.
     */
    public RollingNumberLabel() {
        this(0.0);
    }

    /**
     * Constructs a rolling number label with an initial numeric value.
     *
     * @param initialValue the starting number
     */
    public RollingNumberLabel(double initialValue) {
        this.currentValue = initialValue;
        this.targetValue = initialValue;
        updateFormatter();
        updateTextDisplay();
    }

    /**
     * Constructs a rolling number label with initial value, currency prefix, and suffix.
     *
     * @param initialValue the starting number
     * @param prefix text placed before the number (e.g. "$", "CVE ")
     * @param suffix text placed after the number (e.g. " CVE", " pts")
     */
    public RollingNumberLabel(double initialValue, String prefix, String suffix) {
        this.currentValue = initialValue;
        this.targetValue = initialValue;
        this.prefix = (prefix != null) ? prefix : EMPTY_STRING;
        this.suffix = (suffix != null) ? suffix : EMPTY_STRING;
        updateFormatter();
        updateTextDisplay();
    }

    private void updateFormatter() {
        StringBuilder pattern = new StringBuilder(BASE_NUMBER_PATTERN);
        if (decimalPlaces > 0) {
            pattern.append(DECIMAL_SEPARATOR);
            pattern.append(ZERO_DIGIT.repeat(decimalPlaces));
        }
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.getDefault());
        this.formatter = new DecimalFormat(pattern.toString(), symbols);
    }

    private void updateTextDisplay() {
        setText(prefix + formatter.format(currentValue) + suffix);
    }

    /**
     * Sets the number of decimal places to format and display.
     *
     * @param decimalPlaces decimal precision (e.g. 2 for currency, 0 for integers)
     * @return this label instance for fluent configuration
     */
    public RollingNumberLabel setDecimalPlaces(int decimalPlaces) {
        this.decimalPlaces = Math.max(0, decimalPlaces);
        updateFormatter();
        updateTextDisplay();
        return this;
    }

    /**
     * Sets the text prefix prepended before the number.
     *
     * @param prefix the prefix string
     * @return this label instance
     */
    public RollingNumberLabel setPrefix(String prefix) {
        this.prefix = (prefix != null) ? prefix : EMPTY_STRING;
        updateTextDisplay();
        return this;
    }

    /**
     * Sets the text suffix appended after the number.
     *
     * @param suffix the suffix string
     * @return this label instance
     */
    public RollingNumberLabel setSuffix(String suffix) {
        this.suffix = (suffix != null) ? suffix : EMPTY_STRING;
        updateTextDisplay();
        return this;
    }

    /**
     * Sets the animation roll duration in milliseconds.
     *
     * @param durationMs duration in milliseconds (default: 500ms)
     * @return this label instance
     */
    public RollingNumberLabel setAnimationDurationMs(int durationMs) {
        this.animationDurationMs = Math.max(MIN_ANIMATION_DURATION_MS, durationMs);
        return this;
    }

    /**
     * Smoothly rolls the display from the current value to the new target value.
     *
     * @param target the target numeric value
     */
    public void rollTo(double target) {
        if (Double.compare(this.currentValue, target) == 0) {
            return;
        }

        if (animator != null && animator.isRunning()) {
            animator.stop();
        }

        final double startVal = this.currentValue;
        this.targetValue = target;

        animator = NativeAnimator.createWithInterpolator(
                animationDurationMs,
                fraction -> {
                    this.currentValue = startVal + (targetValue - startVal) * fraction;
                    updateTextDisplay();
                },
                NativeAnimator.EASE_IN_OUT
        );
        animator.start();
    }

    /**
     * Convenience method to animate to a {@link BigDecimal} value.
     *
     * @param target the target amount
     */
    public void rollTo(BigDecimal target) {
        if (target != null) {
            rollTo(target.setScale(decimalPlaces, RoundingMode.HALF_UP).doubleValue());
        }
    }

    /**
     * Instantly sets the numeric value without animation.
     *
     * @param value the direct value
     */
    public void setValueImmediate(double value) {
        if (animator != null && animator.isRunning()) {
            animator.stop();
        }
        this.currentValue = value;
        this.targetValue = value;
        updateTextDisplay();
    }

    /**
     * Returns the current value being displayed.
     *
     * @return numeric value
     */
    public double getValue() {
        return currentValue;
    }

    /**
     * Returns the final target value the label is rolling towards.
     *
     * @return target value
     */
    public double getTargetValue() {
        return targetValue;
    }
}
