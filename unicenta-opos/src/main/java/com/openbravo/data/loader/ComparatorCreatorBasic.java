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

import java.util.Comparator;

/**
 *
 * @author JG uniCenta
 */
public class ComparatorCreatorBasic implements ComparatorCreator {
    
    private String[] m_sHeaders;
    private Datas[] m_aDatas;
    private int[] m_iAvailableIndexes;
    
    /** Creates a new instance of ComparatorCreatorBasic
     * @param sHeaders
     * @param aDatas
     * @param iAvailableIndexes */
    public ComparatorCreatorBasic(String[] sHeaders, Datas[] aDatas, int[] iAvailableIndexes) {
        
        m_sHeaders = sHeaders;
        m_aDatas = aDatas;
        m_iAvailableIndexes = iAvailableIndexes;
    }

    /**
     *
     * @param sHeaders
     * @param aDatas
     */
    public ComparatorCreatorBasic(String[] sHeaders, Datas[] aDatas) {
        m_sHeaders = sHeaders;        
        m_aDatas = aDatas;
        m_iAvailableIndexes = new int[aDatas.length];
        for (int i = 0; i < aDatas.length; i++) {
            m_iAvailableIndexes[i] = i;
        }
    }
    
    /**
     *
     * @return
     */
    public String[] getHeaders() {
        
        String[] sTempHeaders = new String[m_iAvailableIndexes.length];
        
        for (int i = 0; i < m_iAvailableIndexes.length; i++) {
            sTempHeaders[i] = m_sHeaders[m_iAvailableIndexes[i]];
        }         
        return sTempHeaders;
    }
    
    /**
     *
     * @param aiOrderBy
     * @return
     */
    public Comparator createComparator(int[] aiOrderBy) {
        return new ComparatorBasic(aiOrderBy);
    }
    
    /**
     *
     */
    public class ComparatorBasic implements Comparator {

        private int[] m_aiOrderBy;

        /** Creates a new instance of ComparatorBasic
         * @param aiOrderBy */
        public ComparatorBasic(int[] aiOrderBy) {
            m_aiOrderBy = aiOrderBy;
        }
        public int compare(Object o1, Object o2) {
            if (o1 == null) {
                if (o2 == null) {
                    return 0;
                } else {
                    return -1;
                }
            } else if (o2 == null) {
                return +1;
            } else {
                // ninguno de los dos es nulo...
                Object[] ao1 = (Object[]) o1;
                Object[] ao2 = (Object[]) o2;
                for (int i = 0; i < m_aiOrderBy.length; i++) {
                    int result = m_aDatas[m_iAvailableIndexes[m_aiOrderBy[i]]].compare(ao1[m_iAvailableIndexes[m_aiOrderBy[i]]], ao2[m_iAvailableIndexes[m_aiOrderBy[i]]]);
                    if (result != 0) {
                        return result;
                    }
                }
                return 0;
            }
        }
    }    
}
