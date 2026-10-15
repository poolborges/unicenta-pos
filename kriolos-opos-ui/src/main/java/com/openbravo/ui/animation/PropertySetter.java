package com.openbravo.ui.animation;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.util.function.BiConsumer;
import org.jdesktop.core.animation.timing.Animator;
import org.jdesktop.core.animation.timing.TimingTargetAdapter;

/**
 * Custom framework extension bridging the Timing Framework with modern Java method references.
 * <p>
 * Enables type-safe interpolation of Swing properties such as {@link Color}, {@link Point},
 * {@link Dimension}, and numeric properties over time.
 * </p>
 *
 * @param <T> the type of the component being animated (e.g., {@link javax.swing.JPanel}, {@link javax.swing.JButton})
 * @param <V> the type of the property value (e.g., {@link Color}, {@link Dimension}, {@link Point})
 * @author KriolOS
 * @since 1.0.0
 */
public class PropertySetter<T, V> extends TimingTargetAdapter {

    private final T target;
    private final BiConsumer<T, V> setter;
    private final V startValue;
    private final V endValue;

    /**
     * Constructs a property setter for the specified target object and setter reference.
     *
     * @param target target object
     * @param setter method reference to set property
     * @param startValue initial value at t=0
     * @param endValue final value at t=1
     */
    public PropertySetter(T target, BiConsumer<T, V> setter, V startValue, V endValue) {
        if (target == null || setter == null) {
            throw new IllegalArgumentException("Target and Setter method reference cannot be null");
        }
        this.target = target;
        this.setter = setter;
        this.startValue = startValue;
        this.endValue = endValue;
    }

    /**
     * Helper factory method to cleanly instantiate targets inside animator build cycles.
     *
     * @param <T> component type
     * @param <V> property type
     * @param target component instance
     * @param setter method reference
     * @param start initial value
     * @param end target value
     * @return configured PropertySetter
     */
    public static <T, V> PropertySetter<T, V> getTarget(T target, BiConsumer<T, V> setter, V start, V end) {
        return new PropertySetter<>(target, setter, start, end);
    }

    @Override
    public void begin(Animator source) {
        setter.accept(target, startValue);
        repaintTarget();
    }

    @Override
    public void timingEvent(Animator source, double fraction) {
        V nextValue = interpolate(startValue, endValue, fraction);
        setter.accept(target, nextValue);
        repaintTarget();
    }

    @Override
    public void end(Animator source) {
        setter.accept(target, endValue);
        repaintTarget();
    }

    private void repaintTarget() {
        if (target instanceof Component comp) {
            comp.repaint();
            Component parent = comp.getParent();
            if (parent != null) {
                parent.repaint();
            }
        }
    }

    @SuppressWarnings("unchecked")
    private V interpolate(V start, V end, double fraction) {
        if (start instanceof Color s && end instanceof Color e) {
            return (V) AnimationUtils.blend(s, e, (float) fraction);
        }

        if (start instanceof Point s && end instanceof Point e) {
            int x = (int) (s.x + fraction * (e.x - s.x));
            int y = (int) (s.y + fraction * (e.y - s.y));
            return (V) new Point(x, y);
        }

        if (start instanceof Dimension s && end instanceof Dimension e) {
            int w = (int) (s.width + fraction * (e.width - s.width));
            int h = (int) (s.height + fraction * (e.height - s.height));
            return (V) new Dimension(w, h);
        }

        if (start instanceof Integer s && end instanceof Integer e) {
            return (V) Integer.valueOf((int) (s + fraction * (e - s)));
        }

        return fraction < 0.5 ? start : end;
    }
}
