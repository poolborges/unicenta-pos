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
//    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-130

package com.openbravo.pos.panels;

import java.util.Enumeration;
import java.util.Iterator;

/**
 *
 * @author adrianromero
 */
public class EnumerationIter implements Enumeration {
    
    private Iterator i;

    /**
     *
     * @param i
     */
    public EnumerationIter(Iterator i) {
        this.i = i;
    }
    @Override
    public boolean hasMoreElements() {
        return i.hasNext();
    }
    @Override
    public Object nextElement() {
        return i.next();
    } 
}
