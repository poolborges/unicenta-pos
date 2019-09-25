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

import javax.swing.Icon;

/**
 *
 * @author  adrian
 */
public class NullIcon implements Icon {
    
    private int m_iWidth;
    private int m_iHeight;
    
    /** Creates a new instance of NullIcon
     * @param width
     * @param height */
    public NullIcon(int width, int height) {
        m_iWidth = width;
        m_iHeight = height;
    }
    
    @Override
    public int getIconHeight() {
        return m_iHeight;
    }
    
    @Override
    public int getIconWidth() {
        return m_iWidth;
    }
    
    @Override
    public void paintIcon(java.awt.Component c, java.awt.Graphics g, int x, int y) {
    }
    
}
