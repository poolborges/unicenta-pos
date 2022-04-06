/*
 * Copyright (C) 2022 KriolOS
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
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author JG uniCenta
 */
public final class JDBCDataResultSet<T> implements DataResultSet<T> {
    private static final Logger LOGGER = Logger.getLogger(JDBCDataResultSet.class.getName());

    private final ResultSet m_rs;
    private final SerializerRead<T> m_serread;

    public JDBCDataResultSet(ResultSet rs, SerializerRead<T> serread) {
        m_rs = rs;
        m_serread = serread;
    }

    @Override
    public Integer getInt(int columnIndex) throws BasicException {
        try {
            int iValue = m_rs.getInt(columnIndex);
            return m_rs.wasNull() ? null : iValue;
        } catch (SQLException eSQL) {
            LOGGER.log(Level.SEVERE, "Exception on JDBCDataResultSet", eSQL);
            throw new BasicException(eSQL);
        }
    }

    @Override
    public String getString(int columnIndex) throws BasicException {
        try {
            return m_rs.getString(columnIndex);
        } catch (SQLException eSQL) {
            LOGGER.log(Level.SEVERE, "Exception on JDBCDataResultSet", eSQL);
            throw new BasicException(eSQL);
        }
    }

    @Override
    public Double getDouble(int columnIndex) throws BasicException {
        try {
            double dValue = m_rs.getDouble(columnIndex);
            return m_rs.wasNull() ? null : dValue;
        } catch (SQLException eSQL) {
            LOGGER.log(Level.SEVERE, "Exception on JDBCDataResultSet", eSQL);
            throw new BasicException(eSQL);
        }
    }

    @Override
    public Boolean getBoolean(int columnIndex) throws BasicException {
        try {
            boolean bValue = m_rs.getBoolean(columnIndex);
            return m_rs.wasNull() ? null : bValue;
        } catch (SQLException eSQL) {
            LOGGER.log(Level.SEVERE, "Exception on JDBCDataResultSet", eSQL);
            throw new BasicException(eSQL);
        }
    }

    @Override
    public java.util.Date getTimestamp(int columnIndex) throws BasicException {
        try {
            java.sql.Timestamp ts = m_rs.getTimestamp(columnIndex);
            return ts == null ? null : new java.util.Date(ts.getTime());
        } catch (SQLException eSQL) {
            LOGGER.log(Level.SEVERE, "Exception on JDBCDataResultSet", eSQL);
            throw new BasicException(eSQL);
        }
    }

    @Override
    public byte[] getBytes(int columnIndex) throws BasicException {
        try {
            return m_rs.getBytes(columnIndex);
        } catch (SQLException eSQL) {
            LOGGER.log(Level.SEVERE, "Exception on JDBCDataResultSet", eSQL);
            throw new BasicException(eSQL);
        }
    }

    @Override
    public Object getObject(int columnIndex) throws BasicException {
        try {
            return m_rs.getObject(columnIndex);
        } catch (SQLException eSQL) {
            LOGGER.log(Level.SEVERE, "Exception on JDBCDataResultSet", eSQL);
            throw new BasicException(eSQL);
        }
    }

    @Override
    public DataField[] getDataField() throws BasicException {
        try {
            ResultSetMetaData md = m_rs.getMetaData();
            DataField[] df = new DataField[md.getColumnCount()];
            for (int i = 0; i < df.length; i++) {
                df[i] = new DataField();
                df[i].Name = md.getColumnName(i + 1);
                df[i].Size = md.getColumnDisplaySize(i + 1);
                df[i].Type = md.getColumnType(i + 1);
            }
            return df;
        } catch (SQLException eSQL) {
            LOGGER.log(Level.SEVERE, "Exception on JDBCDataResultSet", eSQL);
            throw new BasicException(eSQL);
        }
    }

    @Override
    public T getCurrent() throws BasicException {
        return m_serread.readValues(this);
    }

    @Override
    public boolean next() throws BasicException {
        try {
            return m_rs.next();
        } catch (SQLException eSQL) {
            LOGGER.log(Level.SEVERE, "Exception on JDBCDataResultSet", eSQL);
            throw new BasicException(eSQL);
        }
    }

    @Override
    public void close() throws BasicException {
        try {
            m_rs.close();
        } catch (SQLException eSQL) {
            LOGGER.log(Level.SEVERE, "Exception on JDBCDataResultSet", eSQL);
            throw new BasicException(eSQL);
        }
    }

    @Override
    public int updateCount() throws BasicException {
        return -1; // es decir somos datos.
    }
}
