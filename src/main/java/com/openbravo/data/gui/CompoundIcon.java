//    uniCenta oPOS  - Touch Friendly Point Of Sale
//    Copyright (c) 2009-2018 uniCenta & previous Openbravo POS works
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

import javax.swing.Icon;

/**
 *
 * @author  adrian
 */
public class CompoundIcon implements Icon {
    
    private Icon m_icon1;
    private Icon m_icon2;
    
    /** Creates a new instance of CompoundIcon
     * @param icon1
     * @param icon2 */
    public CompoundIcon(Icon icon1, Icon icon2) {
        m_icon1 = icon1;
        m_icon2 = icon2;
    }
    
    @Override
    public int getIconHeight() {
        return Math.max(m_icon1.getIconHeight(), m_icon2.getIconHeight());
    }
    
    @Override
    public int getIconWidth() {
        return m_icon1.getIconWidth() + m_icon2.getIconWidth();
    }
    
    @Override
    public void paintIcon(java.awt.Component c, java.awt.Graphics g, int x, int y) {
        m_icon1.paintIcon(c, g, x, y);
        m_icon2.paintIcon(c, g, x + m_icon1.getIconWidth(), y);
    }
    
}
