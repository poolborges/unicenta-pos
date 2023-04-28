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

import com.openbravo.basic.BasicException;
import com.openbravo.data.loader.SentenceList;
import com.openbravo.data.loader.TableDefinition;
import java.util.*;

/**
 *
 * @author JG uniCenta
 */
public class ListProviderCreator<T> implements ListProvider<T> {
    
    private SentenceList<T> sentenceList;
    private EditorCreator<T> editorCreator;
    private Object params;
    
    /** Creates a new instance of ListProviderEditor
     * @param sent
     * @param prov */
    public ListProviderCreator(SentenceList<T> sent, EditorCreator<T> prov) {
        this.sentenceList = sent;
        this.editorCreator = prov;
        this.params = null;
    }
    
    /**
     *
     * @param sent
     */
    public ListProviderCreator(SentenceList<T> sent) {
        this(sent, null);
    }
    
    /**
     *
     * @param table
     */
    public ListProviderCreator(TableDefinition<T> table) {        
        this(table.getListSentence(), null);
    }

    /**
     *
     * @return
     * @throws BasicException
     */
    @Override
    public List<T> loadData() throws BasicException {       
        params = (editorCreator == null) ? null : editorCreator.createValue();
        return refreshData();
    }
    
    /**
     *
     * @return
     * @throws BasicException
     */
    @Override
    public List<T> refreshData() throws BasicException {
        return sentenceList.list(params);
    }    
}
