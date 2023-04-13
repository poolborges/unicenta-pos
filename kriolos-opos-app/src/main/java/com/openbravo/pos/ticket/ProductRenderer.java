//    KrOS POS
//    Copyright (c) 2009-2018 uniCenta
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
            ThumbNailBuilder tnbprod = null;
            if(prod.getImage() != null) {
                tnbprod = new ThumbNailBuilder(PROD_DEFAULT_WIDTH,PROD_DEFAULT_HEIGHT,prod.getImage());
            }else{
                tnbprod = new ThumbNailBuilder(PROD_DEFAULT_WIDTH,PROD_DEFAULT_HEIGHT, "com/openbravo/images/null.png");
            }
            Image img = tnbprod.getThumbNail();
            setIcon(img == null ? null :new ImageIcon(img));
        }
        return this;
    }      
}
