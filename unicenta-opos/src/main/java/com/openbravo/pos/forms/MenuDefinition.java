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

package com.openbravo.pos.forms;

import java.util.ArrayList;
import javax.swing.Action;

/**
 *
 * @author adrianromero
 */
public class MenuDefinition {
    
    private final String m_sKey;
    
    private final ArrayList m_aMenuElements;
    
    /** Creates a new instance of MenuDefinition
     * @param skey */
    public MenuDefinition(String skey) {
        m_sKey = skey;
        m_aMenuElements = new ArrayList();
    }
    
    /**
     *
     * @return
     */
    public String getKey() {
        return m_sKey;
    }
    
    /**
     *
     * @return
     */
    public String getTitle() {
        return AppLocal.getIntString(m_sKey);
    }

    /**
     *
     * @param act
     */
    public void addMenuItem(Action act) {
        MenuItemDefinition menuitem = new MenuItemDefinition(act);
        m_aMenuElements.add(menuitem);
    }
    
    /**
     *
     * @param keytext
     */
    public void addMenuTitle(String keytext) {
        MenuTitleDefinition menutitle = new MenuTitleDefinition();
        menutitle.KeyText = keytext;
        m_aMenuElements.add(menutitle);
    }
    
    /**
     *
     * @param i
     * @return
     */
    public MenuElement getMenuElement(int i) {
        return (MenuElement) m_aMenuElements.get(i);
    }

    /**
     *
     * @return
     */
    public int countMenuElements() {
        return m_aMenuElements.size();
    }

}
