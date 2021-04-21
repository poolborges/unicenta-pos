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
import java.sql.*;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author adrianromero
 */
public class PreparedSentence<T> extends JDBCSentence<T> {

    private static final Logger LOGGER = Logger.getLogger(PreparedSentence.class.getName());

    private String m_sentence;
    protected SerializerWrite<Object> m_SerWrite;
    protected SerializerRead<T> m_SerRead;
    private PreparedStatement m_Stmt;

    public PreparedSentence(Session s, String sentence, SerializerWrite<Object> serwrite, SerializerRead<T> serread) {
        super(s);
        m_sentence = sentence;
        m_SerWrite = serwrite;
        m_SerRead = serread;
        m_Stmt = null;
    }

    /**
     *
     * @param s
     * @param sentence
     * @param serwrite
     */
    public PreparedSentence(Session s, String sentence, SerializerWrite<Object> serwrite) {
        this(s, sentence, serwrite, null);
    }

    /**
     *
     * @param s
     * @param sentence
     */
    public PreparedSentence(Session s, String sentence) {
        this(s, sentence, null, null);
    }

    private static final class PreparedSentencePars implements DataWrite {

        private PreparedStatement m_ps;

        PreparedSentencePars(PreparedStatement ps) {
            m_ps = ps;
        }

        @Override
        public void setInt(int paramIndex, Integer iValue) throws BasicException {
            try {
                m_ps.setObject(paramIndex, iValue, Types.INTEGER);
            } catch (SQLException eSQL) {
                LOGGER.log(Level.SEVERE, "Exception on PreparedSentence", eSQL);
                throw new BasicException(eSQL);
            }
        }

        @Override
        public void setString(int paramIndex, String sValue) throws BasicException {
            try {
                m_ps.setString(paramIndex, sValue);
            } catch (SQLException eSQL) {
                LOGGER.log(Level.SEVERE, "Exception on PreparedSentence", eSQL);
                throw new BasicException(eSQL);
            }
        }

        @Override
        public void setDouble(int paramIndex, Double dValue) throws BasicException {
            try {
                m_ps.setObject(paramIndex, dValue, Types.DOUBLE);
            } catch (SQLException eSQL) {
                LOGGER.log(Level.SEVERE, "Exception on PreparedSentence", eSQL);
                throw new BasicException(eSQL);
            }
        }

        @Override
        public void setBoolean(int paramIndex, Boolean bValue) throws BasicException {
            try {
                if (bValue == null) {
                    m_ps.setObject(paramIndex, null);
                } else {
                    m_ps.setBoolean(paramIndex, bValue);
                }
            } catch (SQLException eSQL) {
                LOGGER.log(Level.SEVERE, "Exception on PreparedSentence", eSQL);
                throw new BasicException(eSQL);
            }
        }

        @Override
        public void setTimestamp(int paramIndex, java.util.Date dValue) throws BasicException {
            try {
                m_ps.setObject(paramIndex, dValue == null ? null : new Timestamp(dValue.getTime()), Types.TIMESTAMP);
            } catch (SQLException eSQL) {
                LOGGER.log(Level.SEVERE, "Exception on PreparedSentence", eSQL);
                throw new BasicException(eSQL);
            }
        }

        @Override
        public void setBytes(int paramIndex, byte[] value) throws BasicException {
            try {
                m_ps.setBytes(paramIndex, value);
            } catch (SQLException eSQL) {
                LOGGER.log(Level.SEVERE, "Exception on PreparedSentence", eSQL);
                throw new BasicException(eSQL);
            }
        }

        @Override
        public void setObject(int paramIndex, Object value) throws BasicException {
            try {
                m_ps.setObject(paramIndex, value);
            } catch (SQLException eSQL) {
                LOGGER.log(Level.SEVERE, "Exception on PreparedSentence", eSQL);
                throw new BasicException(eSQL);
            }
        }
    }

    /**
     *
     * @param params
     * @return
     * @throws BasicException
     */
    @Override
    public DataResultSet<T> openExec(Object params) throws BasicException {
        closeExec();

        DataResultSet<T> result;

        try {

            LOGGER.log(Level.INFO, "Prepared statement show SQL: " + m_sentence);

            if (params != null && LOGGER.isLoggable(Level.FINER)) {

                Object[] objectsArray;

                if (params instanceof Collection) {
                    Collection objectsCollection = (Collection) params;

                    objectsArray = objectsCollection.toArray();

                } else if (params instanceof Object[]) {
                    objectsArray = (Object[]) params;
                } else {
                    objectsArray = new Object[]{params};
                }

                StringBuilder sb = new StringBuilder();
                if (objectsArray != null) {
                    for (int count = 0; count < objectsArray.length; count++) {
                        sb.append("{pos: ");
                        sb.append(count);
                        sb.append(", val: ");
                        sb.append((objectsArray[count] != null ? objectsArray[count].toString() : "null"));
                        sb.append("} ");

                        if (count < objectsArray.length-1) {
                            sb.append(",");
                        }
                    }
                }

                LOGGER.log(Level.FINER, "Prepared statement show Parameters: " + sb.toString());
            }

            m_Stmt = m_s.getConnection().prepareStatement(m_sentence);

            if (m_SerWrite != null) {
                PreparedSentencePars preparedSentencePars = new PreparedSentencePars(m_Stmt);
                m_SerWrite.writeValues(preparedSentencePars, params);
            }

            if (m_Stmt.execute()) {
                result = new JDBCDataResultSet<T>(m_Stmt.getResultSet(), m_SerRead);
            } else {
                int iUC = m_Stmt.getUpdateCount();
                if (iUC < 0) {
                    result = null;
                } else {
                    result = new SentenceUpdateResultSet(iUC);
                }
            }
        } catch (SQLException eSQL) {
            LOGGER.log(Level.SEVERE, "Exception on Executing SQL", eSQL);
            throw new BasicException(eSQL);
        }

        return result;
    }

    /**
     *
     * @return @throws BasicException
     */
    @Override
    public final DataResultSet<T> moreResults() throws BasicException {
        // true -> un resultset
        // false -> un updatecount (si -1 entonces se acabo)

        try {
            if (m_Stmt.getMoreResults()) {
                // tenemos resultset
                return new JDBCDataResultSet(m_Stmt.getResultSet(), m_SerRead);
            } else {
                // tenemos updatecount o si devuelve -1 ya no hay mas
                int iUC = m_Stmt.getUpdateCount();
                if (iUC < 0) {
                    return null;
                } else {
                    return new SentenceUpdateResultSet(iUC);
                }
            }
        } catch (SQLException eSQL) {
            LOGGER.log(Level.SEVERE, "Exception on PreparedSentence", eSQL);
            throw new BasicException(eSQL);
        }
    }

    /**
     *
     * @throws BasicException
     */
    @Override
    public final void closeExec() throws BasicException {

        if (m_Stmt != null) {
            try {
                m_Stmt.close();
            } catch (SQLException eSQL) {
                LOGGER.log(Level.SEVERE, "Exception on PreparedSentence", eSQL);
                throw new BasicException(eSQL);
            } finally {
                m_Stmt = null;
            }
        }
    }
}
