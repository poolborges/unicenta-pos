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
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Arrays;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author adrianromero
 */
public class PreparedSentenceExt<W extends Object, T> extends JDBCBaseSentence<T> {
    
    private static final Logger LOGGER = Logger.getLogger("com.openbravo.data.loader.PreparedSentence");

    private String m_sentence;

    private SerializerWrite m_SerWrite = null;
    private SerializerRead m_SerRead = null;
    private PreparedStatement m_Stmt;

    public PreparedSentenceExt(Session s) {
        super(s);
    }
   

    public PreparedSentenceExt(Session s, String sentence, SerializerWrite<W> serwrite, SerializerRead<T> serread) {
        super(s);
        m_sentence = sentence;
        m_SerWrite = serwrite;
        m_SerRead=serread;
    }

    public PreparedSentenceExt(Session s, String sentence, SerializerWrite<W> serwrite) {
        this(s, sentence, serwrite, null);
    }

    public PreparedSentenceExt(Session s, String sentence) {
        this(s, sentence, null, null);
    }
    
    /*
    public final int exec(DataParams params) throws BasicException {
        params.setDataWrite(new PreparedSentencePars(this.m_Stmt));
    }*/

    @Override
    public DataResultSet<T> openExec(Object paramObject) throws BasicException {
        closeExec();
        try {
            LOGGER.log(Level.INFO, "Executing prepared SQL: {0}", this.m_sentence);
            String params = Arrays.toString((Object[])paramObject);
            LOGGER.log(Level.INFO, "Executing prepared SQL Parameters: {0}", params);
            this.m_Stmt = this.session.getConnection().prepareStatement(this.m_sentence);
            if (this.m_SerWrite != null) {
                this.m_SerWrite.writeValues(new PreparedSentencePars(this.m_Stmt), paramObject);
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

    private static final class PreparedSentencePars implements DataWrite {

        private final PreparedStatement m_ps;

        PreparedSentencePars(PreparedStatement ps) {
            this.m_ps = ps;
        }
        
        private boolean isNull(String value){
            return (value == null || value.equalsIgnoreCase("NULL")) ;
        }

        @Override
        public void setInt(int paramIndex, Integer value) throws BasicException {
            try {
                if (value == null) {
                    this.m_ps.setNull(paramIndex, Types.INTEGER);
                } else {
                    this.m_ps.setInt(paramIndex, value);
                }
            } catch (SQLException ex) {
                LOGGER.log(Level.WARNING, "Exception", ex);
                throw new BasicException(ex);
            }
        }

        @Override
        public void setString(int paramIndex, String value) throws BasicException {
            try {
                if (isNull(value)) {
                    this.m_ps.setNull(paramIndex, Types.VARCHAR);
                } else {
                    this.m_ps.setString(paramIndex, value);
                }
            } catch (SQLException ex) {
                LOGGER.log(Level.WARNING, "Exception", ex);
                throw new BasicException(ex);
            }
        }

        @Override
        public void setDouble(int paramIndex, Double value) throws BasicException {
            try {
                if (value == null) {
                    this.m_ps.setNull(paramIndex, Types.DOUBLE);
                } else {
                    this.m_ps.setDouble(paramIndex, value);
                }
            } catch (SQLException ex) {
                LOGGER.log(Level.WARNING, "Exception", ex);
                throw new BasicException(ex);
            }
        }

        @Override
        public void setBoolean(int paramIndex, Boolean value) throws BasicException {
            try {
                if (value == null) {
                    this.m_ps.setNull(paramIndex, Types.BOOLEAN);
                } else {
                    this.m_ps.setBoolean(paramIndex, value);
                }
            } catch (SQLException ex) {
                LOGGER.log(Level.WARNING, "Exception", ex);
                throw new BasicException(ex);
            }
        }

        @Override
        public void setTimestamp(int paramIndex, Date value) throws BasicException {
            try {
                if (value == null) {
                    this.m_ps.setNull(paramIndex, Types.TIMESTAMP);
                } else {
                    this.m_ps.setTimestamp(paramIndex, new Timestamp(value.getTime()));
                }
            } catch (SQLException ex) {
                LOGGER.log(Level.WARNING, "Exception", ex);
                throw new BasicException(ex);
            }
        }

        @Override
        public void setBytes(int paramIndex, byte[] value) throws BasicException {
            try {
                if (value == null) {
                    this.m_ps.setNull(paramIndex, Types.BINARY);
                } else {
                    this.m_ps.setBytes(paramIndex, value);
                }
            } catch (SQLException ex) {
                LOGGER.log(Level.WARNING, "Exception", ex);
                throw new BasicException(ex);
            }
        }

        @Override
        public void setObject(int paramIndex, Object value) throws BasicException {
            try {
                if (value == null) {
                    this.m_ps.setNull(paramIndex, Types.BINARY);
                } else {
                    this.m_ps.setObject(paramIndex, value);
                }
            } catch (SQLException ex) {
                LOGGER.log(Level.WARNING, "Exception", ex);
                throw new BasicException(ex);
            }
        }
    }

    @Override
    public final DataResultSet<T> moreResults() throws BasicException {
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
}
