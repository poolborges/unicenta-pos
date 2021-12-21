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

import com.openbravo.format.Formats;

/**
 *
 * @author JG uniCenta
 */
public class TableDefinition<T> {
    
    private Session m_s;
    private String tablename;
   
    private String[] fieldname;
    private String[] fieldtran;
    private Datas[] fielddata;
    private Formats[] fieldformat;
    
    private int[] idinx;
   
    
    /** Creates a new instance of TableDefinition
     * @param s
     * @param fieldformat
     * @param tablename
     * @param fieldname
     * @param fieldtran
     * @param idinx
     * @param fielddata */
    public TableDefinition(Session s, String tablename, String[] fieldname, String[] fieldtran, Datas[] fielddata, Formats[] fieldformat,int[] idinx) {
        
        m_s = s;
        this.tablename = tablename;       
        
        this.fieldname = fieldname;
        this.fieldtran = fieldtran;
        this.fielddata = fielddata;
        this.fieldformat = fieldformat;
  
        this.idinx = idinx;
    }  

    /**
     *
     * @param s
     * @param tablename
     * @param fieldname
     * @param fielddata
     * @param fieldformat
     * @param idinx
     */
    public TableDefinition(Session s,String tablename, String[] fieldname, Datas[] fielddata, Formats[] fieldformat,int[] idinx) {
        this(s, tablename, fieldname, fieldname, fielddata, fieldformat, idinx);
    }

    /**
     *
     * @return
     */
    public String getTableName() {
        return tablename;
    }
    
    /**
     *
     * @return
     */
    public String[] getFields() {
        return fieldname;
    }
    
    /**
     *
     * @param aiFields
     * @return
     */
    public Vectorer getVectorerBasic(int[] aiFields) {
        return new VectorerBasic(fieldtran, fieldformat, aiFields);
    }
    
    /**
     *
     * @param aiFields
     * @return
     */
    public IRenderString getRenderStringBasic(int[] aiFields) {
        return new RenderStringBasic(fieldformat, aiFields);
    }

    public ComparatorCreator getComparatorCreator(int [] aiOrders) {
        return new ComparatorCreatorBasic(fieldtran, fielddata, aiOrders);
    }

    public IKeyGetter getKeyGetterBasic() {
        if (idinx.length == 1) {
            return new KeyGetterFirst(idinx);
        } else {
            return new KeyGetterBasic(idinx);     
        }
    }

    public SerializerRead getSerializerReadBasic() {
        return new SerializerReadBasic(fielddata);
    }

    public SerializerWrite getSerializerInsertBasic(int[] fieldindx) {
        return new SerializerWriteBasicExt(fielddata, fieldindx);
    }

    public SerializerWrite getSerializerDeleteBasic() {     
        return new SerializerWriteBasicExt(fielddata, idinx);
    }

    public SerializerWrite getSerializerUpdateBasic(int[] fieldindx) {
        
        int[] aindex = new int[fieldindx.length + idinx.length];

        for (int i = 0; i < fieldindx.length; i++) {
            aindex[i] = fieldindx[i];
        } 
        for (int i = 0; i < idinx.length; i++) {
            aindex[i + fieldindx.length] = idinx[i];
        }       
 
        return new SerializerWriteBasicExt(fielddata, aindex);
    }

    public SentenceList<T> getListSentence() {
        return getListSentence(getSerializerReadBasic());
    }

    public SentenceList<T> getListSentence(SerializerRead sr) {
        return new PreparedSentence(m_s, getListSQL(), null,  sr);
    }

    private String getListSQL() {
        
        StringBuilder sent = new StringBuilder();
        sent.append("select ");

        for (int i = 0; i < fieldname.length; i ++) {
            if (i > 0) {
                sent.append(", ");
            }
            sent.append(fieldname[i]);
        }        
        
        sent.append(" from ");        
        sent.append(tablename);
        
        return sent.toString();    
    }

    public SentenceExec getDeleteSentence() {
        return getDeleteSentence(getSerializerDeleteBasic());
    }

    public SentenceExec getDeleteSentence(SerializerWrite sw) {
        return new PreparedSentence(m_s, getDeleteSQL(), sw, null);
    }
    

    private String getDeleteSQL() {
        
        StringBuilder sent = new StringBuilder();
        sent.append("delete from ");
        sent.append(tablename);
        
        for (int i = 0; i < idinx.length; i ++) {
            sent.append((i == 0) ? " where " : " and ");
            sent.append(fieldname[idinx[i]]);
            sent.append(" = ?");
        }
        
        return sent.toString();     
    }

    public SentenceExec getInsertSentence() {
        return getInsertSentence(getAllFields());
    }
    
    public SentenceExec getInsertSentence(int[] fieldindx) {
        return new PreparedSentence(m_s, getInsertSQL(fieldindx), getSerializerInsertBasic(fieldindx), null);
    }
    
    private String getInsertSQL(int[] fieldindx) {
        
        StringBuilder sent = new StringBuilder();
        StringBuilder values = new StringBuilder();
        
        sent.append("insert into ");
        sent.append(tablename);
        sent.append(" (");        
        
        for (int i = 0; i < fieldindx.length; i ++) {
            if (i > 0) {
                sent.append(", ");
                values.append(", ");
            }
            sent.append(fieldname[fieldindx[i]]);
            values.append("?");
        }
        
        sent.append(") values (");
        sent.append(values.toString());
        sent.append(")");

        return sent.toString();       
    }
    
    private int[] getAllFields() {
        
        int[] fieldindx = new int[fieldname.length];
        for (int i = 0; i < fieldname.length; i++) {
            fieldindx[i] = i;
        }
        return fieldindx;        
    }

    public SentenceExec getUpdateSentence() {
        return getUpdateSentence(getAllFields());
    }

    public SentenceExec getUpdateSentence(int[] fieldindx) {
        return new PreparedSentence(m_s, getUpdateSQL(fieldindx), getSerializerUpdateBasic(fieldindx), null);
    }
    
    private String getUpdateSQL(int[] fieldindx) {
        
        StringBuilder sent = new StringBuilder();
        
        sent.append("update ");
        sent.append(tablename);
        sent.append(" set ");
        
        for (int i = 0; i < fieldindx.length; i ++) {
            if (i > 0) {
                sent.append(", ");
            }
            sent.append(fieldname[fieldindx[i]]);
            sent.append(" = ?");
        }
        
        for (int i = 0; i < idinx.length; i ++) {
            sent.append((i == 0) ? " where " : " and ");
            sent.append(fieldname[idinx[i]]);
            sent.append(" = ?");
        }
        
        return sent.toString();               
    }
}
