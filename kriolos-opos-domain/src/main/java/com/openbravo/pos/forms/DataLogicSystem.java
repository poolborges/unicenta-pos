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
package com.openbravo.pos.forms;

import com.openbravo.basic.BasicException;
import com.openbravo.data.loader.*;
import com.openbravo.format.Formats;
import com.openbravo.pos.admin.ResourceInfo;
import com.openbravo.pos.forms.AppLocal;
import com.openbravo.pos.forms.AppUser;
import com.openbravo.pos.forms.BeanFactoryDataSingle;
import com.openbravo.pos.util.ThumbNailBuilder;
import com.openbravo.pos.voucher.VoucherInfo;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

/**
 *
 * @author JG uniCenta
 */
public class DataLogicSystem extends BeanFactoryDataSingle {

    private final static Logger LOGGER = Logger.getLogger(ImageUtils.class.getName());

    private Session session;
    private String m_dbVersion;
    private TableDefinition<ResourceInfo> m_tresources;
    
    public DataLogicSystem() {
    }

    @Override
    public void init(Session session) {
        this.session = session;
        this.m_dbVersion = this.session.DB.getName();
        
        m_tresources = new TableDefinition(session,
            "resources",
            new String[]{
                "ID", "NAME", "RESTYPE", "CONTENT"},
            new String[]{
                "ID",
                AppLocal.getIntString("label.name"),
                AppLocal.getIntString("label.type"),
                "CONTENT"},
            new Datas[]{
                Datas.STRING, Datas.STRING, Datas.INT, Datas.BYTES},
            new Formats[]{
                Formats.STRING, Formats.STRING, Formats.INT, Formats.NULL},
            new int[]{0}
    );

//// <editor-fold defaultstate="collapsed" desc="START OF PRODUCT">
// </editor-fold>
//// <editor-fold defaultstate="collapsed" desc="START OF CUSTOMER">
//// </editor-fold>   

//// <editor-fold defaultstate="collapsed" desc="START OF CASH">
//// </editor-fold>   
//// <editor-fold defaultstate="collapsed" desc="START OF LOCATION AND PLACES">
//// </editor-fold>   


    }


    public final TableDefinition<ResourceInfo> getTableResources() {
        return m_tresources;
    }

    public String getDBVersion() {
        return m_dbVersion;
    }

    public final String findVersion() throws BasicException {
        final SentenceFind<String> m_version = new PreparedSentence<String, String>(this.session,
                "SELECT VERSION FROM applications WHERE ID = ?",
                SerializerWriteString.INSTANCE, SerializerReadString.INSTANCE);

        return m_version.find(AppLocal.APP_ID);
    }

    public final String getUser() throws BasicException {
        return ("");
    }

    public final void execDummy() throws BasicException {

        SentenceExec m_dummy = new StaticSentence(this.session, "SELECT * FROM people WHERE 1 = 0");
        m_dummy.exec();
    }

//// <editor-fold defaultstate="collapsed" desc="START OF PEOPLE">
  
    /**
     *
     * @return @throws BasicException
     */
    public final List<AppUser> listPeopleVisible() throws BasicException {
        final SentenceList m_peoplevisible = new StaticSentence(this.session,
                "SELECT ID, NAME, APPPASSWORD, CARD, ROLE "
                + "FROM people "
                + "WHERE VISIBLE = " + this.session.DB.TRUE() + " ORDER BY NAME",
                new AppuserReader());

        return m_peoplevisible.list();
    }

    /**
     *
     * @param role
     * @return
     * @throws BasicException
     */
    public final List<String> getPermissions(String role) throws BasicException {
        final SentenceList<String> m_permissionlist = new StaticSentence(this.session,
                "SELECT PERMISSIONS FROM permissions WHERE ID = ?",
                SerializerWriteString.INSTANCE,
                new SerializerReadBasic(new Datas[]{Datas.STRING}));

        return m_permissionlist.list(role);
    }

    /**
     *
     * @param card
     * @return
     * @throws BasicException
     */
    public final AppUser findPeopleByCard(String card) throws BasicException {

        final SentenceFind<AppUser> m_peoplebycard = new PreparedSentence<String, AppUser>(this.session,
                "SELECT ID, NAME, APPPASSWORD, CARD, ROLE, IMAGE "
                + "FROM people "
                + "WHERE CARD = ? AND VISIBLE = " + this.session.DB.TRUE(),
                SerializerWriteString.INSTANCE,
                new AppuserReader());
        return m_peoplebycard.find(card);
    }

    /**
     *
     * @param sRole
     * @return
     */
    public final String findRolePermissions(String sRole) {

        final SentenceFind m_rolepermissions = new PreparedSentence(this.session,
                "SELECT PERMISSIONS FROM roles WHERE ID = ?",
                SerializerWriteString.INSTANCE,
                SerializerReadBytes.INSTANCE);

        String content = new String();
        try {
            content = Formats.BYTEA.formatValue((byte[])m_rolepermissions.find(sRole));
        } catch (BasicException e) {
            LOGGER.log(Level.SEVERE, "Exception on format permissions for role: " + sRole, e);
        }
        return content;
    }

    /**
     *
     * @param userdata
     * @throws BasicException
     */
    public final void execChangePassword(Object[] userdata) throws BasicException {

        final SentenceExec m_changepassword = new StaticSentence(this.session,
                "UPDATE people SET APPPASSWORD = ? WHERE ID = ?",
                new SerializerWriteBasic(new Datas[]{Datas.STRING, Datas.STRING}));

        m_changepassword.exec(userdata);
    }
    
    /**
     *
     * @param permissions
     * @throws BasicException
     */
    public final void execUpdatePermissions(Object[] permissions) throws BasicException {
        final SentenceExec m_updatepermissions = new StaticSentence(this.session,
                "INSERT INTO permissions (ID, PERMISSIONS) "
                + "VALUES (?, ?)",
                new SerializerWriteBasic(new Datas[]{
            Datas.STRING,
            Datas.STRING}));
        m_updatepermissions.exec(permissions);
    }

//// </editor-fold> 
    
//// <editor-fold defaultstate="collapsed" desc="START OF RESOURCE">
    private byte[] getResource(String name) {

        SentenceFind m_resourcebytes = new PreparedSentence(this.session,
                "SELECT CONTENT FROM resources WHERE NAME = ?",
                SerializerWriteString.INSTANCE,
                SerializerReadBytes.INSTANCE);

        byte[] resource;

        try {
            resource = (byte[]) m_resourcebytes.find(name);
            if (resource != null) {
            } else {
                LOGGER.log(Level.WARNING, "Resource NOT found name: {0}", name);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Exception while get resource name: " + name, e);
            resource = null;
        }

        return resource;
    }

    /**
     *
     * @param name
     * @param type
     * @param data
     */
    public final void setResource(String name, int type, byte[] data) {

        Datas[] resourcedata = new Datas[]{Datas.STRING, Datas.STRING, Datas.INT, Datas.BYTES};
        Object[] value = new Object[]{UUID.randomUUID().toString(), name, type, data};

        SentenceExec m_resourcebytesinsert = new PreparedSentenceExec(this.session,
                "INSERT INTO resources(ID, NAME, RESTYPE, CONTENT) VALUES (?, ?, ?, ?)",
                resourcedata, new int[]{0, 1, 2, 3});

        SentenceExec m_resourcebytesupdate = new PreparedSentenceExec(this.session,
                "UPDATE resources SET NAME = ?, RESTYPE = ?, CONTENT = ? WHERE NAME = ?",
                resourcedata, new int[]{1, 2, 3, 1});

        try {
            if (m_resourcebytesupdate.exec(value) != 0) {
                LOGGER.log(Level.INFO, "Resource update: " + name);
            } else {
                m_resourcebytesinsert.exec(value);
                LOGGER.log(Level.INFO, "Resource insert: " + name);
            }
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Exception while save resource name: " + name, ex);
        }
    }

    /**
     *
     * @param sName
     * @param data
     */
    public final void setResourceAsBinary(String sName, byte[] data) {
        setResource(sName, 2, data);
    }

    /**
     *
     * @param sName
     * @return
     */
    public final byte[] getResourceAsBinary(String sName) {
        return getResource(sName);
    }

    /**
     *
     * @param sName
     * @return
     */
    public final String getResourceAsText(String sName) {
        return Formats.BYTEA.formatValue(getResource(sName));
    }

    /**
     *
     * @param sName
     * @return
     */
    public final String getResourceAsXML(String sName) {
        return Formats.BYTEA.formatValue(getResource(sName));
    }

    /**
     *
     * @param sName
     * @return
     */
    public final BufferedImage getResourceAsImage(String sName) {
        
        LOGGER.log(Level.INFO, "Get image resource id: " + sName);
        BufferedImage img = null;
        try {
            InputStream strem = new ByteArrayInputStream(getResource(sName));
            img = ImageIO.read(strem);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Exception on get resource: " + sName, e);
        }
        return img;
    }

    /**
     *
     * @param sName
     * @param p
     */
    public final void setResourceAsProperties(String sName, Properties p) {
        if (p == null) {
            setResource(sName, 0, null); // texto
        } else {
            try {
                ByteArrayOutputStream o = new ByteArrayOutputStream();
                p.storeToXML(o, AppLocal.APP_NAME, "UTF8");
                setResource(sName, 0, o.toByteArray()); // El texto de las propiedades   
            } catch (IOException e) { // no deberia pasar nunca
                LOGGER.log(Level.SEVERE, "Exception on set resource: " + sName, e);
            }
        }
    }

    /**
     *
     * @param sName
     * @return
     */
    public final Properties getResourceAsProperties(String sName) {

        Properties p = new Properties();
        try {
            byte[] xml = getResource(sName);
            if (xml != null) {
                p.loadFromXML(new ByteArrayInputStream(xml));
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Exception on get resource as Properties, name: " + sName, e);
        }
        return p;
    }

//// </editor-fold> 
//// <editor-fold defaultstate="collapsed" desc="START OF CASH">    
    /**
     *
     * @param host
     * @return
     * @throws BasicException
     */
    public final int getSequenceCash(String host) throws BasicException {
        final SentenceFind m_sequencecash = new PreparedSentence(this.session,
                "SELECT MAX(HOSTSEQUENCE) FROM closedcash WHERE HOST = ?",
                SerializerWriteString.INSTANCE,
                SerializerReadInteger.INSTANCE);

        Integer i = (Integer) m_sequencecash.find(host);
        return (i == null) ? 1 : i;
    }

    /**
     *
     * @param sActiveCashIndex
     * @return
     * @throws BasicException
     */
    public final Object[] findActiveCash(String sActiveCashIndex) throws BasicException {

        final SentenceFind m_activecash = new PreparedSentence(this.session,
                "SELECT HOST, HOSTSEQUENCE, DATESTART, DATEEND, NOSALES "
                + "FROM closedcash WHERE MONEY = ?",
                SerializerWriteString.INSTANCE,
                new SerializerReadBasic(new Datas[]{
            Datas.STRING,
            Datas.INT,
            Datas.TIMESTAMP,
            Datas.TIMESTAMP,
            Datas.INT}));

        return (Object[]) m_activecash.find(sActiveCashIndex);
    }

    /**
     *
     * @param sClosedCashIndex
     * @return
     * @throws BasicException
     */
    public final Object[] findClosedCash(String sClosedCashIndex) throws BasicException {

        final SentenceFind m_closedcash = new StaticSentence(this.session,
                "SELECT HOST, HOSTSEQUENCE, DATESTART, DATEEND, NOSALES "
                + "FROM closedcash WHERE HOSTSEQUENCE = ?",
                SerializerWriteString.INSTANCE,
                new SerializerReadBasic(new Datas[]{
            Datas.STRING,
            Datas.INT,
            Datas.TIMESTAMP,
            Datas.TIMESTAMP,
            Datas.INT}));

        return (Object[]) m_closedcash.find(sClosedCashIndex);
    }

    /**
     *
     * @param cash
     * @throws BasicException
     */
    public final void execInsertCash(Object[] cash) throws BasicException {
        final SentenceExec m_insertcash = new StaticSentence(this.session,
                "INSERT INTO closedcash(MONEY, HOST, HOSTSEQUENCE, DATESTART, DATEEND) "
                + "VALUES (?, ?, ?, ?, ?)",
                new SerializerWriteBasic(new Datas[]{
            Datas.STRING,
            Datas.STRING,
            Datas.INT,
            Datas.TIMESTAMP,
            Datas.TIMESTAMP}));

        m_insertcash.exec(cash);
    }

    /**
     *
     * @param drawer
     * @throws BasicException
     */
    public final void execDrawerOpened(Object[] drawer) throws BasicException {
        final SentenceExec m_draweropened = new StaticSentence(this.session,
                "INSERT INTO draweropened ( NAME, TICKETID) "
                + "VALUES (?, ?)",
                new SerializerWriteBasic(new Datas[]{
            Datas.STRING,
            Datas.STRING}));
        m_draweropened.exec(drawer);
    }
//// </editor-fold> 

    
//// <editor-fold defaultstate="collapsed" desc="START OF LINEREMOVED">
     
    /**
     *
     * @param line
     */
    public final void execLineRemoved(Object[] line) {

        final SentenceExec m_lineremoved = new StaticSentence(this.session,
                "INSERT INTO lineremoved (NAME, TICKETID, PRODUCTID, PRODUCTNAME, UNITS) "
                + "VALUES (?, ?, ?, ?, ?)",
                new SerializerWriteBasic(new Datas[]{
            Datas.STRING, Datas.STRING,
            Datas.STRING, Datas.STRING,
            Datas.DOUBLE
        }));

        try {
            m_lineremoved.exec(line);
        } catch (BasicException e) {
            LOGGER.log(Level.SEVERE, "Exception on execute line removed: ", e);
        }
    }

    /**
     *
     * @param ticket
     */
    public final void execTicketRemoved(Object[] ticket) {
        final SentenceExec m_ticketremoved = new StaticSentence(this.session,
                "INSERT INTO lineremoved (NAME, TICKETID, PRODUCTNAME, UNITS) "
                + "VALUES (?, ?, ?, ?)",
                new SerializerWriteBasic(new Datas[]{
            Datas.STRING, Datas.STRING,
            Datas.STRING, Datas.DOUBLE
        }));
        try {
            m_ticketremoved.exec(ticket);
        } catch (BasicException e) {
            LOGGER.log(Level.SEVERE, "Exception on execute ticket removed: ", e);
        }
    }
//// </editor-fold>  
    
    /**
     *
     * @param iLocation
     * @return
     * @throws BasicException
     */
    public final String findLocationName(String iLocation) throws BasicException {
        final SentenceFind m_locationfind = new StaticSentence(this.session,
                "SELECT NAME FROM locations WHERE ID = ?",
                SerializerWriteString.INSTANCE,
                SerializerReadString.INSTANCE);
        return (String) m_locationfind.find(iLocation);
    }

//// <editor-fold defaultstate="collapsed" desc="START OF CVIMPORT">     

    /**
     *
     * @param csv
     * @throws BasicException
     */
    public final void execCSVStockUpdate(Object[] csv) throws BasicException {
        //  Push Product Quantity Update into CSVImport table      
        final SentenceExec m_insertStockUpdateCSVEntry = new StaticSentence(this.session,
                "INSERT INTO csvimport ( "
                + "ID, ROWNUMBER, CSVERROR, REFERENCE, CODE, PRICEBUY ) "
                + "VALUES (?, ?, ?, ?, ?, ?)",
                new SerializerWriteBasic(new Datas[]{
            Datas.STRING,
            Datas.STRING,
            Datas.STRING,
            Datas.STRING,
            Datas.STRING,
            Datas.DOUBLE
        }));
        m_insertStockUpdateCSVEntry.exec(csv);

    }

    /**
     *
     * @param csv
     * @throws BasicException
     */
    public final void execAddCSVEntry(Object[] csv) throws BasicException {
        //  Push Products into CSVImport table 
        final SentenceExec m_insertCSVEntry = new StaticSentence(this.session,
                "INSERT INTO csvimport ( "
                + "ID, ROWNUMBER, CSVERROR, REFERENCE, "
                + "CODE, NAME, PRICEBUY, PRICESELL, "
                + "PREVIOUSBUY, PREVIOUSSELL, CATEGORY, TAX) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                new SerializerWriteBasic(new Datas[]{
            Datas.STRING,
            Datas.STRING,
            Datas.STRING,
            Datas.STRING,
            Datas.STRING,
            Datas.STRING,
            Datas.DOUBLE,
            Datas.DOUBLE,
            Datas.DOUBLE,
            Datas.DOUBLE,
            Datas.STRING,
            Datas.STRING
        }));
        m_insertCSVEntry.exec(csv);

    }

    /**
     *
     * @param csv
     * @throws BasicException
     */
    public final void execCustomerAddCSVEntry(Object[] csv) throws BasicException {
        //  Push Customers into CSVImport table      
        final SentenceExec m_insertCustomerCSVEntry = new StaticSentence(this.session,
                "INSERT INTO csvimport ( "
                + "ID, ROWNUMBER, CSVERROR, SEARCHKEY, NAME) "
                + "VALUES (?, ?, ?, ?, ?)",
                new SerializerWriteBasic(new Datas[]{
            Datas.STRING,
            Datas.STRING,
            Datas.STRING,
            Datas.STRING,
            Datas.STRING
        }));
        m_insertCustomerCSVEntry.exec(csv);

    }
//// </editor-fold>  
    
// This is used by CSVimport to detect what type of product insert we are looking at, or what error occured
    /**
     *
     * @param myProduct Object[]. The array is [0]
     * @return
     * @throws BasicException
     */
    public final String getProductRecordType(Object[] myProduct) throws BasicException {

        final SentenceFind m_getProductAllFields;
        final SentenceFind m_getProductRefAndCode;
        final SentenceFind m_getProductRefAndName;
        final SentenceFind m_getProductCodeAndName;
        final SentenceFind m_getProductByReference;
        final SentenceFind m_getProductByCode;
        final SentenceFind m_getProductByName;

        m_getProductAllFields = new PreparedSentence(this.session,
                "SELECT ID FROM products WHERE REFERENCE=? AND CODE=? AND NAME=? ",
                new SerializerWriteBasic(new Datas[]{Datas.STRING, Datas.STRING, Datas.STRING}),
                new ProductIdRead()
        );

        m_getProductRefAndCode = new PreparedSentence(this.session,
                "SELECT ID FROM products WHERE REFERENCE=? AND CODE=?",
                new SerializerWriteBasic(new Datas[]{Datas.STRING, Datas.STRING}),
                new ProductIdRead()
        );

        m_getProductRefAndName = new PreparedSentence(this.session,
                "SELECT ID FROM products WHERE REFERENCE=? AND NAME=? ",
                new SerializerWriteBasic(new Datas[]{Datas.STRING, Datas.STRING}),
                new ProductIdRead()
        );

        m_getProductCodeAndName = new PreparedSentence(this.session,
                "SELECT ID FROM products WHERE CODE=? AND NAME=? ",
                new SerializerWriteBasic(new Datas[]{Datas.STRING, Datas.STRING}),
                new ProductIdRead()
        );

        m_getProductByReference = new PreparedSentence(this.session,
                "SELECT ID FROM products WHERE REFERENCE=? ",
                SerializerWriteString.INSTANCE,
                new ProductIdRead()
        );

        m_getProductByCode = new PreparedSentence(this.session,
                "SELECT ID FROM products WHERE CODE=? ",
                SerializerWriteString.INSTANCE,
                new ProductIdRead()
        );

        m_getProductByName = new PreparedSentence(this.session,
                "SELECT ID FROM products WHERE NAME=? ",
                SerializerWriteString.INSTANCE,
                new ProductIdRead()
        );

        // check if the product exist with all the details, if so return product ID
        if (m_getProductAllFields.find(myProduct) != null) {
            return m_getProductAllFields.find(myProduct).toString();
        }
        // check if the product exists with matching reference and code, but a different name
        if (m_getProductRefAndCode.find(myProduct[0], myProduct[1]) != null) {
            return "Name change";
        }

        if (m_getProductRefAndName.find(myProduct[0], myProduct[2]) != null) {
            return "Barcode change";
        }

        if (m_getProductCodeAndName.find(myProduct[1], myProduct[2]) != null) {
            return "Reference change";
        }

        if (m_getProductByReference.find(myProduct[0]) != null) {
            return "Duplicate Reference found.";
        }

        if (m_getProductByCode.find(myProduct[1]) != null) {
            return "Duplicate Barcode found.";
        }

        if (m_getProductByName.find(myProduct[2]) != null) {
            return "Duplicate Description found.";
        }

        return "new";
    }

    /**
     *
     * @param myCustomer
     * @return
     * @throws BasicException
     */
    public final String getCustomerRecordType(Object[] myCustomer) throws BasicException {

        final SerializerRead customerIdRead = (DataRead dr) -> (dr.getString(1));

        final SentenceFind m_getCustomerAllFields;
        final SentenceFind m_getCustomerSearchKeyAndName;
        final SentenceFind m_getCustomerBySearchKey;
        final SentenceFind m_getCustomerByName;

        // duplicate this for now as will extend in future release 
        m_getCustomerAllFields = new PreparedSentence(this.session,
                "SELECT ID FROM customers WHERE SEARCHKEY=? AND NAME=? ",
                new SerializerWriteBasic(new Datas[]{Datas.STRING, Datas.STRING}),
                customerIdRead
        );

        m_getCustomerSearchKeyAndName = new PreparedSentence(this.session,
                "SELECT ID FROM customers WHERE SEARCHKEY=? AND NAME=? ",
                new SerializerWriteBasic(new Datas[]{Datas.STRING, Datas.STRING}),
                customerIdRead
        );

        m_getCustomerBySearchKey = new PreparedSentence(this.session,
                "SELECT ID FROM customers WHERE SEARCHKEY=? ",
                SerializerWriteString.INSTANCE,
                customerIdRead
        );

        m_getCustomerByName = new PreparedSentence(this.session,
                "SELECT ID FROM customers WHERE NAME=? ",
                SerializerWriteString.INSTANCE,
                customerIdRead
        );

        if (m_getCustomerAllFields.find(myCustomer) != null) {
            return m_getCustomerAllFields.find(myCustomer).toString();
        }

        if (m_getCustomerSearchKeyAndName.find(myCustomer[0], myCustomer[1]) != null) {
            return "reference error";
        }

        if (m_getCustomerBySearchKey.find(myCustomer[0]) != null) {
            return "Duplicate Search Key found.";
        }

        if (m_getCustomerByName.find(myCustomer[1]) != null) {
            return "Duplicate Name found.";
        }

        return "new";
    }

    public final void updatePlaces(int x, int y, String id) throws BasicException {
        final SentenceExec m_updatePlaces = new StaticSentence(this.session,
                "UPDATE PLACES SET X = ?, Y = ? WHERE ID = ?",
                new SerializerWriteBasic(new Datas[]{Datas.INT, Datas.INT, Datas.STRING}));
        m_updatePlaces.exec(new Object[]{x, y, id});
    }

    public final List<VoucherInfo> getVouchersActiveList() throws BasicException {
        final SentenceList<VoucherInfo>  m_voucherlist = new StaticSentence(this.session,
                "SELECT id, voucher_number, customer, amount, status FROM vouchers WHERE status LIKE 'A'",
                SerializerWriteString.INSTANCE,
                VoucherInfo.getSerializerRead());

        return m_voucherlist.list();
    }

//// <editor-fold defaultstate="collapsed" desc="START OF ORDER">   
    
    /**
     * 
     * @param orderId
     * @param qty
     * @param details
     * @param attributes
     * @param notes
     * @param ticketId
     * @param ordertime
     * @param displayId
     * @param auxiliary
     * @param completetime
     * @throws BasicException 
     */
    public final void addOrder(String orderId, Integer qty,
            String details, String attributes, String notes, String ticketId,
            String ordertime, Integer displayId, String auxiliary, String completetime
    ) throws BasicException {

        if (ordertime == null) {
            ordertime = Long.toString(new Date().getTime());
        }

        final SentenceExec m_addOrder = new StaticSentence(this.session,
                "INSERT INTO orders (ORDERID, QTY, DETAILS, ATTRIBUTES, "
                + "NOTES, TICKETID, ORDERTIME, DISPLAYID, AUXILIARY, "
                + "COMPLETETIME) "
                + "VALUES (?, ?, ?, ?, ?, "
                + "?, ?, ?, ?, ? ) ",
                new SerializerWriteBasic(new Datas[]{
            Datas.STRING, // OrderId
            Datas.INT,    // Qty
            Datas.STRING, // Details
            Datas.STRING, // Attributes
            Datas.STRING, // Notes
            Datas.STRING, // TicketId
            Datas.TIMESTAMP, // OrderTime
            Datas.INT,    // DisplayId
            Datas.INT,    // Auxiliary
            Datas.TIMESTAMP  // CompleteTime
        }));
        m_addOrder.exec(new Object[]{orderId, qty, details, attributes, notes, ticketId,
                ordertime, displayId, auxiliary, completetime});
    }

    /**
     * 
     * @param orderId
     * @param qty
     * @param details
     * @param attributes
     * @param notes
     * @param ticketId
     * @param ordertime
     * @param displayId
     * @param auxiliary
     * @param completetime
     * @throws BasicException 
     */
    public final void updateOrder(String orderId, Integer qty,
            String details, String attributes, String notes, String ticketId,
            String ordertime, Integer displayId, String auxiliary, String completetime
    ) throws BasicException {

        final SentenceExec m_updateOrder = new StaticSentence(this.session,
                "UPDATE orders SET "
                + "ORDERID = ?, "
                + "QTY = ?, "
                + "DETAILS = ?, "
                + "ATTRIBUTES = ?, "
                + "NOTES = ?, "
                + "TICKETID = ?, "
                + "ORDERTIME = ?, "
                + "DISPLAYID = ?, "
                + "AUXILIARY = ?, "
                + "COMPLETETIME = ? "
                + "WHERE ORDERID = ? ",
                new SerializerWriteBasic(new Datas[]{
            Datas.STRING, // OrderId
            Datas.INT, // Qty
            Datas.STRING, // Details
            Datas.STRING, // Attributes
            Datas.STRING, // Notes
            Datas.STRING, // TicketId
            Datas.STRING, // OrderTime
            Datas.INT, // DisplayId
            Datas.INT, // Auxiliary
            Datas.STRING // CompleteTime
        }));
        m_updateOrder.exec(new Object[]{orderId, qty, details, attributes, notes, ticketId,
                ordertime, displayId, auxiliary, completetime});
    }

    /**
     * Delete Order
     * @param orderId
     * @throws BasicException 
     */
    public void deleteOrder(String orderId) throws BasicException {
        final SentenceExec m_deleteOrder = new StaticSentence(this.session,
                "DELETE FROM orders WHERE ORDERID = ?",
                SerializerWriteString.INSTANCE);
        m_deleteOrder.exec(orderId);
    }
//// </editor-fold> 
    
    private final static class AppuserReader implements SerializerRead<AppUser> {

        final ThumbNailBuilder defaultUserTN = new ThumbNailBuilder(32, 32, "com/openbravo/images/user.png");

        @Override
        public AppUser readValues(DataRead dr) throws BasicException {

            return new AppUser(
                    dr.getString(1),
                    dr.getString(2),
                    dr.getString(3),
                    dr.getString(4),
                    dr.getString(5),
                    //new ImageIcon(tnb.getThumbNail(ImageUtils.readImage(dr.getBytes(6)))));
                    new ImageIcon(defaultUserTN.getThumbNail()));
        }
    }

    private final static class ProductIdRead implements SerializerRead<String> {

        @Override
        public String readValues(DataRead dr) throws BasicException {
            return dr.getString(1);
        }
    };
}
