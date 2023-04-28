//    KriolOS POS
//    Copyright (c) 2019-2023 KriolOS
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
//    along with this program.  If not, see <http://www.gnu.org/licenses/>.

package com.openbravo.pos.ticket;

import com.openbravo.format.Formats;
import com.openbravo.pos.util.ThumbNailBuilder;
import java.awt.*;
import javax.swing.*;

/**
 *
 * @author uniCenta 2017
 *
 */
public class ProductRenderer extends DefaultListCellRenderer {
                
    private static final int PROD_DEFAULT_WIDTH = 64;
    private static final int PROD_DEFAULT_HEIGHT = 54;
    
    private final static String DEFAULT_IMAGE = "com/openbravo/images/null.png";
    private final static ThumbNailBuilder THUMB_NAIL = new ThumbNailBuilder(PROD_DEFAULT_WIDTH,PROD_DEFAULT_HEIGHT, DEFAULT_IMAGE);
    private static final long serialVersionUID = 1L;

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        super.getListCellRendererComponent(list, null, index, isSelected, cellHasFocus);
        
        ProductInfoExt prod = (ProductInfoExt) value;
       
        if (prod != null) {
            
            String prodLabe = "<html>" 
                    + prod.getReference()  
                    +  "<br>" + prod.getName() 
                    +  "<br>" + Formats.CURRENCY.formatValue(prod.getPriceSell())
                    + "</html>";
            
            String toolTip = ""+ prod.getReference()  
                    +  "|" + prod.getName() 
                    +  " = " + Formats.CURRENCY.formatValue(prod.getPriceSell());
            
            setText(prodLabe);
            setToolTipText(toolTip);
            Image img;
            
            if(prod.getImage() != null) {
                img = THUMB_NAIL.getThumbNail(prod.getImage());
            }else{
                img = THUMB_NAIL.getThumbNail();
            }
            setIcon(img == null ? null :new ImageIcon(img));
        }
        return this;
    }      
}
