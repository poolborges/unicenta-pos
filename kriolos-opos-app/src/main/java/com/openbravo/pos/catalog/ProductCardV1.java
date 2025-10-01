/*
 * Copyright (C) 2025 Paulo Borges
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
package com.openbravo.pos.catalog;

import java.awt.Insets;
import java.awt.event.ActionListener;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.SwingConstants;

/**
 *
 * @author poolborges
 */
public class ProductCardV1 extends JButton{
   
    private static final int CATALOG_BUTTON_WITH = 96;
    private static final int CATALOG_BUTTON_HEIGHT = 82;
    
    public ProductCardV1(CatalogItem catalogItem, ActionListener actionListener){
        this();
        if(catalogItem.getImage() != null){
            this.setIcon(new ImageIcon(catalogItem.getImage()));
        }

        if (catalogItem.getTextTip() != null) {
            this.setToolTipText(catalogItem.getTextTip());
        }
        
        if(catalogItem.getText() != null){
            this.setText(catalogItem.getText());
        }
        
        this.addActionListener(actionListener);
    }

    
    
    private ProductCardV1(){
        super();
        this.setFocusPainted(false);
        this.setFocusable(false);
        this.setRequestFocusEnabled(false);
        this.setHorizontalTextPosition(SwingConstants.CENTER);
        this.setVerticalTextPosition(SwingConstants.CENTER);
        this.setFont(new java.awt.Font("Arial Bold", 0, 12));
        this.setMargin(new Insets(2, 2, 2, 2));
        this.setMaximumSize(new java.awt.Dimension(CATALOG_BUTTON_WITH, CATALOG_BUTTON_HEIGHT));
        this.setMinimumSize(new java.awt.Dimension(CATALOG_BUTTON_WITH, CATALOG_BUTTON_HEIGHT));
        this.setPreferredSize(new java.awt.Dimension(CATALOG_BUTTON_WITH, CATALOG_BUTTON_HEIGHT));
        this.applyComponentOrientation(getComponentOrientation());
    }
    
}
