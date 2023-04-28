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
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author adrianromero
 * @param <W>
 * @param <T>
 */
public class StaticSentence<W extends Object, T> extends JDBCBaseSentence<T> {

    protected static final Logger LOGGER = Logger.getLogger(StaticSentence.class.getName());

    protected ISQLBuilderStatic m_SqlBuilder;
    protected SerializerWrite<W> m_SerWrite = null;
    protected SerializerRead<T> m_SerRead = null;
    protected Statement m_Stmt;

    public StaticSentence(Session s, ISQLBuilderStatic sqlBuilder, SerializerWrite<W> serwrite, SerializerRead<T> serread) {
        super(s);
        m_SqlBuilder = sqlBuilder;
        m_SerWrite = serwrite;
        m_SerRead = serread;
        m_Stmt = null;
    }

    public StaticSentence(Session s, ISQLBuilderStatic sqlBuilder) {
        this(s, sqlBuilder, null, null);
    }

    public StaticSentence(Session s, ISQLBuilderStatic sqlBuilder, SerializerWrite<W> serwrite) {
        this(s, sqlBuilder, serwrite, null);
    }

    public StaticSentence(Session s, String sqlStatement, SerializerWrite<W> serwrite, SerializerRead<T> serread) {
        this(s, new NormalBuilder(sqlStatement), serwrite, serread);
    }

    public StaticSentence(Session s, String sqlStatement, SerializerWrite<W> serwrite) {
        this(s, new NormalBuilder(sqlStatement), serwrite, null);
    }

    public StaticSentence(Session s, String sqlStatement, SerializerRead<T> serread) {
        this(s, new NormalBuilder(sqlStatement), null, serread);
    }

    public StaticSentence(Session s, String sqlStatement) {
        this(s, new NormalBuilder(sqlStatement), null, null);
    }

    @Override
    public DataResultSet<T> openExec(Object params) throws BasicException {
        closeExec();

        DataResultSet<T> result;
        String sentence = "";

        LOGGER.log(Level.FINE, "Executing SQL params: {0}", params);
        log(params);
        try {

            sentence = m_SqlBuilder.getSQL(m_SerWrite, params);

            LOGGER.log(Level.FINE, "Executing SQL sentence : {0}", sentence);

            m_Stmt = session.getConnection().createStatement();

            if (m_Stmt.execute(sentence)) {
                result = new JDBCDataResultSet<>(m_Stmt.getResultSet(), m_SerRead);
            } else {
                int iUC = m_Stmt.getUpdateCount();
                if (iUC < 0) {
                    result = null;
                } else {
                    result = new UpdateDataResultSet<>(iUC);
                }
            }
        } catch (BasicException | SQLException eSQL) {
            LOGGER.log(Level.SEVERE, "Exception while execute SQL: " + sentence, eSQL);
            throw new BasicException(eSQL);
        }
        return result;
    }

    @Override
    public DataResultSet<T> moreResults() throws BasicException {

        try {
            if (m_Stmt.getMoreResults()) {
                return new JDBCDataResultSet<>(m_Stmt.getResultSet(), m_SerRead);
            } else {
                int iUC = m_Stmt.getUpdateCount();
                if (iUC < 0) {
                    return null;
                } else {
                    return new UpdateDataResultSet<>(iUC);
                }
            }
        } catch (SQLException eSQL) {
            LOGGER.log(Level.SEVERE, "Exception while execute: ", eSQL);
            throw new BasicException(eSQL);
        }
    }

    @Override
    public void closeExec() throws BasicException {

        if (m_Stmt != null) {
            try {
                m_Stmt.close();
            } catch (SQLException eSQL) {
                throw new BasicException(eSQL);
            } finally {
                m_Stmt = null;
            }
        }
    }

    private void log(Object params) {

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

                    if (count < objectsArray.length - 1) {
                        sb.append(",");
                    }
                }
            }

            LOGGER.log(Level.FINER, "SQL params: {0}", sb.toString());
        }
    }
}
