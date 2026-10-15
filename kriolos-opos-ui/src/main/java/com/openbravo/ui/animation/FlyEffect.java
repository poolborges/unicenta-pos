package com.openbravo.ui.animation;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.RoundRectangle2D;

/**
 * Fly-to-cart parabolic trajectory animation for POS item selection.
 * <p>
 * When an operator taps a product card or item button, an animated visual badge
 * arcs smoothly across the window into the shopping cart, receipt table, or ticket panel.
 * Uses the window's {@link JLayeredPane} to render above all components with zero layout disturbance.
 * Correctly anchors at origin to prevent top-left flash artifacts.
 * Fully constant-driven and Look-and-Feel agnostic.
 * </p>
 *
 * @author KriolOS
 * @since 1.0.0
 */
public final class FlyEffect {

    /** Default duration of the parabolic flight in milliseconds. */
    public static final int DEFAULT_FLIGHT_DURATION_MS = 550;

    /** Minimum arc elevation in pixels for the parabolic peak. */
    public static final int MIN_ARC_ELEVATION_PX = 60;

    /** Horizontal distance divisor determining proportional arc apex height. */
    public static final int ARC_SPAN_DIVISOR = 4;

    /** Scale shrinkage factor at final impact. */
    public static final float IMPACT_SCALE_FACTOR = 0.35f;

    /** Corner arc radius for the flying chip badge in pixels. */
    public static final int CHIP_CORNER_RADIUS = 16;

    /** Default fallback background color for the flying chip. */
    public static final Color DEFAULT_CHIP_BACKGROUND = new Color(41, 128, 185);

    /** Subtle drop shadow color rendered beneath the flying chip. */
    public static final Color CHIP_SHADOW_COLOR = new Color(0, 0, 0, 50);

    private static final String DEFAULT_ITEM_LABEL = "Item";

    private FlyEffect() {
        // Utility class: prevent instantiation
    }

    /**
     * Animates an item badge flying in an elevated arc from the source component to the target.
     *
     * @param source the origin component (e.g. catalog product card)
     * @param target the destination component (e.g. cart table or checkout button)
     * @param labelText text displayed on the flying badge (e.g., product title or price)
     * @param onComplete optional callback executed upon arrival at destination
     */
    public static void fly(Component source, Component target, String labelText, Runnable onComplete) {
        if (source == null || target == null || !source.isShowing() || !target.isShowing()) {
            if (onComplete != null) {
                onComplete.run();
            }
            return;
        }

        JRootPane rootPane = SwingUtilities.getRootPane(source);
        if (rootPane == null) {
            if (onComplete != null) {
                onComplete.run();
            }
            return;
        }

        JLayeredPane layeredPane = rootPane.getLayeredPane();

        // 1. Calculate global coordinates in the root coordinate system
        Point p0 = SwingUtilities.convertPoint(source, source.getWidth() / 2, source.getHeight() / 2, layeredPane);
        Point p1 = SwingUtilities.convertPoint(target, target.getWidth() / 2, target.getHeight() / 2, layeredPane);

        // Control point elevated upwards to create a natural parabolic trajectory
        int ctrlX = (p0.x + p1.x) / 2;
        int ctrlY = Math.min(p0.y, p1.y) - Math.max(MIN_ARC_ELEVATION_PX, Math.abs(p1.x - p0.x) / ARC_SPAN_DIVISOR);
        Point pCtrl = new Point(ctrlX, ctrlY);

        // 2. Create transient ghost flying chip
        Color chipBg = (source.getBackground() != null && source.getBackground().getAlpha() > 0)
                ? source.getBackground()
                : DEFAULT_CHIP_BACKGROUND;
        Color chipFg = (source.getForeground() != null)
                ? source.getForeground()
                : AnimationUtils.getContrastingColor(chipBg);

        FlyingChip chip = new FlyingChip(labelText, chipBg, chipFg);
        Dimension prefSize = chip.getPreferredSize();
        final int initW = prefSize.width;
        final int initH = prefSize.height;

        // Anchor IMMEDIATELY at starting point p0 before adding to layered pane
        // This completely eliminates any top-left corner (0,0) flash artifact
        chip.setBounds(p0.x - initW / 2, p0.y - initH / 2, initW, initH);
        layeredPane.add(chip, JLayeredPane.DRAG_LAYER);

        // 3. Animate along quadratic Bézier curve: B(t) = (1-t)^2 * P0 + 2(1-t)t * P_ctrl + t^2 * P1
        NativeAnimator anim = NativeAnimator.createWithInterpolator(
                DEFAULT_FLIGHT_DURATION_MS,
                fraction -> {
                    Point2D.Float pos = AnimationUtils.evaluateQuadraticBezier(p0, pCtrl, p1, fraction);

                    float scale = 1.0f - (fraction * IMPACT_SCALE_FACTOR);
                    chip.setScale(scale);
                    chip.setOpacity(Math.max(0.1f, 1.0f - (fraction * fraction * 0.4f)));

                    Rectangle oldBounds = chip.getBounds();
                    int w = Math.max(1, (int) (initW * scale));
                    int h = Math.max(1, (int) (initH * scale));
                    chip.setBounds((int) pos.x - w / 2, (int) pos.y - h / 2, w, h);

                    layeredPane.repaint(oldBounds);
                    layeredPane.repaint(chip.getBounds());
                },
                NativeAnimator.FAST_OUT_SLOW_IN
        );

        anim.addEndCallback(() -> {
            Rectangle finalBounds = chip.getBounds();
            layeredPane.remove(chip);
            layeredPane.repaint(finalBounds);
            if (onComplete != null) {
                onComplete.run();
            }
        });

        anim.start();
    }

    private static class FlyingChip extends JComponent {
        private final String text;
        private final Color bg;
        private final Color fg;
        private float scale = 1.0f;
        private float opacity = 1.0f;

        public FlyingChip(String text, Color bg, Color fg) {
            this.text = (text != null) ? text : DEFAULT_ITEM_LABEL;
            this.bg = (bg != null) ? bg : DEFAULT_CHIP_BACKGROUND;
            this.fg = (fg != null) ? fg : Color.WHITE;
            setFont(new Font("SansSerif", Font.BOLD, 12));
            setOpaque(false);
        }

        public void setScale(float scale) {
            this.scale = scale;
        }

        public void setOpacity(float opacity) {
            this.opacity = opacity;
        }

        @Override
        public Dimension getPreferredSize() {
            FontMetrics fm = getFontMetrics(getFont());
            int w = fm.stringWidth(text) + 24;
            int h = fm.getHeight() + 10;
            return new Dimension(w, h);
        }

        @Override
        protected void paintComponent(Graphics g) {
            if (getWidth() <= 0 || getHeight() <= 0) {
                return;
            }

            Graphics2D g2 = (Graphics2D) g.create();
            AnimationUtils.applyHighQualityRenderingHints(g2);

            if (opacity < 1.0f) {
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
            }

            int w = getWidth();
            int h = getHeight();

            // Draw shadow
            g2.setColor(CHIP_SHADOW_COLOR);
            g2.fill(new RoundRectangle2D.Float(1, 2, w - 2, h - 2, CHIP_CORNER_RADIUS, CHIP_CORNER_RADIUS));

            // Draw pill badge
            g2.setColor(bg);
            g2.fill(new RoundRectangle2D.Float(0, 0, w, h, CHIP_CORNER_RADIUS, CHIP_CORNER_RADIUS));

            // Draw text
            g2.setColor(fg);
            g2.setFont(getFont());
            FontMetrics fm = g2.getFontMetrics();
            int tx = (w - fm.stringWidth(text)) / 2;
            int ty = (h - fm.getHeight()) / 2 + fm.getAscent();
            g2.drawString(text, tx, ty);

            g2.dispose();
        }
    }
}
