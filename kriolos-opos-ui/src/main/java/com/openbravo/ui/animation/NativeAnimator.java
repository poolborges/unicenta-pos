package com.openbravo.ui.animation;

import java.util.concurrent.TimeUnit;
import org.jdesktop.core.animation.timing.Animator;
import org.jdesktop.core.animation.timing.Interpolator;
import org.jdesktop.core.animation.timing.TimingTarget;
import org.jdesktop.core.animation.timing.TimingTargetAdapter;
import org.jdesktop.core.animation.timing.interpolators.SplineInterpolator;
import org.jdesktop.swing.animation.timing.sources.SwingTimerTimingSource;

/**
 * Modern timing animation engine and facade over the Timing Framework.
 * <p>
 * Centralizes industry-standard Cubic Bézier curve interpolator presets,
 * continuous looping support (infinite cycles), functional callback interfaces for Java lambdas,
 * and lifecycle listener management.
 * Designed to provide smooth, LAF-agnostic 60fps animations across all POS interfaces.
 * </p>
 *
 * @author KriolOS
 * @since 1.0.0
 */
public class NativeAnimator {

    /**
     * Functional callback invoked on each animation frame tick with progress fraction.
     */
    @FunctionalInterface
    public interface TimingCallback {
        /**
         * Invoked on every animation clock tick.
         *
         * @param fraction progress fraction ranging from 0.0f (start) to 1.0f (end)
         */
        void onTick(float fraction);
    }

    // --- Standard W3C / CSS Interpolator Presets ---
    public static final Interpolator EASE_IN_OUT = new SplineInterpolator(0.42f, 0.0f, 0.58f, 1.0f);
    public static final Interpolator EASE_IN     = new SplineInterpolator(0.42f, 0.0f, 1.0f, 1.0f);
    public static final Interpolator EASE_OUT    = new SplineInterpolator(0.0f, 0.0f, 0.58f, 1.0f);

    // --- Modern UI / Material Design Presets ---
    public static final Interpolator FAST_OUT_SLOW_IN   = new SplineInterpolator(0.4f, 0.0f, 0.2f, 1.0f);
    public static final Interpolator FAST_OUT_LINEAR_IN = new SplineInterpolator(0.4f, 0.0f, 1.0f, 1.0f);

    // --- Advanced Organic Motion Presets ---
    public static final Interpolator ANTICIPATE = fraction -> {
        double tension = 1.70158;
        return fraction * fraction * ((tension + 1.0) * fraction - tension);
    };
    public static final Interpolator OVERSHOOT  = fraction -> {
        double tension = 1.70158;
        double t = fraction - 1.0;
        return t * t * ((tension + 1.0) * t + tension) + 1.0;
    };

    static {
        ensureTimingSource();
    }

    private static synchronized void ensureTimingSource() {
        if (Animator.getDefaultTimingSource() == null) {
            SwingTimerTimingSource sysTimer = new SwingTimerTimingSource();
            Animator.setDefaultTimingSource(sysTimer);
            sysTimer.init();
        }
    }

    private final Animator internalAnimator;

    /**
     * Constructs a NativeAnimator wrapping an existing Timing Framework {@link Animator}.
     *
     * @param animator internal framework animator
     */
    public NativeAnimator(Animator animator) {
        if (animator == null) {
            throw new IllegalArgumentException("Animator cannot be null");
        }
        this.internalAnimator = animator;
    }

    /**
     * Standard Linear Animation Constructor with a {@link TimingTarget}.
     *
     * @param durationMs duration in milliseconds
     * @param target framework timing target
     */
    public NativeAnimator(int durationMs, TimingTarget target) {
        this(durationMs, target, null);
    }

    /**
     * Creates an animator using a framework interpolator and timing target.
     *
     * @param durationMs duration in milliseconds
     * @param target framework timing target
     * @param interpolator motion curve interpolator
     * @return new {@link NativeAnimator}
     */
    public static NativeAnimator createWithInterpolator(int durationMs, TimingTarget target, Interpolator interpolator) {
        return new NativeAnimator(durationMs, target, interpolator);
    }

    /**
     * Creates an animator using a functional lambda callback and motion curve interpolator.
     *
     * @param durationMs duration in milliseconds
     * @param callback functional tick callback receiving progress fraction [0.0f, 1.0f]
     * @param interpolator motion curve interpolator
     * @return new {@link NativeAnimator}
     */
    public static NativeAnimator createWithInterpolator(int durationMs, TimingCallback callback, Interpolator interpolator) {
        if (callback == null) {
            throw new IllegalArgumentException("Callback cannot be null");
        }
        return new NativeAnimator(durationMs, new TimingTargetAdapter() {
            @Override
            public void timingEvent(Animator source, double fraction) {
                callback.onTick((float) fraction);
            }
        }, interpolator);
    }

    /**
     * Creates a linear animator using a functional lambda callback.
     *
     * @param durationMs duration in milliseconds
     * @param callback functional tick callback
     * @return new {@link NativeAnimator}
     */
    public static NativeAnimator create(int durationMs, TimingCallback callback) {
        return createWithInterpolator(durationMs, callback, null);
    }

    /**
     * Constructs a continuous looping animator using a functional lambda callback and motion curve.
     *
     * @param durationMs duration of one cycle in milliseconds
     * @param callback functional tick callback receiving progress fraction [0.0f, 1.0f]
     * @param interpolator motion curve interpolator
     * @param repeatBehavior looping behavior ({@link Animator.RepeatBehavior#LOOP} or {@link Animator.RepeatBehavior#REVERSE})
     * @return new continuous {@link NativeAnimator}
     */
    public static NativeAnimator createLoop(int durationMs, TimingCallback callback, Interpolator interpolator, Animator.RepeatBehavior repeatBehavior) {
        if (durationMs <= 0) {
            throw new IllegalArgumentException("Duration must be greater than 0");
        }
        if (callback == null) {
            throw new IllegalArgumentException("Callback cannot be null");
        }

        ensureTimingSource();
        org.jdesktop.core.animation.timing.TimingSource swingSource = Animator.getDefaultTimingSource();

        Animator.Builder builder = new Animator.Builder(swingSource)
                .setDuration(durationMs, TimeUnit.MILLISECONDS)
                .setRepeatCount(Animator.INFINITE)
                .setRepeatBehavior(repeatBehavior != null ? repeatBehavior : Animator.RepeatBehavior.LOOP)
                .addTarget(new TimingTargetAdapter() {
                    @Override
                    public void timingEvent(Animator source, double fraction) {
                        callback.onTick((float) fraction);
                    }
                });

        if (interpolator != null) {
            builder.setInterpolator(interpolator);
        }

        return new NativeAnimator(builder.build());
    }

    /**
     * Constructs a continuous forward looping animator using a functional lambda callback and motion curve.
     *
     * @param durationMs duration of one cycle in milliseconds
     * @param callback functional tick callback receiving progress fraction [0.0f, 1.0f]
     * @param interpolator motion curve interpolator
     * @return new continuous {@link NativeAnimator}
     */
    public static NativeAnimator createLoop(int durationMs, TimingCallback callback, Interpolator interpolator) {
        return createLoop(durationMs, callback, interpolator, Animator.RepeatBehavior.LOOP);
    }

    /**
     * Overloaded constructor applying custom Bézier control points directly.
     *
     * @param durationMs duration in milliseconds
     * @param target framework timing target
     * @param x1 P1 control point X
     * @param y1 P1 control point Y
     * @param x2 P2 control point X
     * @param y2 P2 control point Y
     * @return new {@link NativeAnimator}
     */
    public static NativeAnimator createWithCustomBezier(int durationMs, TimingTarget target, float x1, float y1, float x2, float y2) {
        return new NativeAnimator(durationMs, target, new SplineInterpolator(x1, y1, x2, y2));
    }

    private NativeAnimator(int durationMs, TimingTarget target, Interpolator interpolator) {
        if (durationMs <= 0) {
            throw new IllegalArgumentException("Duration must be greater than 0");
        }
        if (target == null) {
            throw new IllegalArgumentException("Animation target cannot be null");
        }

        ensureTimingSource();
        org.jdesktop.core.animation.timing.TimingSource swingSource = Animator.getDefaultTimingSource();

        Animator.Builder builder = new Animator.Builder(swingSource)
                .setDuration(durationMs, TimeUnit.MILLISECONDS)
                .addTarget(target);

        if (interpolator != null) {
            builder.setInterpolator(interpolator);
        }

        this.internalAnimator = builder.build();
    }

    /**
     * Registers a completion callback executed when the animation reaches its end.
     *
     * @param onEnd callback runnable executed upon completion
     * @return this animator instance for fluent chaining
     */
    public NativeAnimator addEndCallback(Runnable onEnd) {
        if (onEnd != null) {
            internalAnimator.addTarget(new TimingTargetAdapter() {
                @Override
                public void end(Animator source) {
                    onEnd.run();
                }
            });
        }
        return this;
    }

    /**
     * Appends an additional timing target to the animator lifecycle.
     *
     * @param target target to add
     * @return this animator instance
     */
    public NativeAnimator addTarget(TimingTarget target) {
        if (target != null) {
            internalAnimator.addTarget(target);
        }
        return this;
    }

    /**
     * Starts the animation playback.
     */
    public void start() {
        if (internalAnimator != null && !internalAnimator.isRunning()) {
            internalAnimator.start();
        }
    }

    /**
     * Stops the animation playback immediately.
     */
    public void stop() {
        if (internalAnimator != null && internalAnimator.isRunning()) {
            internalAnimator.stop();
        }
    }

    /**
     * Checks whether the animation is currently active.
     *
     * @return {@code true} if running
     */
    public boolean isRunning() {
        return internalAnimator != null && internalAnimator.isRunning();
    }

    /**
     * Accesses the underlying Timing Framework {@link Animator} instance.
     *
     * @return the internal animator
     */
    public Animator getInternalAnimator() {
        return this.internalAnimator;
    }
}
