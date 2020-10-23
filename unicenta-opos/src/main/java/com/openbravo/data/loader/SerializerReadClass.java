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

package com.openbravo.data.loader;

import com.openbravo.basic.BasicException;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author JG uniCenta
 */
public class SerializerReadClass implements SerializerRead {

    private static final Logger LOGGER = Logger.getLogger(SerializerReadClass.class.getName());
    private final Class m_clazz;
    
    /** Creates a new instance of DefaultSerializerRead
     * @param clazz */
    public SerializerReadClass(Class clazz) {
        m_clazz = clazz;
    }
    
    /**
     *
     * @param dr
     * @return
     * @throws BasicException
     */
    @Override
    public Object readValues(DataRead dr) throws BasicException {
        SerializableRead sr = null;
        try {
            sr = (SerializableRead) m_clazz.getDeclaredConstructor().newInstance();
            sr.readValues(dr);
// JG 16 May 12 use multicatch
        } catch (java.lang.InstantiationException | IllegalAccessException | ClassCastException | NoSuchMethodException | SecurityException | IllegalArgumentException | InvocationTargetException ex) {
            LOGGER.log(Level.WARNING, "Cannot found readvalues ", ex);
        }
        
        return sr;
    }
}
