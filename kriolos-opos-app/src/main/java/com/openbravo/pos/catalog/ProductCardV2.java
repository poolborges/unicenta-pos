package com.openbravo.pos.catalog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

/**
 *
 * @author poolborges
 */
public class ProductCardV2 extends JPanel {

    private static final int PRODUCT_IMAGE_SIZE = 80;

    private final Border normalBorder;
    private final Border pressedBorder;

    public ProductCardV2(CatalogItem item, ActionListener listener) {

        // Get the default border from UIManager
        Color baseBorderColor;
        try {
            Border buttonBorder = UIManager.getBorder("Button.border");
            if (buttonBorder instanceof LineBorder border) {
                baseBorderColor = border.getLineColor();
            }else {
                baseBorderColor = Color.LIGHT_GRAY;
            }
        }
        catch (Exception ex) {
            //EX OMMITED
            baseBorderColor = Color.LIGHT_GRAY;
        }

        Border baseBorder = BorderFactory.createLineBorder(baseBorderColor, 1);
        Border highlightBorder = BorderFactory.createLineBorder(Color.decode("#4CAF50"), 2);
        Border emptyBorder = BorderFactory.createEmptyBorder(1, 1, 1, 1);

        normalBorder = BorderFactory.createCompoundBorder(baseBorder, emptyBorder);
        pressedBorder = highlightBorder;

        this.setLayout(new BorderLayout());

        this.setBorder(normalBorder);
        this.setPreferredSize(new Dimension(PRODUCT_IMAGE_SIZE + 20, PRODUCT_IMAGE_SIZE + 50));
        this.putClientProperty("item", item);

        MouseAdapter mouseAdapter = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JPanel sourcePanel = (JPanel) e.getSource();
                listener.actionPerformed(new ActionEvent(sourcePanel, ActionEvent.ACTION_PERFORMED, "product_click"));
            }

            @Override
            public void mousePressed(MouseEvent e) {
                JPanel clickedPanel = (JPanel) e.getSource();
                clickedPanel.setBorder(pressedBorder);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                JPanel releasedPanel = (JPanel) e.getSource();
                releasedPanel.setBorder(normalBorder);
            }
        };
        this.addMouseListener(mouseAdapter);

        //Set Card Image
        ImageIcon icon = null;
        if (item.getImage() != null) {
            icon = new ImageIcon(item.getImage());
        } else {
            //icon = new ImageIcon(createDefaultImage(PRODUCT_IMAGE_SIZE, PRODUCT_IMAGE_SIZE, createSmallText(item.getText())));
        }

        //Set Card Background Color
        if (item.getColorHex() != null) {
            Color categoryColor = Color.decode(item.getColorHex());
            this.setBackground(categoryColor);
        }

        this.setToolTipText(item.getTextTip());
        if (icon != null) {
            JLabel imageLabel = new JLabel(icon);
            imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
            imageLabel.setPreferredSize(new Dimension(PRODUCT_IMAGE_SIZE, PRODUCT_IMAGE_SIZE));

            this.add(imageLabel, BorderLayout.CENTER);
        }

        JPanel textPanel = new JPanel(new BorderLayout());
        textPanel.setBorder(new EmptyBorder(5, 5, 5, 5));

        JLabel nameLabel = new JLabel(item.getText(), SwingConstants.CENTER);
        nameLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        textPanel.add(nameLabel, BorderLayout.NORTH);

        String priceStr = item.getPrice();
        JLabel priceLabel = new JLabel(priceStr, SwingConstants.CENTER);
        priceLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        textPanel.add(priceLabel, BorderLayout.SOUTH);

        this.add(textPanel, BorderLayout.SOUTH);
    }

    private String createSmallText(String text) {
        return text.substring(0, Math.min(5, text.length()));
    }

    private Image createDefaultImage(int width, int height, String text) {
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();
        //g2d.setColor(Color.LIGHT_GRAY);
        //g2d.fillRect(0, 0, width, height);
        //g2d.setColor(Color.DARK_GRAY);
        g2d.setFont(new Font("SansSerif", Font.BOLD, 24));
        FontMetrics fm = g2d.getFontMetrics();
        int x = (width - fm.stringWidth(text)) / 2;
        int y = (fm.getAscent() + (height - (fm.getAscent() + fm.getDescent())) / 2);
        g2d.drawString(text, x, y);
        g2d.dispose();
        return img;
    }
}
