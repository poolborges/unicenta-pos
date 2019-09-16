//    uniCenta oPOS  - Touch Friendly Point Of Sale
//    Copyright (c) 2009-2018 uniCenta
//    https://unicenta.com
//
//    This file is part of uniCenta oPOS
//
//    uniCenta oPOS is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    (at your option) any later version.
//
//   uniCenta oPOS is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with uniCenta oPOS.  If not, see <http://www.gnu.org/licenses/>.

package com.openbravo.pos.suppliers;

import com.openbravo.basic.BasicException;
import com.openbravo.data.loader.*;
import com.openbravo.format.Formats;
import com.openbravo.pos.forms.AppLocal;
import com.openbravo.pos.forms.BeanFactoryDataSingle;
import java.util.List;

/**
 * @author JG uniCenta
 */
public class DataLogicSuppliers extends BeanFactoryDataSingle {
    
    /**
     * Main Method for supplier object
     */
    protected Session s;
    private TableDefinition tsuppliers;
    private static final Datas[] supplierdatas = new Datas[] {
        Datas.STRING, 
        Datas.STRING, 
        Datas.STRING,
        Datas.STRING, 
        Datas.STRING, 
        Datas.STRING,         
        Datas.STRING};
    
    /**
     *
     * @param s
     */
    @Override
    public void init(Session s){
        this.s = s;
        tsuppliers = new TableDefinition(s
            , "suppliers"
            , new String[] { 
                "ID", 
                "SEARCHKEY", 
                "TAXID", 
                "NAME", 
                "MAXDEBT", 
                "ADDRESS",
                "ADDRESS2",
                "POSTAL",
                "CITY",
                "REGION",
                "COUNTRY",
                "FIRSTNAME",
                "LASTNAME",
                "EMAIL",
                "PHONE",
                "PHONE2",
                "FAX",
                "NOTES", 
                "VISIBLE", 
                "CURDATE", 
                "CURDEBT",
                "VATID"  }
            , new String[] { 
                "ID", 
                AppLocal.getIntString("label.searchkey"),
                AppLocal.getIntString("label.suppliertaxid"),
                AppLocal.getIntString("label.name"),

                AppLocal.getIntString("label.maxdebt"),
                AppLocal.getIntString("label.address"),
                AppLocal.getIntString("label.address2"),
                AppLocal.getIntString("label.postal"),
                
                AppLocal.getIntString("label.city"),
                AppLocal.getIntString("label.region"),
                AppLocal.getIntString("label.country"), 
                AppLocal.getIntString("label.firstname"),

                AppLocal.getIntString("label.lastname"),
                AppLocal.getIntString("label.email"),
                AppLocal.getIntString("label.phone"),
                AppLocal.getIntString("label.phone2"),

                AppLocal.getIntString("label.fax"),
                AppLocal.getIntString("label.notes"),
                "VISIBLE",
                AppLocal.getIntString("label.curdate"),

                AppLocal.getIntString("label.curdebt"),
                AppLocal.getIntString("label.suppliervatid") }
            , new Datas[] { 
                Datas.STRING, Datas.STRING, Datas.STRING, Datas.STRING, 
                Datas.DOUBLE, Datas.STRING, Datas.STRING, Datas.STRING, 
                Datas.STRING, Datas.STRING, Datas.STRING, Datas.STRING,
                Datas.STRING, Datas.STRING, Datas.STRING, Datas.STRING,
                Datas.STRING, Datas.STRING, Datas.BOOLEAN, Datas.TIMESTAMP,                 
                Datas.DOUBLE, Datas.STRING }
            , new Formats[] {
                Formats.STRING, Formats.STRING, Formats.STRING, Formats.STRING,
                Formats.CURRENCY, Formats.STRING, Formats.STRING, Formats.STRING,
                Formats.STRING, Formats.STRING, Formats.STRING, Formats.STRING,                
                Formats.STRING, Formats.STRING, Formats.STRING, Formats.STRING,
                Formats.STRING, Formats.STRING, Formats.BOOLEAN, Formats.TIMESTAMP,
                Formats.CURRENCY, Formats.STRING }
            , new int[] {0}
        );   
    }

    /**
     *
     * @return supplier data
     */
        public SentenceList getSupplierList() {
        return new StaticSentence(s
            , new QBFBuilder("SELECT "
                    + "ID, SEARCHKEY, TAXID, NAME, "
                    + "POSTAL, PHONE, EMAIL "
                    + "FROM suppliers "
                    + "WHERE VISIBLE = " + s.DB.TRUE() + " AND ?(QBF_FILTER) ORDER BY NAME"
                , new String[] {"SEARCHKEY", "TAXID", "NAME", "POSTAL", "PHONE", "EMAIL"})
            , new SerializerWriteBasic(new Datas[] {
                Datas.OBJECT, Datas.STRING, 
                Datas.OBJECT, Datas.STRING, 
                Datas.OBJECT, Datas.STRING, 
                Datas.OBJECT, Datas.STRING, 
                Datas.OBJECT, Datas.STRING, 
                Datas.OBJECT, Datas.STRING})
            , (DataRead dr) -> {
                SupplierInfo s1 = new SupplierInfo(dr.getString(1));
            s1.setSearchkey(dr.getString(2));
            s1.setTaxid(dr.getString(3));
            s1.setName(dr.getString(4));
            s1.setPostal(dr.getString(5));
            s1.setPhone(dr.getString(6));
            s1.setEmail(dr.getString(7));
            return s1;
        });
    }
        
    /**
     *
     * @return supplier data
     */
        public SentenceList getSuppList() {
        return new StaticSentence(s
            , new QBFBuilder("SELECT "
                    + "ID, SEARCHKEY, TAXID, NAME, "
                    + "POSTAL, PHONE, EMAIL "
                    + "FROM suppliers "
                    + "WHERE VISIBLE = " + s.DB.TRUE() + " ORDER BY NAME"
                , new String[] {"SEARCHKEY", "TAXID", "NAME", "POSTAL", "PHONE", "EMAIL"})
            , new SerializerWriteBasic(new Datas[] {
                Datas.OBJECT, Datas.STRING, 
                Datas.OBJECT, Datas.STRING, 
                Datas.OBJECT, Datas.STRING, 
                Datas.OBJECT, Datas.STRING, 
                Datas.OBJECT, Datas.STRING, 
                Datas.OBJECT, Datas.STRING})
            , (DataRead dr) -> {
                SupplierInfo s1 = new SupplierInfo(dr.getString(1));
            s1.setSearchkey(dr.getString(2));
            s1.setTaxid(dr.getString(3));
            s1.setName(dr.getString(4));
            s1.setPostal(dr.getString(5));
            s1.setPhone(dr.getString(6));
            s1.setEmail(dr.getString(7));
            return s1;
        });
    }        
        
    /**
     *
     * @param supplier
     * @return
     * @throws BasicException
     */
    public int updateSupplierExt(final SupplierInfoExt supplier) throws BasicException {
     
        return new PreparedSentence(s
                , "UPDATE suppliers SET NOTES = ? WHERE ID = ?"
                , SerializerWriteParams.INSTANCE )
                .exec(new DataParams() {
                @Override
                    public void writeValues() throws BasicException {
                        setString(1, supplier.getNotes());
                        setString(2, supplier.getID());
                    }
                });        
    }
    public final TableDefinition getTableSuppliers() {
        return tsuppliers;
    }
    
    /**
     * JG Aug 2017 - Return Supplier Id - sId param
     * @param sId
     * @return
     * @throws BasicException
     */
        @SuppressWarnings("unchecked")
    public final List<SupplierTransaction> getSuppliersTransactionList(String sId) throws BasicException {
                    
        return new PreparedSentence(s,               
                "SELECT "
                    + "stockdiary.datenew, "
                    + "products.NAME, "
                    + "stockdiary.units, "
                    + "stockdiary.price, "
                    + "stockdiary.reason, "
                    + "suppliers.id "                        
                + "FROM (stockdiary stockdiary "
                        + "INNER JOIN suppliers suppliers "
                        + "ON (stockdiary.supplier = suppliers.id)) "
                        + "INNER JOIN products products "
                        + "ON (stockdiary.product = products.ID) "
                + "WHERE suppliers.id = ? " 
                    + "GROUP BY "
                        + "stockdiary.datenew, "
                        + "products.NAME, " 
                        + "stockdiary.reason "
                + "ORDER BY stockdiary.datenew DESC",
                    SerializerWriteString.INSTANCE,                
                        SupplierTransaction.getSerializerRead()).list(sId);
    }    
}