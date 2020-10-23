//    KrOS POS
//    Copyright (c) 2009-2017 uniCenta
//    
//
//     
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
//    along with KrOS POS.  If not, see <http://www.gnu.org/licenses/>.

package com.openbravo.pos.ticket;

import java.util.*;

/**
 *
 * @author JG uniCenta
 */
public class Signumprovider {
    
    private Set m_positives = new HashSet();
    private Set m_negatives = new HashSet();
    
    /** Creates a new instance of Signumprovider */
    public Signumprovider() {
    }
    
    /**
     *
     * @param key
     */
    public void addPositive(Object key) {
        m_positives.add(key);
    }
    
    /**
     *
     * @param key
     */
    public void addNegative(Object key) {
        m_negatives.add(key);
    }
    
    /**
     *
     * @param key
     * @param value
     * @return
     */
    public Double correctSignum(Object key, Double value) {
        if (m_positives.contains(key)) {
            return value < 0.0 ? -value : value;
        } else if (m_negatives.contains(key)) {
            return value > 0.0 ? -value : value;
        } else {
            return value;
        }        
    }    
}
