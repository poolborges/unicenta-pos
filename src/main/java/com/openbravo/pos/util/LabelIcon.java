//    uniCenta oPOS  - Touch Friendly Point Of Sale
//    Copyright (c) 2009-2017 uniCenta
//    https://unicenta.com
//
//    This file is part of uniCenta oPOS
//
//    uniCenta oPOS is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    (at your option) any later version.
//
//   uniCenta oPOS is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with uniCenta oPOS.  If not, see <http://www.gnu.org/licenses/>.

package com.openbravo.pos.util;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.swing.Icon;
import javax.swing.JLabel;

/**
 *
 * @author adrianromero
 */
public class LabelIcon extends JLabel implements Icon {
    
    private int iconwidth;
    private int iconheight;
    
    /**
     *
     * @param width
     * @param height
     */
    public LabelIcon(int width, int height) {
        iconwidth = width;
        iconheight = height;
    }

    /**
     *
     * @param mywidth
     * @param myheight
     * @return
     */
    public BufferedImage getImage(int mywidth, int myheight) {
        
        BufferedImage imgtext = new BufferedImage(mywidth, myheight,  BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = imgtext.createGraphics();
        
        setBounds(0, 0, mywidth, myheight);          
        paint(g2d);
        g2d.dispose();
        
        return imgtext;         
    }

    public void paintIcon(Component c, Graphics g, int x, int y) {
        
        setBounds(0, 0, iconwidth, iconheight);
        g.translate(x, y);
        paint(g);
        g.translate(-x, -y);
    }

    public int getIconWidth() {
        return iconwidth;
    }

    public int getIconHeight() {
        return iconheight;
    }

}
