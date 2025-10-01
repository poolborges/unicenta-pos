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

package com.openbravo.pos.menu;

import com.openbravo.pos.forms.AppLocal;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Action;

/**
 *
 * @author adrianromero
 */
public class MenuDefinition{
    
    private final String key;
    private final List<MenuElement> menuElements;
    

    public MenuDefinition(String skey) {
        key = skey;
        menuElements = new ArrayList<>();
    }

    public String getKey() {
        return key;
    }

    public String getTitle() {
        return AppLocal.getIntString(key);
    }

    public void addMenuItem(Action act) {
        MenuItemDefinition menuitem = new MenuItemDefinition(act);
        menuElements.add(menuitem);
    }

    public void addMenuTitle(String titleKey) {
        MenuTitleDefinition menutitle = new MenuTitleDefinition();
        menutitle.setKeyText(titleKey);
        menuElements.add(menutitle);
    }

    public MenuElement getMenuElement(int i) {
        return menuElements.get(i);
    }

    public int countMenuElements() {
        return menuElements.size();
    }

}
