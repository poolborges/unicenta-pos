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
//    along with this program.  If not, see <http://www.gnu.org/licenses/>
package com.openbravo.pos.forms;

import com.openbravo.basic.BasicException;
import com.openbravo.data.gui.JMessageDialog;
import com.openbravo.data.gui.MessageInf;
import com.openbravo.data.loader.Session;
import com.openbravo.format.Formats;
import com.openbravo.pos.printer.DeviceTicket;
import com.openbravo.pos.printer.TicketParser;
import com.openbravo.pos.printer.TicketPrinterException;
import com.openbravo.pos.scale.DeviceScale;
import com.openbravo.pos.scanpal2.DeviceScanner;
import com.openbravo.pos.scanpal2.DeviceScannerFactory;
import com.openbravo.pos.scripting.ScriptEngine;
import com.openbravo.pos.scripting.ScriptException;
import com.openbravo.pos.scripting.ScriptFactory;
import java.awt.CardLayout;
import java.awt.ComponentOrientation;
import java.awt.Cursor;
import java.io.*;
import java.sql.SQLException;
import java.text.DateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import org.openide.util.Exceptions;

/**
 *
 * @author adrianromero
 */
public class JRootApp extends JPanel implements AppView {

    private static final Logger LOGGER = Logger.getLogger(JRootApp.class.getName());
    private static final long serialVersionUID = 1L;

    private final AppProperties appFileProperties;
    private Session session;
    private DataLogicSystem m_dlSystem;

    private Properties hostSavedProperties = null;
    private CashDrawer activeCash = new CashDrawer();
    private CashDrawer closedCash = new CashDrawer();
    private String m_sInventoryLocation;

    private DeviceScale m_Scale;
    private DeviceScanner m_Scanner;
    private DeviceTicket m_DeviceTicket;
    private TicketParser m_TicketParser;

    private JPrincipalApp m_principalapp = null;
    private JAuthPanel mAuthPanel = null;

    private String getLineTimer() {
        return Formats.HOURMIN.formatValue(new Date());
    }

    private String getLineDate() {
        DateFormat df = DateFormat.getDateInstance(DateFormat.DEFAULT, getDefaultLocale());
        return df.format(new Date());
    }

    public JRootApp(AppProperties props) {
        initComponents();

        appFileProperties = props;
    }

    public void initApp() throws BasicException{

        applyComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));

        try {
            session = AppViewConnection.createSession(this, appFileProperties);

        } catch (BasicException e) {
            LOGGER.log(Level.WARNING, "Exception on DB createSession", e);
            throw new BasicException("Exception on DB createSession", e);
        }

        m_dlSystem = (DataLogicSystem) getBean("com.openbravo.pos.forms.DataLogicSystem");

        LOGGER.log(Level.INFO, "DB Migration execution Starting");
        try {
            com.openbravo.pos.data.DBMigrator.execDBMigration(session);
            LOGGER.log(Level.INFO, "Database verification or migration done sucessfully");
        }catch(BasicException ex) {
            throw new BasicException("Database verification fail", ex);
        }

        logStartup();

        hostSavedProperties = m_dlSystem.getResourceAsProperties(getHostID());

        if (checkActiveCash()) {
            LOGGER.log(Level.WARNING, "Fail on verify ActiveCash");
            throw new BasicException("Fail on verify ActiveCash");
        }

        setInventoryLocation();

        initPeripheral();

        setTitlePanel();

        setStatusBarPanel();

        showLoginPanel();
    }

    private void setTitlePanel() {
        
        String customTile = m_dlSystem.getResourceAsText("Window.Title");
        if(customTile == null){
        
        }
        
        appTitleLabel.setText(customTile);
        appTitleLabel.repaint();

        /*Timer show Date Hour:min:seg
        javax.swing.Timer clockTimer = new javax.swing.Timer(1000, new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent evt) {
                String m_clock = getLineTimer();
                String m_date = getLineDate();
                jLabel2.setText("  " + m_date + " " + m_clock);
            }
        });

        clockTimer.start();*/
        
    }

    private String getHostID() {
        return appFileProperties.getHost() + "/properties";
    }

    private void setInventoryLocation() {
        m_sInventoryLocation = hostSavedProperties.getProperty("location");
        if (m_sInventoryLocation == null) {
            m_sInventoryLocation = "0";
            hostSavedProperties.setProperty("location", m_sInventoryLocation);
            m_dlSystem.setResourceAsProperties(getHostID(), hostSavedProperties);
        }
    }

    private void initPeripheral() {
        m_DeviceTicket = new DeviceTicket(this, appFileProperties);

        m_TicketParser = new TicketParser(getDeviceTicket(), m_dlSystem);
        printerStart();

        m_Scale = new DeviceScale(this, appFileProperties);

        m_Scanner = DeviceScannerFactory.createInstance(appFileProperties);
    }

    private boolean checkActiveCash() {
        try {
            String sActiveCashIndex = hostSavedProperties.getProperty("activecash");
            Object[] valcash = sActiveCashIndex == null
                    ? null
                    : m_dlSystem.findActiveCash(sActiveCashIndex);
            if (valcash == null || !appFileProperties.getHost().equals(valcash[0])) {
                setActiveCash(UUID.randomUUID().toString(),
                        m_dlSystem.getSequenceCash(appFileProperties.getHost()) + 1, new Date(), null);
                m_dlSystem.execInsertCash(
                        new Object[]{getActiveCashIndex(), appFileProperties.getHost(),
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
            return true;
        }
        return false;
    }

    private void logStartup() {
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
    }

    private String readDataBaseVersion() {
        try {
            return m_dlSystem.findVersion();
        } catch (BasicException ed) {
            return null;
        }
    }


    @Override
    public DeviceTicket getDeviceTicket() {
        return m_DeviceTicket;
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
        return activeCash.getCashIndex();
    }

    @Override
    public int getActiveCashSequence() {
        return activeCash.getCashSequence();
    }

    @Override
    public Date getActiveCashDateStart() {
        return activeCash.getCashDateStart();
    }

    @Override
    public Date getActiveCashDateEnd() {
        return activeCash.getCashDateEnd();
    }

    @Override
    public void setActiveCash(String sIndex, int iSeq, Date dStart, Date dEnd) {
        activeCash.setCashIndex(sIndex);
        activeCash.setCashSequence(iSeq);
        activeCash.setCashDateStart(dStart);
        activeCash.setCashDateEnd(dEnd);

        hostSavedProperties.setProperty("activecash", activeCash.getCashIndex());
        m_dlSystem.setResourceAsProperties(getHostID(), hostSavedProperties);
    }

    @Override
    public String getClosedCashIndex() {
        return closedCash.getCashIndex();
    }

    @Override
    public int getClosedCashSequence() {
        return closedCash.getCashSequence();
    }

    @Override
    public Date getClosedCashDateStart() {
        return closedCash.getCashDateStart();
    }

    @Override
    public Date getClosedCashDateEnd() {
        return closedCash.getCashDateEnd();
    }

    @Override
    public void setClosedCash(String sIndex, int iSeq, Date dStart, Date dEnd) {
        closedCash.setCashIndex(sIndex);
        closedCash.setCashSequence(iSeq);
        closedCash.setCashDateStart(dStart);
        closedCash.setCashDateEnd(dEnd);
        hostSavedProperties.setProperty("closecash", closedCash.getCashIndex());
        m_dlSystem.setResourceAsProperties(getHostID(), hostSavedProperties);
    }

    @Override
    public AppProperties getProperties() {
        return appFileProperties;
    }

    @Override
    public Object getBean(String beanfactory) throws BeanFactoryException {
        return BeanContainer.geBean(beanfactory, this);
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

    @Override
    public boolean hasPermission(String permission) {
        return Optional.ofNullable(this.getAppUserView())
                .map(i -> i.getUser())
                .map(u -> u.hasPermission(permission))
                .orElse(false);
    }

    private void printerStart() {

        String sresource = m_dlSystem.getResourceAsXML("Printer.Start");
        if (sresource == null) {
            m_DeviceTicket.getDeviceDisplay().writeVisor(AppLocal.APP_NAME, AppLocal.APP_VERSION);
        } else {
            try {
                ScriptEngine script = ScriptFactory.getScriptEngine(ScriptFactory.VELOCITY);
                script.put("appname", AppLocal.APP_NAME);
                script.put("appShortDescription", AppLocal.APP_SHORT_DESCRIPTION);
                String xmlContent = script.eval(sresource).toString();
                m_TicketParser.printTicket(xmlContent);
            } catch (TicketPrinterException eTP) {
                m_DeviceTicket.getDeviceDisplay().writeVisor(AppLocal.APP_NAME, AppLocal.APP_VERSION);
            }
            catch (ScriptException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }

    private void showView(String view) {
        CardLayout cl = (CardLayout) (contentContainerPanel.getLayout());
        cl.show(contentContainerPanel, view);
    }

    private void openAppView(AppUser user) {

        LOGGER.log(Level.WARNING, "INFO :: showMainAppPanel");
        if (closeAppView()) {

            m_principalapp = new JPrincipalApp(this, user);

            statusBarSecondPanel.add(m_principalapp.getNotificator());
            statusBarSecondPanel.revalidate();

            String viewID = "_" + m_principalapp.getUser().getId();
            contentContainerPanel.add(m_principalapp, viewID);
            showView(viewID);

            m_principalapp.activate();
        }
    }
    
    //Release hardware,files,...
    private void releaseResources() {
        if(m_DeviceTicket != null){
            m_DeviceTicket.getDeviceDisplay().clearVisor();
        }
    }

    public void tryToClose() {

        if (closeAppView()) {
            releaseResources();
            if(session != null){
                try {
                    session.close();
                } catch (SQLException ex) {
                    LOGGER.log(Level.WARNING, "", ex);
                }
            }
            java.awt.Window parent = SwingUtilities.getWindowAncestor(this);
            if(parent != null){
                parent.dispose();
            }else {
                this.setVisible(false);
                this.setEnabled(false);
            }
        }
    }

    @Override
    public boolean closeAppView() {

        if (m_principalapp == null) {
            return true;
        } else if (!m_principalapp.deactivate()) {
            return false;
        } else {
            statusBarSecondPanel.remove(m_principalapp.getNotificator());
            statusBarSecondPanel.revalidate();
            statusBarSecondPanel.repaint();

            contentContainerPanel.remove(m_principalapp);
            m_principalapp = null;

            //showLoginPanel();
            return true;
        }
    }

    private void showLoginPanel() {
        LOGGER.log(Level.WARNING, "INFO :: showLoginPanel");
        if (mAuthPanel == null) {
            mAuthPanel = new JAuthPanel(m_dlSystem, new JAuthPanel.AuthListener() {
                @Override
                public void onSucess(AppUser user) {
                    openAppView(user);
                }
            });
            contentContainerPanel.add(mAuthPanel, "login");
        }
        showView("login");
    }

    private void setStatusBarPanel() {
        String sWareHouse;

        try {
            sWareHouse = m_dlSystem.findLocationName(m_sInventoryLocation);
        } catch (BasicException e) {
            sWareHouse = "";
        }

        String url;
        try {
            url = session.getURL();
        } catch (SQLException e) {
            url = "";
        }
        appInfoLabel.setText("<html>" + appFileProperties.getHost() + " ;<b>WareHouse<b>: " + sWareHouse + "<br>" + url + "</html>");
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the FormEditor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        topPanel = new javax.swing.JPanel();
        appTitleLabel = new javax.swing.JLabel();
        appPowerByLabel = new javax.swing.JLabel();
        appTopLabel = new javax.swing.JLabel();
        contentContainerPanel = new javax.swing.JPanel();
        statusBarPanel = new javax.swing.JPanel();
        statusBarFirstPanel = new javax.swing.JPanel();
        appInfoLabel = new javax.swing.JLabel();
        statusBarSecondPanel = new javax.swing.JPanel();
        appExitButton = new javax.swing.JButton();

        setEnabled(false);
        setPreferredSize(new java.awt.Dimension(1024, 768));
        setLayout(new java.awt.BorderLayout());

        topPanel.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 1, 0, javax.swing.UIManager.getDefaults().getColor("Button.darkShadow")));
        topPanel.setPreferredSize(new java.awt.Dimension(449, 40));
        topPanel.setLayout(new java.awt.BorderLayout());

        appTitleLabel.setFont(new java.awt.Font("Arial", 1, 16)); // NOI18N
        appTitleLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        appTitleLabel.setText("Point of Sales (POS)");
        topPanel.add(appTitleLabel, java.awt.BorderLayout.CENTER);

        appPowerByLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        appPowerByLabel.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 5, 0, 5));
        appPowerByLabel.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        appPowerByLabel.setMaximumSize(new java.awt.Dimension(180, 34));
        appPowerByLabel.setPreferredSize(new java.awt.Dimension(180, 34));
        topPanel.add(appPowerByLabel, java.awt.BorderLayout.LINE_END);

        appTopLabel.setFont(new java.awt.Font("Arial", 1, 16)); // NOI18N
        appTopLabel.setForeground(new java.awt.Color(102, 102, 102));
        appTopLabel.setPreferredSize(new java.awt.Dimension(180, 34));
        topPanel.add(appTopLabel, java.awt.BorderLayout.LINE_START);

        add(topPanel, java.awt.BorderLayout.NORTH);

        contentContainerPanel.setLayout(new java.awt.CardLayout());
        add(contentContainerPanel, java.awt.BorderLayout.CENTER);

        statusBarPanel.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 0, 0, 0, javax.swing.UIManager.getDefaults().getColor("Button.darkShadow")));
        statusBarPanel.setLayout(new javax.swing.BoxLayout(statusBarPanel, javax.swing.BoxLayout.LINE_AXIS));

        statusBarFirstPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        appInfoLabel.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        appInfoLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/display.png"))); // NOI18N
        appInfoLabel.setText("*Hostname");
        appInfoLabel.setMaximumSize(new java.awt.Dimension(200, 32));
        appInfoLabel.setMinimumSize(new java.awt.Dimension(200, 32));
        appInfoLabel.setPreferredSize(new java.awt.Dimension(200, 32));
        statusBarFirstPanel.add(appInfoLabel);

        statusBarPanel.add(statusBarFirstPanel);

        statusBarSecondPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

        appExitButton.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        appExitButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/exit.png"))); // NOI18N
        appExitButton.setText(AppLocal.getIntString("button.exit")); // NOI18N
        appExitButton.setFocusPainted(false);
        appExitButton.setFocusable(false);
        appExitButton.setPreferredSize(new java.awt.Dimension(100, 50));
        appExitButton.setRequestFocusEnabled(false);
        appExitButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                appExitButtonActionPerformed(evt);
            }
        });
        statusBarSecondPanel.add(appExitButton);

        statusBarPanel.add(statusBarSecondPanel);

        add(statusBarPanel, java.awt.BorderLayout.SOUTH);
    }// </editor-fold>//GEN-END:initComponents

    private void appExitButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_appExitButtonActionPerformed
        tryToClose();
    }//GEN-LAST:event_appExitButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton appExitButton;
    private javax.swing.JLabel appInfoLabel;
    private javax.swing.JLabel appPowerByLabel;
    private javax.swing.JLabel appTitleLabel;
    private javax.swing.JLabel appTopLabel;
    private javax.swing.JPanel contentContainerPanel;
    private javax.swing.JPanel statusBarFirstPanel;
    private javax.swing.JPanel statusBarPanel;
    private javax.swing.JPanel statusBarSecondPanel;
    private javax.swing.JPanel topPanel;
    // End of variables declaration//GEN-END:variables
}
