package com.openbravo.ui.components;

import java.awt.Color;
import java.awt.Component;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import org.jdesktop.core.animation.timing.Animator; // [REQUIRED IMPORT]
import org.jdesktop.swing.animation.timing.sources.SwingTimerTimingSource; // [REQUIRED IMPORT]

/**
 * Thread-safe manager responsible for handling the queue and layout 
 * distribution of active toast notifications on the screen.
 * 
 * @author KriolOS
 * @since 1.0.0
 */
public class ToastManager {

    // Default color palettes for different feedback types
    public static final Color SUCCESS = new Color(46, 204, 113, 230); 
    public static final Color ERROR = new Color(231, 76, 60, 230);    
    public static final Color INFO = new Color(52, 152, 219, 230);    
    public static final Color WARNING = new Color(241, 196, 15, 230); 

    // Thread-safe list containing all currently active toasts on screen
    private static final CopyOnWriteArrayList<ToastNotification> activeToasts = new CopyOnWriteArrayList<>();
    private static final int GAP = 10; 

    // [FIX START] Global initialization of the Swing Timing Source
    static {
        SwingTimerTimingSource sysTimer = new SwingTimerTimingSource();
        Animator.setDefaultTimingSource(sysTimer);
        sysTimer.init(); // Starts the internal Swing timer thread loop
    }
    // [FIX END]

    public static void show(JFrame parent, String message, Color typeColor) {
        if (parent == null) return;
        
        SwingUtilities.invokeLater(() -> {
            ToastNotification toast = new ToastNotification(parent, message, typeColor);
            activeToasts.add(toast);
            
            repositionToasts(parent);
            
            toast.startAnimation();
        });
    }
    
    public static void show(Component component, String message, Color typeColor) {
        JFrame frame = ComponentUtils.getParentFrame(component);
        show(frame, message, typeColor);
    }

    protected static void removeToast(ToastNotification toast, JFrame parent) {
        activeToasts.remove(toast);
        SwingUtilities.invokeLater(() -> repositionToasts(parent));
    }

    private static void repositionToasts(JFrame parent) {
        if (parent == null || !parent.isShowing()) return;

        int currentY = parent.getLocationOnScreen().y + parent.getHeight() - 60;
        int baseX = parent.getLocationOnScreen().x + parent.getWidth() - 20;

        for (ToastNotification toast : activeToasts) {
            int x = baseX - toast.getWidth();
            currentY -= toast.getHeight();
            
            toast.setLocation(x, currentY);
            currentY -= GAP; 
        }
    }
}