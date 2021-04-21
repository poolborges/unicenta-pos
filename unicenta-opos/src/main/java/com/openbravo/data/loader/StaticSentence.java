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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author  adrianromero
 */
public class StaticSentence<T> extends JDBCSentence<T> {

    private static final Logger logger = Logger.getLogger("com.openbravo.data.loader.StaticSentence");
    
    private ISQLBuilderStatic m_sentence;
    protected SerializerWrite<Object> m_SerWrite = null;
    protected SerializerRead<T> m_SerRead = null;
    private Statement m_Stmt;

    public StaticSentence(Session s, ISQLBuilderStatic sentence, SerializerWrite<Object> serwrite, SerializerRead<T> serread) {
        super(s);
        m_sentence = sentence;
        m_SerWrite = serwrite;
        m_SerRead = serread;
        m_Stmt = null;
    }    

    public StaticSentence(Session s, ISQLBuilderStatic sentence) {
        this(s, sentence, null, null);
    }     

    public StaticSentence(Session s, ISQLBuilderStatic sentence, SerializerWrite<Object> serwrite) {
        this(s, sentence, serwrite, null);
    }     

    public StaticSentence(Session s, String sentence, SerializerWrite<Object> serwrite, SerializerRead<T> serread) {
        this(s, new NormalBuilder(sentence), serwrite, serread);
    }

    public StaticSentence(Session s, String sentence, SerializerWrite<Object> serwrite) {
        this(s, new NormalBuilder(sentence), serwrite, null);
    }

    public StaticSentence(Session s, String sentence) {
        this(s, new NormalBuilder(sentence), null, null);
    }

    @Override
    public DataResultSet<T> openExec(Object params) throws BasicException {
        // true -> un resultset
        // false -> un updatecount (si -1 entonces se acabo)
        
        closeExec();
            
        try {
            m_Stmt = m_s.getConnection().createStatement();

            String sentence = m_sentence.getSQL(m_SerWrite, params);
            
           logger.log(Level.INFO, "Executing static SQL: {0}", sentence);

            if (m_Stmt.execute(sentence)) {
                return new JDBCDataResultSet(m_Stmt.getResultSet(), m_SerRead);
            } else { 
                int iUC = m_Stmt.getUpdateCount();
                if (iUC < 0) {
                    return null;
                } else {
                    return new SentenceUpdateResultSet(iUC);
                }
            }
        } catch (SQLException eSQL) {
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

    @Override
    public DataResultSet<T> moreResults() throws BasicException {

        try {
            if (m_Stmt.getMoreResults()){
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
            throw new BasicException(eSQL);
        }
    }    
    
}
