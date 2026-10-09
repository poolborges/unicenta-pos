package com.openbravo.pos.catalog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

/**
 * Modernized and highly configurable catalog card for POS terminals. Supports
 * customizable dimensions, fonts, paddings, and view modes.
 *
 * @author poolborges
 * @author KriolOS
 */
public class ProductCardV2 extends JButton {

    private static final long serialVersionUID = 1L;

    /**
     * Card rendering modes.
     */
    public enum ViewMode {
        COMPACT, // Minimal legacy footprint: central icon/text
        DETAILED  // Modern structured card: image, multiline title, accent bar, price
    }

    /**
     * Configuration specifications for card sizing, fonts, and paddings.
     */
    public record CardConfig(
            int cardWidth,
            int cardHeight,
            int imageBoxSize,
            Font titleFont,
            Font priceFont,
            Font fallbackFont,
            Insets padding,
            Color priceColor,
            int accentBarHeight,
            int cardArc
            ) {

        /**
         * Preset configuration for modern detailed mode.
         */
        public static CardConfig defaultDetailed() {
            return new CardConfig(
                    130, // cardWidth
                    140, // cardHeight
                    72, // imageBoxSize
                    new Font("Segoe UI", Font.BOLD, 12), // titleFont
                    new Font("Segoe UI", Font.BOLD, 13), // priceFont
                    new Font("Segoe UI", Font.BOLD, 22), // fallbackFont
                    new Insets(6, 6, 6, 6), // padding
                    new Color(22, 163, 74), // priceColor (Green)
                    4, // accentBarHeight
                    8 // cardArc
            );
        }

        /**
         * Preset configuration for compact/dense grid mode.
         */
        public static CardConfig defaultCompact() {
            return new CardConfig(
                    96, // cardWidth
                    82, // imageBoxSize
                    48, // imageBoxSize
                    new Font("Arial Bold", Font.PLAIN, 12), // titleFont
                    new Font("Arial Bold", Font.PLAIN, 11), // priceFont
                    new Font("Segoe UI", Font.BOLD, 16), // fallbackFont
                    new Insets(2, 2, 2, 2), // padding
                    new Color(22, 163, 74), // priceColor
                    3, // accentBarHeight
                    4 // cardArc
            );
        }

        /*
        
        ProductCardV2.CardConfig cardConfig = new ProductCardV2.CardConfig(
        config.getInt("catalog.card.width", 130),
        config.getInt("catalog.card.height", 140),
        config.getInt("catalog.card.image.size", 72),
        new Font("Segoe UI", Font.BOLD, 12),
        new Font("Segoe UI", Font.BOLD, 13),
        new Font("Segoe UI", Font.BOLD, 22),
        new Insets(6, 6, 6, 6),
        new Color(22, 163, 74),
        config.getInt("catalog.card.accent.height", 4),
        config.getInt("catalog.card.arc", 8)
);
        
         */
    }

    private final CatalogItem item;
    private final ViewMode mode;
    private final CardConfig config;
    private Color categoryAccentColor;

    /**
     * Default constructor using standard DETAILED configuration.
     */
    public ProductCardV2(CatalogItem item, ActionListener listener) {
        this(item, ViewMode.DETAILED, CardConfig.defaultDetailed(), listener);
    }

    /**
     * Constructor allowing ViewMode selection with standard mode presets.
     */
    public ProductCardV2(CatalogItem item, ViewMode mode, ActionListener listener) {
        this(item, mode, mode == ViewMode.COMPACT ? CardConfig.defaultCompact() : CardConfig.defaultDetailed(), listener);
    }

    /**
     * Fully parameterized constructor with custom CardConfig.
     */
    public ProductCardV2(CatalogItem item, ViewMode mode, CardConfig config, ActionListener listener) {
        this.item = item;
        this.mode = mode != null ? mode : ViewMode.DETAILED;
        this.config = config != null ? config : CardConfig.defaultDetailed();

        initBaseProperties();

        if (this.mode == ViewMode.COMPACT) {
            setupCompactMode();
        } else {
            setupDetailedMode();
        }

        if (listener != null) {
            addActionListener(e -> listener.actionPerformed(
                    new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "product_click")
            ));
        }
    }

    private void initBaseProperties() {
        setFocusPainted(false);
        setFocusable(false);
        setRequestFocusEnabled(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        applyComponentOrientation(getComponentOrientation());
        putClientProperty("item", item);

        // Native properties for FlatLaf themes
        putClientProperty("JButton.buttonType", "roundRect");
        putClientProperty("JButton.arc", config.cardArc());

        // Parse optional category accent color
        if (item != null && item.getColorHex() != null && !item.getColorHex().isBlank()) {
            try {
                this.categoryAccentColor = Color.decode(item.getColorHex());
            }
            catch (NumberFormatException ignored) {
                this.categoryAccentColor = null;
            }
        }

        if (item != null && item.getTextTip() != null) {
            setToolTipText(item.getTextTip());
        }
    }

    private void setupCompactMode() {
        setHorizontalTextPosition(SwingConstants.CENTER);
        setVerticalTextPosition(SwingConstants.BOTTOM);
        setFont(config.titleFont());
        setMargin(config.padding());

        Dimension dim = new Dimension(config.cardWidth(), config.cardHeight());
        setPreferredSize(dim);
        setMinimumSize(dim);
        setMaximumSize(dim);

        if (item != null) {
            if (item.getImage() != null) {
                Image scaled = scaleImage(item.getImage(), config.imageBoxSize(), config.imageBoxSize());
                setIcon(new ImageIcon(scaled));
            }
            if (item.getText() != null) {
                setText(item.getText());
            }
        }
    }

    private void setupDetailedMode() {
        setLayout(new BorderLayout(0, 4));

        Dimension dim = new Dimension(config.cardWidth(), config.cardHeight());
        setPreferredSize(dim);
        setMinimumSize(dim);

        Color borderColor = UIManager.getColor("Component.borderColor") != null
                ? UIManager.getColor("Component.borderColor")
                : new Color(220, 224, 230);

        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderColor, 1),
                new EmptyBorder(config.padding().top, config.padding().left, config.padding().bottom, config.padding().right)
        ));

        if (item == null) {
            return;
        }

        // 1. Center Image or Text Placeholder
        JLabel imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imageLabel.setVerticalAlignment(SwingConstants.CENTER);

        if (item.getImage() != null) {
            Image scaled = scaleImage(item.getImage(), config.imageBoxSize(), config.imageBoxSize());
            imageLabel.setIcon(new ImageIcon(scaled));
        } else {
            imageLabel.setText(extractInitials(item.getText()));
            imageLabel.setFont(config.fallbackFont());
            imageLabel.setForeground(UIManager.getColor("Label.disabledForeground") != null
                    ? UIManager.getColor("Label.disabledForeground")
                    : Color.GRAY);
        }
        add(imageLabel, BorderLayout.CENTER);

        // 2. Info Panel (Name & Price)
        JPanel textPanel = new JPanel(new BorderLayout(0, 2));
        textPanel.setOpaque(false);

        JLabel nameLabel = new JLabel("<html><center>" + sanitizeHtml(item.getText()) + "</center></html>", SwingConstants.CENTER);
        nameLabel.setFont(config.titleFont());
        textPanel.add(nameLabel, BorderLayout.CENTER);

        String priceText = item.getPrice() != null ? item.getPrice() : "";
        JLabel priceLabel = new JLabel(priceText, SwingConstants.CENTER);
        priceLabel.setFont(config.priceFont());
        priceLabel.setForeground(config.priceColor());
        textPanel.add(priceLabel, BorderLayout.SOUTH);

        add(textPanel, BorderLayout.SOUTH);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (categoryAccentColor != null && config.accentBarHeight() > 0) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(categoryAccentColor);

            int barHeight = config.accentBarHeight();
            if (mode == ViewMode.DETAILED) {
                g2.fillRoundRect(8, 4, getWidth() - 16, barHeight, 2, 2);
            } else {
                g2.fillRect(0, 0, getWidth(), barHeight);
            }
            g2.dispose();
        }
    }

    private Image scaleImage(Image srcImg, int targetW, int targetH) {
        if (srcImg.getWidth(null) <= targetW && srcImg.getHeight(null) <= targetH) {
            return srcImg;
        }
        return srcImg.getScaledInstance(targetW, targetH, Image.SCALE_SMOOTH);
    }

    private String extractInitials(String title) {
        if (title == null || title.isBlank()) {
            return "POS";
        }
        String[] words = title.trim().split("\\s+");
        if (words.length == 1) {
            return words[0].substring(0, Math.min(2, words[0].length())).toUpperCase();
        }
        return ("" + words[0].charAt(0) + words[1].charAt(0)).toUpperCase();
    }

    private String sanitizeHtml(String text) {
        if (text == null) {
            return "";
        }
        return text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    public CatalogItem getCatalogItem() {
        return item;
    }

    public ViewMode getViewMode() {
        return mode;
    }

    public CardConfig getConfig() {
        return config;
    }
}
