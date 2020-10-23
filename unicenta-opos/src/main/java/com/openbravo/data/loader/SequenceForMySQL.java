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

/**
 *
 * @author JG uniCenta
 */
public class SequenceForMySQL extends BaseSentence {

    private final BaseSentence sent1;
    private final BaseSentence sent2;

    /**
     * Creates a new instance of SequenceForMySQL
     *
     * @param s
     * @param sSeqTable
     */
    public SequenceForMySQL(Session s, String sSeqTable) {

        sent1 = new StaticSentence(s, "UPDATE " + sSeqTable + " SET ID = LAST_INSERT_ID(ID + 1)");
        sent2 = new StaticSentence(s, "SELECT LAST_INSERT_ID()", null, SerializerReadInteger.INSTANCE);
    }

    // Funciones de bajo nivel
    /**
     *
     * @param params
     * @return
     * @throws BasicException
     */
    @Override
    public DataResultSet openExec(Object params) throws BasicException {
        sent1.exec();
        return sent2.openExec(null);
    }

    /**
     *
     * @return @throws BasicException
     */
    @Override
    public DataResultSet moreResults() throws BasicException {
        return sent2.moreResults();
    }

    /**
     *
     * @throws BasicException
     */
    @Override
    public void closeExec() throws BasicException {
        sent2.closeExec();
    }
}
