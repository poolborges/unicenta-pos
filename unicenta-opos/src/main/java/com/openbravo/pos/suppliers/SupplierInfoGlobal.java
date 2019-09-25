//    uniCenta oPOS  - Touch Friendly Point Of Sale
//    Copyright (c) 2009-2018 uniCenta
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
//

//    For BrowseEditableData

package com.openbravo.pos.suppliers;

import com.openbravo.data.user.BrowsableEditableData;

/**
 *
 * @author Jack Gerrard
 */
public class SupplierInfoGlobal {

    private static SupplierInfoGlobal INSTANCE;
    private SupplierInfoExt supplierInfoExt;
    private BrowsableEditableData editableData;

    private SupplierInfoGlobal() {
    }

    /**
     *
     * @return
     */
        public static SupplierInfoGlobal getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new SupplierInfoGlobal();
        }

        return INSTANCE;
    }

    /**
     *
     * @return
     */
    public SupplierInfoExt getSupplierInfoExt() {
        return supplierInfoExt;
    }

    /**
     *
     * @param supplierInfoExt
     */
    public void setSupplierInfoExt(SupplierInfoExt supplierInfoExt) {
        this.supplierInfoExt = supplierInfoExt;
    }

    /**
     *
     * @return
     */
    public BrowsableEditableData getEditableData() {
        return editableData;
}

    /**
     *
     * @param editableData
     */
    public void setEditableData(BrowsableEditableData editableData) {
        this.editableData = editableData;
    }
}
