/*
 * Copyright (C) 2022 KiolOS<https://github.com/kriolos>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.openbravo.pos.util;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Paint;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JLabel;

/**
 *
 * @author JG uniCenta
 */
public class ThumbNailBuilder {

    private static final Logger LOGGER = Logger.getLogger(ThumbNailBuilder.class.getName());

    private Image m_imgdefault;

    private int m_width;

    private int m_height;

    public ThumbNailBuilder(int width, int height) {
        init(width, height, null);
    }

    public ThumbNailBuilder(int width, int height, Image imgdef) {
        init(width, height, imgdef);
    }

    public ThumbNailBuilder(int width, int height, String img) {
        try {
            LOGGER.info("Loading image: " + img);
            InputStream inputStrem = getClass().getClassLoader().getResourceAsStream(img);
            BufferedImage bufImage = null;
            if (inputStrem != null) {
                bufImage = ImageIO.read(inputStrem);
            } else {

                inputStrem = ThumbNailBuilder.class.getResource(img).openStream();

                if (inputStrem != null) {
                    bufImage = ImageIO.read(inputStrem);
                } else {
                    //Creating of EMPTY IMAGE
                    bufImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
                }
            }
            init(width, height, bufImage);
        } catch (IOException ex) {
            LOGGER.log(Level.WARNING, "Exception loading resource image" + img, ex);
            init(width, height, null);
        }
    }

    private void init(int width, int height, Image imgdef) {
        this.m_width = width;
        this.m_height = height;
        if (imgdef == null) {
            this.m_imgdefault = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        } else {
            this.m_imgdefault = createThumbNail(imgdef);
        }
    }

    public int getWidth() {
        return this.m_width;
    }

    public int getHeight() {
        return this.m_height;
    }

    public Image getThumbNail(Image img) {
        if (img == null) {
            return this.m_imgdefault;
        }
        return createThumbNail(img);
    }

    public Image getThumbNail() {
        return this.m_imgdefault;
    }

    public Image getThumbNailText(Image img, String text) {
        img = getThumbNail(img);
        BufferedImage imgtext = new BufferedImage(img.getWidth(null), img.getHeight(null), 2);
        Graphics2D g2d = imgtext.createGraphics();
        JLabel label = new JLabel();
        label.setOpaque(false);
        label.setText(text);
        label.setHorizontalAlignment(0);
        label.setVerticalAlignment(3);
        Dimension d = label.getPreferredSize();
        label.setBounds(0, 0, imgtext.getWidth(), d.height);
        Color c1 = new Color(255, 255, 255, 64);
        Color c2 = new Color(255, 255, 255, 208);
        Paint gpaint = new GradientPaint(new Point(0, 0), c1, new Point(label.getWidth() / 2, 0), c2, true);
        g2d.drawImage(img, 0, 0, (ImageObserver) null);
        g2d.translate(0, imgtext.getHeight() - label.getHeight());
        g2d.setPaint(gpaint);
        g2d.fillRect(0, 0, imgtext.getWidth(), label.getHeight());
        label.paint(g2d);
        g2d.dispose();
        return imgtext;
    }

    public Image getThumbNailText(BufferedImage imgBuff, String text) {
        Image img = imgBuff.getScaledInstance(imgBuff.getWidth(), imgBuff.getHeight(), imgBuff.getType());
        return getThumbNailText(img, text);
    }

    public Image getThumbNail(BufferedImage imgBuff, String text) {
        return getThumbNailText(imgBuff, text);
    }

    private Image createThumbNail(Image img) {

        int targetw;
        int targeth;

        double scalex = (double) m_width / (double) img.getWidth(null);
        double scaley = (double) m_height / (double) img.getHeight(null);
        if (scalex < scaley) {
            targetw = m_width;
            targeth = (int) (img.getHeight(null) * scalex);
        } else {
            targetw = (int) (img.getWidth(null) * scaley);
            targeth = m_height;
        }

        int midw = img.getWidth(null);
        int midh = img.getHeight(null);
        BufferedImage midimg = null;
        Graphics2D g2d = null;

        Image previmg = img;
        int prevw = img.getWidth(null);
        int prevh = img.getHeight(null);

        do {
            if (midw > targetw) {
                midw /= 2;
                if (midw < targetw) {
                    midw = targetw;
                }
            } else {
                midw = targetw;
            }
            if (midh > targeth) {
                midh /= 2;
                if (midh < targeth) {
                    midh = targeth;
                }
            } else {
                midh = targeth;
            }
            if (midimg == null) {
                midimg = new BufferedImage(midw, midh, BufferedImage.TYPE_INT_ARGB);
                g2d = midimg.createGraphics();
                g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            }

            if (g2d != null) {
                g2d.drawImage(previmg, 0, 0, midw, midh, 0, 0, prevw, prevh, null);
            }
            prevw = midw;
            prevh = midh;
            previmg = midimg;
        } while (midw != targetw || midh != targeth);

        if (g2d != null) {
            g2d.dispose();
        }

        if (m_width != midimg.getWidth() || m_height != midimg.getHeight()) {
            midimg = new BufferedImage(m_width, m_height, BufferedImage.TYPE_INT_ARGB);
            int x = (m_width > targetw) ? (m_width - targetw) / 2 : 0;
            int y = (m_height > targeth) ? (m_height - targeth) / 2 : 0;
            g2d = midimg.createGraphics();
            g2d.drawImage(previmg, x, y, x + targetw, y + targeth,
                    0, 0, targetw, targeth, null);
            g2d.dispose();
            previmg = midimg;
        }
        return previmg;
    }

    public static BufferedImage resize(BufferedImage image, int width, int height) {
        BufferedImage bi = new BufferedImage(width, height, 3);
        Graphics2D g2d = bi.createGraphics();
        g2d.addRenderingHints(new RenderingHints(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY));
        g2d.drawImage(image, 0, 0, width, height, null);
        g2d.dispose();
        return bi;
    }

    /**
     * Create Image with White color
     * @param width Image with
     * @param height Image height
     * @return Image
     */
    public static BufferedImage createImage(int width, int height) {
        return ThumbNailBuilder.createImage(width, height, Color.WHITE);
    }

    public static BufferedImage createImage(int width, int height, Color color) {
        BufferedImage bi = new BufferedImage(width, height, 3);
        Graphics2D g2d = bi.createGraphics();
        g2d.addRenderingHints(new RenderingHints(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY));
        g2d.setColor(color);
        g2d.fillRect(0, 0, bi.getWidth(), bi.getHeight());
        //g2d.drawImage(bi, 0, 0, width, height, null);
        g2d.dispose();
        return bi;
    }
}
