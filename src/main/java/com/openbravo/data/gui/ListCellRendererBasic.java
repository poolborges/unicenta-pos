//    uniCenta oPOS  - Touch Friendly Point Of Sale
//    Copyright (c)  uniCenta & previous Openbravo POS works
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

package com.openbravo.data.gui;

import com.openbravo.data.loader.IRenderString;
import java.awt.*;
import javax.swing.*;

/**
 *
 * @author JG uniCenta
 */
public class ListCellRendererBasic extends DefaultListCellRenderer {
    
    private IRenderString m_renderer;
    
    /** Creates a new instance of ListCellRendererBasic
     * @param renderer */
    public ListCellRendererBasic(IRenderString renderer) {
        m_renderer = renderer;
    }

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        super.getListCellRendererComponent(list, null, index, isSelected, cellHasFocus);
        
        String s = m_renderer.getRenderString(value);
        setText((s == null || s.equals("")) ? " " : s); // Un espacio en caso de nulo para que no deja la celda chiquitita.
        return this;
    }        
 
}
