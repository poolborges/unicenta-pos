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

import com.openbravo.data.loader.ImageUtils;
import com.openbravo.pos.config.JPanelConfigGeneral;
import java.awt.image.*;
import java.awt.*;
import java.awt.font.TextAttribute;
import java.io.IOException;
import java.net.URL;
import java.text.AttributedString;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import net.coobird.thumbnailator.Thumbnailator;

/**
 *
 * @author JG uniCenta
 */
public class ThumbNailBuilder {

    private static final Logger LOGGER = Logger.getLogger(JPanelConfigGeneral.class.getName());

    private Image defaultImage;
    private int thumbWidth;
    private int thumbHeight;

    public ThumbNailBuilder(int width, int height, Image imgdef) {
        init(width, height, imgdef);
    }

    public ThumbNailBuilder(int width, int height, String img) {
        try {
            URL imageUrl = getClass().getResource(img);

            if (imageUrl == null) {
                imageUrl = getClass().getClassLoader().getResource(img);
                
                if (imageUrl == null) {
                    imageUrl = ClassLoader.getSystemResource(img);
                }
            }

            if (imageUrl != null) {
                init(width, height, ImageIO.read(imageUrl));
            }

        } catch (IOException fnfe) {
            //TODO should log
        }

    }

    private void init(int width, int height, Image imgdef) {
        thumbWidth = width;
        thumbHeight = height;
        if (imgdef == null) {
            defaultImage = ImageUtils.generateColorImage(Color.WHITE, width, height);
        } else {
            defaultImage = createThumb(imgdef);
        }
    }

    public Image getThumbNail() {
        return defaultImage;
    }
    
    public Image getThumbNail(Image img) {
        if(img != null)
            return createThumb(img);
        else 
            return defaultImage;
    }

    public Image getThumbNail(Image img, String text) {
        if(img != null)
            return createThumb(img, text);
        else{
            return createThumb(defaultImage, text);
        }
    }
    
    private Image createThumb(Image img, String text){
        
        BufferedImage imageBuf = ImageUtils.toBufferedImage(createThumb(img));

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

    private Image createThumb(Image img) {

        //Image is already in size or too small
        if (img.getHeight(null) <= thumbHeight && img.getWidth(null) <= thumbWidth) {
            return img;
        }

        return Thumbnailator.createThumbnail(img, thumbWidth, thumbWidth);
    }
}
