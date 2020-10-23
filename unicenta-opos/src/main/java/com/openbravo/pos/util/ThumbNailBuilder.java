//    KrOS POS
//    Copyright (c) 2009-2016 uniCenta
//    
//
//     
//
//    This program is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    (at your option) any later version.
//
//    This program is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with KrOS POS.  If not, see <http://www.gnu.org/licenses/>.
package com.openbravo.pos.util;

import java.awt.image.*;
import java.awt.*;
import java.awt.font.TextAttribute;
import java.io.IOException;
import java.text.AttributedString;
import javax.imageio.ImageIO;
import net.coobird.thumbnailator.Thumbnailator;

/**
 *
 * @author JG uniCenta
 */
public class ThumbNailBuilder {

    private Image m_imgdefault;
    private int m_width;
    private int m_height;

    /**
     * Creates a new instance of ThumbNailBuilder
     *
     * @param width
     * @param height
     */
    public ThumbNailBuilder(int width, int height) {
        init(width, height, null);
    }

    /**
     *
     * @param width
     * @param height
     * @param imgdef
     */
    public ThumbNailBuilder(int width, int height, Image imgdef) {
        init(width, height, imgdef);

    }

    /**
     *
     * @param width
     * @param height
     * @param img
     */
    public ThumbNailBuilder(int width, int height, String img) {

        try {
            init(width, height, ImageIO.read(getClass().getClassLoader().getResourceAsStream(img)));
        } catch (IOException fnfe) {
            init(width, height, null);
        }
    }

    private void init(int width, int height, Image imgdef) {
        m_width = width;
        m_height = height;
        if (imgdef == null) {
            m_imgdefault = null;
        } else {
            m_imgdefault = createThumbNail(imgdef);
        }
    }

    /**
     *
     * @param img
     * @return
     */
    public Image getThumbNail(Image img) {

        if (img == null) {
            return m_imgdefault;
        } else {
            return createThumbNail(img);
        }
    }

    /**
     * get Thumbnail Image
     *
     * @return
     */
    public Image getThumbNail() {
        return m_imgdefault;
    }

    /**
     *
     * @param img
     * @param text
     * @return
     */
    public Image getThumbNailText(Image img, String text) {
        
        BufferedImage imageBuf = toBufferedImage(getThumbNail(img));

        Font font = new Font("Arial", Font.BOLD, 18);

	AttributedString attributedText = new AttributedString(text);
	attributedText.addAttribute(TextAttribute.FONT, font);
	attributedText.addAttribute(TextAttribute.FOREGROUND, Color.BLACK);
	 
	Graphics g = imageBuf.getGraphics();
        
        FontMetrics metrics = g.getFontMetrics(font);
        
        //Center
	//int positionX = (imageBuf.getWidth() - metrics.stringWidth(text)) / 2;
	//int positionY = (imageBuf.getHeight() - metrics.getHeight()) / 2 + metrics.getAscent();
        //Top Left
        int positionX = 0;
	int positionY = metrics.getAscent();
        
	g.drawString(attributedText.getIterator(), positionX, positionY);
  
        return imageBuf;
    }

    private Image createThumbNail(Image img) {

        //Image is already in size or too small
        if (img.getHeight(null) <= m_height && img.getWidth(null) <= m_width) {
            return img;
        }

        return Thumbnailator.createThumbnail(img, m_width, m_width);
    }

    /**
     * Converts a given Image into a BufferedImage
     *
     * @param img The Image to be converted
     * @return The converted BufferedImage
     */
    private static BufferedImage toBufferedImage(Image img) {
        if (img instanceof BufferedImage) {
            return (BufferedImage) img;
        }

        // Create a buffered image with transparency
        BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

        // Draw the image on to the buffered image
        Graphics2D bGr = bimage.createGraphics();
        bGr.drawImage(img, 0, 0, null);
        bGr.dispose();

        // Return the buffered image
        return bimage;
    }
}
