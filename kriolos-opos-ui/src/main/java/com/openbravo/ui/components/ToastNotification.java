package com.openbravo.ui.components;

import com.openbravo.ui.animation.NativeAnimator;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.concurrent.TimeUnit;
import javax.swing.*;
import org.jdesktop.core.animation.timing.Animator;
import org.jdesktop.core.animation.timing.TimingTargetAdapter;

/**
 * Floating lightweight window overlay simulating an adaptive toast component.
 * Features an automatic hardware fallback strategy to handle systems without translucency support
 * and allows interactive manual dismissal via mouse clicks.
 * 
 * @author KriolOS
 * @since 1.0.0
 */
public class ToastNotification extends JWindow {
    
    private float opacity = 0.0f;
    private final int displayTimeMs = 3000; 
    private final JFrame parentFrame;
    private final boolean isTranslucencySupported;
    
    private Timer displayTimer;
    private Animator fadeOutAnimator;

    public ToastNotification(JFrame owner, String message, Color bgColor) {
        super(owner);
        this.parentFrame = owner;
        
        // Query runtime hardware/OS layout engine capacities before execution
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        this.isTranslucencySupported = ge.getDefaultScreenDevice().isWindowTranslucencySupported(
                GraphicsDevice.WindowTranslucency.PERPIXEL_TRANSLUCENT);

        setType(Type.POPUP);
        setAlwaysOnTop(true);
        
        if (isTranslucencySupported) {
            setBackground(new Color(0, 0, 0, 0)); 
        } else {
            // Apply standard parent frame panel background fallback
            setBackground(owner.getBackground());
        }

        JLabel label = new JLabel(message);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Arial", Font.BOLD, 14));

        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                float factor = Math.min(1.0f, Math.max(0.0f, opacity));
                if (isTranslucencySupported) {
                    int alpha = (int) (bgColor.getAlpha() * factor);
                    g2.setColor(new Color(bgColor.getRed(), bgColor.getGreen(), bgColor.getBlue(), alpha));
                    label.setForeground(new Color(255, 255, 255, (int) (255 * factor)));
                } else {
                    g2.setColor(new Color(bgColor.getRed(), bgColor.getGreen(), bgColor.getBlue()));
                    label.setForeground(Color.WHITE);
                }
                
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                g2.dispose();
            }
        };
        panel.setOpaque(false);
        panel.setLayout(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        panel.add(label, BorderLayout.CENTER);
        
        panel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                dismissEarly();
            }
        });
        
        add(panel);
        pack(); 
    }

    /**
     * Instantly bypasses active lifecycle visibility blocks to hide components.
     */
    private synchronized void dismissEarly() {
        if (displayTimer != null && displayTimer.isRunning()) {
            displayTimer.stop();
        }
        
        if (isTranslucencySupported) {
            if (fadeOutAnimator != null && !fadeOutAnimator.isRunning() && isShowing()) {
                fadeOutAnimator.start();
            }
        } else {
            // Instant hardware toggle dismissal fallback bypass
            finishToastLifespan();
        }
    }

    /**
     * Binds opacity attributes with safe hardware capability guard rails.
     */
    public void setToastOpacity(float opacity) {
        this.opacity = opacity;
        repaint();
    }
    
        /**
     * Executes the visual lifecycle path using pre-defined Bézier curve constants.
     */
    public void startAnimation() {
        // Fallback strategy for operating environments without translucency support
        if (!isTranslucencySupported) {
            setVisible(true);
            setToastOpacity(1.0f);
            
            this.displayTimer = new Timer(displayTimeMs, e -> finishToastLifespan());
            this.displayTimer.setRepeats(false);
            this.displayTimer.start();
            return;
        }

        // 1. Entry Transition Curve: Fluid acceleration with gentle deceleration
        NativeAnimator fadeIn = NativeAnimator.createWithInterpolator(
                250, 
                new TimingTargetAdapter() {
                    @Override
                    public void begin(Animator source) { 
                        setVisible(true); 
                    }
                    @Override
                    public void timingEvent(Animator source, double fraction) { 
                        setToastOpacity((float) fraction); 
                    }
                }, 
                NativeAnimator.FAST_OUT_SLOW_IN
        );

        // 2. Exit Transition Curve: Fast snappy departure to clear screen real estate
        NativeAnimator fadeOut = NativeAnimator.createWithInterpolator(
                200, 
                new TimingTargetAdapter() {
                    @Override
                    public void timingEvent(Animator source, double fraction) { 
                        setToastOpacity((float) (1.0 - fraction)); 
                    }
                    @Override
                    public void end(Animator source) { 
                        finishToastLifespan();
                    }
                }, 
                NativeAnimator.FAST_OUT_LINEAR_IN
        );

        // Map the internal raw instance to the class field to allow early click-dismissal interdiction
        this.fadeOutAnimator = fadeOut.getInternalAnimator();

        // Start the entry phase
        fadeIn.start();
        
        // Lifecycle stay duration delay sequence
        this.displayTimer = new Timer(displayTimeMs, e -> {
            if (fadeOutAnimator != null && !fadeOutAnimator.isRunning()) {
                fadeOutAnimator.start();
            }
        });
        this.displayTimer.setRepeats(false);
        this.displayTimer.start();
    }


    /**
     * Executes the visual lifecycle path using the best available rendering strategy.
     */
    public void startAnimationOLD() {
        // [FIX BRANCH] Handle operating environment profile variations cleanly
        if (!isTranslucencySupported) {
            setVisible(true);
            setToastOpacity(1.0f);
            
            // Standard non-blocking fallback timer loop skipping framework overhead entirely
            this.displayTimer = new Timer(displayTimeMs, e -> finishToastLifespan());
            this.displayTimer.setRepeats(false);
            this.displayTimer.start();
            return;
        }

        // Hardware accelerated execution architecture path (Translucency supported)
        org.jdesktop.core.animation.timing.TimingSource swingSource = 
                org.jdesktop.core.animation.timing.Animator.getDefaultTimingSource();

        Animator fadeIn = new Animator.Builder(swingSource)
                .setDuration(250, TimeUnit.MILLISECONDS)
                .addTarget(new TimingTargetAdapter() {
                    @Override
                    public void begin(Animator source) { 
                        setVisible(true); 
                    }
                    @Override
                    public void timingEvent(Animator source, double fraction) { 
                        setToastOpacity((float) fraction); 
                    }
                }).build();

        this.fadeOutAnimator = new Animator.Builder(swingSource)
                .setDuration(300, TimeUnit.MILLISECONDS)
                .addTarget(new TimingTargetAdapter() {
                    @Override
                    public void timingEvent(Animator source, double fraction) { 
                        setToastOpacity((float) (1.0 - fraction)); 
                    }
                    @Override
                    public void end(Animator source) { 
                        finishToastLifespan();
                    }
                }).build();

        fadeIn.start();
        
        this.displayTimer = new Timer(displayTimeMs, e -> {
            if (fadeOutAnimator != null && !fadeOutAnimator.isRunning()) {
                fadeOutAnimator.start();
            }
        });
        this.displayTimer.setRepeats(false);
        this.displayTimer.start();
    }

    /**
     * Cleanly detaches references inside the supervisor core and disposes graphics window contexts.
     */
    private void finishToastLifespan() {
        setVisible(false);
        ToastManager.removeToast(ToastNotification.this, parentFrame);
        dispose();
    }
}