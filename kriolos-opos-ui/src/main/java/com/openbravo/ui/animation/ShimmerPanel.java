package com.openbravo.ui.animation;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Modern skeleton loading panel featuring an animated gradient shimmer wave.
 * <p>
 * Replaces conventional spinners with a mobile-style placeholder shimmer,
 * signaling active content loading for product catalogs, reports, or receipt tables.
 * Loops continuously at 60 FPS until explicitly stopped.
 * Fully Look-and-Feel agnostic, automatically computing harmonious dark or light
 * palette tones based on the active LAF background.
 * </p>
 *
 * @author KriolOS
 * @since 1.0.0
 */
public class ShimmerPanel extends JPanel {

    /** Default duration in milliseconds for one complete light sweep pass. */
    public static final int DEFAULT_SHIMMER_DURATION_MS = 1200;

    /** Minimum allowable sweep duration in milliseconds. */
    public static final int MIN_SHIMMER_DURATION_MS = 300;

    /** Base placeholder background color for dark mode environments. */
    public static final Color DARK_BASE_COLOR = new Color(48, 54, 65);

    /** Shimmer light sweep highlight color for dark mode environments. */
    public static final Color DARK_HIGHLIGHT_COLOR = new Color(75, 84, 100);

    /** Base placeholder background color for light mode environments. */
    public static final Color LIGHT_BASE_COLOR = new Color(228, 232, 238);

    /** Shimmer light sweep highlight color for light mode environments. */
    public static final Color LIGHT_HIGHLIGHT_COLOR = new Color(248, 250, 253);

    /** Peak alpha transparency of the sweep highlight wave. */
    public static final int SHIMMER_PEAK_ALPHA = 220;

    private static final float SWEEP_START_OFFSET = -0.5f;
    private static final float SWEEP_SPAN = 2.0f;
    private static final float MIN_WAVE_WIDTH = 120.0f;
    private static final float WAVE_WIDTH_RATIO = 0.40f;

    /**
     * Record representing a placeholder block shape within the skeleton.
     *
     * @param bounds rectangle area of the block
     * @param cornerRadius radius of rounded corners
     */
    public record SkeletonBlock(Rectangle bounds, int cornerRadius) {}

    private final List<SkeletonBlock> blocks = new ArrayList<>();
    private float shimmerPosition = SWEEP_START_OFFSET;
    private NativeAnimator animator;
    private boolean shimmering = false;
    private int durationMs = DEFAULT_SHIMMER_DURATION_MS;

    /**
     * Constructs a ShimmerPanel with transparent background.
     */
    public ShimmerPanel() {
        setOpaque(false);
    }

    /**
     * Adds a rounded rectangular skeleton block to the panel.
     *
     * @param x x position
     * @param y y position
     * @param width block width
     * @param height block height
     * @param cornerRadius corner arc radius
     * @return this ShimmerPanel for fluent chaining
     */
    public ShimmerPanel addBlock(int x, int y, int width, int height, int cornerRadius) {
        blocks.add(new SkeletonBlock(new Rectangle(x, y, width, height), cornerRadius));
        repaint();
        return this;
    }

    /**
     * Clears all registered skeleton blocks.
     */
    public void clearBlocks() {
        blocks.clear();
        repaint();
    }

    /**
     * Starts the continuous shimmer light sweep animation loop.
     */
    public void startShimmer() {
        if (shimmering) {
            return;
        }
        this.shimmering = true;

        if (animator != null && animator.isRunning()) {
            animator.stop();
        }

        animator = NativeAnimator.createLoop(
                durationMs,
                fraction -> {
                    this.shimmerPosition = SWEEP_START_OFFSET + fraction * SWEEP_SPAN;
                    repaint();
                },
                NativeAnimator.FAST_OUT_SLOW_IN
        );
        animator.start();
    }

    /**
     * Stops the shimmer animation.
     */
    public void stopShimmer() {
        this.shimmering = false;
        if (animator != null && animator.isRunning()) {
            animator.stop();
        }
        this.shimmerPosition = SWEEP_START_OFFSET;
        repaint();
    }

    /**
     * Checks if the shimmer animation is currently active.
     *
     * @return {@code true} if shimmering
     */
    public boolean isShimmering() {
        return shimmering;
    }

    /**
     * Sets the sweep cycle duration in milliseconds.
     *
     * @param durationMs duration in milliseconds
     * @return this ShimmerPanel
     */
    public ShimmerPanel setDurationMs(int durationMs) {
        this.durationMs = Math.max(MIN_SHIMMER_DURATION_MS, durationMs);
        if (shimmering) {
            stopShimmer();
            startShimmer();
        }
        return this;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (blocks.isEmpty()) {
            return;
        }

        Graphics2D g2 = (Graphics2D) g.create();
        AnimationUtils.applyHighQualityRenderingHints(g2);

        int w = getWidth();

        // 1. LAF-agnostic palette detection based on current resolved background luminance
        Color parentBg = AnimationUtils.resolveBackground(this);
        boolean darkTheme = AnimationUtils.isDark(parentBg);

        Color baseColor = darkTheme ? DARK_BASE_COLOR : LIGHT_BASE_COLOR;
        Color highlightColor = darkTheme ? DARK_HIGHLIGHT_COLOR : LIGHT_HIGHLIGHT_COLOR;

        // 2. Draw base placeholder blocks
        for (SkeletonBlock b : blocks) {
            g2.setColor(baseColor);
            g2.fillRoundRect(b.bounds().x, b.bounds().y, b.bounds().width, b.bounds().height, b.cornerRadius(), b.cornerRadius());
        }

        // 3. Render moving shimmer gradient wave if active
        if (shimmering && w > 0) {
            float waveWidth = Math.max(MIN_WAVE_WIDTH, w * WAVE_WIDTH_RATIO);
            float startX = shimmerPosition * w;
            float endX = startX + waveWidth;

            LinearGradientPaint gradient = new LinearGradientPaint(
                    startX, 0, endX, 0,
                    new float[]{0.0f, 0.5f, 1.0f},
                    new Color[]{
                            AnimationUtils.withAlpha(baseColor, AnimationUtils.ALPHA_TRANSPARENT),
                            AnimationUtils.withAlpha(highlightColor, SHIMMER_PEAK_ALPHA),
                            AnimationUtils.withAlpha(baseColor, AnimationUtils.ALPHA_TRANSPARENT)
                    }
            );

            for (SkeletonBlock b : blocks) {
                Shape clip = new java.awt.geom.RoundRectangle2D.Float(
                        b.bounds().x, b.bounds().y, b.bounds().width, b.bounds().height, b.cornerRadius(), b.cornerRadius()
                );
                Graphics2D gWave = (Graphics2D) g2.create();
                gWave.clip(clip);
                gWave.setPaint(gradient);
                gWave.fillRect(b.bounds().x, b.bounds().y, b.bounds().width, b.bounds().height);
                gWave.dispose();
            }
        }

        g2.dispose();
    }
}
