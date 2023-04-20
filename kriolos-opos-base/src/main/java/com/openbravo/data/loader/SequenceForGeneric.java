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
                        new Datas[]{Datas.STRING, Datas.STRING},
                        new int[]{0, 1}));
    }

    /**
     *
     * @param params this is ignored
     * @return the last seq as DataResultSet<Integer>
     * @throws BasicException
     */
    @Override
    public DataResultSet openExec(Object params) throws BasicException {

        Integer dt = (Integer) selectSetence.find(new Object[]{this.tableName});

        Logger.getLogger(SequenceForGeneric.class.getName()).log(Level.INFO, "FOUND SEQUENCE: ", dt);
        
        if (dt == null) {
         dt = insert(1);
        }

        Logger.getLogger(SequenceForGeneric.class.getName()).log(Level.INFO, "CURRENT SEQUENCE: ", dt);

        updateSentence.exec(new Object[]{this.tableName});

        DataResultSet drs = selectSetence.openExec(new Object[]{this.tableName});

        Logger.getLogger(SequenceForGeneric.class.getName()).log(Level.INFO, "RESULT SEQUENCE: ", drs);

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
            //Force to Insert Sequence (0) for a tableName

        } catch (Exception ex) {
            Logger.getLogger(SequenceForGeneric.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    private int insert(int num) throws BasicException {

        Logger.getLogger(SequenceForGeneric.class.getName()).log(Level.INFO, "INSERT SEQUENCE: ", num);
        var ins = new PreparedSentence(session, "INSERT INTO seqnumber (tablename, seqnum) VALUES(?, ?)", new SerializerWriteBasicExt(
                new Datas[]{Datas.STRING, Datas.INT},
                new int[]{0, 1}));
        return ins.exec(new Object[]{this.tableName, num});
    }
}
