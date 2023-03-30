//    KrOS POS  - Open Source Point Of Sale
//    Copyright (c) 2009-2018 uniCenta & previous Openbravo POS works
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

package com.openbravo.pos.catalog;

import com.openbravo.basic.BasicException;
import java.awt.Component;
import java.awt.event.ActionListener;

/**
 *
 * @author adrianromero
 */
public interface CatalogSelector {
    
    /**
     *
     * @throws BasicException
     */
    public void loadCatalog() throws BasicException;

    /**
     *
     * @param id
     */
    public void showCatalogPanel(String id);

    /**
     *
     * @param value
     */
    public void setComponentEnabled(boolean value);

    /**
     *
     * @return
     */
    public Component getComponent();
    
    /**
     *
     * @param l
     */
    public void addActionListener(ActionListener l);  

    /**
     *
     * @param l
     */
    public void removeActionListener(ActionListener l);    
}
