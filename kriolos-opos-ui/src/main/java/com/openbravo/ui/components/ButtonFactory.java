package com.openbravo.ui.components;

import com.openbravo.ui.icon.IconFactory;
import com.openbravo.ui.icon.IconKey;
import com.openbravo.ui.icon.PosIcon;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

/**
 * Factory for creating ergonomic, touch-friendly action buttons with
 * consistent geometry, typography scaling, keyboard shortcuts, and focus protection hooks.
 *
 * @author KriolOS
 * @since 1.0.0
 */
public class ButtonFactory {

    public static final Color SUCCESS_COLOR = UIColor.SUCCESS;
    public static final Color DANGER_COLOR = UIColor.DANGER;
    public static final Color ACCENT_COLOR = UIColor.PRIMARY;
    public static final Color WARNING_COLOR = UIColor.WARNING;
    public static final Color PRIMARY_COLOR = UIColor.PRIMARY;
    public static final Color INFO_COLOR = UIColor.INFO;
    public static final Color MUTED_COLOR = UIColor.MUTED;

    /**
     * Creates a JButton configured with native LTR/RTL support without
     * Look-and-Feel coupling.
     *
     * @param size The desired size variant (MEDIUM, LARGE, EXTRA_LARGE, MASSIVE).
     * @return A customized native JButton.
     */
    public static JButton createButton(ButtonSize size) {
        JButton btn = new JButton();

        // 1. Logical Alignment: [Icon TEXT ......] compatible with LTR/RTL
        btn.applyComponentOrientation(btn.getComponentOrientation());
        btn.setHorizontalTextPosition(SwingConstants.TRAILING);
        btn.setHorizontalAlignment(SwingConstants.LEADING);

        // 2. Native Swing Sizing: Define height using minimum and preferred structures
        int h = size != null ? size.getHeight() : ButtonSize.DEFAULT_HEIGHT;
        Dimension baseSize = new Dimension(100, h);
        btn.setPreferredSize(baseSize);
        btn.setMinimumSize(baseSize);

        // 3. Native Padding: Generous margins for touch-ready interactions using EmptyBorder
        int padX = size != null ? size.getPaddingX() : ButtonSize.DEFAULT_PADDING;
        btn.setBorder(BorderFactory.createCompoundBorder(
                btn.getBorder(),
                new EmptyBorder(0, padX, 0, padX)
        ));

        // 4. Native Spacing: Scaled gap between Icon and Text
        int fontSize = size != null ? size.getFontSize() : ButtonSize.DEFAULT_FONT_SIZE;
        btn.setIconTextGap(fontSize / 4);

        // 5. Touch Accessibility Settings
        btn.setFocusPainted(false);
        btn.setFocusable(false);
        btn.setRequestFocusEnabled(false);

        // 6. Native Typography Scaling
        Font currentFont = btn.getFont();
        if (currentFont != null) {
            btn.setFont(new Font(currentFont.getName(), Font.BOLD, fontSize));
        }

        // Focus-Clearing Hook: Defends the global Numpad Enter mapping from theft
        btn.addActionListener(e -> clearFocus(btn));

        return btn;
    }

    /**
     * Creates an ergonomic action button with an icon,
     * accessible keyboard mnemonic, descriptive tooltip, and touch-ready styling.
     *
     * @param text Button label text
     * @param icon Vector or bitmap icon, or null
     * @param size Sizing variant (MEDIUM, LARGE, EXTRA_LARGE, MASSIVE)
     * @param mnemonic KeyEvent.VK_* constant (or 0 if none)
     * @param tooltip Informative tooltip or null
     * @param listener Action listener to execute on click/trigger
     * @return Fully configured JButton
     */
    public static JButton createActionButton(
            String text,
            Icon icon,
            ButtonSize size,
            int mnemonic,
            String tooltip,
            ActionListener listener) {

        JButton btn = new JButton();
        btn.setText(text != null ? text : "");
        if (icon != null) {
            btn.setIcon(icon);
        }

        btn.applyComponentOrientation(btn.getComponentOrientation());
        btn.setHorizontalAlignment(SwingConstants.CENTER);
        btn.setHorizontalTextPosition(SwingConstants.TRAILING);
        btn.setVerticalTextPosition(SwingConstants.CENTER);

        int h = size != null ? size.getHeight() : ButtonSize.DEFAULT_HEIGHT;
        Dimension baseSize = new Dimension(100, h);
        btn.setPreferredSize(baseSize);
        btn.setMinimumSize(baseSize);

        int padX = size != null ? size.getPaddingX() : ButtonSize.DEFAULT_PADDING;
        btn.setBorder(BorderFactory.createCompoundBorder(
                btn.getBorder(),
                new EmptyBorder(0, padX, 0, padX)
        ));

        int fontSize = size != null ? size.getFontSize() : ButtonSize.DEFAULT_FONT_SIZE;
        btn.setIconTextGap(fontSize / 4);

        Font currentFont = btn.getFont();
        if (currentFont != null) {
            btn.setFont(new Font(currentFont.getName(), Font.BOLD, fontSize));
        }

        btn.setFocusPainted(false);
        btn.setFocusable(false);
        btn.setRequestFocusEnabled(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        btn.putClientProperty("JButton.buttonType", "roundRect");

        setupMnemonicAndTooltip(btn, mnemonic, tooltip, text);

        if (listener != null) {
            btn.addActionListener(listener);
        }

        btn.addActionListener(e -> clearFocus(btn));

        return btn;
    }

    /**
     * Creates an ergonomic action button with optional text-based icon.
     *
     * @param text Button label text
     * @param fontIcon Font-based icon glyph or null
     * @param size Sizing variant (MEDIUM, LARGE, EXTRA_LARGE, MASSIVE)
     * @param mnemonic KeyEvent.VK_* constant (or 0 if none)
     * @param tooltip Informative tooltip or null
     * @param listener Action listener to execute on click/trigger
     * @return Fully configured JButton
     */
    public static JButton createActionButton(
            String text,
            String fontIcon,
            ButtonSize size,
            int mnemonic,
            String tooltip,
            ActionListener listener) {

        JButton btn = new JButton();
        btn.setText(formatButtonText(text, fontIcon));

        btn.applyComponentOrientation(btn.getComponentOrientation());
        btn.setHorizontalAlignment(SwingConstants.CENTER);
        btn.setHorizontalTextPosition(SwingConstants.TRAILING);
        btn.setVerticalTextPosition(SwingConstants.CENTER);

        int h = size != null ? size.getHeight() : ButtonSize.DEFAULT_HEIGHT;
        Dimension baseSize = new Dimension(100, h);
        btn.setPreferredSize(baseSize);
        btn.setMinimumSize(baseSize);

        int padX = size != null ? size.getPaddingX() : ButtonSize.DEFAULT_PADDING;
        btn.setBorder(BorderFactory.createCompoundBorder(
                btn.getBorder(),
                new EmptyBorder(0, padX, 0, padX)
        ));

        int fontSize = size != null ? size.getFontSize() : ButtonSize.DEFAULT_FONT_SIZE;
        btn.setIconTextGap(fontSize / 4);

        Font currentFont = btn.getFont();
        if (currentFont != null) {
            btn.setFont(new Font(currentFont.getName(), Font.BOLD, fontSize));
        }

        btn.setFocusPainted(false);
        btn.setFocusable(false);
        btn.setRequestFocusEnabled(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        btn.putClientProperty("JButton.buttonType", "roundRect");

        setupMnemonicAndTooltip(btn, mnemonic, tooltip, text);

        if (listener != null) {
            btn.addActionListener(listener);
        }

        btn.addActionListener(e -> clearFocus(btn));

        return btn;
    }

    public static JButton createActionButton(
            String text,
            String fontIcon,
            ButtonSize size,
            ActionListener listener) {
        return createActionButton(text, fontIcon, size, 0, null, listener);
    }

    public static JButton createActionButton(
            String text,
            Icon icon,
            ButtonSize size,
            ActionListener listener) {
        return createActionButton(text, icon, size, 0, null, listener);
    }

    public static JButton createActionButton(
            String text,
            IconKey iconKey,
            ButtonSize size,
            ActionListener listener) {
        return createActionButton(text, iconKey, size, 0, null, listener);
    }

    public static JButton createActionButton(
            String text,
            IconKey iconKey,
            ButtonSize size,
            int mnemonic,
            String tooltip,
            ActionListener listener) {
        int iconSize = size != null ? size.getFontSize() + 4 : 20;
        Icon icon = iconKey != null ? IconFactory.getIcon(iconKey, iconSize) : null;
        return createActionButton(text, icon, size, mnemonic, tooltip, listener);
    }

    public static JButton createActionButton(
            String text,
            IconKey iconKey,
            Color iconColor,
            ButtonSize size,
            int mnemonic,
            String tooltip,
            ActionListener listener) {
        int iconSize = size != null ? size.getFontSize() + 4 : 20;
        Icon icon = iconKey != null ? IconFactory.getIcon(iconKey, iconSize, iconColor) : null;
        return createActionButton(text, icon, size, mnemonic, tooltip, listener);
    }

    public static JButton createActionButton(
            String text,
            PosIcon posIcon,
            ButtonSize size,
            int mnemonic,
            String tooltip,
            ActionListener listener) {
        return createActionButton(text, posIcon != null ? posIcon.getIconKey() : null, size, mnemonic, tooltip, listener);
    }

    public static JButton createIconButton(
            IconKey iconKey,
            ButtonSize size,
            String tooltip,
            ActionListener listener) {
        int iconSize = size != null ? size.getFontSize() + 4 : 20;
        Icon icon = iconKey != null ? IconFactory.getIcon(iconKey, iconSize) : null;
        return createActionButton("", icon, size, 0, tooltip, listener);
    }

    public static JButton createActionButton(
            String text,
            String fontIcon,
            ButtonSize size,
            int mnemonic,
            ActionListener listener) {
        return createActionButton(text, fontIcon, size, mnemonic, null, listener);
    }

    public static JButton createActionButton(
            String text,
            Icon icon,
            ButtonSize size,
            int mnemonic,
            ActionListener listener) {
        return createActionButton(text, icon, size, mnemonic, null, listener);
    }

    public static JButton createActionButton(
            String text,
            ButtonSize size,
            int mnemonic,
            String tooltip,
            ActionListener listener) {
        return createActionButton(text, (String) null, size, mnemonic, tooltip, listener);
    }

    public static JButton createActionButton(
            String text,
            ButtonSize size,
            int mnemonic,
            ActionListener listener) {
        return createActionButton(text, (String) null, size, mnemonic, null, listener);
    }

    public static JButton createActionButton(
            String text,
            ButtonSize size,
            ActionListener listener) {
        return createActionButton(text, (String) null, size, 0, null, listener);
    }

    /**
     * Creates a success / positive Call-To-Action button (e.g. Pay, Checkout).
     */
    public static JButton createSuccessButton(
            String text,
            String fontIcon,
            ButtonSize size,
            int mnemonic,
            String tooltip,
            ActionListener listener) {
        JButton btn = createActionButton(text, fontIcon, size, mnemonic, tooltip, listener);
        btn.setBackground(SUCCESS_COLOR);
        btn.setForeground(Color.WHITE);
        btn.setOpaque(true);
        btn.putClientProperty("FlatLaf.styleClass", "accent");
        return btn;
    }

    public static JButton createSuccessButton(
            String text,
            Icon icon,
            ButtonSize size,
            int mnemonic,
            String tooltip,
            ActionListener listener) {
        JButton btn = createActionButton(text, icon, size, mnemonic, tooltip, listener);
        btn.setBackground(SUCCESS_COLOR);
        btn.setForeground(Color.WHITE);
        btn.setOpaque(true);
        btn.putClientProperty("FlatLaf.styleClass", "accent");
        return btn;
    }

    public static JButton createSuccessButton(
            String text,
            ButtonSize size,
            int mnemonic,
            String tooltip,
            ActionListener listener) {
        return createSuccessButton(text, (String) null, size, mnemonic, tooltip, listener);
    }

    /**
     * Creates a danger / destructive action button (e.g. Close Cash, Delete Line).
     */
    public static JButton createDangerButton(
            String text,
            String fontIcon,
            ButtonSize size,
            int mnemonic,
            String tooltip,
            ActionListener listener) {
        JButton btn = createActionButton(text, fontIcon, size, mnemonic, tooltip, listener);
        btn.setBackground(DANGER_COLOR);
        btn.setForeground(Color.WHITE);
        btn.setOpaque(true);
        return btn;
    }

    public static JButton createDangerButton(
            String text,
            Icon icon,
            ButtonSize size,
            int mnemonic,
            String tooltip,
            ActionListener listener) {
        JButton btn = createActionButton(text, icon, size, mnemonic, tooltip, listener);
        btn.setBackground(DANGER_COLOR);
        btn.setForeground(Color.WHITE);
        btn.setOpaque(true);
        return btn;
    }

    public static JButton createDangerButton(
            String text,
            ButtonSize size,
            int mnemonic,
            String tooltip,
            ActionListener listener) {
        return createDangerButton(text, (String) null, size, mnemonic, tooltip, listener);
    }

    /**
     * Creates an accent-highlighted action button.
     */
    public static JButton createAccentButton(
            String text,
            String fontIcon,
            ButtonSize size,
            int mnemonic,
            String tooltip,
            ActionListener listener) {
        JButton btn = createActionButton(text, fontIcon, size, mnemonic, tooltip, listener);
        btn.putClientProperty("FlatLaf.styleClass", "accent");
        return btn;
    }

    public static JButton createAccentButton(
            String text,
            Icon icon,
            ButtonSize size,
            int mnemonic,
            String tooltip,
            ActionListener listener) {
        JButton btn = createActionButton(text, icon, size, mnemonic, tooltip, listener);
        btn.putClientProperty("FlatLaf.styleClass", "accent");
        return btn;
    }

    public static JButton createAccentButton(
            String text,
            ButtonSize size,
            int mnemonic,
            String tooltip,
            ActionListener listener) {
        return createAccentButton(text, (String) null, size, mnemonic, tooltip, listener);
    }

    /**
     * Formats button text with optional font-based vector icon.
     */
    public static String formatButtonText(String text, String fontIcon) {
        if (fontIcon != null && !fontIcon.isBlank()) {
            if (text != null && !text.isBlank()) {
                return fontIcon + "  " + text;
            }
            return fontIcon;
        }
        return text != null ? text : "";
    }

    private static void setupMnemonicAndTooltip(JButton btn, int mnemonic, String tooltip, String text) {
        if (mnemonic > 0) {
            btn.setMnemonic(mnemonic);
        }
        String keyText = mnemonic > 0 ? KeyEvent.getKeyText(mnemonic) : null;
        if (tooltip != null && !tooltip.isBlank()) {
            if (keyText != null && !tooltip.contains("Alt+") && !tooltip.contains("F")) {
                btn.setToolTipText(tooltip + " (Alt+" + keyText + ")");
            } else {
                btn.setToolTipText(tooltip);
            }
        } else if (keyText != null) {
            String base = (text != null && !text.isBlank()) ? text.trim() : "Action";
            btn.setToolTipText(base + " (Alt+" + keyText + ")");
        }
    }

    /**
     * Safely returns focus back to the primary window ancestor.
     */
    public static void clearFocus(JButton button) {
        Component window = SwingUtilities.getWindowAncestor(button);
        if (window != null) {
            window.requestFocusInWindow();
        }
    }
}
