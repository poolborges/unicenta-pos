/*
 * Copyright (C) 2022 KriolOS
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
package com.openbravo.pos.inventory;

import com.openbravo.data.loader.DataRead;
import com.openbravo.data.loader.Datas;
import com.openbravo.data.loader.PreparedSentence;
import com.openbravo.data.loader.SentenceExec;
import com.openbravo.data.loader.SentenceFind;
import com.openbravo.data.loader.SentenceList;
import com.openbravo.data.loader.SerializerReadString;
import com.openbravo.data.loader.SerializerWriteBasic;
import com.openbravo.data.loader.SerializerWriteString;
import com.openbravo.data.loader.Session;
import com.openbravo.pos.forms.BeanFactoryDataSingle;
import com.openbravo.pos.inventory.AttributeInstInfo;
import com.openbravo.pos.inventory.AttributeSetInfo;

/**
 *
 * @author poolborges
 */
public class DataLogicAttribute extends BeanFactoryDataSingle {

    public SentenceFind attsetSent;
    public SentenceList attvaluesSent;
    public SentenceList attinstSent;
    public SentenceList attinstSent2;
    public SentenceFind attsetinstExistsSent;
    public SentenceExec attsetSave;
    public SentenceExec attinstSave;

    private Session s;

    public DataLogicAttribute() {
    }

    @Override
    public void init(Session session) {
        this.s = session;

        attsetSave = new PreparedSentence(s,
                "INSERT INTO attributesetinstance (ID, ATTRIBUTESET_ID, DESCRIPTION) VALUES (?, ?, ?)",
                new SerializerWriteBasic(Datas.STRING, Datas.STRING, Datas.STRING));

        attinstSave = new PreparedSentence(s,
                "INSERT INTO attributeinstance(ID, ATTRIBUTESETINSTANCE_ID, ATTRIBUTE_ID, VALUE) VALUES (?, ?, ?, ?)",
                new SerializerWriteBasic(Datas.STRING, Datas.STRING, Datas.STRING, Datas.STRING));

        attsetSent = new PreparedSentence(s,
                "SELECT ID, NAME FROM attributeset WHERE ID = ?",
                SerializerWriteString.INSTANCE,
                (DataRead dr) -> new AttributeSetInfo(dr.getString(1), dr.getString(2)));

        attsetinstExistsSent = new PreparedSentence(s,
                "SELECT ID FROM attributesetinstance WHERE ATTRIBUTESET_ID = ? AND DESCRIPTION = ?",
                new SerializerWriteBasic(Datas.STRING, Datas.STRING),
                SerializerReadString.INSTANCE);

        attinstSent = new PreparedSentence(s, "SELECT A.ID, A.NAME, " + s.DB.CHAR_NULL() + ", " + s.DB.CHAR_NULL() + " "
                + "FROM attributeuse AU JOIN attribute A ON AU.ATTRIBUTE_ID = A.ID "
                + "WHERE AU.ATTRIBUTESET_ID = ? "
                + "ORDER BY AU.LINENO",
                SerializerWriteString.INSTANCE,
                (DataRead dr) -> new AttributeInstInfo(dr.getString(1), dr.getString(2), dr.getString(3), dr.getString(4)));

        attinstSent2 = new PreparedSentence(s, "SELECT A.ID, A.NAME, AI.ID, AI.VALUE "
                + "FROM attributeuse AU JOIN attribute A ON AU.ATTRIBUTE_ID = A.ID "
                + "LEFT OUTER JOIN attributeinstance AI ON AI.ATTRIBUTE_ID = A.ID "
                + "WHERE AU.ATTRIBUTESET_ID = ? AND AI.ATTRIBUTESETINSTANCE_ID = ?"
                + "ORDER BY AU.LINENO",
                new SerializerWriteBasic(Datas.STRING, Datas.STRING),
                (DataRead dr) -> new AttributeInstInfo(dr.getString(1), dr.getString(2), dr.getString(3), dr.getString(4)));

        attvaluesSent = new PreparedSentence(s, "SELECT VALUE FROM attributevalue WHERE ATTRIBUTE_ID = ? ORDER BY VALUE",
                SerializerWriteString.INSTANCE,
                SerializerReadString.INSTANCE);
    }

}
