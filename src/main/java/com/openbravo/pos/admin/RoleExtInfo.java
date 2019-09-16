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

package com.openbravo.pos.admin;

import com.openbravo.basic.BasicException;
import com.openbravo.data.loader.DataRead;

/**
 *
 * @author adrianromero
 * Created on 27 de febrero de 2007, 23:46
 *
 */
public class RoleExtInfo extends RoleInfo {
    
    /**
     *
     */
    protected byte[] m_aPermissions;
    
    /** Creates a new instance of RoleExtInfo */
    public RoleExtInfo() {
        super();
        m_aPermissions = null;
    }
    
    /**
     *
     * @param dr
     * @throws BasicException
     */
    @Override
    public void readValues(DataRead dr) throws BasicException {
        m_sName = dr.getString(1);
        m_aPermissions = dr.getBytes(2);
    }   
    
    //  implements Vectorer, ComparatorCreator

    /**
     *
     * @return
     */
        public static String[] getHeaders() {
        return new String[] {"Name"};
    }
    
    /**
     *
     * @return
     */
    public String[] toStringArray() {
        return new String[] {m_sName};
    } 

    /**
     *
     * @return
     */
    public Comparable[] toComparableArray() {
        return new Comparable[] {m_sName};
    }
}
