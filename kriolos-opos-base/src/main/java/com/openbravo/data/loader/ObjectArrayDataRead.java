/*
 * Copyright (C) 2022 Paulo Borges
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.openbravo.data.loader;

import com.openbravo.basic.BasicException;
import java.util.Date;

/**
 *
 * @author bellah
 */
public class ObjectArrayDataRead implements DataRead{
    
    private Object[] content;
    public ObjectArrayDataRead(Object[] con){
        content = con;
    }
    
    private void ensureSize(int columnIndex)throws BasicException {
        if (columnIndex > content.length){
        
              throw new BasicException("Index is > than Object");
        }
    }
    
    private <E> E get(int columnIndex) throws BasicException {
        ensureSize(columnIndex);
        return (E)(content[columnIndex]);
    }

    @Override
    public Integer getInt(int columnIndex) throws BasicException {
        return get(columnIndex);
    }

    @Override
    public String getString(int columnIndex) throws BasicException {
        return get(columnIndex);
    }

    @Override
    public Double getDouble(int columnIndex) throws BasicException {
        return get(columnIndex);
    }

    @Override
    public Boolean getBoolean(int columnIndex) throws BasicException {
        return get(columnIndex);
    }

    @Override
    public Date getTimestamp(int columnIndex) throws BasicException {
        return get(columnIndex);
    }

    @Override
    public byte[] getBytes(int columnIndex) throws BasicException {
        return get(columnIndex);
    }

    @Override
    public Object getObject(int columnIndex) throws BasicException {
        return get(columnIndex);
    }

    @Override
    public DataField[] getDataField() throws BasicException {
        DataField[] df = new DataField[content.length];
        return df;
    }
    
}
