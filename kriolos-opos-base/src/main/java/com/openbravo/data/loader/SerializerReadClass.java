//    KriolOS POS
//    Copyright (c) 2019-2023 KriolOS
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
public class SerializerReadClass<T extends SerializableRead> implements SerializerRead<SerializableRead> {

    private static final Logger LOGGER = Logger.getLogger(SerializerReadClass.class.getName());
    private final Class<T> m_clazz;

    public SerializerReadClass(Class<T> clazz) {
        m_clazz = clazz;
    }

    @Override
    public T readValues(DataRead dr) throws BasicException {
        T sr = null;
        try {
            sr = m_clazz.getDeclaredConstructor().newInstance();
            sr.readValues(dr);
        } catch (java.lang.InstantiationException | IllegalAccessException | ClassCastException | NoSuchMethodException | SecurityException | IllegalArgumentException | InvocationTargetException ex) {
            LOGGER.log(Level.WARNING, "Exception on casting or read value ", ex);
        }
        
        return sr;
    }
}
