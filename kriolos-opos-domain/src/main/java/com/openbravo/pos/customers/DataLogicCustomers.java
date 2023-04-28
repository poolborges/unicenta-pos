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
package com.openbravo.pos.customers;

import com.openbravo.basic.BasicException;
import com.openbravo.data.loader.*;
import com.openbravo.data.user.DefaultSaveProvider;
import com.openbravo.data.user.SaveProvider;
import com.openbravo.format.Formats;
import com.openbravo.pos.customers.CustomerInfo;
import com.openbravo.pos.customers.CustomerInfoExt;
import com.openbravo.pos.forms.AppLocal;
import com.openbravo.pos.forms.BeanFactoryDataSingle;
import com.openbravo.pos.voucher.VoucherInfo;

/**
 * @author JG uniCenta
 * @author adrianromero
 */
public class DataLogicCustomers extends BeanFactoryDataSingle {

    protected Session s;
    private static final Datas[] RESERVATION_DATA = new Datas[]{
        Datas.STRING,     //R.ID 
        Datas.TIMESTAMP,  //R.CREATED
        Datas.TIMESTAMP,  //R.DATENEW
        Datas.STRING,     //C.CUSTOMER
        Datas.STRING,     //customers.TAXID
        Datas.STRING,     //customers.SEARCHKEY
        Datas.STRING,     //COALESCE(customers.NAME, R.TITLE)
        Datas.INT,        //R.CHAIRS
        Datas.BOOLEAN,    //R.ISDONE
        Datas.STRING      //R.DESCRIPTION
    };

    private static final Datas[] CUSTOMER_DATA = new Datas[]{
        Datas.OBJECT, Datas.STRING, //TAXID
        Datas.OBJECT, Datas.STRING, //SEARCHKEY
        Datas.OBJECT, Datas.STRING, //NAME
        Datas.OBJECT, Datas.STRING, //POSTAL
        Datas.OBJECT, Datas.STRING, //PHONE
        Datas.OBJECT, Datas.STRING //EMAIL
    };

    @Override
    public void init(Session s) {
        this.s = s;
    }

    public SentenceList<CustomerInfo> getCustomerList() {
        return new StaticSentence(s,
                new QBFBuilder("SELECT "
                        + "ID, TAXID, SEARCHKEY, NAME, "
                        + "POSTAL, EMAIL, PHONE, IMAGE "
                        + "FROM customers "
                        + "WHERE VISIBLE = " + s.DB.TRUE() + " AND ?(QBF_FILTER) ORDER BY NAME",
                        new String[]{"TAXID", "SEARCHKEY", "NAME", "POSTAL", "PHONE", "EMAIL"}),
                new SerializerWriteBasic(CUSTOMER_DATA),
                new CustomerInfoRead());
    }

    public final CustomerInfo getCustomerInfo(String id) throws BasicException {
        return (CustomerInfo) new PreparedSentence(s,
                "SELECT "
                + "ID, TAXID, SEARCHKEY, NAME, "
                + "POSTAL, EMAIL, PHONE, IMAGE "
                + "FROM customers WHERE VISIBLE = " + s.DB.TRUE() + " "
                + "AND ID = ?",
                SerializerWriteString.INSTANCE,
                new CustomerInfoRead()).find(id);
    }

    public int updateCustomerExt(final CustomerInfoExt customer) throws BasicException {

        return new PreparedSentence(s,
                "UPDATE customers SET NOTES = ? WHERE ID = ?",
                SerializerWriteParams.INSTANCE
        ).exec(new DataParams() {
            @Override
            public void writeValues() throws BasicException {
                setString(1, customer.getNotes());
                setString(2, customer.getId());
            }
        });
    }

    // <editor-fold defaultstate="collapsed" desc="Reservation">
    public final SentenceList getReservationsList() {
        return new PreparedSentence(s,
                "SELECT "
                + "R.ID, R.CREATED, R.DATENEW, C.CUSTOMER, customers.TAXID, customers.SEARCHKEY, "
                + "COALESCE(customers.NAME, R.TITLE),  R.CHAIRS, R.ISDONE, R.DESCRIPTION "
                + "FROM reservations R "
                + "LEFT OUTER JOIN reservation_customers C ON R.ID = C.ID "
                + "LEFT OUTER JOIN customers ON C.CUSTOMER = customers.ID "
                + "WHERE R.DATENEW >= ? AND R.DATENEW < ?",
                new SerializerWriteBasic(new Datas[]{Datas.TIMESTAMP, Datas.TIMESTAMP}),
                new SerializerReadBasic(RESERVATION_DATA));
    }

    public final SentenceExec getReservationsUpdate() {
        return new SentenceExecTransaction(s) {
            @Override
            public int execInTransaction(Object[] params) throws BasicException {

                new PreparedSentence(s,
                        "DELETE FROM reservation_customers WHERE ID = ?",
                        new SerializerWriteBasicExt(RESERVATION_DATA, new int[]{0})).exec(params);

                if (params[3] != null) {
                    new PreparedSentence(s,
                            "INSERT INTO reservation_customers (ID, CUSTOMER) VALUES (?, ?)",
                            new SerializerWriteBasicExt(RESERVATION_DATA, new int[]{0, 3})).exec(params);
                }
                return new PreparedSentence(s,
                        "UPDATE reservations SET ID = ?, CREATED = ?, DATENEW = ?, TITLE = ?, CHAIRS = ?, ISDONE = ?, DESCRIPTION = ? WHERE ID = ?",
                        new SerializerWriteBasicExt(RESERVATION_DATA, new int[]{0, 1, 2, 6, 7, 8, 9, 0})).exec(params);
            }
        };
    }

    public final SentenceExec getReservationsDelete() {
        return new SentenceExecTransaction(s) {
            @Override
            public int execInTransaction(Object[] params) throws BasicException {

                new PreparedSentence(s,
                        "DELETE FROM reservation_customers WHERE ID = ?",
                        new SerializerWriteBasicExt(RESERVATION_DATA, new int[]{0})).exec(params);
                return new PreparedSentence(s,
                        "DELETE FROM reservations WHERE ID = ?",
                        new SerializerWriteBasicExt(RESERVATION_DATA, new int[]{0})).exec(params);
            }
        };
    }

    public final SentenceExec getReservationsInsert() {
        return new SentenceExecTransaction(s) {
            @Override
            public int execInTransaction(Object[] params) throws BasicException {

                int i = new PreparedSentence(s,
                        "INSERT INTO reservations (ID, CREATED, DATENEW, TITLE, CHAIRS, ISDONE, DESCRIPTION) VALUES (?, ?, ?, ?, ?, ?, ?)",
                        new SerializerWriteBasicExt(RESERVATION_DATA, new int[]{0, 1, 2, 6, 7, 8, 9})).exec(params);

                if (params[3] != null) {
                    new PreparedSentence(s,
                            "INSERT INTO reservation_customers (ID, CUSTOMER) VALUES (?, ?)",
                            new SerializerWriteBasicExt(RESERVATION_DATA, new int[]{0, 3})).exec(params);
                }
                return i;
            }
        };
    }
    // </editor-fold>

    public final TableDefinition getTableCustomers() {
        TableDefinition tcustomers = new TableDefinition(s,
                "customers",
                new String[]{
                    "ID",
                    "SEARCHKEY",
                    "TAXID",
                    "NAME",
                    "TAXCATEGORY",
                    "CARD",
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
                    "IMAGE",
                    "ISVIP",
                    "DISCOUNT",
                    "MEMODATE"
                },
                new String[]{
                    "ID",
                    AppLocal.getIntString("label.searchkey"),
                    AppLocal.getIntString("label.taxid"),
                    AppLocal.getIntString("label.name"),
                    "TAXCATEGORY",
                    "CARD",
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
                    "IMAGE",
                    "ISVIP",
                    "DISCOUNT",
                    "MEMODATE"
                },
                new Datas[]{
                    Datas.STRING, //id
                    Datas.STRING, //searchkey
                    Datas.STRING, //taxid
                    Datas.STRING, //name
                    Datas.STRING, //taxcat
                    Datas.STRING, //card
                    Datas.DOUBLE, //maxdebt
                    Datas.STRING, //add
                    Datas.STRING, //add2
                    Datas.STRING, //postal
                    Datas.STRING, //city
                    Datas.STRING, //region
                    Datas.STRING, //cntry
                    Datas.STRING, //fname
                    Datas.STRING, //lname
                    Datas.STRING, //email
                    Datas.STRING, //phone
                    Datas.STRING, //phone2
                    Datas.STRING, //fax
                    Datas.STRING, //notes
                    Datas.BOOLEAN, //visible
                    Datas.TIMESTAMP, //curdate
                    Datas.DOUBLE, //curdebt
                    Datas.IMAGE, //image
                    Datas.BOOLEAN, //isvip
                    Datas.DOUBLE, //discount
                    Datas.TIMESTAMP //memodate
                },
                new Formats[]{
                    Formats.STRING, //id
                    Formats.STRING, //searchkey
                    Formats.STRING, //taxid
                    Formats.STRING, //name
                    Formats.STRING, //taxcat
                    Formats.STRING, //card
                    Formats.CURRENCY, //maxdebt
                    Formats.STRING, //add
                    Formats.STRING, //add2
                    Formats.STRING, //postal
                    Formats.STRING, //city
                    Formats.STRING, //region
                    Formats.STRING, //cntry
                    Formats.STRING, //fname
                    Formats.STRING, //lname
                    Formats.STRING, //email
                    Formats.STRING, //phone
                    Formats.STRING, //phone2
                    Formats.STRING, //fax
                    Formats.STRING, //notes
                    Formats.BOOLEAN, //visible
                    Formats.TIMESTAMP, //curdate
                    Formats.CURRENCY, //curdebt
                    Formats.NULL, //image
                    Formats.BOOLEAN, //isvip
                    Formats.DOUBLE, //discount
                    Formats.TIMESTAMP //memodate
                },
                new int[]{0}
        );
        return tcustomers;
    }

    // <editor-fold defaultstate="collapsed" desc="Voucher">
    public final PreparedSentence getVoucherNumber() {
        return new PreparedSentence(s,
                "SELECT SUBSTRING(MAX(VOUCHER_NUMBER),10,3) AS LAST_NUMBER FROM vouchers "
                + "WHERE SUBSTRING(VOUCHER_NUMBER,1,8) = ?",
                SerializerWriteString.INSTANCE, (SerializerRead<String>) (DataRead dr) -> dr.getString(1));
    }

    public final VoucherInfo getVoucherInfo(String id) throws BasicException {
        return (VoucherInfo) new PreparedSentence(s,
                "SELECT vouchers.ID, VOUCHER_NUMBER, CUSTOMER, "
                + "customers.NAME, AMOUNT, STATUS "
                + "FROM vouchers "
                + "JOIN customers ON customers.id = vouchers.CUSTOMER "
                + "WHERE STATUS='A' AND vouchers.ID=?" //"WHERE STATUS='A' "                         
                ,
                 SerializerWriteString.INSTANCE,
                VoucherInfo.getSerializerRead()).<VoucherInfo>find(id);
    }

    public final VoucherInfo getVoucherInfoAll(String id) throws BasicException {
        return (VoucherInfo) new PreparedSentence(s,
                "SELECT vouchers.ID, VOUCHER_NUMBER, CUSTOMER, "
                + "customers.NAME, AMOUNT, STATUS "
                + "FROM vouchers "
                + "JOIN customers ON customers.id = vouchers.CUSTOMER  "
                + "WHERE vouchers.ID=?",
                SerializerWriteString.INSTANCE,
                VoucherInfo.getSerializerRead()).<VoucherInfo>find(id);
    }
    // </editor-fold>

    protected static class CustomerInfoRead implements SerializerRead<CustomerInfo> {

        @Override
        public CustomerInfo readValues(DataRead dr) throws BasicException {
            CustomerInfo c = new CustomerInfo(dr.getString(1));
            c.setTaxid(dr.getString(2));
            c.setSearchkey(dr.getString(3));
            c.setName(dr.getString(4));
            c.setPostal(dr.getString(5));
            c.setPhone(dr.getString(6));
            c.setEmail(dr.getString(7));
            c.setImage(ImageUtils.readImage(dr.getBytes(8)));
            return c;
        }
    }

    Datas[] customerData = new Datas[]{
        Datas.STRING, //id
        Datas.STRING, //searchkey
        Datas.STRING, //taxid
        Datas.STRING, //name
        Datas.STRING, //taxcat
        Datas.STRING, //card
        Datas.DOUBLE, //maxdebt
        Datas.STRING, //add
        Datas.STRING, //add2
        Datas.STRING, //postal
        Datas.STRING, //city
        Datas.STRING, //region
        Datas.STRING, //cntry
        Datas.STRING, //fname
        Datas.STRING, //lname
        Datas.STRING, //email
        Datas.STRING, //phone
        Datas.STRING, //phone2
        Datas.STRING, //fax
        Datas.STRING, //notes
        Datas.BOOLEAN, //visible
        Datas.TIMESTAMP, //curdate
        Datas.DOUBLE, //curdebt
        Datas.IMAGE, //image
        Datas.BOOLEAN, //isvip
        Datas.DOUBLE, //discount
        Datas.TIMESTAMP //memodate
    };

    private SentenceExec customerSentenceExecUpdate() {
        SentenceExec sentupdate = new PreparedSentenceExec(this.s,
                "update customers set ID = ?, SEARCHKEY = ?, TAXID = ?, NAME = ?, TAXCATEGORY = ?, CARD = ?, MAXDEBT = ?, ADDRESS = ?, ADDRESS2 = ?, POSTAL = ?, CITY = ?, REGION = ?, COUNTRY = ?, FIRSTNAME = ?, LASTNAME = ?, EMAIL = ?, PHONE = ?, PHONE2 = ?, FAX = ?, NOTES = ?, VISIBLE = ?, CURDATE = ?, CURDEBT = ?, IMAGE = ?, ISVIP = ?, DISCOUNT = ?, MEMODATE = ? where ID = ?",
                customerData, new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 0});

        return sentupdate;
    }

    private SentenceExec customerSentenceExecDelete() {
        Datas[] resourcedata = new Datas[]{Datas.STRING};
        SentenceExec sentdelete = new PreparedSentenceExec(this.s,
                "DELETE FROM customers WHERE ID = ?",
                resourcedata, new int[]{0});

        return sentdelete;
    }

    private SentenceExec customerSentenceExecInsert() {
        SentenceExec sentinsert = new PreparedSentenceExec(this.s,
                "insert into customers (ID, SEARCHKEY, TAXID, NAME, TAXCATEGORY, CARD, MAXDEBT, ADDRESS, ADDRESS2, POSTAL, CITY, REGION, COUNTRY, FIRSTNAME, LASTNAME, EMAIL, PHONE, PHONE2, FAX, NOTES, VISIBLE, CURDATE, CURDEBT, IMAGE, ISVIP, DISCOUNT, MEMODATE) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
                customerData, new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26});

        return sentinsert;
    }

    public SaveProvider<Object[]> getCustomerSaveProvider() {
        return new DefaultSaveProvider(
                customerSentenceExecUpdate(),
                customerSentenceExecInsert(),
                customerSentenceExecDelete());
    }

}
