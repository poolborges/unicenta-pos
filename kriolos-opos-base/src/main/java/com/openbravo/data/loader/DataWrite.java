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

/**
 *
 * @author  adrian
 */
public interface DataWrite {

    public void setInt(int paramIndex, Integer iValue) throws BasicException;

    public void setString(int paramIndex, String sValue) throws BasicException;

    public void setDouble(int paramIndex, Double dValue) throws BasicException;

    public void setBoolean(int paramIndex, Boolean bValue) throws BasicException;

    public void setTimestamp(int paramIndex, java.util.Date dValue) throws BasicException;
    
    public void setBytes(int paramIndex, byte[] value) throws BasicException;

    public void setObject(int paramIndex, Object value) throws BasicException;
}
