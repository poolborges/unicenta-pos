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
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author adrianromero
 */
abstract class PreparedSentenceAbstract<W extends Object, R> extends JDBCBaseSentence<R> {

    private static final Logger LOGGER = Logger.getLogger("com.openbravo.data.loader.PreparedSentence");

    private final String m_sentence;

    private SerializerWrite m_SerWrite = null;
    private SerializerRead<R> m_SerRead = null;
    private PreparedStatement m_Stmt;

    public PreparedSentenceAbstract(Session s, String sentence) {
        this(s, sentence, null);
    }

    public PreparedSentenceAbstract(Session s, String sentence, SerializerWrite<W> serwrite) {
        this(s, sentence, serwrite, null);
    }

    public PreparedSentenceAbstract(Session s, String sentence, SerializerWrite<W> serwrite, SerializerRead<R> serread) {
        super(s);
        m_sentence = sentence;
        m_SerWrite = serwrite;
        m_SerRead = serread;
    }

    @Override
    public DataResultSet<R> openExec(Object paramObject) throws BasicException {
        closeExec();
        try {
            this.m_Stmt = this.session.getConnection().prepareStatement(this.m_sentence);
            if (this.m_SerWrite != null) {
                this.m_SerWrite.writeValues(new PreparedSentenceDataWrite(this.m_Stmt), paramObject);
            }
            if (this.m_Stmt.execute()) {
                return new JDBCDataResultSet(this.m_Stmt.getResultSet(), this.m_SerRead);
            }
            int iUC = this.m_Stmt.getUpdateCount();
            if (iUC < 0) {
                return null;
            }
            return new UpdateDataResultSet(iUC);
        } catch (SQLException ex) {
            LOGGER.log(Level.WARNING, "Exception", ex);
            throw new BasicException(ex);
        }
    }

    @Override
    public final DataResultSet<R> moreResults() throws BasicException {
        try {
            if (this.m_Stmt.getMoreResults()) {
                return new JDBCDataResultSet(this.m_Stmt.getResultSet(), this.m_SerRead);
            }
            int iUC = this.m_Stmt.getUpdateCount();
            if (iUC < 0) {
                return null;
            }
            return new UpdateDataResultSet(iUC);
        } catch (SQLException ex) {
            LOGGER.log(Level.WARNING, "Exception", ex);
            throw new BasicException(ex);
        }
    }

    @Override
    public final void closeExec() throws BasicException {
        if (this.m_Stmt != null)
      try {
            this.m_Stmt.close();
        } catch (SQLException ex) {
            LOGGER.log(Level.WARNING, "Exception", ex);
            throw new BasicException(ex);
        } finally {
            this.m_Stmt = null;
        }
    }

    private void _logDebug(Object paramObject) {
        if (LOGGER.getLevel() == Level.FINE) {
            LOGGER.log(Level.INFO, "Executing prepared SQL: {0}", this.m_sentence);
            String params = (paramObject instanceof Object[]) ? Arrays.toString((Object[]) paramObject) : paramObject.toString();
            LOGGER.log(Level.INFO, "Executing prepared SQL Parameters: {0}", params);
        }
    }
}
