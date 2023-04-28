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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author JG uniCenta
 */
public class SerializerWriteComposed implements SerializerWrite<Object[]> {

    private final static Logger LOGGER = Logger.getLogger(SerializerWriteComposed.class.getName());
    private List<SerializerWrite> serwrites = new ArrayList<SerializerWrite>();

    public SerializerWriteComposed() {}

    public void add(SerializerWrite sw) {
        serwrites.add(sw);
    }

    public void writeValues(DataWrite dp, Object[] parameters) throws BasicException {

        int posi = 0;
        try {
            DataWriteComposed dpc = new DataWriteComposed(dp);
            for (SerializerWrite sw : serwrites) {
                dpc.next();
                sw.writeValues(dpc, parameters[posi++]);
            }

        } catch (Exception ex) {
            LOGGER.log(Level.WARNING, "Exception while set value on posi: " + posi,ex);
            throw new BasicException(ex);
        }
    }

    private static class DataWriteComposed implements DataWrite {

        private DataWrite dp;
        private int offset = 0;
        private int max = 0;

        public DataWriteComposed(DataWrite dp) {
            this.dp = dp;
        }

        public void next() {
            offset = max;
        }

        @Override
        public void setInt(int paramIndex, Integer iValue) throws BasicException {
            dp.setInt(offset + paramIndex, iValue);
            max = Math.max(max, offset + paramIndex);
        }

        @Override
        public void setString(int paramIndex, String sValue) throws BasicException {
            dp.setString(offset + paramIndex, sValue);
            max = Math.max(max, offset + paramIndex);
        }

        @Override
        public void setDouble(int paramIndex, Double dValue) throws BasicException {
            dp.setDouble(offset + paramIndex, dValue);
            max = Math.max(max, offset + paramIndex);
        }

        @Override
        public void setBoolean(int paramIndex, Boolean bValue) throws BasicException {
            dp.setBoolean(offset + paramIndex, bValue);
            max = Math.max(max, offset + paramIndex);
        }

        @Override
        public void setTimestamp(int paramIndex, Date dValue) throws BasicException {
            dp.setTimestamp(offset + paramIndex, dValue);
            max = Math.max(max, offset + paramIndex);
        }

        @Override
        public void setBytes(int paramIndex, byte[] value) throws BasicException {
            dp.setBytes(offset + paramIndex, value);
            max = Math.max(max, offset + paramIndex);
        }

        @Override
        public void setObject(int paramIndex, Object value) throws BasicException {
            dp.setObject(offset + paramIndex, value);
            max = Math.max(max, offset + paramIndex);
        }
    }

}
