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

import com.openbravo.format.Formats;
import com.openbravo.basic.BasicException;

/**
 *
 * @author JG uniCenta
 */
public class VectorerBasic implements Vectorer {
    
    private int[] m_aiIndex;
    private String[] m_asHeaders;
    private Formats[] m_aFormats;
    
    /**
     *
     * @param asHeaders
     * @param aFormats
     * @param aiIndex
     */
    public VectorerBasic(String[] asHeaders, Formats[] aFormats, int[] aiIndex) {
        m_asHeaders = asHeaders;
        m_aFormats = aFormats;
        m_aiIndex = aiIndex;
    }
      
    /**
     *
     * @return
     * @throws BasicException
     */
    public String[] getHeaders() throws BasicException {
        
        String[] asvalues = new String[m_aiIndex.length];
        for (int i = 0; i < m_aiIndex.length; i++) {
            asvalues[i] = m_asHeaders[m_aiIndex[i]];
        }
        
        return asvalues;
    }
    
    /**
     *
     * @param obj
     * @return
     * @throws BasicException
     */
    public String[] getValues(Object obj) throws BasicException {
        Object[] avalues = (Object[]) obj;
        String[] asvalues = new String[m_aiIndex.length];
        for (int i = 0; i < m_aiIndex.length; i++) {
            asvalues[i] = m_aFormats[m_aiIndex[i]].formatValue(avalues[m_aiIndex[i]]);
        }
        
        return asvalues;
    }    
}
