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


package com.openbravo.data.loader;

/**
 *
 * @author  adrian
 */
public class KeyGetterBasic implements IKeyGetter {
    
    private int [] m_aElems;
    
    /** Creates a new instance of KeyGetterBasic
     * @param aElems */
    public KeyGetterBasic(int[] aElems) {
        m_aElems = aElems;
    }
    
    /**
     *
     * @param value
     * @return
     */
    public Object getKey(Object value) {
        if (value == null) {
            return null;
        } else {
            Object[] avalue = (Object []) value;
            Object[] akey = new Object[m_aElems.length];
            for (int i = 0; i < m_aElems.length; i++) {
                akey[i] = avalue[m_aElems[i]];
            }
            return akey;
        }
    }   
}
