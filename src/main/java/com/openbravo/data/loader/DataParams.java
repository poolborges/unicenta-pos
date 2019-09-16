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

import com.openbravo.basic.BasicException;
import java.util.Date;

/**
 *
 * @author adrianromero
 */
public abstract class DataParams implements DataWrite {
    
    /**
     *
     */
    protected DataWrite dw;
    
    /**
     *
     * @throws BasicException
     */
    public abstract void writeValues() throws BasicException;

    /**
     *
     * @param paramIndex
     * @param iValue
     * @throws BasicException
     */
    public void setInt(int paramIndex, Integer iValue) throws BasicException {
        dw.setInt(paramIndex, iValue);
    }

    public void setString(int paramIndex, String sValue) throws BasicException {
        dw.setString(paramIndex, sValue);
    }

    /**
     *
     * @param paramIndex
     * @param dValue
     * @throws BasicException
     */
    public void setDouble(int paramIndex, Double dValue) throws BasicException {
        dw.setDouble(paramIndex, dValue);
    }

    /**
     *
     * @param paramIndex
     * @param bValue
     * @throws BasicException
     */
    public void setBoolean(int paramIndex, Boolean bValue) throws BasicException {
        dw.setBoolean(paramIndex, bValue);
    }

    /**
     *
     * @param paramIndex
     * @param dValue
     * @throws BasicException
     */
    public void setTimestamp(int paramIndex, Date dValue) throws BasicException {
        dw.setTimestamp(paramIndex, dValue);
    }

    /**
     *
     * @param paramIndex
     * @param value
     * @throws BasicException
     */
    public void setBytes(int paramIndex, byte[] value) throws BasicException {
        dw.setBytes(paramIndex, value);
    }

    /**
     *
     * @param paramIndex
     * @param value
     * @throws BasicException
     */
    public void setObject(int paramIndex, Object value) throws BasicException {
        dw.setObject(paramIndex, value);
    }

    /**
     *
     * @return
     */
    public DataWrite getDataWrite() {
        return dw;
    }

    /**
     *
     * @param dw
     */
    public void setDataWrite(DataWrite dw) {
        this.dw = dw;
    }
}
