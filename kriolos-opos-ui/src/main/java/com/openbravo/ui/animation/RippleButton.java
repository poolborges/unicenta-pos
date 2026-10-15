package com.openbravo.ui.animation;

import javax.swing.*;
import java.awt.*;

/**
 * Modern touch-optimized button featuring Material Design radial ripples and rounded corners.
 * <p>
 * Provides tactile visual confirmation on point of contact, essential for capacitive
 * POS touchscreens and high-throughput cashier environments.
 * Inherits colors from active Look and Feel and adapts cleanly at runtime.
 * </p>
 *
 * @author KriolOS
 * @since 1.0.0
 */
public class RippleButton extends JButton {

    /** Default corner arc radius in pixels for rounded button rendering. */
    public static final int DEFAULT_CORNER_RADIUS = 8;

    /** Default translucent rollover overlay highlighting the button on hover. */
    public static final Color DEFAULT_HOVER_OVERLAY = new Color(255, 255, 255, 20);

    private final RippleEffect ripple;
    private int cornerRadius = DEFAULT_CORNER_RADIUS;
    private Color hoverOverlay = DEFAULT_HOVER_OVERLAY;

    /**
     * Constructs a new RippleButton with the specified label text.
     *
     * @param text the button text
     */
    public RippleButton(String text) {
        super(text);
        this.ripple = RippleEffect.applyTo(this);
        initAesthetics();
    }

    /**
     * Constructs a new RippleButton with an action icon and text.
     *
     * @param text the button text
     * @param icon the button icon
     */
    public RippleButton(String text, Icon icon) {
        super(text, icon);
        this.ripple = RippleEffect.applyTo(this);
        initAesthetics();
    }

    private void initAesthetics() {
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setOpaque(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Inherit directly from active Look & Feel defaults via centralized keys
        Color lafBg = UIManager.getColor(AnimationUtils.UI_KEY_BUTTON_BACKGROUND);
        if (lafBg != null) {
            setBackground(lafBg);
        }
        Color lafFg = UIManager.getColor(AnimationUtils.UI_KEY_BUTTON_FOREGROUND);
        if (lafFg != null) {
            setForeground(lafFg);
        }
    }

    @Override
    public void updateUI() {
        super.updateUI();
        setContentAreaFilled(false);
        setBorderPainted(false);
        setOpaque(false);
    }

    /**
     * Sets the corner radius in pixels for rounded button rendering.
     *
     * @param cornerRadius radius in pixels
     */
    public void setCornerRadius(int cornerRadius) {
        this.cornerRadius = Math.max(0, cornerRadius);
        repaint();
    }

    /**
     * Returns the button's corner radius in pixels.
     *
     * @return corner radius
     */
    public int getCornerRadius() {
        return cornerRadius;
    }

    /**
     * Accesses the underlying {@link RippleEffect} controller for customized wave colors.
     *
     * @return the ripple controller
     */
    public RippleEffect getRipple() {
        return ripple;
    }

    /**
     * Sets the hover overlay color applied when cursor rolls over the button.
     *
     * @param hoverOverlay overlay color
     */
    public void setHoverOverlay(Color hoverOverlay) {
        this.hoverOverlay = (hoverOverlay != null) ? hoverOverlay : DEFAULT_HOVER_OVERLAY;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        AnimationUtils.applyHighQualityRenderingHints(g2);

        int w = getWidth();
        int h = getHeight();

        // 1. Draw rounded background
        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, w, h, cornerRadius, cornerRadius);

        // 2. Hover overlay
        if (getModel().isRollover() && isEnabled()) {
            g2.setColor(hoverOverlay);
            g2.fillRoundRect(0, 0, w, h, cornerRadius, cornerRadius);
        }

        // 3. Render active ripple wave
        ripple.paintRipple(g2);
        g2.dispose();

        // 4. Render label text and icon
        super.paintComponent(g);
    }
}
