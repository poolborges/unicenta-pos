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

package com.openbravo.data.user;
import com.openbravo.data.loader.TableDefinition;
import com.openbravo.basic.BasicException;
import com.openbravo.data.loader.SentenceExec;

/**
 *
 * @author JG uniCenta
 */
public class DefaultSaveProvider implements SaveProvider<Object[]>{
    
    /**
     *
     */
    protected SentenceExec m_sentupdate;

    /**
     *
     */
    protected SentenceExec m_sentinsert;

    /**
     *
     */
    protected SentenceExec m_sentdelete;
    
    /** Creates a new instance of SavePrSentence
     * @param sentupdate
     * @param sentdelete
     * @param sentinsert */
    public DefaultSaveProvider(SentenceExec sentupdate, SentenceExec sentinsert, SentenceExec sentdelete) {
        m_sentupdate = sentupdate;
        m_sentinsert = sentinsert;
        m_sentdelete = sentdelete;
    }

    /**
     *
     * @param table
     */
    public DefaultSaveProvider(TableDefinition table) {
        m_sentupdate = table.getUpdateSentence();
        m_sentdelete = table.getDeleteSentence();
        m_sentinsert = table.getInsertSentence();
    }

    /**
     *
     * @param table
     * @param fields
     */
    public DefaultSaveProvider(TableDefinition table, int[] fields) {
        m_sentupdate = table.getUpdateSentence(fields);
        m_sentdelete = table.getDeleteSentence();
        m_sentinsert = table.getInsertSentence(fields);
    }
    
    /**
     *
     * @return
     */
    @Override
    public boolean canDelete() {
        return m_sentdelete != null;      
    }

    /**
     *
     * @param value
     * @return
     * @throws BasicException
     */
    @Override
    public int deleteData(Object[] value) throws BasicException {
        return m_sentdelete.exec(value);
    }
    
    /**
     *
     * @return
     */
    @Override
    public boolean canInsert() {
        return m_sentinsert != null;          
    }
    
    /**
     *
     * @param value
     * @return
     * @throws BasicException
     */
    @Override
    public int insertData(Object[] value) throws BasicException {
        return m_sentinsert.exec(value);
    }
    
    /**
     *
     * @return
     */
    @Override
    public boolean canUpdate() {
        return m_sentupdate != null;      
    }

    /**
     *
     * @param value
     * @return
     * @throws BasicException
     */
    @Override
    public int updateData(Object[] value) throws BasicException {
        return m_sentupdate.exec(value);
    }
}
