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
import java.util.Date;

/**
 *
 * @author adrianromero
 */
public abstract class DataParams implements DataWrite {

    protected DataWrite datawrite;

    public abstract void writeValues() throws BasicException;

    @Override
    public void setInt(int paramIndex, Integer iValue) throws BasicException {
        datawrite.setInt(paramIndex, iValue);
    }

    @Override
    public void setString(int paramIndex, String sValue) throws BasicException {
        datawrite.setString(paramIndex, sValue);
    }

    @Override
    public void setDouble(int paramIndex, Double dValue) throws BasicException {
        datawrite.setDouble(paramIndex, dValue);
    }

    @Override
    public void setBoolean(int paramIndex, Boolean bValue) throws BasicException {
        datawrite.setBoolean(paramIndex, bValue);
    }

    @Override
    public void setTimestamp(int paramIndex, Date dValue) throws BasicException {
        datawrite.setTimestamp(paramIndex, dValue);
    }

    @Override
    public void setBytes(int paramIndex, byte[] value) throws BasicException {
        datawrite.setBytes(paramIndex, value);
    }

    @Override
    public void setObject(int paramIndex, Object value) throws BasicException {
        datawrite.setObject(paramIndex, value);
    }

    public DataWrite getDataWrite() {
        return datawrite;
    }

    public void setDataWrite(DataWrite dw) {
        this.datawrite = dw;
    }
}
