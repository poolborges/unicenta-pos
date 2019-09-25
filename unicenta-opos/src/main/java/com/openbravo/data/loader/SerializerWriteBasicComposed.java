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

package com.openbravo.data.loader;

import com.openbravo.basic.BasicException;

/**
 *
 * @author JG uniCenta
 */
public class SerializerWriteBasicComposed implements SerializerWrite<Object[]> {
    
    private Datas[][] m_classes;    
    
    /** Creates a new instance of SerializerWriteComposed
     * @param classes */
    public SerializerWriteBasicComposed(Datas[]... classes) {
         m_classes = classes;
    }
    
    /**
     *
     * @param dp
     * @param obj
     * @throws BasicException
     */
    public void writeValues(DataWrite dp, Object[] obj) throws BasicException {

        int index = 0;
        for (int i = 0; i < m_classes.length; i++) {
            Object[] val = (Object[]) obj[i];
            for (int j = 0; j < m_classes[i].length; j++) {
                index++;
                m_classes[i][j].setValue(dp, index, val[j]);
            }
        }
    }  
}
