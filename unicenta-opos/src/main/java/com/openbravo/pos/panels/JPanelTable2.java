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

package com.openbravo.pos.panels;

import com.openbravo.data.loader.ComparatorCreator;
import com.openbravo.data.loader.Vectorer;
import com.openbravo.data.model.Row;
import com.openbravo.data.user.ListProvider;
import com.openbravo.data.user.SaveProvider;
import javax.swing.ListCellRenderer;

/**
 *
 * @author adrianromero
 */
public abstract class JPanelTable2 extends JPanelTable {
   
    /**
     *
     */
    protected Row row;

    /**
     *
     */
    protected ListProvider lpr;

    /**
     *
     */
    protected SaveProvider spr;

    /**
     *
     * @return
     */
    @Override
    public final ListProvider getListProvider() {
        return lpr;
    }

    /**
     *
     * @return
     */
    @Override
    public final SaveProvider getSaveProvider() {
        return spr;
    }
    
    /**
     *
     * @return
     */
    @Override
    public final Vectorer getVectorer() {
        return row.getVectorer();
    }
    
    /**
     *
     * @return
     */
    @Override
    public final ComparatorCreator getComparatorCreator() {
        return row.getComparatorCreator();
    }
    
    /**
     *
     * @return
     */
    @Override
    public final ListCellRenderer getListCellRenderer() {
        return row.getListCellRenderer();
    } 
}
