//    KriolOS POS
//    Copyright (c) 2019-2023 KriolOS
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
//    along with this program.  If not, see <http://www.gnu.org/licenses/>.

package com.openbravo.pos.sales.restaurant;

import com.openbravo.basic.BasicException;
import com.openbravo.data.loader.DataRead;
import com.openbravo.data.loader.ImageUtils;
import com.openbravo.data.loader.SerializableRead;
import com.openbravo.pos.resources.ImageResources;
import com.openbravo.pos.util.ThumbNailBuilder;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

/**
 *
 * @author JG uniCenta
 */
public class Floor implements SerializableRead {
    
    private static final long serialVersionUID = 8694154682897L;
    private String id;
    private String name;
    private Container container;
    private Icon icon;

    @Override
    public void readValues(DataRead dr) throws BasicException {
        id = dr.getString(1);
        name = dr.getString(2);
        BufferedImage img = ImageUtils.readImage(dr.getBytes(3));
        container = new DrawingJPanel(img);

        if(img == null){
            icon = ImageResources.ICON_FLOORS.getIcon();
        }else{
            ThumbNailBuilder tnbcat = new ThumbNailBuilder(32, 32, img);
            icon = new ImageIcon(tnbcat.getThumbNail());
        }
    }

    public String getID() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Icon getIcon() {
        return icon;
    }    

    public Container getContainer() {
        return container;
    }    
    
    private static class DrawingJPanel extends JPanel {

        private static final long serialVersionUID = 1L;
        
        private final Image img;
        
        public DrawingJPanel(Image img) {
            this.img = img;
            initComponents();
        }
        
        private void initComponents(){
            setLayout(null);
        }
        
        @Override
        protected void paintComponent (Graphics g) { 
            super.paintComponent(g);
            if (img != null) {
                g.drawImage(img, 0, 0, this);
            }
        }
        
        @Override
        public Dimension getPreferredSize() {
            return (img == null) 
                ? new Dimension(950, 560)                     
                : new Dimension(img.getWidth(this), img.getHeight(this));
        }
        @Override
        public Dimension getMinimumSize() {
            return getPreferredSize();
        }
        @Override
        public Dimension getMaximumSize() {
            return getPreferredSize();
        }
    }    
}
