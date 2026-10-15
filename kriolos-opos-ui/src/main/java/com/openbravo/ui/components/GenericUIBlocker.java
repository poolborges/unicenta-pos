package com.openbravo.ui.components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.InputEvent;
import java.beans.PropertyChangeEvent;
import javax.swing.plaf.LayerUI;

/**
 * Universal UI Blocker for KriolOS POS.
 * Intercepts inputs, animates a loading spinner, forces a system wait cursor,
 * and supports custom text, transparency, and escape callbacks.
 *
 * @author KriolOS
 * @since 1.0.0
 */
public class GenericUIBlocker extends LayerUI<JPanel> implements ActionListener {

    /**
     * Visual display mode for the blocker.
     */
    public enum Mode {
        SPINNER,
        FLASH_ERROR
    }

    // Default Configuration Constants
    public static final String DEFAULT_MAIN_TEXT = "PROCESSING";
    public static final String DEFAULT_FONT_FAMILY = "Arial";
    public static final String FONT_FAMILY = DEFAULT_FONT_FAMILY;
    public static final int DEFAULT_MAX_ALPHA = 140;
    public static final int DEFAULT_ALPHA_STEP = 15;
    public static final int DEFAULT_FLASH_DURATION_MS = 600;

    public static final Color DEFAULT_BACKGROUND_COLOR = new Color(0, 0, 0);
    public static final Color DEFAULT_SPINNER_COLOR = new Color(52, 152, 219);
    public static final Color DEFAULT_TEXT_COLOR = Color.WHITE;
    public static final Color DEFAULT_HINT_COLOR = new Color(200, 200, 200);
    public static final Color DEFAULT_FLASH_COLOR = new Color(231, 76, 60);
    public static final Color DEFAULT_FLASH_BORDER_COLOR = new Color(192, 57, 43);

    // Fade-In, Transparency, and Effects
    private int maxAlpha = DEFAULT_MAX_ALPHA;
    private int alphaStep = DEFAULT_ALPHA_STEP;
    private int flashDurationMs = DEFAULT_FLASH_DURATION_MS;

    // Configurable Blocker Colors
    private Color backgroundColor = DEFAULT_BACKGROUND_COLOR;
    private Color spinnerColor = DEFAULT_SPINNER_COLOR;
    private Color textColor = DEFAULT_TEXT_COLOR;
    private Color hintTextColor = DEFAULT_HINT_COLOR;
    private Color flashColor = DEFAULT_FLASH_COLOR;
    private Color flashBorderColor = DEFAULT_FLASH_BORDER_COLOR;

    private boolean isBlocking = false;
    private final Timer animator;
    private int angle = 0;
    private int currentAlpha = 0;

    // Configuração do Modo Atual
    private Mode currentMode = Mode.SPINNER;
    private long flashStartTime = 0;

    // Reference to the active component layer to manage cursor state
    private JLayer<? extends JPanel> activeLayer;

    // Dynamic text variables initialized using the constants
    private String mainText = DEFAULT_MAIN_TEXT;
    private String hintText = null; // Optional escape instruction text
    private Runnable cancelCallback;

    public GenericUIBlocker() {
        this(new Color(0, 0, 0), 140);
    }

    public GenericUIBlocker(Color backgroundColor, int maxAlpha) {
        if (backgroundColor != null) {
            this.backgroundColor = new Color(backgroundColor.getRed(), backgroundColor.getGreen(), backgroundColor.getBlue());
            if (backgroundColor.getAlpha() < 255 && maxAlpha == 140) {
                this.maxAlpha = backgroundColor.getAlpha();
            } else {
                this.maxAlpha = Math.max(0, Math.min(255, maxAlpha));
            }
        } else {
            this.maxAlpha = Math.max(0, Math.min(255, maxAlpha));
        }
        // Smooth ~25 FPS animation loop
        this.animator = new Timer(40, this);
    }

    public GenericUIBlocker(Color backgroundColor, float opacity) {
        this(backgroundColor, Math.round(Math.max(0.0f, Math.min(1.0f, opacity)) * 255.0f));
    }

    /**
     * Sets the primary status text displayed below the loading spinner.
     */
    public void setMainText(String mainText) {
        this.mainText = mainText;
    }

    /**
     * Sets the optional instructional hint text displayed at the bottom.
     */
    public void setHintText(String hintText) {
        this.hintText = hintText;
    }

    /**
     * Sets the action to execute when the operator presses the ESC key.
     */
    public void setCancelCallback(Runnable cancelCallback) {
        this.cancelCallback = cancelCallback;
    }

    // =========================================================================
    // COLOR & TRANSPARENCY CONFIGURATION
    // =========================================================================

    /**
     * Sets the background overlay color.
     * If the specified color contains an alpha component (< 255), that alpha
     * is automatically applied as the maximum opacity.
     */
    public void setBackgroundColor(Color color) {
        if (color != null) {
            this.backgroundColor = new Color(color.getRed(), color.getGreen(), color.getBlue());
            if (color.getAlpha() < 255) {
                this.maxAlpha = color.getAlpha();
            }
            triggerRepaint();
        }
    }

    public Color getBackgroundColor() {
        return backgroundColor;
    }

    /**
     * Sets the maximum opacity/alpha level of the background overlay (0 to 255).
     * Higher values make the background darker / more saturated with color.
     */
    public void setMaxAlpha(int maxAlpha) {
        this.maxAlpha = Math.max(0, Math.min(255, maxAlpha));
        triggerRepaint();
    }

    public int getMaxAlpha() {
        return maxAlpha;
    }

    /**
     * Sets the opacity level as a float between 0.0f (transparent) and 1.0f (opaque).
     */
    public void setOpacity(float opacity) {
        setMaxAlpha(Math.round(Math.max(0.0f, Math.min(1.0f, opacity)) * 255.0f));
    }

    public float getOpacity() {
        return (float) maxAlpha / 255.0f;
    }

    public void setAlphaStep(int alphaStep) {
        this.alphaStep = Math.max(1, alphaStep);
    }

    public int getAlphaStep() {
        return alphaStep;
    }

    public void setSpinnerColor(Color spinnerColor) {
        if (spinnerColor != null) {
            this.spinnerColor = spinnerColor;
            triggerRepaint();
        }
    }

    public Color getSpinnerColor() {
        return spinnerColor;
    }

    public void setTextColor(Color textColor) {
        if (textColor != null) {
            this.textColor = textColor;
            triggerRepaint();
        }
    }

    public Color getTextColor() {
        return textColor;
    }

    public void setHintTextColor(Color hintTextColor) {
        if (hintTextColor != null) {
            this.hintTextColor = hintTextColor;
            triggerRepaint();
        }
    }

    public Color getHintTextColor() {
        return hintTextColor;
    }

    public void setFlashColor(Color flashColor) {
        if (flashColor != null) {
            this.flashColor = flashColor;
        }
    }

    public Color getFlashColor() {
        return flashColor;
    }

    public void setFlashBorderColor(Color flashBorderColor) {
        if (flashBorderColor != null) {
            this.flashBorderColor = flashBorderColor;
        }
    }

    public Color getFlashBorderColor() {
        return flashBorderColor;
    }

    public void setFlashDurationMs(int flashDurationMs) {
        this.flashDurationMs = Math.max(50, flashDurationMs);
    }

    public int getFlashDurationMs() {
        return flashDurationMs;
    }

    
    public void setBlocking(boolean block) {
        setBlocking(block, Mode.SPINNER);
    }

    public void setBlocking(boolean block, Mode mode) {
        boolean oldBlocking = this.isBlocking;
        this.isBlocking = block;
        this.currentMode = mode;

        if (block) {
            this.currentAlpha = maxAlpha; // Cor de fundo e opacidade ativas imediatamente desde o primeiro frame
            this.flashStartTime = System.currentTimeMillis();
            animator.start();
            updateCursor(Cursor.WAIT_CURSOR);
        } else {
            this.currentAlpha = 0;
            animator.stop();
            updateCursor(Cursor.DEFAULT_CURSOR);
        }

        firePropertyChange("blocking", oldBlocking, block);
        triggerRepaint();

    }

    public boolean isBlocking() {
        return this.isBlocking;
    }

    @Override
    public void applyPropertyChange(PropertyChangeEvent evt, JLayer<? extends JPanel> l) {
        if ("tick".equals(evt.getPropertyName()) || "blocking".equals(evt.getPropertyName())) {
            l.repaint();
        }
    }

    /**
     * Safely updates the cursor style on the active JLayer component container.
     */
    private void updateCursor(int cursorType) {
        if (activeLayer != null) {
            // Ensures cursor changes are dispatched directly on the Event Dispatch Thread
            SwingUtilities.invokeLater(() -> {
                activeLayer.setCursor(Cursor.getPredefinedCursor(cursorType));
            });
        }
    }

    private void triggerRepaint() {
        if (activeLayer != null) {
            SwingUtilities.invokeLater(() -> {
                activeLayer.revalidate();
                activeLayer.repaint();
                Component topWindow = SwingUtilities.getWindowAncestor(activeLayer);
                if (topWindow != null) {
                    topWindow.repaint();
                }
            });
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (isBlocking) {
            if (currentMode == Mode.SPINNER) {
                // Rotação contínua do spinner e manutenção da opacidade
                angle = (angle + 10) % 360;
                if (currentAlpha < maxAlpha) {
                    currentAlpha = Math.min(maxAlpha, currentAlpha + alphaStep);
                }
            } else if (currentMode == Mode.FLASH_ERROR) {
                // Lógica do Flash Tátil: Desvanece (fade-out) após o tempo estipulado
                long elapsed = System.currentTimeMillis() - flashStartTime;
                if (elapsed > flashDurationMs) {
                    // Remove o bloqueio automaticamente quando o tempo acaba
                    setBlocking(false);
                    return;
                } else {
                    // Calcula o fade-out baseado no tempo restante
                    float percentLeft = 1.0f - ((float) elapsed / flashDurationMs);
                    currentAlpha = (int) (maxAlpha * percentLeft);
                }
            }

            if (activeLayer != null) {
                activeLayer.repaint();
            }
            firePropertyChange("tick", 0, 1);
        }
    }

    @Override
    public void paint(Graphics g, JComponent c) {
        // 1. Paint underlying application GUI components first
        super.paint(g, c);

        // 2. Overlay visual blockade layers if active
        if (isBlocking) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Garante que o preenchimento cobre 100% da área do ecrã mesmo se um filho disparou repaint parcial
            g2.setClip(0, 0, c.getWidth(), c.getHeight());

            int centerX = c.getWidth() / 2;
            int centerY = c.getHeight() / 2;

            if (currentMode == Mode.SPINNER) {
                // --- RENDERIZAÇÃO DO MODO SPINNER COM COR E TRANSPARÊNCIA CONFIGURÁVEIS ---
                if (currentAlpha > 0) {
                    g2.setColor(new Color(
                            backgroundColor.getRed(),
                            backgroundColor.getGreen(),
                            backgroundColor.getBlue(),
                            Math.min(255, Math.max(0, currentAlpha))
                    ));
                    g2.fillRect(0, 0, c.getWidth(), c.getHeight());
                }

                // Aplica a transparência progressiva a todos os elementos internos do Spinner
                if (currentAlpha > 30) {
                    float textAlpha = (maxAlpha > 0) ? (float) currentAlpha / maxAlpha : 1.0f;
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, textAlpha));
                }

                // --- SPINNER RADIAL GRAPHIC ---
                g2.setStroke(new BasicStroke(5.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.setColor(new Color(255, 255, 255, 40));
                g2.drawOval(centerX - 30, centerY - 80, 60, 60);

                g2.setColor(spinnerColor);
                g2.drawArc(centerX - 30, centerY - 80, 60, 60, -angle, 120);

                // Renderiza o texto sob o efeito do Alpha do Spinner
                renderText(g2, c, centerY);

            } else if (currentMode == Mode.FLASH_ERROR) {
                // --- RENDERIZAÇÃO DO MODO FLASH ERRO COM COR E BORDA CONFIGURÁVEIS ---
                if (currentAlpha > 0) {
                    g2.setColor(new Color(
                            flashColor.getRed(),
                            flashColor.getGreen(),
                            flashColor.getBlue(),
                            Math.min(255, Math.max(0, currentAlpha))
                    ));
                    g2.fillRect(0, 0, c.getWidth(), c.getHeight());

                    g2.setStroke(new BasicStroke(15.0f));
                    g2.setColor(new Color(
                            flashBorderColor.getRed(),
                            flashBorderColor.getGreen(),
                            flashBorderColor.getBlue(),
                            Math.min(255, Math.max(0, currentAlpha))
                    ));
                    g2.drawRect(0, 0, c.getWidth(), c.getHeight());
                }

                if (currentAlpha > 30) {
                    float textAlpha = (maxAlpha > 0) ? (float) currentAlpha / maxAlpha : 1.0f;
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, textAlpha));
                }

                renderText(g2, c, centerY);
            }
            g2.dispose();
        }
    }

    private void renderText(Graphics2D g2, JComponent c, int centerY) {
        // --- STATUS TEXT RENDER ---
        if (mainText != null && !mainText.isEmpty()) {
            g2.setColor(textColor);
            g2.setFont(new Font(FONT_FAMILY, Font.BOLD, 22));
            FontMetrics fm = g2.getFontMetrics();
            int x = (c.getWidth() - fm.stringWidth(mainText)) / 2;
            int y = centerY + 30;
            g2.drawString(mainText, x, y);
        }

        // --- ACTION HINT TEXT RENDER ---
        // Adicionada a proteção para apenas desenhar a dica do ESC se o modo ativo for SPINNER
        if (hintText != null && !hintText.isEmpty() && cancelCallback != null && currentMode == Mode.SPINNER) {
            g2.setFont(new Font(FONT_FAMILY, Font.PLAIN, 14));
            g2.setColor(hintTextColor);
            FontMetrics fmHint = g2.getFontMetrics();
            int hintX = (c.getWidth() - fmHint.stringWidth(hintText)) / 2;
            int hintY = centerY + 70;
            g2.drawString(hintText, hintX, hintY);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void installUI(JComponent c) {
        super.installUI(c);
        this.activeLayer = (JLayer<? extends JPanel>) c;
        // Enable global mouse and keyboard event capturing for this layer
        this.activeLayer.setLayerEventMask(AWTEvent.MOUSE_EVENT_MASK | AWTEvent.KEY_EVENT_MASK);
    }

    @Override
    public void uninstallUI(JComponent c) {
        super.uninstallUI(c);
        animator.stop();
        if (activeLayer != null) {
            activeLayer.setLayerEventMask(0);
            activeLayer.setCursor(Cursor.getDefaultCursor());
            activeLayer = null;
        }
    }

    @Override
    public void eventDispatched(AWTEvent e, JLayer<? extends JPanel> l) {
        if (isBlocking) {
            // Check if input is a key release for the Escape key (Apenas no modo SPINNER)
            if (e instanceof KeyEvent && e.getID() == KeyEvent.KEY_RELEASED) {
                KeyEvent ke = (KeyEvent) e;
                if (ke.getKeyCode() == KeyEvent.VK_ESCAPE && cancelCallback != null && currentMode == Mode.SPINNER) {
                    SwingUtilities.invokeLater(cancelCallback);
                }
            }

            // Consume the event if it's an input subclass so it never passes down
            if (e instanceof InputEvent) {
                ((InputEvent) e).consume();
            }
        }
    }
}
