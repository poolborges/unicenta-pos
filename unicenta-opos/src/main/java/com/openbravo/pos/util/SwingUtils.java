package com.openbravo.pos.util;
import java.awt.FontMetrics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JDialog;
import javax.swing.Timer;

/**
 * Swing utilities methods. Based on code from:
 * http://www.aurelienribon.com/blog/2012/07/tutorial-beautifying-the-dialogs-in-swing-applications/
 *
 * @author Harald Barsnes
 */
public class SwingUtils {
    
    // @TODO: requires java 7, hence not in use at the moment, see the lines that are commented out

    /**
     * Creates an animation to fade the dialog opacity from 0 to 1. Using a
     * default delay of 5 ms and an increment size of 0.05.
     *
     * @param dialog the dialog to fade in
     */
    public static void fadeIn(final JDialog dialog) {
        fadeIn(dialog, 5, 0.05f);
    }

    /**
     * Creates an animation to fade the dialog opacity from 0 to 1.
     *
     * @param dialog the dialog to fade in
     * @param delay the delay in ms before starting and between each change
     * @param incrementSize the increment size
     */
    public static void fadeIn(final JDialog dialog, int delay, final float incrementSize) {
        final Timer timer = new Timer(delay, null);
        timer.setRepeats(true);
        timer.addActionListener(new ActionListener() {
            private float opacity = 0;

            @Override
            public void actionPerformed(ActionEvent e) {
                opacity += incrementSize;
                dialog.setOpacity(Math.min(opacity, 1)); // requires java 1.7
                if (opacity >= 1) {
                    timer.stop();
                }
            }
        });

        dialog.setOpacity(0); // requires java 1.7
        timer.start();
        dialog.setVisible(true);
    }

    /**
     * Creates an animation to fade the dialog opacity from 1 to 0. Using a
     * default delay of 5 ms and an increment size of 0.05.
     *
     * @param dialog the dialog to fade out
     */
    public static void fadeOut(final JDialog dialog) {
        fadeOut(dialog, 5, 0.05f);
    }

    /**
     * Creates an animation to fade the dialog opacity from 1 to 0, and then
     * dispose.
     *
     * @param dialog the dialog to fade out
     * @param delay the delay in ms before starting and between each change
     * @param incrementSize the increment size
     */
    public static void fadeOut(final JDialog dialog, int delay, final float incrementSize) {
        final Timer timer = new Timer(delay, null);
        timer.setRepeats(true);
        timer.addActionListener(new ActionListener() {
            private float opacity = 1;

            @Override
            public void actionPerformed(ActionEvent e) {
                opacity -= incrementSize;
                dialog.setOpacity(Math.max(opacity, 0)); // requires java 1.7
                if (opacity < 0) {
                    timer.stop();
                    dialog.dispose();
                }
            }
        });

        dialog.setOpacity(1); // requires java 1.7
        timer.start();
    }

    /**
     * Creates an animation to fade the dialog opacity from 0 to 1, wait at 1
     * and then fade to 0. Default initial time of 50 ms, increment size of 0.05
     * and display time of 10000 ms.
     *
     * @param dialog the dialog to display
     */
    public static void fadeInAndOut(final JDialog dialog) {
        fadeInAndOut(dialog, 50, 0.05f, 10000);
    }

    /**
     * Creates an animation to fade the dialog opacity from 0 to 1, wait at 1
     * and then fade to 0 and dispose.
     *
     * @param dialog the dialog to display
     * @param delay the delay in ms before starting and between each change
     * @param incrementSize the increment size
     * @param displayTime the time in ms the dialog is fully visible
     */
    public static void fadeInAndOut(final JDialog dialog, final int delay, final float incrementSize, final int displayTime) {
        final Timer timer = new Timer(delay, null);
        timer.setRepeats(true);
        timer.addActionListener(new ActionListener() {
            private float opacity = 0;
            private boolean displayed = false;

            @Override
            public void actionPerformed(ActionEvent e) {

                if (!displayed) {
                    opacity += incrementSize;
                    dialog.setOpacity(Math.min(opacity, 1)); // requires java 1.7
                    if (opacity >= 1) {
                        timer.setDelay(displayTime);
                        displayed = true;
                    }
                } else {
                    timer.setDelay(delay);
                    opacity -= incrementSize;
                    dialog.setOpacity(Math.max(opacity, 0)); // requires java 1.7
                    if (opacity < 0) {
                        timer.stop();
                        dialog.dispose();
                    }
                }
            }
        });

        dialog.setOpacity(0); // requires java 1.7
        timer.start();
        dialog.setVisible(true);
    }

    static FontMetrics getDefaultLabelFontMetrics() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}