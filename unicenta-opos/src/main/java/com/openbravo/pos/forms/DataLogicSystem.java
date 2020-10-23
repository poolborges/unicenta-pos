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
package com.openbravo.pos.forms;

import com.openbravo.basic.BasicException;
import com.openbravo.data.loader.*;
import com.openbravo.format.Formats;
import com.openbravo.pos.util.ThumbNailBuilder;
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

    protected String m_sInitScript;
    private SentenceFind m_version;
    private SentenceExec m_dummy;
    private String m_dbVersion;
    protected SentenceList m_peoplevisible;
    protected SentenceFind m_peoplebycard;
    protected SerializerRead peopleread;
    protected SentenceList m_permissionlist;
    protected SerializerRead productIdRead;
    protected SerializerRead customerIdRead;

    private SentenceFind m_rolepermissions;
    private SentenceExec m_changepassword;
    private SentenceFind m_locationfind;
    private SentenceExec m_insertCSVEntry;
    private SentenceExec m_insertStockUpdateEntry;

    private SentenceFind m_getProductAllFields;
    private SentenceFind m_getProductRefAndCode;
    private SentenceFind m_getProductRefAndName;
    private SentenceFind m_getProductCodeAndName;
    private SentenceFind m_getProductByReference;
    private SentenceFind m_getProductByCode;
    private SentenceFind m_getProductByName;

    private SentenceExec m_insertCustomerCSVEntry;
    private SentenceFind m_getCustomerAllFields;
    private SentenceFind m_getCustomerSearchKeyAndName;
    private SentenceFind m_getCustomerBySearchKey;
    private SentenceFind m_getCustomerByName;

    private SentenceFind m_resourcebytes;
    private SentenceExec m_resourcebytesinsert;
    private SentenceExec m_resourcebytesupdate;

    protected SentenceFind m_sequencecash;
    protected SentenceFind m_activecash;
    protected SentenceFind m_closedcash;
    protected SentenceExec m_insertcash;
    protected SentenceExec m_draweropened;
    protected SentenceExec m_updatepermissions;
    protected SentenceExec m_lineremoved;
    protected SentenceExec m_ticketremoved;

    private String SQL;
    private Map<String, byte[]> resourcescache;

    private SentenceList m_voucherlist;

    protected SentenceExec m_addOrder;
    protected SentenceExec m_updateOrder;
    protected SentenceExec m_deleteOrder;

    private SentenceExec m_updatePlaces;

    /**
     * Creates a new instance of DataLogicSystem
     */
    public DataLogicSystem() {
    }

    /**
     *
     * @param s
     */
    @Override
    public void init(Session s) {

        m_sInitScript = "/com/openbravo/pos/scripts/" + s.DB.getName();
        m_dbVersion = s.DB.getName();

        m_version = new PreparedSentence(s, "SELECT VERSION FROM applications WHERE ID = ?",
                SerializerWriteString.INSTANCE, SerializerReadString.INSTANCE);

        m_dummy = new StaticSentence(s, "SELECT * FROM people WHERE 1 = 0");

        final ThumbNailBuilder tnb = new ThumbNailBuilder(32, 32, "com/openbravo/images/user.png");

        peopleread = new SerializerRead() {
            @Override
            public Object readValues(DataRead dr) throws BasicException {
                return new AppUser(
                        dr.getString(1),
                        dr.getString(2),
                        dr.getString(3),
                        dr.getString(4),
                        dr.getString(5),
                        //new ImageIcon(tnb.getThumbNail(ImageUtils.readImage(dr.getBytes(6)))));
                        new ImageIcon(tnb.getThumbNail()));
            }
        };

//// <editor-fold defaultstate="collapsed" desc="START OF PRODUCT">
        productIdRead = new SerializerRead() {
            public Object readValues(DataRead dr) throws BasicException {
                return dr.getString(1);
            }
        };

        m_getProductAllFields = new PreparedSentence(s,
                "SELECT ID FROM products WHERE REFERENCE=? AND CODE=? AND NAME=? ",
                new SerializerWriteBasic(new Datas[]{Datas.STRING, Datas.STRING, Datas.STRING}),
                productIdRead
        );

        m_getProductRefAndCode = new PreparedSentence(s,
                "SELECT ID FROM products WHERE REFERENCE=? AND CODE=?",
                new SerializerWriteBasic(new Datas[]{Datas.STRING, Datas.STRING}),
                productIdRead
        );

        m_getProductRefAndName = new PreparedSentence(s,
                "SELECT ID FROM products WHERE REFERENCE=? AND NAME=? ",
                new SerializerWriteBasic(new Datas[]{Datas.STRING, Datas.STRING}),
                productIdRead
        );

        m_getProductCodeAndName = new PreparedSentence(s,
                "SELECT ID FROM products WHERE CODE=? AND NAME=? ",
                new SerializerWriteBasic(new Datas[]{Datas.STRING, Datas.STRING}),
                productIdRead
        );

        m_getProductByReference = new PreparedSentence(s,
                "SELECT ID FROM products WHERE REFERENCE=? ",
                SerializerWriteString.INSTANCE //(Datas.STRING)
                ,
                 productIdRead
        );

        m_getProductByCode = new PreparedSentence(s,
                "SELECT ID FROM products WHERE CODE=? ",
                SerializerWriteString.INSTANCE //(Datas.STRING)
                //, new SerializerWriteBasic(Datas.STRING)
                ,
                 productIdRead
        );

        m_getProductByName = new PreparedSentence(s,
                "SELECT ID FROM products WHERE NAME=? ",
                SerializerWriteString.INSTANCE //(Datas.STRING)
                //, new SerializerWriteBasic(Datas.STRING)
                ,
                 productIdRead
        );

// </editor-fold>
        
//// <editor-fold defaultstate="collapsed" desc="START OF CUSTOMER">
        customerIdRead = (DataRead dr) -> (dr.getString(1));
        // duplicate this for now as will extend in future release 
        m_getCustomerAllFields = new PreparedSentence(s,
                "SELECT ID FROM customers WHERE SEARCHKEY=? AND NAME=? ",
                new SerializerWriteBasic(new Datas[]{
            Datas.STRING, Datas.STRING}),
                customerIdRead
        );

        m_getCustomerSearchKeyAndName = new PreparedSentence(s,
                "SELECT ID FROM customers WHERE SEARCHKEY=? AND NAME=? ",
                new SerializerWriteBasic(new Datas[]{
            Datas.STRING, Datas.STRING}),
                customerIdRead
        );

        m_getCustomerBySearchKey = new PreparedSentence(s,
                "SELECT ID FROM customers WHERE SEARCHKEY=? ",
                SerializerWriteString.INSTANCE,
                customerIdRead
        );

        m_getCustomerByName = new PreparedSentence(s,
                "SELECT ID FROM customers WHERE NAME=? ",
                SerializerWriteString.INSTANCE,
                customerIdRead
        );

//// </editor-fold>     
        m_peoplevisible = new StaticSentence(s,
                "SELECT ID, NAME, APPPASSWORD, CARD, ROLE, IMAGE FROM people WHERE VISIBLE = " + s.DB.TRUE() + " ORDER BY NAME",
                null,
                peopleread);

        m_peoplebycard = new PreparedSentence(s,
                "SELECT ID, NAME, APPPASSWORD, CARD, ROLE, IMAGE FROM people WHERE CARD = ? AND VISIBLE = " + s.DB.TRUE(),
                SerializerWriteString.INSTANCE,
                peopleread);

        m_resourcebytes = new PreparedSentence(s,
                "SELECT CONTENT FROM resources WHERE NAME = ?",
                SerializerWriteString.INSTANCE,
                SerializerReadBytes.INSTANCE);

        Datas[] resourcedata = new Datas[]{
            Datas.STRING, Datas.STRING,
            Datas.INT, Datas.BYTES};

        m_resourcebytesinsert = new PreparedSentence(s,
                "INSERT INTO resources(ID, NAME, RESTYPE, CONTENT) VALUES (?, ?, ?, ?)",
                new SerializerWriteBasic(resourcedata));

        m_resourcebytesupdate = new PreparedSentence(s,
                "UPDATE resources SET NAME = ?, RESTYPE = ?, CONTENT = ? WHERE NAME = ?",
                new SerializerWriteBasicExt(resourcedata, new int[]{1, 2, 3, 1}));

        m_rolepermissions = new PreparedSentence(s,
                "SELECT PERMISSIONS FROM roles WHERE ID = ?",
                SerializerWriteString.INSTANCE,
                SerializerReadBytes.INSTANCE);

        m_changepassword = new StaticSentence(s,
                "UPDATE people SET APPPASSWORD = ? WHERE ID = ?",
                new SerializerWriteBasic(new Datas[]{Datas.STRING, Datas.STRING}));

        m_sequencecash = new StaticSentence(s,
                "SELECT MAX(HOSTSEQUENCE) FROM closedcash WHERE HOST = ?",
                SerializerWriteString.INSTANCE,
                SerializerReadInteger.INSTANCE);

        m_activecash = new StaticSentence(s,
                "SELECT HOST, HOSTSEQUENCE, DATESTART, DATEEND, NOSALES FROM closedcash WHERE MONEY = ?",
                SerializerWriteString.INSTANCE,
                new SerializerReadBasic(new Datas[]{
            Datas.STRING,
            Datas.INT,
            Datas.TIMESTAMP,
            Datas.TIMESTAMP,
            Datas.INT}));

        m_closedcash = new StaticSentence(s,
                "SELECT HOST, HOSTSEQUENCE, DATESTART, DATEEND, NOSALES "
                + "FROM closedcash WHERE HOSTSEQUENCE = ?",
                SerializerWriteString.INSTANCE,
                new SerializerReadBasic(new Datas[]{
            Datas.STRING,
            Datas.INT,
            Datas.TIMESTAMP,
            Datas.TIMESTAMP,
            Datas.INT}));

        m_insertcash = new StaticSentence(s,
                "INSERT INTO closedcash(MONEY, HOST, HOSTSEQUENCE, DATESTART, DATEEND) "
                + "VALUES (?, ?, ?, ?, ?)",
                new SerializerWriteBasic(new Datas[]{
            Datas.STRING,
            Datas.STRING,
            Datas.INT,
            Datas.TIMESTAMP,
            Datas.TIMESTAMP}));

        m_draweropened = new StaticSentence(s,
                "INSERT INTO draweropened ( NAME, TICKETID) "
                + "VALUES (?, ?)",
                new SerializerWriteBasic(new Datas[]{
            Datas.STRING,
            Datas.STRING}));

        m_lineremoved = new StaticSentence(s,
                "INSERT INTO lineremoved (NAME, TICKETID, PRODUCTID, PRODUCTNAME, UNITS) "
                + "VALUES (?, ?, ?, ?, ?)",
                new SerializerWriteBasic(new Datas[]{
            Datas.STRING, Datas.STRING,
            Datas.STRING, Datas.STRING,
            Datas.DOUBLE
        }));

        m_ticketremoved = new StaticSentence(s,
                "INSERT INTO lineremoved (NAME, TICKETID, PRODUCTNAME, UNITS) "
                + "VALUES (?, ?, ?, ?)",
                new SerializerWriteBasic(new Datas[]{
            Datas.STRING, Datas.STRING,
            Datas.STRING, Datas.DOUBLE
        }));

        m_locationfind = new StaticSentence(s,
                "SELECT NAME FROM locations WHERE ID = ?",
                SerializerWriteString.INSTANCE,
                SerializerReadString.INSTANCE);

        m_permissionlist = new StaticSentence(s,
                "SELECT PERMISSIONS FROM permissions WHERE ID = ?",
                SerializerWriteString.INSTANCE,
                new SerializerReadBasic(new Datas[]{
            Datas.STRING
        }));

        m_updatepermissions = new StaticSentence(s,
                "INSERT INTO permissions (ID, PERMISSIONS) "
                + "VALUES (?, ?)",
                new SerializerWriteBasic(new Datas[]{
            Datas.STRING,
            Datas.STRING}));

//  Push Products into CSVImport table      
        m_insertCSVEntry = new StaticSentence(s,
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

//  Push Product Quantity Update into CSVImport table      
        m_insertStockUpdateEntry = new StaticSentence(s,
                "INSERT INTO csvimport ( "
                + "ID, ROWNUMBER, CSVERROR, REFERENCE, "
                + "CODE, PRICEBUY ) "
                + "VALUES (?, ?, ?, ?, ?, ?)",
                new SerializerWriteBasic(new Datas[]{
            Datas.STRING,
            Datas.STRING,
            Datas.STRING,
            Datas.STRING,
            Datas.STRING,
            Datas.DOUBLE
        }));

//  Push Customers into CSVImport table      
        m_insertCustomerCSVEntry = new StaticSentence(s,
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

        m_addOrder = new StaticSentence(s,
                "INSERT INTO orders (ORDERID, QTY, DETAILS, ATTRIBUTES, "
                + "NOTES, TICKETID, ORDERTIME, DISPLAYID, AUXILIARY, "
                + "COMPLETETIME) "
                + "VALUES (?, ?, ?, ?, ?, "
                + "?, ?, ?, ?, ? ) ",
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

        m_updateOrder = new StaticSentence(s,
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

        m_deleteOrder = new StaticSentence(s,
                "DELETE FROM orders WHERE ORDERID = ?",
                SerializerWriteString.INSTANCE);

        m_updatePlaces = new StaticSentence(s, "UPDATE PLACES SET X = ?, Y = ? "
                + "WHERE ID = ?   ", new SerializerWriteBasic(new Datas[]{
            Datas.INT,
            Datas.INT,
            Datas.STRING
        }));

        resetResourcesCache();
    }

    /**
     *
     * @return
     */
    public String getInitScript() {
        return m_sInitScript;
    }

    /**
     *
     * @return
     */
    public String getDBVersion() {
        return m_dbVersion;
    }

    /**
     *
     * @return @throws BasicException
     */
    public final String findVersion() throws BasicException {
        return (String) m_version.find(AppLocal.APP_ID);
    }

    /**
     *
     * @return @throws BasicException
     */
    public final String getUser() throws BasicException {
        return ("");

    }

    /**
     *
     * @throws BasicException
     */
    public final void execDummy() throws BasicException {
        m_dummy.exec();
    }

    /**
     *
     * @return @throws BasicException
     */
    public final List listPeopleVisible() throws BasicException {
        return m_peoplevisible.list();
    }

    /**
     *
     * @param role
     * @return
     * @throws BasicException
     */
    public final List<String> getPermissions(String role) throws BasicException {
        return m_permissionlist.list(role);
    }

    /**
     *
     * @param card
     * @return
     * @throws BasicException
     */
    public final AppUser findPeopleByCard(String card) throws BasicException {
        return (AppUser) m_peoplebycard.find(card);
    }

    /**
     *
     * @param sRole
     * @return
     */
    public final String findRolePermissions(String sRole) {
        String content = new String();
        try {
            content = Formats.BYTEA.formatValue(m_rolepermissions.find(sRole));
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
        m_changepassword.exec(userdata);
    }

    /**
     *
     */
    public final void resetResourcesCache() {
        resourcescache = new HashMap<>();
    }

    private byte[] getResource(String name) {

        byte[] resource = resourcescache.get(name);

        if (resource == null) {
            try {
                resource = (byte[]) m_resourcebytes.find(name);
                if(resource != null){
                    resourcescache.put(name, resource);
                }else{
                    LOGGER.log(Level.WARNING, "NOT found resource: " + name);
                }
            } catch (BasicException e) {
                LOGGER.log(Level.SEVERE, "Exception on get resource: " + name, e);
                resource = null;
            }
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

        Object[] value = new Object[]{UUID.randomUUID().toString(), name, type, data};
        try {
            if (m_resourcebytesupdate.exec(value) == 0) {
                m_resourcebytesinsert.exec(value);
            }
            resourcescache.put(name, data);
        } catch (BasicException e) {
            LOGGER.log(Level.SEVERE, "Exception on set resource: " + name, e);
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
            byte[] img = getResourceAsBinary(sName);
            if (img != null) {
                p.loadFromXML(new ByteArrayInputStream(img));
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Exception on get resource: " + sName, e);
        }
        return p;
    }

    /**
     *
     * @param host
     * @return
     * @throws BasicException
     */
    public final int getSequenceCash(String host) throws BasicException {
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
        return (Object[]) m_activecash.find(sActiveCashIndex);
    }

    /**
     *
     * @param sClosedCashIndex
     * @return
     * @throws BasicException
     */
    public final Object[] findClosedCash(String sClosedCashIndex) throws BasicException {
        return (Object[]) m_activecash.find(sClosedCashIndex);
    }

    /**
     *
     * @param cash
     * @throws BasicException
     */
    public final void execInsertCash(Object[] cash) throws BasicException {
        m_insertcash.exec(cash);
    }

    /**
     *
     * @param drawer
     * @throws BasicException
     */
    public final void execDrawerOpened(Object[] drawer) throws BasicException {
        m_draweropened.exec(drawer);
    }

    /**
     *
     * @param permissions
     * @throws BasicException
     */
    public final void execUpdatePermissions(Object[] permissions) throws BasicException {
        m_updatepermissions.exec(permissions);
    }

    /**
     *
     * @param line
     */
    public final void execLineRemoved(Object[] line) {
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
        try {
            m_ticketremoved.exec(ticket);
        } catch (BasicException e) {
            LOGGER.log(Level.SEVERE, "Exception on execute ticket removed: ", e);
        }
    }

    /**
     *
     * @param iLocation
     * @return
     * @throws BasicException
     */
    public final String findLocationName(String iLocation) throws BasicException {
        return (String) m_locationfind.find(iLocation);
    }

    /**
     *
     * @param csv
     * @throws BasicException
     */
    public final void execCSVStockUpdate(Object[] csv) throws BasicException {
        m_insertStockUpdateEntry.exec(csv);

    }

    /**
     *
     * @param csv
     * @throws BasicException
     */
    public final void execAddCSVEntry(Object[] csv) throws BasicException {
        m_insertCSVEntry.exec(csv);

    }

    /**
     *
     * @param csv
     * @throws BasicException
     */
    public final void execCustomerAddCSVEntry(Object[] csv) throws BasicException {
        m_insertCustomerCSVEntry.exec(csv);

    }

// This is used by CSVimport to detect what type of product insert we are looking at, or what error occured
    /**
     *
     * @param myProduct
     * @return
     * @throws BasicException
     */
    public final String getProductRecordType(Object[] myProduct) throws BasicException {
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
        m_updatePlaces.exec(x, y, id);
    }

    public final SentenceList getVouchersActiveList() {
        return m_voucherlist;
    }

    public final void addOrder(String orderId, Integer qty,
            String details, String attributes, String notes, String ticketId,
            String ordertime, Integer displayId, String auxiliary, String completetime
    ) throws BasicException {

        m_addOrder.exec(orderId, qty, details, attributes, notes, ticketId,
                ordertime, displayId, auxiliary, completetime);
    }

    public final void updateOrder(String orderId, Integer qty,
            String details, String attributes, String notes, String ticketId,
            String ordertime, Integer displayId, String auxiliary, String completetime
    ) throws BasicException {

        m_updateOrder.exec(orderId, qty, details, attributes, notes, ticketId,
                ordertime, displayId, auxiliary, completetime);
    }

    public void deleteOrder(String orderId) throws BasicException {

        m_deleteOrder.exec(orderId);
    }
}
