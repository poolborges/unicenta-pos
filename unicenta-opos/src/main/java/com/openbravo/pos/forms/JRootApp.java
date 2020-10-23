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
//    along with this program.  If not, see <http://www.gnu.org/licenses/>
package com.openbravo.pos.forms;

import com.openbravo.basic.BasicException;
import com.openbravo.beans.JFlowPanel;
import com.openbravo.beans.JPasswordDialog;
import com.openbravo.data.gui.JMessageDialog;
import com.openbravo.data.gui.MessageInf;
import com.openbravo.data.loader.BatchSentence;
import com.openbravo.data.loader.BatchSentenceResource;
import com.openbravo.data.loader.Session;
import com.openbravo.format.Formats;
import com.openbravo.pos.printer.DeviceTicket;
import com.openbravo.pos.printer.TicketParser;
import com.openbravo.pos.printer.TicketPrinterException;
import com.openbravo.pos.scale.DeviceScale;
import com.openbravo.pos.scanpal2.DeviceScanner;
import com.openbravo.pos.scanpal2.DeviceScannerFactory;
import java.awt.CardLayout;
import java.awt.ComponentOrientation;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.text.DateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import javax.swing.*;
import com.dalsemi.onewire.OneWireAccessProvider;
import com.dalsemi.onewire.adapter.DSPortAdapter;
import com.dalsemi.onewire.OneWireException;
import com.dalsemi.onewire.container.OneWireContainer;
import com.dalsemi.onewire.utils.*;
import com.dalsemi.onewire.application.monitor.*;
import com.openbravo.pos.util.uOWWatch;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.time.Instant;

/**
 *
 * @author adrianromero
 */
public class JRootApp extends JPanel implements AppView, DeviceMonitorEventListener {

    private static final Logger LOGGER = Logger.getLogger(JRootApp.class.getName());

    private AppProperties m_props;
    private Session session;
    private DataLogicSystem m_dlSystem;

    private Properties m_propsdb = null;
    private String m_sActiveCashIndex;
    private int m_iActiveCashSequence;
    private Date m_dActiveCashDateStart;
    private Date m_dActiveCashDateEnd;

    private String m_sClosedCashIndex;
    private int m_iClosedCashSequence;
    private Date m_dClosedCashDateStart;
    private Date m_dClosedCashDateEnd;

    private String m_sInventoryLocation;

    private StringBuilder inputtext;

    private DeviceScale m_Scale;
    private DeviceScanner m_Scanner;
    private DeviceTicket m_TP;
    private TicketParser m_TTP;

    private final Map<String, BeanFactory> m_aBeanFactories;

    private JPrincipalApp m_principalapp = null;

    private static HashMap<String, String> m_oldclasses;

    private String m_clock;
    private String m_date;



    static {
        initOldClasses();
    }

    private class PrintTimeAction implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent evt) {
            m_clock = getLineTimer();
            m_date = getLineDate();
            jLabel2.setText("  " + m_date + " " + m_clock);
        }
    }

    private String getLineTimer() {
        return Formats.HOURMIN.formatValue(new Date());
    }

    private String getLineDate() {
        DateFormat df = DateFormat.getDateInstance(DateFormat.DEFAULT, getDefaultLocale());
        return df.format(new Date());
    }

    public JRootApp() {

        m_aBeanFactories = new HashMap<>();

        //TODO load Windows Title 
        //m_jLblTitle.setText(m_dlSystem.getResourceAsText("Window.Title"));
        //m_jLblTitle.repaint();

        initComponents();
        jScrollPane1.getVerticalScrollBar().setPreferredSize(new Dimension(30, 30));
        serverMonitor.setVisible(false);
        webMemoryBar1.setShowMaximumMemory(true);
    }
    private DSPortAdapter m_oneWireAdapter;
    private DeviceMonitor m_oneWireMonitor;

    private void initIButtonMonitor() {

        assert m_oneWireMonitor == null;
        try {
            m_oneWireAdapter = OneWireAccessProvider.getDefaultAdapter();
            m_oneWireAdapter.setSearchAllDevices();
            m_oneWireAdapter.targetFamily(0x01);
            m_oneWireAdapter.setSpeed(DSPortAdapter.SPEED_REGULAR);
            m_oneWireMonitor = new DeviceMonitor(m_oneWireAdapter);
// Normal state
            m_oneWireMonitor.setMaxStateCount(5);
// Use for testing
//            m_oneWireMonitor.setMaxStateCount(100);                        
            m_oneWireMonitor.addDeviceMonitorEventListener(this);
            new Thread(m_oneWireMonitor).start();
        } catch (OneWireException e) {
            JMessageDialog.showMessage(this,
                    new MessageInf(MessageInf.SGN_WARNING,
                            AppLocal.getIntString("message.ibuttonnotfound"), e));
        }
    }

    private void shutdownIButtonMonitor() {
        if (m_oneWireMonitor != null) {
            m_oneWireMonitor.killMonitor();
            try {
                m_oneWireAdapter.freePort();
            } catch (OneWireException e) {
                LOGGER.log(Level.SEVERE, "Exception on shuttdown IButtonMonitor", e);
            }
        }
    }

    public void releaseResources() {
        shutdownIButtonMonitor();
    }

    final static int UNIQUE_KEY_FAMILY = 0x01;

    private boolean isDeviceRelevant(OneWireContainer container) {
        String iButtonId = container.getAddressAsString();
        try {
            if (container.getAdapter().getAdapterAddress().equals(iButtonId)) {
                return false;
            }
        } catch (OneWireException e) {
            LOGGER.log(Level.SEVERE, "Exception on getting IButton Address", e);
        }

        int familyNumber = Address.toByteArray(iButtonId)[0];
        return (familyNumber == UNIQUE_KEY_FAMILY);
    }

    /**
     * Called when an iButton is inserted.
     *
     * @param devt
     */
    @Override
    public void deviceArrival(DeviceMonitorEvent devt) {
        assert m_dlSystem != null;

        for (int i = 0; i < devt.getDeviceCount(); i++) {
            OneWireContainer container = devt.getContainerAt(i);
            if (!isDeviceRelevant(container)) {
                continue;
            }

            String iButtonId = devt.getAddressAsStringAt(i);

            AppUser user = null;
            try {
                user = m_dlSystem.findPeopleByCard(iButtonId);
            } catch (BasicException e) {
                LOGGER.log(Level.WARNING, "Exception: ", e);
                if (user == null) {
                    JOptionPane.showMessageDialog(this,
                            AppLocal.getIntString("message.ibuttonnotassign"),
                            AppLocal.getIntString("title.editor"),
                            JOptionPane.INFORMATION_MESSAGE);
                }
            }

            if (user == null) {
                JOptionPane.showMessageDialog(this,
                        AppLocal.getIntString("message.ibuttonnotassign"),
                        AppLocal.getIntString("title.editor"),
                        JOptionPane.INFORMATION_MESSAGE);

            } else {
                setVisible(false);
                openAppView(user);
                setVisible(true);
            }
        }
    }

    /**
     * Called when an iButton is removed.
     *
     * @param devt
     */
    @Override
    public void deviceDeparture(DeviceMonitorEvent devt) {

        for (int i = 0; i < devt.getDeviceCount(); i++) {
            OneWireContainer container = devt.getContainerAt(i);
            if (!isDeviceRelevant(container)) {
                continue;
            }

            String iButtonId = devt.getAddressAsStringAt(i);

            if (m_principalapp != null) {
                AppUser currentUser = m_principalapp.getUser();
                if (currentUser != null && currentUser.getCard().equals(iButtonId)) {
                    closeAppView();
                }
            }
        }
    }

    @Override
    public void networkException(DeviceMonitorException dexc) {
//        System.out.println("ERROR: " + dexc.toString());
    }

    /**
     *
     * @param props
     * @return
     */
    public boolean initApp(AppProperties props) {

        m_props = props;
        m_jPanelDown.setVisible(!(Boolean.valueOf(m_props.getProperty("till.hideinfo"))));

        applyComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));

        try {
            session = AppViewConnection.createSession(m_props);

        } catch (BasicException e) {
            LOGGER.log(Level.WARNING, "Exception: ", e);
            JMessageDialog.showMessage(this, new MessageInf(MessageInf.SGN_DANGER, e.getMessage(), e));
            return false;
        }

        m_dlSystem = (DataLogicSystem) getBean("com.openbravo.pos.forms.DataLogicSystem");

        String sDBVersion = readDataBaseVersion();
        if (!AppLocal.APP_VERSION.equals(sDBVersion)) {
            String sScript = sDBVersion == null
                    ? m_dlSystem.getInitScript() + "-create.sql"
                    : m_dlSystem.getInitScript() + "-upgrade-" + sDBVersion + ".sql";

            if (JRootApp.class.getResource(sScript) == null) {
                JMessageDialog.showMessage(this, new MessageInf(MessageInf.SGN_DANGER, sDBVersion == null
                        ? AppLocal.getIntString("message.databasenotsupported", session.DB.getName() + " " + sDBVersion)
                        : AppLocal.getIntString("message.noupdatescript")));
                session.close();
                return false;
            } else {
                if (JOptionPane.showConfirmDialog(this,
                        AppLocal.getIntString(sDBVersion == null
                                ? "message.createdatabase"
                                : "message.eolupdate", session.DB.getName() + " " + sDBVersion),
                        AppLocal.getIntString("message.title"),
                        JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.OK_OPTION) {
                    if (sScript.endsWith("MySQL-create.sql")) {
                        try {
                            BatchSentence bsentence = new BatchSentenceResource(session, sScript);
                            bsentence.putParameter("APP_ID", Matcher.quoteReplacement(AppLocal.APP_ID));
                            bsentence.putParameter("APP_NAME", Matcher.quoteReplacement(AppLocal.APP_NAME));
                            bsentence.putParameter("APP_VERSION", Matcher.quoteReplacement(AppLocal.APP_VERSION));

                            java.util.List l = bsentence.list();

                            if (l.size() > 0) {
                                JMessageDialog.showMessage(this, new MessageInf(MessageInf.SGN_WARNING,
                                        AppLocal.getIntString("database.scriptwarning"),
                                        l.toArray(new Throwable[l.size()])));
                            }
                        } catch (BasicException e) {

                            LOGGER.log(Level.WARNING, "Exception: ", e);
                            JMessageDialog.showMessage(this,
                                    new MessageInf(MessageInf.SGN_DANGER,
                                            AppLocal.getIntString("database.scripterror"), e));
                            session.close();
                            return false;
                        }
                    }
                } else {
                    session.close();
                    return false;
                }
            }
        }

// create the filename
        String sUserPath = AppConfig.getInstance().getAppDataDirectory();

        Instant machineTimestamp = Instant.now();
        String sContent = sUserPath + ","
                + machineTimestamp + ","
                + AppLocal.APP_ID + ","
                + AppLocal.APP_NAME + ","
                + AppLocal.APP_VERSION + "\n";

        try {
            Files.write(new File(sUserPath, AppLocal.getLogFileName()).toPath(), sContent.getBytes(),
                    StandardOpenOption.APPEND, StandardOpenOption.CREATE);
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }

        try {
            Files.write(new File(sUserPath, AppLocal.getLockFileName()).toPath(), sContent.getBytes(),
                    StandardOpenOption.CREATE);
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }

        /**
         * TODO Send Logging file to remote server, Ping remote server
         */
        m_propsdb = m_dlSystem.getResourceAsProperties(m_props.getHost() + "/properties");

        try {
            String sActiveCashIndex = m_propsdb.getProperty("activecash");
            Object[] valcash = sActiveCashIndex == null
                    ? null
                    : m_dlSystem.findActiveCash(sActiveCashIndex);
            if (valcash == null || !m_props.getHost().equals(valcash[0])) {
                setActiveCash(UUID.randomUUID().toString(),
                        m_dlSystem.getSequenceCash(m_props.getHost()) + 1, new Date(), null);
                m_dlSystem.execInsertCash(
                        new Object[]{getActiveCashIndex(), m_props.getHost(),
                            getActiveCashSequence(),
                            getActiveCashDateStart(),
                            getActiveCashDateEnd()});
            } else {
                setActiveCash(sActiveCashIndex,
                        (Integer) valcash[1],
                        (Date) valcash[2],
                        (Date) valcash[3]);
            }
        } catch (BasicException e) {
            MessageInf msg = new MessageInf(MessageInf.SGN_NOTICE,
                    AppLocal.getIntString("message.cannotclosecash"), e);
            msg.show(this);
            session.close();
            return false;
        }

        m_sInventoryLocation = m_propsdb.getProperty("location");
        if (m_sInventoryLocation == null) {
            m_sInventoryLocation = "0";
            m_propsdb.setProperty("location", m_sInventoryLocation);
            m_dlSystem.setResourceAsProperties(m_props.getHost() + "/properties",
                    m_propsdb);
        }

        m_TP = new DeviceTicket(this, m_props);

        m_TTP = new TicketParser(getDeviceTicket(), m_dlSystem);
        printerStart();

        m_Scale = new DeviceScale(this, m_props);

        m_Scanner = DeviceScannerFactory.createInstance(m_props);

        new javax.swing.Timer(1000, new PrintTimeAction()).start();

        String sWareHouse;

        try {
            sWareHouse = m_dlSystem.findLocationName(m_sInventoryLocation);
        } catch (BasicException e) {
            sWareHouse = null;
        }

        String url;
        try {
            url = session.getURL();
        } catch (SQLException e) {
            url = "";
        }
        m_jHost.setText("<html>" + m_props.getHost() + " - " + sWareHouse + "<br>" + url);

        String newLogo = m_props.getProperty("start.logo");
        if (newLogo != null) {
            if ("".equals(newLogo)) {
                jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/logo.png")));
            } else {
                jLabel1.setIcon(new javax.swing.ImageIcon(newLogo));
            }
        }

        String newText = m_props.getProperty("start.text");
        if (newText != null) {
            if (newText.equals("")) {
                jLabel1.setText("<html><center>KrOS POS - Open Source Point Of Sale<br>"
                        + "Copyright \u00A9 KrOS POS <br>"
                        + "More info on official <href a=\"https://github.com/poolborges/unicenta-pos\">site</a><br>"
                        + "</center>"
                        + "<br><br>"
                        + "<h2>License: GPL v3</h2>"
                        + "<br>"
                        + "This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.<br>"
                        + "<br>"
                        + " This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.<br>"
                        + "<br>"
                        + "You should have received a copy of the GNU General Public License along with this program.  If not, see http://www.gnu.org/licenses/<br>"
                );

            } else {
                try {
                    String newTextCode = new Scanner(new File(newText),
                            "UTF-8").useDelimiter("\\A").next();
                    jLabel1.setText(newTextCode);
                } catch (FileNotFoundException e) {
                }

                jLabel1.setAlignmentX(0.5F);
                jLabel1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
                jLabel1.setMaximumSize(new java.awt.Dimension(800, 1024));
                jLabel1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
            }
        }

        showLogin();

        String ibutton = m_props.getProperty("machine.iButton");
        if (ibutton.equals("true")) {
            initIButtonMonitor();
            uOWWatch.iButtonOn();
        }
        return true;
    }

    private String readDataBaseVersion() {
        try {
            return m_dlSystem.findVersion();
        } catch (BasicException ed) {
            return null;
        }
    }

    public void tryToClose() {

        if (closeAppView()) {
            m_TP.getDeviceDisplay().clearVisor();
            shutdownIButtonMonitor();

// delete the open.db tracking file
            String sUserPath = AppConfig.getInstance().getAppDataDirectory();
            File filePath = new File(sUserPath, "open.db");
            try {
                Files.deleteIfExists(filePath.toPath());
            } catch (IOException ex) {
                Logger.getLogger(JRootApp.class.getName()).log(Level.SEVERE, null, ex);
            }

            session.close();
            SwingUtilities.getWindowAncestor(this).dispose();
        }
    }

    @Override
    public DeviceTicket getDeviceTicket() {
        return m_TP;
    }

    @Override
    public DeviceScale getDeviceScale() {
        return m_Scale;
    }

    @Override
    public DeviceScanner getDeviceScanner() {
        return m_Scanner;
    }

    @Override
    public Session getSession() {
        return session;
    }

    @Override
    public String getInventoryLocation() {
        return m_sInventoryLocation;
    }

    @Override
    public String getActiveCashIndex() {
        return m_sActiveCashIndex;
    }

    @Override
    public int getActiveCashSequence() {
        return m_iActiveCashSequence;
    }

    @Override
    public Date getActiveCashDateStart() {
        return m_dActiveCashDateStart;
    }

    @Override
    public Date getActiveCashDateEnd() {
        return m_dActiveCashDateEnd;
    }

    @Override
    public void setActiveCash(String sIndex, int iSeq, Date dStart, Date dEnd) {
        m_sActiveCashIndex = sIndex;
        m_iActiveCashSequence = iSeq;
        m_dActiveCashDateStart = dStart;
        m_dActiveCashDateEnd = dEnd;

        m_propsdb.setProperty("activecash", m_sActiveCashIndex);
        m_dlSystem.setResourceAsProperties(m_props.getHost() + "/properties", m_propsdb);
    }

    @Override
    public String getClosedCashIndex() {
        return m_sClosedCashIndex;
    }

    @Override
    public int getClosedCashSequence() {
        return m_iClosedCashSequence;
    }

    @Override
    public Date getClosedCashDateStart() {
        return m_dClosedCashDateStart;
    }

    @Override
    public Date getClosedCashDateEnd() {
        return m_dClosedCashDateEnd;
    }

    @Override
    public void setClosedCash(String sIndex, int iSeq, Date dStart, Date dEnd) {
        m_sClosedCashIndex = sIndex;
        m_iClosedCashSequence = iSeq;
        m_dClosedCashDateStart = dStart;
        m_dClosedCashDateEnd = dEnd;

        m_dlSystem.setResourceAsProperties(m_props.getHost() + "/properties", m_propsdb);
    }

    @Override
    public AppProperties getProperties() {
        return m_props;
    }

    @Override
    public Object getBean(String beanfactory) throws BeanFactoryException {

        beanfactory = mapNewClass(beanfactory);
        BeanFactory bf = m_aBeanFactories.get(beanfactory);

        if (bf == null) {

            if (beanfactory.startsWith("/")) {
                bf = new BeanFactoryScript(beanfactory);
            } else {
                try {
                    Class bfclass = Class.forName(beanfactory);

                    if (BeanFactory.class.isAssignableFrom(bfclass)) {
                        bf = (BeanFactory) bfclass.getDeclaredConstructor().newInstance();
                    } else {
                        Constructor constMyView = bfclass.getConstructor(new Class[]{AppView.class});
                        Object bean = constMyView.newInstance(new Object[]{this});
                        bf = new BeanFactoryObj(bean);
                    }

                } catch (ClassNotFoundException | InstantiationException
                        | IllegalAccessException | NoSuchMethodException
                        | SecurityException | IllegalArgumentException | InvocationTargetException e) {
                    LOGGER.log(Level.WARNING, "Cannot found Bean: " + beanfactory, e);
                    throw new BeanFactoryException(e);
                }
            }

            m_aBeanFactories.put(beanfactory, bf);

            if (bf instanceof BeanFactoryApp) {
                ((BeanFactoryApp) bf).init(this);
            }
        }
        return bf.getBean();
    }

    private static String mapNewClass(String classname) {
        String newclass = m_oldclasses.get(classname);
        return newclass == null
                ? classname
                : newclass;
    }

    private static void initOldClasses() {
        m_oldclasses = new HashMap<>();

        m_oldclasses.put("com.openbravo.pos.reports.JReportCustomers", "/com/openbravo/reports/customers.bs");
        m_oldclasses.put("com.openbravo.pos.reports.JReportCustomersB", "/com/openbravo/reports/customersb.bs");
        m_oldclasses.put("com.openbravo.pos.reports.JReportClosedPos", "/com/openbravo/reports/closedpos.bs");
        m_oldclasses.put("com.openbravo.pos.reports.JReportClosedProducts", "/com/openbravo/reports/closedproducts.bs");
        m_oldclasses.put("com.openbravo.pos.reports.JChartSales", "/com/openbravo/reports/chartsales.bs");
        m_oldclasses.put("com.openbravo.pos.reports.JReportInventory", "/com/openbravo/reports/inventory.bs");
        m_oldclasses.put("com.openbravo.pos.reports.JReportInventory2", "/com/openbravo/reports/inventoryb.bs");
        m_oldclasses.put("com.openbravo.pos.reports.JReportInventoryBroken", "/com/openbravo/reports/inventorybroken.bs");
        m_oldclasses.put("com.openbravo.pos.reports.JReportInventoryDiff", "/com/openbravo/reports/inventorydiff.bs");
        m_oldclasses.put("com.openbravo.pos.reports.JReportPeople", "/com/openbravo/reports/people.bs");
        m_oldclasses.put("com.openbravo.pos.reports.JReportTaxes", "/com/openbravo/reports/taxes.bs");
        m_oldclasses.put("com.openbravo.pos.reports.JReportUserSales", "/com/openbravo/reports/usersales.bs");
        m_oldclasses.put("com.openbravo.pos.reports.JReportProducts", "/com/openbravo/reports/products.bs");
        m_oldclasses.put("com.openbravo.pos.reports.JReportCatalog", "/com/openbravo/reports/productscatalog.bs");

        m_oldclasses.put("com.openbravo.pos.panels.JPanelTax", "com.openbravo.pos.inventory.TaxPanel");

    }

    @Override
    public void waitCursorBegin() {
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    }

    @Override
    public void waitCursorEnd() {
        setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }

    @Override
    public AppUserView getAppUserView() {
        return m_principalapp;
    }

    private void printerStart() {

        String sresource = m_dlSystem.getResourceAsXML("Printer.Start");
        if (sresource == null) {
            m_TP.getDeviceDisplay().writeVisor(AppLocal.APP_NAME, AppLocal.APP_VERSION);
        } else {
            try {
                m_TTP.printTicket(sresource);
            } catch (TicketPrinterException eTP) {
                m_TP.getDeviceDisplay().writeVisor(AppLocal.APP_NAME, AppLocal.APP_VERSION);
            }
        }
    }

    private void listPeople() {

        try {

            jScrollPane1.getViewport().setView(null);

            JFlowPanel jPeople = new JFlowPanel();
            jPeople.applyComponentOrientation(getComponentOrientation());

            java.util.List people = m_dlSystem.listPeopleVisible();

            for (Object people1 : people) {
                AppUser user = (AppUser) people1;
                JButton btn = new JButton(new AppUserAction(user));
                btn.applyComponentOrientation(getComponentOrientation());
                btn.setFocusPainted(false);
                btn.setFocusable(false);
                btn.setRequestFocusEnabled(false);
                btn.setMaximumSize(new Dimension(110, 60));
                btn.setPreferredSize(new Dimension(110, 60));
                btn.setMinimumSize(new Dimension(110, 60));
                btn.setHorizontalAlignment(SwingConstants.CENTER);
                btn.setHorizontalTextPosition(AbstractButton.CENTER);
                btn.setVerticalTextPosition(AbstractButton.BOTTOM);
                jPeople.add(btn);
            }

            jScrollPane1.getViewport().setView(jPeople);

        } catch (BasicException ee) {
        }
    }

    class AppUserAction extends AbstractAction {

        private final AppUser m_actionuser;

        public AppUserAction(AppUser user) {
            m_actionuser = user;
            putValue(Action.SMALL_ICON, m_actionuser.getIcon());
            putValue(Action.NAME, m_actionuser.getName());
        }

        public AppUser getUser() {
            return m_actionuser;
        }

        @Override
        public void actionPerformed(ActionEvent evt) {

            if (m_actionuser.authenticate()) {
                openAppView(m_actionuser);
            } else {
                String sPassword = JPasswordDialog.showEditPassword(JRootApp.this,
                        AppLocal.getIntString("label.Password"),
                        m_actionuser.getName(),
                        m_actionuser.getIcon());
                if (sPassword != null) {

                    if (m_actionuser.authenticate(sPassword)) {
                        openAppView(m_actionuser);
                    } else {
                        MessageInf msg = new MessageInf(MessageInf.SGN_WARNING,
                                AppLocal.getIntString("message.BadPassword"));
                        msg.show(JRootApp.this);
                    }
                }
            }
        }
    }

    private void showView(String view) {
        CardLayout cl = (CardLayout) (m_jPanelContainer.getLayout());
        cl.show(m_jPanelContainer, view);
    }

    private void openAppView(AppUser user) {

        if (closeAppView()) {

            m_principalapp = new JPrincipalApp(this, user);

            jPanel3.add(m_principalapp.getNotificator());
            jPanel3.revalidate();

            m_jPanelContainer.add(m_principalapp,
                    "_" + m_principalapp.getUser().getId());
            showView("_" + m_principalapp.getUser().getId());

            m_principalapp.activate();
        }
    }

    public void exitToLogin() {
        closeAppView();
        showLogin();
    }

    public boolean closeAppView() {

        if (m_principalapp == null) {
            return true;
        } else if (!m_principalapp.deactivate()) {
            return false;
        } else {
            jPanel3.remove(m_principalapp.getNotificator());
            jPanel3.revalidate();
            jPanel3.repaint();

            m_jPanelContainer.remove(m_principalapp);
            m_principalapp = null;

            showLogin();

            return true;
        }
    }

    private void showLogin() {

        listPeople();
        showView("login");

        printerStart();

        inputtext = new StringBuilder();
        m_txtKeys.setText(null);
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                m_txtKeys.requestFocus();
            }
        });
    }

    private void processKey(char c) {

        if ((c == '\n') || (c == '?')) {
            AppUser user = null;
            try {
                user = m_dlSystem.findPeopleByCard(inputtext.toString());
            } catch (BasicException e) {
            }

            if (user == null) {
                MessageInf msg = new MessageInf(MessageInf.SGN_WARNING,
                        AppLocal.getIntString("message.nocard"));
                msg.show(this);
            } else {
                openAppView(user);
            }

            inputtext = new StringBuilder();
        } else {
            inputtext.append(c);
        }
    }

    private int getProgressBar() {
        int rate = serverMonitor.getValue();
        return rate;
    }

    private boolean pingServer() throws UnknownHostException {
        /* 
     * This method is for the future. Connects and will include both servers + backup server
     * Tested locally on JG machine and unicenta-server   
         */
        serverMonitor.setString("Checking...");

        InetAddress addr = InetAddress.getByName(AppLocal.getIntString("db.ip"));
        int port = 3306;

        SocketAddress sockaddr = new InetSocketAddress(addr, port);
        Socket sock = new Socket();
        try {
            sock.connect(sockaddr, 2000);
            serverMonitor.setString("Server is alive!");
            serverMonitor.setValue(0);
            return true;
        } catch (IOException ex) {
            Logger.getLogger(JRootApp.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the FormEditor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        m_jPanelTitle = new javax.swing.JPanel();
        m_jLblTitle = new javax.swing.JLabel();
        poweredby = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        m_jPanelContainer = new javax.swing.JPanel();
        m_jPanelLogin = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 10), new java.awt.Dimension(32767, 0));
        jPanel5 = new javax.swing.JPanel();
        m_jLogonName = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jPanel1 = new javax.swing.JPanel();
        m_txtKeys = new javax.swing.JTextField();
        m_jClose = new javax.swing.JButton();
        m_jPanelDown = new javax.swing.JPanel();
        panelTask = new javax.swing.JPanel();
        m_jHost = new javax.swing.JLabel();
        webMemoryBar1 = new com.alee.extended.statusbar.WebMemoryBar();
        serverMonitor = new com.alee.laf.progressbar.WebProgressBar();
        jPanel3 = new javax.swing.JPanel();

        setEnabled(false);
        setPreferredSize(new java.awt.Dimension(1024, 768));
        setLayout(new java.awt.BorderLayout());

        m_jPanelTitle.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 1, 0, javax.swing.UIManager.getDefaults().getColor("Button.darkShadow")));
        m_jPanelTitle.setPreferredSize(new java.awt.Dimension(449, 40));
        m_jPanelTitle.setLayout(new java.awt.BorderLayout());

        m_jLblTitle.setFont(new java.awt.Font("Arial", 1, 16)); // NOI18N
        m_jLblTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        m_jLblTitle.setText("Window.Title");
        m_jPanelTitle.add(m_jLblTitle, java.awt.BorderLayout.CENTER);

        poweredby.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        poweredby.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 5, 0, 5));
        poweredby.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        poweredby.setMaximumSize(new java.awt.Dimension(180, 34));
        poweredby.setPreferredSize(new java.awt.Dimension(180, 34));
        m_jPanelTitle.add(poweredby, java.awt.BorderLayout.LINE_END);

        jLabel2.setFont(new java.awt.Font("Arial", 1, 16)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(102, 102, 102));
        jLabel2.setPreferredSize(new java.awt.Dimension(180, 34));
        m_jPanelTitle.add(jLabel2, java.awt.BorderLayout.LINE_START);

        add(m_jPanelTitle, java.awt.BorderLayout.NORTH);

        m_jPanelContainer.setLayout(new java.awt.CardLayout());

        m_jPanelLogin.setLayout(new java.awt.BorderLayout());

        jPanel4.setLayout(new javax.swing.BoxLayout(jPanel4, javax.swing.BoxLayout.Y_AXIS));

        jLabel1.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/logo_320×320.png"))); // NOI18N
        jLabel1.setText("<html><center>KrOS POS - Open Source Point Of Sale<br>" +
            "Copyright \u00A9 2009-2017 uniCenta <br>" +
            "<br>" +
            "<br>" +
            "This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.<br>" +
            "<br>" +
            " This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.<br>" +
            "<br>" +
            "You should have received a copy of the GNU General Public License along with this program.  If not, see http://www.gnu.org/licenses/<br>" +
            "</center>");
        jLabel1.setAlignmentX(0.5F);
        jLabel1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jLabel1.setMaximumSize(new java.awt.Dimension(800, 1024));
        jLabel1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jPanel4.add(jLabel1);
        jPanel4.add(filler2);

        m_jPanelLogin.add(jPanel4, java.awt.BorderLayout.CENTER);

        jPanel5.setPreferredSize(new java.awt.Dimension(300, 400));

        m_jLogonName.setLayout(new java.awt.BorderLayout());

        jPanel2.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 5, 0, 5));
        jPanel2.setPreferredSize(new java.awt.Dimension(100, 100));
        jPanel2.setLayout(new java.awt.BorderLayout());

        jPanel8.setLayout(new java.awt.GridLayout(0, 1, 5, 5));
        jPanel2.add(jPanel8, java.awt.BorderLayout.NORTH);

        m_jLogonName.add(jPanel2, java.awt.BorderLayout.LINE_END);

        jScrollPane1.setBorder(null);
        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        m_txtKeys.setPreferredSize(new java.awt.Dimension(0, 0));
        m_txtKeys.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                m_txtKeysKeyTyped(evt);
            }
        });

        m_jClose.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        m_jClose.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/exit.png"))); // NOI18N
        m_jClose.setText(AppLocal.getIntString("button.close")); // NOI18N
        m_jClose.setFocusPainted(false);
        m_jClose.setFocusable(false);
        m_jClose.setPreferredSize(new java.awt.Dimension(100, 50));
        m_jClose.setRequestFocusEnabled(false);
        m_jClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jCloseActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel1Layout.createSequentialGroup()
                        .add(m_txtKeys, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(0, 0, Short.MAX_VALUE))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .add(m_jClose, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 267, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .add(m_txtKeys, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(m_jClose, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );

        org.jdesktop.layout.GroupLayout jPanel5Layout = new org.jdesktop.layout.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(jScrollPane1))
                .add(104, 104, 104)
                .add(m_jLogonName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(0, 0, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel5Layout.createSequentialGroup()
                .add(15, 15, 15)
                .add(jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel5Layout.createSequentialGroup()
                        .add(m_jLogonName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(434, 434, 434))
                    .add(jPanel5Layout.createSequentialGroup()
                        .add(jScrollPane1)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())))
        );

        m_jPanelLogin.add(jPanel5, java.awt.BorderLayout.EAST);

        m_jPanelContainer.add(m_jPanelLogin, "login");

        add(m_jPanelContainer, java.awt.BorderLayout.CENTER);

        m_jPanelDown.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 0, 0, 0, javax.swing.UIManager.getDefaults().getColor("Button.darkShadow")));
        m_jPanelDown.setLayout(new java.awt.BorderLayout());

        m_jHost.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        m_jHost.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/display.png"))); // NOI18N
        m_jHost.setText("*Hostname");
        panelTask.add(m_jHost);

        webMemoryBar1.setBackground(new java.awt.Color(153, 153, 153));
        webMemoryBar1.setText("");
        webMemoryBar1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        webMemoryBar1.setPreferredSize(new java.awt.Dimension(150, 30));
        webMemoryBar1.setUsedBorderColor(new java.awt.Color(0, 204, 204));
        webMemoryBar1.setUsedFillColor(new java.awt.Color(0, 204, 255));
        panelTask.add(webMemoryBar1);

        serverMonitor.setToolTipText("");
        serverMonitor.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        serverMonitor.setMaximumSize(new java.awt.Dimension(50, 18));
        serverMonitor.setPreferredSize(new java.awt.Dimension(150, 30));
        serverMonitor.setProgressBottomColor(new java.awt.Color(76, 197, 237));
        serverMonitor.setRound(2);
        serverMonitor.setString("Keep Alive");
        serverMonitor.setStringPainted(true);
        panelTask.add(serverMonitor);

        m_jPanelDown.add(panelTask, java.awt.BorderLayout.LINE_START);
        m_jPanelDown.add(jPanel3, java.awt.BorderLayout.LINE_END);

        add(m_jPanelDown, java.awt.BorderLayout.SOUTH);
    }// </editor-fold>//GEN-END:initComponents


    private void m_txtKeysKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_m_txtKeysKeyTyped

        m_txtKeys.setText("0");
        processKey(evt.getKeyChar());

    }//GEN-LAST:event_m_txtKeysKeyTyped

    private void m_jCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jCloseActionPerformed
        tryToClose();
    }//GEN-LAST:event_m_jCloseActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.Box.Filler filler2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton m_jClose;
    private javax.swing.JLabel m_jHost;
    private javax.swing.JLabel m_jLblTitle;
    private javax.swing.JPanel m_jLogonName;
    private javax.swing.JPanel m_jPanelContainer;
    private javax.swing.JPanel m_jPanelDown;
    private javax.swing.JPanel m_jPanelLogin;
    private javax.swing.JPanel m_jPanelTitle;
    private javax.swing.JTextField m_txtKeys;
    private javax.swing.JPanel panelTask;
    private javax.swing.JLabel poweredby;
    private com.alee.laf.progressbar.WebProgressBar serverMonitor;
    private com.alee.extended.statusbar.WebMemoryBar webMemoryBar1;
    // End of variables declaration//GEN-END:variables
}
