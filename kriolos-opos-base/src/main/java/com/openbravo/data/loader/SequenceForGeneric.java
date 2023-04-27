/*
 * Copyright (C) 2023 Paulo Borges
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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author poolb
 */
public class SequenceForGeneric extends BaseSentence {

    private static final Logger LOGGER = Logger.getLogger("SequenceForGeneric");
    private final BaseSentence updateSentence;
    private final BaseSentence selectSetence;
    private final String tableName;
    private final Session session;

    public SequenceForGeneric(Session session, String tableName) {
        this.tableName = tableName;
        this.session = session;

        selectSetence = new StaticSentence(session, "SELECT seqnum FROM seqnumber WHERE tablename = ?", new SerializerWriteBasicExt(
                new Datas[]{Datas.STRING},
                new int[]{0}), SerializerReadInteger.INSTANCE);

        updateSentence = new StaticSentence(
                session,
                "UPDATE seqnumber SET seqnum = ? WHERE tablename = ?",
                new SerializerWriteBasicExt(
                        new Datas[]{Datas.INT, Datas.STRING},
                        new int[]{0, 1}));
    }

    /**
     *
     * @param params this is ignored
     * @return the last seq as DataResultSet<Integer>
     * @throws BasicException
     */
    @Override
    public DataResultSet<Integer> openExec(Object params) throws BasicException {

        Integer dt = (Integer) selectSetence.find(new Object[]{this.tableName});

        LOGGER.log(Level.INFO, "FOUND SEQUENCE: "+this.tableName +"; seq: "+ dt);
        
        if (dt == null) {
         dt = insert(1);
        }

        LOGGER.log(Level.INFO, "CURRENT SEQUENCE: "+this.tableName +"; seq: "+ dt);
        dt +=1;

        updateSentence.exec(new Object[]{dt+1, this.tableName});

        DataResultSet<Integer> drs = selectSetence.openExec(new Object[]{this.tableName});
        LOGGER.log(Level.INFO, "RESULT SEQUENCE: "+this.tableName +"; seq: "+ dt);

        return drs;
    }

    @Override
    public DataResultSet moreResults() throws BasicException {
        return selectSetence.moreResults();
    }

    @Override
    public void closeExec() throws BasicException {
        updateSentence.closeExec();
        selectSetence.closeExec();
    }

    public SentenceExec reset() {

        try {
            openExec(null);
            return selectSetence;
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Exception execute reset", ex);
        }

        return null;
    }

    private int insert(int num) throws BasicException {

        LOGGER.log(Level.INFO, "INSERT SEQUENCE: "+this.tableName +"; seq: "+ num);
        var ins = new PreparedSentence(session, "INSERT INTO seqnumber (tablename, seqnum) VALUES(?, ?)", new SerializerWriteBasicExt(
                new Datas[]{Datas.STRING, Datas.INT},
                new int[]{0, 1}));
        return ins.exec(new Object[]{this.tableName, num});
    }
}
