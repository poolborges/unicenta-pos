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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author JG uniCenta
 */
public class SerializerWriteBuilder implements SerializerWrite<SerializableWrite> {

    private final static Logger LOGGER = Logger.getLogger(SerializerWriteBuilder.class.getName());
    public static final SerializerWrite INSTANCE = new SerializerWriteBuilder();

    private SerializerWriteBuilder() {}

    public void writeValues(DataWrite dp, SerializableWrite obj) throws BasicException {
        try {
            obj.writeValues(dp);
        } catch (Exception ex) {
            LOGGER.log(Level.WARNING, "Exception while writeValues",ex);
            throw new BasicException(ex);
        }
    }
}
