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

package com.openbravo.pos.sales;

import com.alee.extended.time.ClockType;
import com.alee.extended.time.WebClock;
import com.alee.managers.notification.NotificationIcon;
import com.alee.managers.notification.NotificationManager;
import com.alee.managers.notification.WebNotification;
import com.openbravo.basic.BasicException;
import com.openbravo.beans.JNumberPop;
import com.openbravo.data.gui.ComboBoxValModel;
import com.openbravo.data.gui.ListKeyed;
import com.openbravo.data.gui.MessageInf;
import com.openbravo.data.loader.SentenceList;
import com.openbravo.pos.customers.CustomerInfoExt;
import com.openbravo.pos.customers.CustomerInfoGlobal;
import com.openbravo.pos.customers.DataLogicCustomers;
import com.openbravo.pos.customers.JCustomerFinder;
import com.openbravo.pos.customers.JDialogNewCustomer;
import com.openbravo.pos.forms.*;
import com.openbravo.pos.inventory.ProductStock;
import com.openbravo.pos.inventory.TaxCategoryInfo;
import com.openbravo.pos.panels.JProductFinder;
import com.openbravo.pos.payment.JPaymentSelect;
import com.openbravo.pos.payment.JPaymentSelectReceipt;
import com.openbravo.pos.payment.JPaymentSelectRefund;
import com.openbravo.pos.printer.TicketParser;
import com.openbravo.pos.printer.TicketPrinterException;
import com.openbravo.pos.sales.restaurant.RestaurantDBUtils;
import com.openbravo.pos.scale.ScaleException;
import com.openbravo.pos.scripting.ScriptEngine;
import com.openbravo.pos.scripting.ScriptException;
import com.openbravo.pos.scripting.ScriptFactory;
import com.openbravo.pos.ticket.ProductInfoExt;
import com.openbravo.pos.ticket.TaxInfo;
import com.openbravo.pos.ticket.TicketInfo;
import com.openbravo.pos.ticket.TicketLineInfo;
import com.openbravo.pos.util.AltEncrypter;
import com.openbravo.pos.util.InactivityListener;
import com.openbravo.pos.util.JRPrinterAWT300;
import com.openbravo.pos.util.ReportUtils;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Toolkit;
import java.awt.Window;
import static java.awt.Window.getWindows;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.print.PrintService;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRMapArrayDataSource;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;


/**
 *
 * @author JG uniCenta
 */
public abstract class JPanelTicket extends JPanel implements JPanelView, BeanFactoryApp, TicketsEditor {
   
    private final static Logger LOGGER = Logger.getLogger(JPanelTicket.class.getName());
    
    private final static int NUMBERZERO = 0;
    private final static int NUMBERVALID = 1;
    
    private final static int NUMBER_INPUTZERO = 0;
    private final static int NUMBER_INPUTZERODEC = 1;
    private final static int NUMBER_INPUTINT = 2;
    private final static int NUMBER_INPUTDEC = 3; 
    private final static int NUMBER_PORZERO = 4; 
    private final static int NUMBER_PORZERODEC = 5; 
    private final static int NUMBER_PORINT = 6; 
    private final static int NUMBER_PORDEC = 7; 

    protected JTicketLines m_ticketlines;
        
    private TicketParser m_TTP;
    
    protected TicketInfo m_oTicket; 
    protected Object m_oTicketExt; 
    
    private int m_iNumberStatus;
    private int m_iNumberStatusInput;
    private int m_iNumberStatusPor;
    private StringBuffer m_sBarcode;
            
    private JTicketsBag m_ticketsbag;
    
    private SentenceList senttax;
    private ListKeyed taxcollection;
    
    private SentenceList senttaxcategories;
    //private ListKeyed taxcategoriescollection;
    private ComboBoxValModel taxcategoriesmodel;
    
    private TaxesLogic taxeslogic;
    
    protected JPanelButtons m_jbtnconfig;
    protected AppView m_App;

    protected DataLogicSystem dlSystem;
    protected DataLogicSales dlSales;
    protected DataLogicCustomers dlCustomers;
   
    private JPaymentSelect paymentdialogreceipt;
    private JPaymentSelect paymentdialogrefund;
 

    private InactivityListener listener;
    private Integer delay = 0;

    protected TicketsEditor m_panelticket; 
    private DataLogicReceipts dlReceipts = null;
    private Boolean priceWith00;
    private RestaurantDBUtils restDB;
    
    private Boolean warrantyPrint=false;
    
    private TicketInfo m_ticket;
    private AppConfig m_config;
    
    private Integer count = 0;
    private Integer oCount = 0;
    private Boolean pinOK;
   
    
    /** Creates new form JTicketView */
    public JPanelTicket() {
        
        initComponents ();
    }
   
    /**
     *
     * @param app
     * @throws BeanFactoryException
     */
    @Override
    public void init(AppView app) throws BeanFactoryException {

        m_config = AppConfig.getInstance();
        m_config.load();
        
        m_App = app;
        restDB = new RestaurantDBUtils(m_App);
       
        dlSystem = (DataLogicSystem) m_App.getBean("com.openbravo.pos.forms.DataLogicSystem");
        dlSales = (DataLogicSales) m_App.getBean("com.openbravo.pos.forms.DataLogicSales");
        dlCustomers = (DataLogicCustomers) m_App.getBean("com.openbravo.pos.customers.DataLogicCustomers");
        dlReceipts = (DataLogicReceipts) app.getBean("com.openbravo.pos.sales.DataLogicReceipts");
        

/* uniCenta Feb 2018
 * Changed for 4.3
 * Set up main toolbar area with two rows to add cater for additional scripts 
 * else over-crowding and some dynamic buttons off screen/not visible
 * m_jPanelScripts contains m_jButtonsExt panel for buttons Enabled/Disabled in
 * Resources>Menu.Root
*/        
        
// Set Configuration>General>Tickets toolbar simple : standard : restaurant option
        m_ticketsbag = getJTicketsBag();    
        m_jPanelBag.add(m_ticketsbag.getBagComponent(), BorderLayout.LINE_START);
        add(m_ticketsbag.getNullComponent(), "null");   

// Script event buttons
        m_jbtnconfig = new JPanelButtons("Ticket.Buttons", this);
        m_jButtonsExt.add(m_jbtnconfig);   

// Configuration>Peripheral options        
        if (!m_App.getDeviceScale().existsScale()) {
            m_jbtnScale.setVisible(false);
        }
        jbtnMooring.setVisible(Boolean.valueOf(m_App.getProperties().getProperty("till.marineoption")));
        m_jPanelScripts.setVisible(false);
        m_jButtonsExt.setVisible(false);           
        jTBtnShow.setSelected(false);
        
        if (Boolean.valueOf(m_App.getProperties().getProperty("till.amountattop"))){
            m_jPanEntries.remove(jPanel9);
            m_jPanEntries.remove(m_jNumberKeys);        
            m_jPanEntries.add(jPanel9);
            m_jPanEntries.add(m_jNumberKeys);
        }        
 
        priceWith00 = ("true".equals(m_App.getProperties().getProperty("till.pricewith00")));

        if (priceWith00) {
            m_jNumberKeys.dotIs00(true);
        }
        
        m_ticketlines = new JTicketLines(dlSystem.getResourceAsXML("Ticket.Line"));
        m_jPanelCentral.add(m_ticketlines, java.awt.BorderLayout.CENTER);
        m_TTP = new TicketParser(m_App.getDeviceTicket(), dlSystem);
        catcontainer.add(getSouthComponent(), BorderLayout.CENTER);
        
        senttax = dlSales.getTaxList();
        senttaxcategories = dlSales.getTaxCategoriesList();       
        taxcategoriesmodel = new ComboBoxValModel();
        
        stateToZero();  

        m_oTicket = null;
        m_oTicketExt = null; 
        jCheckStock.setText(AppLocal.getIntString("message.title.checkstock"));

    }

    @Override
    public Object getBean() {
        return this;
    }

    @Override
    public JComponent getComponent() {
        return this;
    }

    private class logout extends AbstractAction {

        public logout() {
        }
        @Override
        public void actionPerformed(ActionEvent ae){
            closeAllDialogs();
            switch (m_App.getProperties().getProperty("machine.ticketsbag")){
            case "restaurant":                   
                if ("false".equals(m_App.getProperties().getProperty("till.autoLogoffrestaurant")))  {
                    deactivate();
                    ((JRootApp)m_App).closeAppView();
                    break;
                }     

                deactivate();
                setActiveTicket(null, null);      
                break;                

            default:
                deactivate();
                ((JRootApp)m_App).closeAppView();
            }
        }
    }
    
    private void closeAllDialogs() {
        Window[] windows = getWindows();

        for (Window window : windows) {
            if (window instanceof JDialog) {
                window.dispose();
            }
        }
    }    

    private void saveCurrentTicket() {
        String currentTicket =(String)m_oTicketExt;
        if (currentTicket != null) {
            try {
                dlReceipts.updateSharedTicket(currentTicket, m_oTicket,m_oTicket.getPickupId());
//            } catch (BasicException e) {
//                new MessageInf(e).show(this);
//            }  
            } catch (BasicException ex) {
                Logger.getLogger(JPanelTicket.class.getName()).log(Level.SEVERE, null, ex);
            }                    
        }    
    }

    /**
     *
     * @throws BasicException
     */
    @Override
    public void activate() throws BasicException {

        Action logout = new logout();        
        String autoLogoff = (m_App.getProperties().getProperty("till.autoLogoff"));

        if (autoLogoff != null){
            if (autoLogoff.equals("true")){
                try{
                    delay = Integer.parseInt(m_App.getProperties().getProperty("till.autotimer"));
                }catch (NumberFormatException e){
                    delay=0;
                }
                delay *= 1000;
            }
        }

        if (delay != 0){
            listener = new InactivityListener(logout,delay); 
            listener.start();
        } 
        
        paymentdialogreceipt = JPaymentSelectReceipt.getDialog(this);
        paymentdialogreceipt.init(m_App);
        paymentdialogrefund = JPaymentSelectRefund.getDialog(this); 
        paymentdialogrefund.init(m_App);
        
        m_jaddtax.setSelected("true".equals(m_jbtnconfig.getProperty("taxesincluded")));

        java.util.List<TaxInfo> taxlist = senttax.list();
        taxcollection = new ListKeyed<>(taxlist);
        java.util.List<TaxCategoryInfo> taxcategorieslist = senttaxcategories.list();
        //taxcategoriescollection = new ListKeyed<>(taxcategorieslist);
        
        taxcategoriesmodel = new ComboBoxValModel(taxcategorieslist);
        m_jTax.setModel(taxcategoriesmodel);

        String taxesid = m_jbtnconfig.getProperty("taxcategoryid");

        if (taxesid == null) {
            if (m_jTax.getItemCount() > 0) {
                m_jTax.setSelectedIndex(0);
            }
        } else {
            taxcategoriesmodel.setSelectedKey(taxesid);
        }              
                
        taxeslogic = new TaxesLogic(taxlist);
        m_jaddtax.setSelected((Boolean.parseBoolean(m_App.getProperties().getProperty("till.taxincluded"))));             

        if (m_App.getAppUserView().getUser().hasPermission("sales.ChangeTaxOptions")) {
            m_jTax.setVisible(true);
            m_jaddtax.setVisible(true);            
        } else {
            m_jTax.setVisible(false);
            m_jaddtax.setVisible(false);            
        }

        m_jDelete.setEnabled(m_App.getAppUserView().getUser().hasPermission("sales.EditLines"));
        m_jNumberKeys.setMinusEnabled(m_App.getAppUserView().getUser().hasPermission("sales.EditLines"));
        m_jNumberKeys.setEqualsEnabled(m_App.getAppUserView().getUser().hasPermission("sales.Total"));
        m_jbtnconfig.setPermissions(m_App.getAppUserView().getUser());  
               
        m_ticketsbag.setEnabled(false);
        m_ticketsbag.activate();  
        
        CustomerInfoGlobal customerInfoGlobal = CustomerInfoGlobal.getInstance();

        if (customerInfoGlobal.getCustomerInfoExt() != null){

            if (m_oTicket != null){
                m_oTicket.setCustomer(customerInfoGlobal.getCustomerInfoExt());
            }
        }
         
        refreshTicket();
    }
    
    @Override
    public boolean deactivate() {
        if (listener  != null) {
            listener.stop();
        }
 
        return m_ticketsbag.deactivate();
    }
    
    protected abstract JTicketsBag getJTicketsBag();
    protected abstract Component getSouthComponent();
    protected abstract void resetSouthComponent();

    /**
     *
     * @param oTicket
     * @param oTicketExt
     */
    @SuppressWarnings("empty-statement")
    @Override
    public void setActiveTicket(TicketInfo oTicket, Object oTicketExt) {
        
        switch (m_App.getProperties().getProperty("machine.ticketsbag")){
           case "restaurant":                              
            if ("true".equals(m_App.getProperties().getProperty("till.autoLogoffrestaurant"))) {
                if (listener != null) {
                    listener.restart();
                }
            }          
        }
         
        m_oTicket = oTicket;
        m_oTicketExt = oTicketExt;
       
        if (m_oTicket != null) {            
            m_oTicket.setUser(m_App.getAppUserView().getUser().getUserInfo());
            m_oTicket.setActiveCash(m_App.getActiveCashIndex());
            m_oTicket.setDate(new Date());
            
            if ("restaurant".equals(m_App.getProperties().getProperty("machine.ticketsbag"))
                    && !oTicket.getOldTicket()){            
                if (restDB.getCustomerNameInTable(oTicketExt.toString())== null ){
                    if (m_oTicket.getCustomer() != null){
                       restDB.setCustomerNameInTable(m_oTicket.getCustomer().toString(), oTicketExt.toString()); 
                    }
                } 
                if (restDB.getWaiterNameInTable(oTicketExt.toString())==null 
                    || "".equals(restDB.getWaiterNameInTable(oTicketExt.toString()))){
                    restDB.setWaiterNameInTable(m_App.getAppUserView().getUser().getName(),oTicketExt.toString());
                }              
                    restDB.setTicketIdInTable(m_oTicket.getId(),oTicketExt.toString());
            }
        }
                           
        if ((m_oTicket != null) && (((Boolean.parseBoolean(m_App.getProperties()
                .getProperty("table.showwaiterdetails"))) 
                || (Boolean.valueOf(m_App.getProperties().getProperty(
                        "table.showcustomerdetails")))))) {
        }        
            
        if ((m_oTicket != null) && (((Boolean.valueOf(m_App.getProperties()
                .getProperty("table.showcustomerdetails"))) || 
                (Boolean.parseBoolean(m_App.getProperties().getProperty("table.showwaiterdetails")))))) {
            if (restDB.getTableMovedFlag(m_oTicket.getId())){
                restDB.moveCustomer(oTicketExt.toString(),m_oTicket.getId());
            }                                                
        }

        executeEvent(m_oTicket, m_oTicketExt, "ticket.show");
    
//        if ("restaurant".equals(m_App
//            .getProperties().getProperty("machine.ticketsbag"))){
//        }else{
//            j_btnRemotePrt.setVisible(m_App
//                .getAppUserView().getUser().hasPermission("sales.PrintKitchen")); 
//        }
          if (m_App.getAppUserView().getUser().hasPermission("sales.PrintRemote")) {
                j_btnRemotePrt.setEnabled(true);              
          } else {
                j_btnRemotePrt.setEnabled(false);
          }

        refreshTicket();               
    }
    
    /**
     *
     * @return
     */
    @Override
    public TicketInfo getActiveTicket() {
        return m_oTicket;
    }
    
    private void refreshTicket() {    
        
        CardLayout cl = (CardLayout)(getLayout());
        
        if (m_oTicket == null) {        
            m_jTicketId.setText(null);            
            m_ticketlines.clearTicketLines();
           
            m_jSubtotalEuros.setText(null);
            m_jTaxesEuros.setText(null);
            m_jTotalEuros.setText(null); 
            jCheckStock.setText(null);

            checkStock();
//            countArticles();        
            stateToZero();
            repaint();

            cl.show(this, "null");  

            if ((m_oTicket != null) &&  (m_oTicket.getLinesCount()==0)) {
                resetSouthComponent();
            }
        } else {
            if (m_oTicket.getTicketType() == TicketInfo.RECEIPT_REFUND) {
                m_jEditLine.setVisible(false);
                m_jList.setVisible(false);
            }
            
            m_oTicket.getLines().forEach((line) -> {
                line.setTaxInfo(taxeslogic.getTaxInfo(line
                        .getProductTaxCategoryID(), m_oTicket.getCustomer()));
            });  
        
            m_jTicketId.setText(m_oTicket.getName(m_oTicketExt));
            m_ticketlines.clearTicketLines();

            for (int i = 0; i < m_oTicket.getLinesCount(); i++) {
                m_ticketlines.addTicketLine(m_oTicket.getLine(i));
            }
            
            countArticles();
            printPartialTotals();
            stateToZero();

            cl.show(this, "ticket");

            if (m_oTicket.getLinesCount()==0) {
                resetSouthComponent();
            }
            
            m_jKeyFactory.setText(null);       
            java.awt.EventQueue.invokeLater(new Runnable() {
                @Override
                public void run() {
                    m_jKeyFactory.requestFocus();
                }
            });
        }
    }

    private void countArticles() {
        oCount = count;                             // existing line before change
        count = (int) m_oTicket.getArticlesCount(); //existing line after change

        if (m_oTicket != null) { 
            for (int i = 0; i < m_oTicket.getLinesCount(); i++) {
                if (m_App.getAppUserView().getUser().hasPermission("sales.Total")
                        && m_oTicket.getArticlesCount() > 1) {
                    btnSplit.setEnabled(true);
                }else{
                    btnSplit.setEnabled(false);
                }
            }
        }
    }
    
    private boolean changeCount(boolean pinOK) {

        pinOK = false;

        if (m_oTicket != null) {

            if (m_App.getProperties().getProperty("override.check").equals("true")) {
                Integer secret = Integer.parseInt(m_App.getProperties().getProperty("override.pin"));
                Integer iValue = JNumberPop.showEditNumber(this, AppLocal.getIntString("title.override.enterpin")); 

                if (iValue == null ? secret == null : iValue.equals(secret)) {
                    pinOK = true;
                    JOptionPane.showMessageDialog(this, "Units changed from "
                            + count + " to " + oCount);                    
                    return pinOK;  
                    
                } else {
                    pinOK = false;
                    JOptionPane.showMessageDialog(this, AppLocal.getIntString("message.override.badpin"));                                        
                    return pinOK;                    
                }
            }
        }
            return pinOK;        
    }
   
    private void printPartialTotals(){
            
        if (m_oTicket.getLinesCount() == 0) {
            m_jSubtotalEuros.setText(null);
            m_jTaxesEuros.setText(null);
            m_jTotalEuros.setText(null);
        } else {
            m_jSubtotalEuros.setText(m_oTicket.printSubTotal());
            m_jTaxesEuros.setText(m_oTicket.printTax());
            m_jTotalEuros.setText(m_oTicket.printTotal());
        }
        repaint();
    }
    
    private void paintTicketLine(int index, TicketLineInfo oLine){
        if (executeEventAndRefresh("ticket.setline", 
            new ScriptArg("index", index), new ScriptArg("line", oLine)) == null) {

            m_oTicket.setLine(index, oLine);
            m_ticketlines.setTicketLine(index, oLine);
            m_ticketlines.setSelectedIndex(index);
            oCount = count;     // pass line old multiplier value

            countArticles();
            visorTicketLine(oLine);
            printPartialTotals();   
            stateToZero();  

            executeEventAndRefresh("ticket.change");
        }
   }

    private void addTicketLine(ProductInfoExt oProduct, double dMul, double dPrice) {           
        
        if (oProduct.isVprice() || oProduct.getID().equals("xxx999_999xxx_x9x9x9")){
            TaxInfo tax = taxeslogic.getTaxInfo(oProduct.getTaxCategoryID(), m_oTicket.getCustomer());
            if (m_jaddtax.isSelected()) {
                dPrice /= (1 + tax.getRate());
            }
            addTicketLine(new TicketLineInfo(oProduct, dMul, dPrice, tax, 
                (java.util.Properties) (oProduct.getProperties().clone())));

        } else if (oProduct.getID().equals("xxx998_998xxx_x8x8x8")){
            
            if(m_App.getProperties().getProperty("till.SCOnOff").equals("true")) {
                TaxInfo tax = taxeslogic.getTaxInfo(oProduct.getTaxCategoryID(), 
                        m_oTicket.getCustomer());
                String SCRate = (m_App.getProperties().getProperty("till.SCRate"));

                double scharge;
                scharge = Double.parseDouble(SCRate);
                scharge = m_oTicket.getTotal() * (scharge / 100);                    
                
                addTicketLine(new TicketLineInfo(oProduct, 1, scharge, tax, 
                (java.util.Properties) (oProduct.getProperties().clone())));
                
            } else {
                JOptionPane.showMessageDialog(this, "Service Charge Not Enabled");
            }
            
        } else {        
// get the line product tax
            TaxInfo tax = taxeslogic.getTaxInfo(oProduct.getTaxCategoryID(), m_oTicket.getCustomer());  

            addTicketLine(new TicketLineInfo(oProduct, dMul, dPrice, tax,
                (java.util.Properties) (oProduct.getProperties().clone()))); 
            refreshTicket();
           
        }

        j_btnRemotePrt.setEnabled(true);
    
    }
    
    /**
     *
     * @param oLine
     */
    protected void addTicketLine(TicketLineInfo oLine) {  
        if (executeEventAndRefresh("ticket.addline", new ScriptArg("line", oLine)) == null) {        
            if (oLine.isProductCom()) {
                int i = m_ticketlines.getSelectedIndex();

                if (i >= 0 && !m_oTicket.getLine(i).isProductCom()) {
                    i++;
                }

                while (i >= 0 && i < m_oTicket.getLinesCount() && m_oTicket.getLine(i).isProductCom()) {
                    i++;
                }
                
                if (i >= 0) {
                    m_oTicket.insertLine(i, oLine);
                    m_ticketlines.insertTicketLine(i, oLine);
                } else {
                    Toolkit.getDefaultToolkit().beep();                                   
                }
            } else {    
                m_oTicket.addLine(oLine);            
                m_ticketlines.addTicketLine(oLine);

                try {
                    int i =  m_ticketlines.getSelectedIndex();
                    TicketLineInfo line = m_oTicket.getLine(i);                

                    if (line.isProductVerpatrib()){
                        JProductAttEdit2 attedit = JProductAttEdit2.getAttributesEditor(this, m_App.getSession());
                        attedit.editAttributes(line.getProductAttSetId(), line.getProductAttSetInstId());
                        attedit.setVisible(true);

                        if (attedit.isOK()) {
                            line.setProductAttSetInstId(attedit.getAttributeSetInst());
                            line.setProductAttSetInstDesc(attedit.getAttributeSetInstDescription());
                            paintTicketLine(i, line);
                        }
                    }

                } catch (BasicException ex) {
                    MessageInf msg = new MessageInf(MessageInf.SGN_WARNING, 
                    AppLocal.getIntString("message.cannotfindattributes"), ex);
                    msg.show(this);
                }          
            }
            
            visorTicketLine(oLine);
            printPartialTotals();   
            stateToZero();  
            checkStock();
            countArticles();
            
            executeEvent(m_oTicket, m_oTicketExt, "ticket.change");             
        }
    }    
    
    private void removeTicketLine(int i){

        if (executeEventAndRefresh("ticket.removeline", new ScriptArg("index", i)) == null) {

            String ticketID = Integer.toString(m_oTicket.getTicketId());
            if (m_oTicket.getTicketId()==0){
                ticketID="Void";
            }
            
            dlSystem.execLineRemoved(
                new Object[] {
                m_App.getAppUserView().getUser().getName(),
                    ticketID,
                    m_oTicket.getLine(i).getProductID(),
                    m_oTicket.getLine(i).getProductName(),
                    m_oTicket.getLine(i).getMultiply()
                }
            );
            
            if (m_oTicket.getLine(i).isProductCom()) {
                m_oTicket.removeLine(i);
                m_ticketlines.removeTicketLine(i);   
            } else {
                if (i < 1) {
                    if (m_App.getAppUserView().getUser().hasPermission("sales.DeleteLines")) {
                        int input = JOptionPane.showConfirmDialog(this,
                            AppLocal.getIntString("message.deletelineyes")
                            ,AppLocal.getIntString("label.deleteline"), JOptionPane.YES_NO_OPTION);
                        if (input == 0) {
                            m_oTicket.removeLine(i);
                            m_ticketlines.removeTicketLine(i);
                        }    
                    } else {
                        JOptionPane.showMessageDialog(this, 
                            AppLocal.getIntString("message.deletelineno")
                            ,AppLocal.getIntString("label.deleteline"), JOptionPane.WARNING_MESSAGE);
                    }
                } else {               
                    m_oTicket.removeLine(i);
                    m_ticketlines.removeTicketLine(i); 

                    while(i < m_oTicket.getLinesCount() && m_oTicket.getLine(i).isProductCom()) {
                        m_oTicket.removeLine(i);
                        m_ticketlines.removeTicketLine(i);
                    }
                }  
            }            

           
            visorTicketLine(null);
            printPartialTotals();
            stateToZero();
            checkStock();
            countArticles();
            
            executeEventAndRefresh("ticket.change");
        }
    }
    
    private ProductInfoExt getInputProduct() { 
        ProductInfoExt oProduct = new ProductInfoExt();
// Always add Default Prod ID + Add Name to Misc. if empty
        oProduct.setID("xxx999_999xxx_x9x9x9");
        oProduct.setReference("xxx999");
        oProduct.setCode("xxx999");
        oProduct.setName("***");
        oProduct.setTaxCategoryID(((TaxCategoryInfo) taxcategoriesmodel
            .getSelectedItem()).getID());
        oProduct.setPriceSell(includeTaxes(oProduct.getTaxCategoryID(), getInputValue())); 

        return oProduct;
    }
    
    private double includeTaxes(String tcid, double dValue) {
        if (m_jaddtax.isSelected()) {
            TaxInfo tax = taxeslogic.getTaxInfo(tcid, m_oTicket.getCustomer());
            double dTaxRate = tax == null ? 0.0 : tax.getRate();           
            return dValue / (1.0 + dTaxRate);      
        } else {
            return dValue;
        }
    }
    
   private double excludeTaxes(String tcid, double dValue) {
        TaxInfo tax = taxeslogic.getTaxInfo(tcid, m_oTicket.getCustomer());
        double dTaxRate = tax == null ? 0.0 : tax.getRate();           
        return dValue / (1.0 + dTaxRate);  
    } 
    
     
    private double getInputValue() {
        try {
            return Double.parseDouble(m_jPrice.getText());
        } catch (NumberFormatException e){
            return 0.0;
        }
    }

    private double getPorValue() {
        try {
            return Double.parseDouble(m_jPor.getText().substring(1));                
        } catch (NumberFormatException | StringIndexOutOfBoundsException e){
            return 1.0;
        }
    }

    private void stateToZero(){
        m_jPor.setText("");
        m_jPrice.setText("");
        m_sBarcode = new StringBuffer();
            
        m_iNumberStatus = NUMBER_INPUTZERO;
        m_iNumberStatusInput = NUMBERZERO;
        m_iNumberStatusPor = NUMBERZERO;
        repaint();
    }
    
    private void incProductByCode(String sCode) {

        try {
            ProductInfoExt oProduct = dlSales.getProductInfoByCode(sCode);

            if (oProduct == null) {                  
                Toolkit.getDefaultToolkit().beep();                   
                JOptionPane.showMessageDialog(null,
                   sCode + " - " + AppLocal.getIntString("message.noproduct"),
                   "Check", JOptionPane.WARNING_MESSAGE);                
                stateToZero();
            } else {
                incProduct(oProduct);
            }
        } catch (BasicException eData) {
            stateToZero();           
            new MessageInf(eData).show(this);           
        }
    }
    
    private void incProductByCodePrice(String sCode, double dPriceSell) {

        try {
            ProductInfoExt oProduct = dlSales.getProductInfoByCode(sCode);
            if (oProduct == null) {                  
                Toolkit.getDefaultToolkit().beep();                   
                new MessageInf(MessageInf.SGN_WARNING, AppLocal
                    .getIntString("message.noproduct")).show(this);           
                stateToZero();
            } else {
                if (m_jaddtax.isSelected()) {
                    TaxInfo tax = taxeslogic.getTaxInfo(oProduct.getTaxCategoryID(), m_oTicket.getCustomer());
                    addTicketLine(oProduct, 1.0, dPriceSell / (1.0 + tax.getRate()));
                } else {
                    addTicketLine(oProduct, 1.0, dPriceSell);
                }                
            }
        } catch (BasicException eData) {
            stateToZero();
            new MessageInf(eData).show(this);               
        }
    }
    
    private void incProduct(ProductInfoExt prod) {

        if (prod.isScale() && m_App.getDeviceScale().existsScale()) {
            try {
                Double value = m_App.getDeviceScale().readWeight();
                if (value != null) {
                    incProduct(value, prod);
                }
            } catch (ScaleException e) {
                Toolkit.getDefaultToolkit().beep();                
                    new MessageInf(MessageInf.SGN_WARNING, AppLocal
                        .getIntString("message.noweight"), e).show(this);           
                stateToZero(); 
            }
        } else {
            if (!prod.isVprice()){
                incProduct(1.0, prod);          
            } else {
                Toolkit.getDefaultToolkit().beep();                
                JOptionPane.showMessageDialog(null, 
                    AppLocal.getIntString("message.novprice"));
            }
        }
    }
     
    private void incProduct(double dPor, ProductInfoExt prod) {

        if (prod.isVprice()){
            addTicketLine(prod, getPorValue(), getInputValue());    
        }else {        
            addTicketLine(prod, dPor, prod.getPriceSell());
        }
    }
       
    /**
     *
     * @param prod
     */
    protected void buttonTransition(ProductInfoExt prod) {

        if (m_iNumberStatusInput == NUMBERZERO && m_iNumberStatusPor == NUMBERZERO) {
            incProduct(prod);
        } else if (m_iNumberStatusInput == NUMBERVALID && m_iNumberStatusPor == NUMBERZERO) {          
            incProduct(getInputValue(), prod);
        } else if (prod.isVprice()) {           
            addTicketLine(prod, getPorValue(), getInputValue()) ;                
        } else {
            Toolkit.getDefaultToolkit().beep();
        }      
    }
    
    @SuppressWarnings("empty-statement")
    private void stateTransition(char cTrans) {
    
        if ((cTrans == '\n') || (cTrans == '?')) {
        
        if (m_sBarcode.length() > 0) { 

            String sCode = m_sBarcode.toString();
            String sCodetype = "EAN";                                           // Declare EAN. It's default        

            if ("true".equals(m_App.getProperties().getProperty("machine.barcodetype"))) {
                sCodetype = "UPC";                
            } else {
                sCodetype = "EAN";                                              // Ensure not null   
            }   
            
            if (sCode.startsWith("C")
                    || sCode.startsWith("c")) {
                try {
                    String card = sCode;
                    CustomerInfoExt newcustomer = dlSales.findCustomerExt(card);

                    if (newcustomer == null) {
                        Toolkit.getDefaultToolkit().beep();                   
                        new MessageInf(MessageInf.SGN_WARNING, AppLocal
                            .getIntString("message.nocustomer")).show(this);           
                    } else {
                        m_oTicket.setCustomer(newcustomer);
                        m_jTicketId.setText(m_oTicket.getName(m_oTicketExt));
                    }
                } catch (BasicException e) {
                    Toolkit.getDefaultToolkit().beep();                   
                    new MessageInf(MessageInf.SGN_WARNING, AppLocal
                        .getIntString("message.nocustomer"), e).show(this);           
                }
                    stateToZero();

            } else if (sCode.startsWith(";")) {
                    stateToZero();


        // START OF BARCODE PARSING
            /*  This block is deliberately verbose and is base for future scanner handling
            *  Some scanners inject a CR+LF... some don't... 
            *  stateTransition() must allow for this as these add characters to .length()
            *  First 3 digits are GS1 CountryCode OR Retailer internal use
            *                     
            *  Prefix   ManCodeProdCode    CheckCode
            *  PPP      MMMMMCCCCC         K                    
            *  012      3456789012         K
            *  Barcode CCCCC must be unique                   
            *  Notes: 
            *      ManufacturerCode and ProductCode must be exactly 10 digits
            *      If code begins with 0 then is actually a UPC-A with prepended 0
            *        
            *  KrOS POS Retailer instore uses these RULES
            *  Prefixes 020 to 029 are set aside for Retailer internal use 
            *  This means that CCCC becomes price/weight values
            *  Prefixes 978 and 979 are set aside for ISBN - Future use
            *       
            *  Prefix   ManCode    ProdCode   CheckCode
            *  PPP      MMMMM      CCCCC       K           Format                    
            *  012      34567      89012       K           Human
            * 
            */                    
        
                } else if ( "EAN".equals(sCodetype) 
                        && ((sCode.startsWith("2")) || (sCode.startsWith("02"))) // check code prefix
                        && ((sCode.length() == 13) || (sCode.length() == 12))) { // check code length variances                                                   
                    
                try {
                    ProductInfoExt oProduct                                     // get product(s) with PMMMMM
                            = dlSales.getProductInfoByShortCode(sCode);

                    if(oProduct == null) {                                      // nothing returned so display message to user
                        Toolkit.getDefaultToolkit().beep();
                        JOptionPane.showMessageDialog(null,
                            sCode + " - " 
                            + AppLocal.getIntString("message.noproduct"),
                            "Check", JOptionPane.WARNING_MESSAGE);  
                        stateToZero();                                          // clear the user input

                    } else if("EAN-13".equals(oProduct.getCodetype())) {        // have a valid barcode
                        oProduct.setProperty("product.barcode", sCode);         // set the screen's barcode from input
                        double dPriceSell = oProduct.getPriceSell();            // default price for product
                        double weight = 0;                                      // used if barcode includes weight of product
                        double dUnits = 0;                                      // used for pro-rata unit
                        String sVariableTypePrefix = sCode.substring(0, 2);     // get first two PPP digits                        
                        String sVariableNum;                                    // CCCCC variable value of barcode

                        if (sCode.length() == 13) {                             // full barcode from scanner
                            sVariableNum =  sCode.substring(8,12);              // get the 5 CCCCC digits              
                        } else {                                                // barcode can be any length
                            sVariableNum =  sCode.substring(7,11);              // get the 5 CCCCC digits
                        }                                                       // scanner has dropped 1st digit so shift get to left    
        
//  PRICE - SET value decimals 
                        switch (sVariableTypePrefix) {                          // Use CCCCC value of 01049 as example
                            case "02":                                          // first 2 PPP digits determine decimal position
                                dUnits = (Double.parseDouble(sVariableNum)      // position decimal in CCC.CC
                                        / 100) / oProduct.getPriceSell();       // 2 decimal = 010.49 
                                break;
                            case "20":
                                dUnits = (Double.parseDouble(sVariableNum)      // position decimal in CCC.CC
                                        / 100) / oProduct.getPriceSell();       // 2 decimal = 010.49
                                break;
                            case "21":
                                dUnits = (Double.parseDouble(sVariableNum)      // position decimal in CC.CCC
                                        / 10) / oProduct.getPriceSell();        // 2 decimal = 0104.9                                
                                break;
                            case "22":
                                dUnits = Double.parseDouble(sVariableNum)       // position decimal in CCCC.C
                                        / oProduct.getPriceSell();              // Price = 01049.                                
                                break;

//  WEIGHT - SET value decimals                                 
                            case "23":                                          // Use CCCCC 01049kg as example
                                weight = Double.parseDouble(sVariableNum)       
                                        / 1000;                                 // Weight = 01.049
                                dUnits = weight;                                // set Units for price calculation
                                break;
                            case "24":
                                weight = Double.parseDouble(sVariableNum) 
                                        / 100;                                  // Weight = 010.49
                                dUnits = weight;                                // set Units for price calculation                             
                                break;
                            case "25":
                                weight = Double.parseDouble(sVariableNum)  
                                        / 10;                                   // Weight = 0104.9
                                dUnits = weight;                                // set Units for price calculation                            
                                break;
                            default:
                                break;
                        }

                        TaxInfo tax = taxeslogic                                // get the TaxRate for the product
                            .getTaxInfo(oProduct.getTaxCategoryID()             
                            , m_oTicket.getCustomer());                         // calculate if ticket has a Customer

                        switch (sVariableTypePrefix) {
//  PRICE - Assign var's
                            case "02":                                          // now we need to calculate some values
                                dPriceSell = oProduct.getPriceSellTax(tax)  
                                    / (1.0 + tax.getRate());                    // selling price with tax
                                dUnits = (Double.parseDouble(sVariableNum) 
                                    / 100) / oProduct.getPriceSellTax(tax);     // Units as proportion of selling price
                                oProduct.setProperty("product.price"
                                    , Double.toString(oProduct.getPriceSell())); // push to screen                                    
                                break; 
                            case "20":                                          // as above
                                dPriceSell = oProduct.getPriceSellTax(tax) 
                                    / (1.0 + tax.getRate());
                                dUnits = (Double.parseDouble(sVariableNum) 
                                    / 100) / oProduct.getPriceSellTax(tax);
                                oProduct.setProperty("product.price"
                                    , Double.toString(oProduct.getPriceSellTax(tax)));                                    
                                break;  
                            case "21":
                                dPriceSell = oProduct.getPriceSellTax(tax) 
                                    / (1.0 + tax.getRate());
                                dUnits = (Double.parseDouble(sVariableNum) 
                                    / 10) / oProduct.getPriceSellTax(tax);
                                oProduct.setProperty("product.price"
                                    , Double.toString(oProduct.getPriceSell()));                                    
                                break;                                
                            case "22":
                                dPriceSell = oProduct.getPriceSellTax(tax) 
                                    / (1.0 + tax.getRate());
                                dUnits = (Double.parseDouble(sVariableNum) 
                                    / 1) / oProduct.getPriceSellTax(tax);
                                oProduct.setProperty("product.price"
                                    , Double.toString(oProduct.getPriceSell()));                                    
                                break;

// WEIGHT - Assign variable to Unit
                            case "23":
                                weight = Double.parseDouble(sVariableNum) 
                                    / 1000;                                     // 3 decimals = 01.049 kg
                                dUnits = weight;                                // which represents 1gramme Units
                                oProduct.setProperty("product.weight"
                                    , Double.toString(weight));
                                oProduct.setProperty("product.price"
                                    , Double.toString(dPriceSell));                                                                    
                                break;                                
                            case "24":
                                weight = Double.parseDouble(sVariableNum) 
                                    / 100;                                      // 2 decimals = 010.49 kg
                                dUnits = weight;                                // which represents 10gramme Units 
                                oProduct.setProperty("product.weight"
                                    , Double.toString(weight));
                                oProduct.setProperty("product.price"
                                    , Double.toString(dPriceSell));                                                                    
                                break;                                
                            case "25":
                                weight = Double.parseDouble(sVariableNum) 
                                    / 10;                                       // 1 decimal = 0104.9 kg
                                dUnits = weight;                                // which represents 100gramme Units
                                oProduct.setProperty("product.weight"
                                    , Double.toString(weight));
                                oProduct.setProperty("product.price"
                                    , Double.toString(dPriceSell));                                    
                                break;

/*
 *  Some countries use different barcode prefix 26-29 or 250 etc.
 *  Use this section to add more case statements but these are not mandatory
 *  If you have your own internal or other barcode schema then...
 *  Example:
        case "28":
        {
        // price has tax. Remove it from sPriceSell
            TaxInfo tax = taxeslogic.getTaxInfo(oProduct.getTaxCategoryID(), m_oTicket.getCustomer());
            dPriceSell /= (1.0 + tax.getRate());
            oProduct.setProperty("product.price", Double.toString(dPriceSell));
            weight = -1.0;
        break;
*/                                
                            default:
                            break;
                        }
                            
                        if(m_jaddtax.isSelected()) {
                            addTicketLine(oProduct
                            , dUnits //weight
                            , dPriceSell = oProduct.getPriceSellTax(tax)); 
                        } else {
                            addTicketLine(oProduct
                            , dUnits
                            , dPriceSell);
                        }
                    }
                } catch(BasicException eData) {
                    stateToZero();
                    LOGGER.log(Level.SEVERE, "", eData);
                    new MessageInf(eData).show(this);
                }
                
// UPC-A
/* Note: if begins 02 then its a standard                 
// UPC-A max value limitation is 4 digit price
// UPC-A Extended uses State digit to give 5 digit price
// KrOS POS does not support UPC-A Extended at this time                
// Identifier   Prod    State   Cost    CheckCode
// I            PPPPP   S       CCCC    K                        
// 1            23456   7       8901    2

 *    0 = Standard UPC number (must have a zero to do zero-suppressed numbers)
 *    1 = Reserved
 *    2 = Random-weight items (fruits, vegetables, meats, etc.)
 *    3 = Pharmaceuticals
 *    4 = In-store marketing for retailers (Other stores will not understand)
 *    5 = Coupons
 *    6 = Standard UPC number
 *    7 = Standard UPC number 
 *    8 = Reserved
 *    9 = Reserved
*/

                } else if ( "UPC".equals(sCodetype) 
                        && (sCode.startsWith("2"))
                        && (sCode.length() == 12)) {                 
                
                try {
                    ProductInfoExt oProduct 
                        = dlSales.getProductInfoByUShortCode(sCode);            // Return only UPC product

                    if(oProduct == null) {
                        Toolkit.getDefaultToolkit().beep();
                        JOptionPane.showMessageDialog(null,
                            sCode + " - " 
                            + AppLocal.getIntString("message.noproduct"),
                            "Check", JOptionPane.WARNING_MESSAGE);  
                        stateToZero();
                    } else if("Upc-A".equals(oProduct.getCodetype())) {
                        oProduct.setProperty("product.barcode", sCode); 
                        double dPriceSell = oProduct.getPriceSell();            // default price for product
                        double weight = 0;                                      // used if barcode includes weight of product
                        double dUnits = 0;                                      // used for pro-rata unit
                        String sVariableNum = sCode.substring(7, 11);           // grab the value from the code only using 4 digit price
                        
                        TaxInfo tax = taxeslogic                                // get the TaxRate for the product
                            .getTaxInfo(oProduct.getTaxCategoryID()             
                            , m_oTicket.getCustomer());                        
                        
                    if (oProduct.getPriceSell() != 0.0) {                       // we have a weight barcode
                        weight = Double.parseDouble(sVariableNum) / 100;        // 2 decimals (e.g. 10.49 kg)
                        dUnits = weight;                                        // Units is now transformed to weight
                        
                        oProduct.setProperty("product.weight"                   // catch-all for weight
                                , Double.toString(weight));
                        oProduct.setProperty("product.price"                    // get the prod sellprice
                                , Double.toString(oProduct.getPriceSell()));                      
                        dPriceSell = oProduct.getPriceSellTax(tax);             // calculate the tax on sellprice
                        dUnits = (Double.parseDouble(sVariableNum)              // calculate Units in sellprice with Tax
                                / 100)
                                / oProduct.getPriceSellTax(tax);
                        
                    } else {                                                    // no sellprice so we have a price barcode
                        dPriceSell = (Double.parseDouble(sVariableNum)          // calculate Units in sellprice with Tax
                                / 100);   
                        dUnits = 1;                                             // no sellprice to calculate so must be 1 Unit
                    }

                        if(m_jaddtax.isSelected()) {
                            addTicketLine(oProduct
                            , dUnits
                            , dPriceSell);
                        } else {
                            addTicketLine(oProduct
                            , dUnits
                            , dPriceSell / (1.0 + tax.getRate()));
                        }
                    }
                    } catch(BasicException eData) {
                        stateToZero();
                        LOGGER.log(Level.SEVERE, "", eData);
                        new MessageInf(eData).show(this);
                    }

            } else {
                incProductByCode(sCode);                                        // returned is standard so go get it
            }
// END OF BARCODE            
               
        } else {
            Toolkit.getDefaultToolkit().beep();
        }

    } else {

            m_sBarcode.append(cTrans);

            if (cTrans == '\u007f') { 
                stateToZero();

            } else if ((cTrans == '0') && (m_iNumberStatus == NUMBER_INPUTZERO)) {
                m_jPrice.setText(Character.toString('0'));
 
            } else if ((cTrans == '1' || cTrans == '2' || cTrans == '3' 
                    || cTrans == '4' || cTrans == '5' || cTrans == '6' 
                    || cTrans == '7' || cTrans == '8' || cTrans == '9') 
                    && (m_iNumberStatus == NUMBER_INPUTZERO)) {

                if (!priceWith00){
                    m_jPrice.setText(m_jPrice.getText() + cTrans);  
                }else{ 
                   m_jPrice.setText(setTempjPrice( m_jPrice.getText() + cTrans));  
                }
                
                m_iNumberStatus = NUMBER_INPUTINT;
                m_iNumberStatusInput = NUMBERVALID;

            } else if ((cTrans == '0' || cTrans == '1' || cTrans == '2' 
                    || cTrans == '3' || cTrans == '4' || cTrans == '5' 
                    || cTrans == '6' || cTrans == '7' || cTrans == '8' 
                    || cTrans == '9') 
                    && (m_iNumberStatus == NUMBER_INPUTINT)) {

                if (!priceWith00){
                    m_jPrice.setText(m_jPrice.getText() + cTrans);
                }else{ 
                   m_jPrice.setText(setTempjPrice( m_jPrice.getText() + cTrans));  
                }


            } else if (cTrans == '.' 
                && m_iNumberStatus == NUMBER_INPUTZERO && !priceWith00) {
                m_jPrice.setText("0.");
                m_iNumberStatus = NUMBER_INPUTZERODEC;
            } else if (cTrans == '.' 
                && m_iNumberStatus == NUMBER_INPUTZERO) {
                m_jPrice.setText("");
                m_iNumberStatus = NUMBER_INPUTZERO;
            } else if (cTrans == '.' 
                && m_iNumberStatus == NUMBER_INPUTINT && !priceWith00) {
                m_jPrice.setText(m_jPrice.getText() + ".");
                m_iNumberStatus = NUMBER_INPUTDEC;
            } else if (cTrans == '.' 
                && m_iNumberStatus == NUMBER_INPUTINT) {

                if (!priceWith00){
                    m_jPrice.setText(m_jPrice.getText() + "00");
                }else{ 
                   m_jPrice.setText(setTempjPrice( m_jPrice.getText() + "00"));  
                }                

                m_iNumberStatus = NUMBER_INPUTINT;

            } else if ((cTrans == '0') 
                && (m_iNumberStatus == NUMBER_INPUTZERODEC 
                || m_iNumberStatus == NUMBER_INPUTDEC)) {

                if (!priceWith00){
                    m_jPrice.setText(m_jPrice.getText() + cTrans);
                }else{ 
                    m_jPrice.setText(setTempjPrice( m_jPrice.getText() + cTrans)); 
                }                
                 
            } else if ((cTrans == '1' || cTrans == '2' || cTrans == '3' 
                    || cTrans == '4' || cTrans == '5' || cTrans == '6' 
                    || cTrans == '7' || cTrans == '8' || cTrans == '9') 
                    && (m_iNumberStatus == NUMBER_INPUTZERODEC 
                    || m_iNumberStatus == NUMBER_INPUTDEC)) {

                m_jPrice.setText(m_jPrice.getText() + cTrans);
                m_iNumberStatus = NUMBER_INPUTDEC;
                m_iNumberStatusInput = NUMBERVALID;

            } else if (cTrans == '*' 
                && (m_iNumberStatus == NUMBER_INPUTINT 
                || m_iNumberStatus == NUMBER_INPUTDEC)) {
                m_jPor.setText("x");
                m_iNumberStatus = NUMBER_PORZERO;
            } else if (cTrans == '*' 
                && (m_iNumberStatus == NUMBER_INPUTZERO 
                || m_iNumberStatus == NUMBER_INPUTZERODEC)) {
                m_jPrice.setText("0");
                m_jPor.setText("x");
                m_iNumberStatus = NUMBER_PORZERO;

            } else if ((cTrans == '0') 
                && (m_iNumberStatus == NUMBER_PORZERO)) {
                m_jPor.setText("x0");
            } else if ((cTrans == '1' || cTrans == '2' || cTrans == '3' 
                    || cTrans == '4' || cTrans == '5' || cTrans == '6' 
                    || cTrans == '7' || cTrans == '8' || cTrans == '9') 
                    && (m_iNumberStatus == NUMBER_PORZERO)) {

                m_jPor.setText("x" + Character.toString(cTrans));
                m_iNumberStatus = NUMBER_PORINT;
                m_iNumberStatusPor = NUMBERVALID;
            } else if ((cTrans == '0' || cTrans == '1' || cTrans == '2' 
                    || cTrans == '3' || cTrans == '4' || cTrans == '5' 
                    || cTrans == '6' || cTrans == '7' || cTrans == '8' 
                    || cTrans == '9') && (m_iNumberStatus == NUMBER_PORINT)) {

                m_jPor.setText(m_jPor.getText() + cTrans);

            } else if (cTrans == '.' 
                && m_iNumberStatus == NUMBER_PORZERO && !priceWith00) {
                m_jPor.setText("x0.");
                m_iNumberStatus = NUMBER_PORZERODEC;
            } else if (cTrans == '.' 
                && m_iNumberStatus == NUMBER_PORZERO) {
                m_jPor.setText("x");
                m_iNumberStatus = NUMBERVALID;
            } else if (cTrans == '.' 
                && m_iNumberStatus == NUMBER_PORINT && !priceWith00) {
                m_jPor.setText(m_jPor.getText() + ".");
                m_iNumberStatus = NUMBER_PORDEC;
            } else if (cTrans == '.' 
                && m_iNumberStatus == NUMBER_PORINT) {
                m_jPor.setText(m_jPor.getText() + "00");
                m_iNumberStatus = NUMBERVALID;

            } else if ((cTrans == '0') 
                && (m_iNumberStatus == NUMBER_PORZERODEC 
                || m_iNumberStatus == NUMBER_PORDEC)) {
                m_jPor.setText(m_jPor.getText() + cTrans);
            } else if ((cTrans == '1' || cTrans == '2' || cTrans == '3' 
                    || cTrans == '4' || cTrans == '5' || cTrans == '6' 
                    || cTrans == '7' || cTrans == '8' || cTrans == '9') 
                    && (m_iNumberStatus == NUMBER_PORZERODEC || m_iNumberStatus == NUMBER_PORDEC)) {

                m_jPor.setText(m_jPor.getText() + cTrans);
                m_iNumberStatus = NUMBER_PORDEC;
                m_iNumberStatusPor = NUMBERVALID;
            
            } else if (cTrans == '\u00a7'   
                && m_iNumberStatusInput == NUMBERVALID 
                && m_iNumberStatusPor == NUMBERZERO) {

                if (m_App.getDeviceScale().existsScale() 
                    && m_App.getAppUserView().getUser().hasPermission("sales.EditLines")) {
                    try {
                        Double value = m_App.getDeviceScale().readWeight();
                        if (value != null) {
                            ProductInfoExt product = getInputProduct();
                            addTicketLine(product, value, product.getPriceSell());
                        }
                    } catch (ScaleException e) {
                        Toolkit.getDefaultToolkit().beep();
                        new MessageInf(MessageInf.SGN_WARNING, AppLocal.getIntString("message.noweight"), e).show(this);           
                        stateToZero(); 
                    }
                } else {

                    Toolkit.getDefaultToolkit().beep();
                }
            } else if (cTrans == '\u00a7' 
                && m_iNumberStatusInput == NUMBERZERO
                && m_iNumberStatusPor == NUMBERZERO) {

                int i = m_ticketlines.getSelectedIndex();
                if (i < 0){
                    Toolkit.getDefaultToolkit().beep();
                } else if (m_App.getDeviceScale().existsScale()) {
                    try {
                        Double value = m_App.getDeviceScale().readWeight();
                        if (value != null) {
                            TicketLineInfo newline = new TicketLineInfo(m_oTicket.getLine(i));
                            newline.setMultiply(value);
                            newline.setPrice(Math.abs(newline.getPrice()));
                            paintTicketLine(i, newline);
                        }
                    } catch (ScaleException e) {
                        Toolkit.getDefaultToolkit().beep();
                        new MessageInf(MessageInf.SGN_WARNING, AppLocal.getIntString("message.noweight"), e).show(this);           
                        stateToZero(); 
                    }
                } else {

                    Toolkit.getDefaultToolkit().beep();
                }      
                
            } else if (cTrans == '+' 
                && m_iNumberStatusInput == NUMBERZERO 
                && m_iNumberStatusPor == NUMBERZERO) {
                int i = m_ticketlines.getSelectedIndex();

                if (i < 0){
                    Toolkit.getDefaultToolkit().beep();
                } else {
                    TicketLineInfo newline = new TicketLineInfo(m_oTicket.getLine(i));
                    //If it's a refund + button means one unit less
                    if (m_oTicket.getTicketType() == TicketInfo.RECEIPT_REFUND){
                        if (m_App.getProperties().getProperty("override.check").equals("true")) {
                            oCount = count - 1;  //increment existing line  
                            pinOK=false;
                            changeCount(pinOK);
                            newline.setMultiply(newline.getMultiply() - 1.0);
                            newline.setProperty("ticket.updated", "true");
                            paintTicketLine(i, newline);
                        } else {
                            newline.setMultiply(newline.getMultiply() - 1.0);
                            newline.setProperty("ticket.updated", "true");
                            paintTicketLine(i, newline);                            
                        }                    
                    } else {
                        if (m_App.getProperties().getProperty("override.check").equals("true")) {
                            oCount = count + 1;  //increment existing line  
                            pinOK=false;
                            if (changeCount(pinOK)) {
                                newline.setMultiply(newline.getMultiply() + 1.0);
                                newline.setProperty("ticket.updated", "true");
                                paintTicketLine(i, newline);
                            }
                        } else {
                            newline.setMultiply(newline.getMultiply() + 1.0);
                            newline.setProperty("ticket.updated", "true");
                            paintTicketLine(i, newline);                            
                        }
                    }
                }
            } else if (cTrans == '-' 
                && m_iNumberStatusInput == NUMBERZERO 
                && m_iNumberStatusPor == NUMBERZERO
                && m_App.getAppUserView().getUser().hasPermission("sales.EditLines")) {
                
                int i = m_ticketlines.getSelectedIndex();
                if (i < 0){
                    Toolkit.getDefaultToolkit().beep();
                } else {
                    TicketLineInfo newline = new TicketLineInfo(m_oTicket.getLine(i));

                    if (m_oTicket.getTicketType() == TicketInfo.RECEIPT_REFUND){
                        if (m_App.getProperties().getProperty("override.check").equals("true")) {
                            oCount = count - 1;  //increment existing line  
                            pinOK=false;
                            changeCount(pinOK);
                            newline.setMultiply(newline.getMultiply() - 1.0);
                            newline.setProperty("ticket.updated", "true");
                            paintTicketLine(i, newline);
                        } else {
                            newline.setMultiply(newline.getMultiply() - 1.0);
                            newline.setProperty("ticket.updated", "true");
                            paintTicketLine(i, newline);                            
                        } 

                        if (newline.getMultiply() >= 0) {
                            removeTicketLine(i);
                        } else {
                            paintTicketLine(i, newline);
                        }
                    } else {
                        if (m_App.getProperties().getProperty("override.check").equals("true")) {
                            oCount = count - 1;  //increment existing line  
                            pinOK=false;
                            if (changeCount(pinOK)) {
                                newline.setMultiply(newline.getMultiply() - 1.0);
                                newline.setProperty("ticket.updated", "true");
                                paintTicketLine(i, newline);
                            }
                        } else {
                            newline.setMultiply(newline.getMultiply() - 1.0);
                            newline.setProperty("ticket.updated", "true");
                            paintTicketLine(i, newline);                            
                        }

                        if (newline.getMultiply() <= 0.0) {                   
                            removeTicketLine(i);
                        } else {
                            paintTicketLine(i, newline);
                        }
                    }
                }

            } else if (cTrans == '+' 
                && m_iNumberStatusInput == NUMBERZERO 
                && m_iNumberStatusPor == NUMBERVALID) {
                int i = m_ticketlines.getSelectedIndex();

                if (i < 0){
                    Toolkit.getDefaultToolkit().beep();
                } else {
                    double dPor = getPorValue();
                    TicketLineInfo newline = new TicketLineInfo(m_oTicket.getLine(i)); 

                    if (m_oTicket.getTicketType() == TicketInfo.RECEIPT_REFUND) {
                        if (m_App.getProperties().getProperty("override.check").equals("true")) {
                            oCount = count - 1;  //increment existing line  
                            pinOK=false;
                            changeCount(pinOK);
                            newline.setMultiply(-dPor);
                            newline.setProperty("ticket.updated", "true");
                            newline.setPrice(Math.abs(newline.getPrice()));
                            paintTicketLine(i, newline);                
                        } else {
                            newline.setMultiply(-dPor);
                            newline.setProperty("ticket.updated", "true");
                            newline.setPrice(Math.abs(newline.getPrice()));
                            paintTicketLine(i, newline);                
                        }                    
                    } else {
                        if (m_App.getProperties().getProperty("override.check").equals("true")) {
                            oCount = count + 1;  //increment existing line  
                            pinOK=false;
                            if (changeCount(pinOK)) {
                            newline.setMultiply(dPor);
                            newline.setProperty("ticket.updated", "true");
                            newline.setPrice(Math.abs(newline.getPrice()));
                            paintTicketLine(i, newline);
                            }
                        } else {
                            newline.setMultiply(dPor);
                            newline.setProperty("ticket.updated", "true");
                            newline.setPrice(Math.abs(newline.getPrice()));
                            paintTicketLine(i, newline);
                        }
                    }
                }
            } else if (cTrans == '-' 
                && m_iNumberStatusInput == NUMBERZERO 
                && m_iNumberStatusPor == NUMBERVALID
                && m_App.getAppUserView().getUser().hasPermission("sales.EditLines")) {                
                int i = m_ticketlines.getSelectedIndex();

                if (i < 0){
                    Toolkit.getDefaultToolkit().beep();
                } else {
                    double dPor = getPorValue();
                    TicketLineInfo newline = new TicketLineInfo(m_oTicket.getLine(i));

                   if (m_oTicket.getTicketType() == TicketInfo.RECEIPT_REFUND) {
                        if (m_App.getProperties().getProperty("override.check").equals("true")) {
                            oCount = count - 1;  //increment existing line  
                            pinOK=false;
                            changeCount(pinOK);
                            newline.setMultiply(-dPor);
                            newline.setProperty("ticket.updated", "true");
                            newline.setPrice(Math.abs(newline.getPrice()));
                            paintTicketLine(i, newline);                
                        } else {
                            newline.setMultiply(-dPor);
                            newline.setProperty("ticket.updated", "true");
                            newline.setPrice(Math.abs(newline.getPrice()));
                            paintTicketLine(i, newline);                
                        }                    
                    } else {
                        if (m_App.getProperties().getProperty("override.check").equals("true")) {
                            oCount = count - 1;  //increment existing line  
                            pinOK=false;
                            if (changeCount(pinOK)) {
                            newline.setMultiply(dPor);
                            newline.setProperty("ticket.updated", "true");
                            newline.setPrice(Math.abs(newline.getPrice()));
                            paintTicketLine(i, newline);
                            }
                        } else {
                            newline.setMultiply(dPor);
                            newline.setProperty("ticket.updated", "true");
                            newline.setPrice(Math.abs(newline.getPrice()));
                            paintTicketLine(i, newline);
                        }
                    }
                }
            } else if (cTrans == '+' 
                && m_iNumberStatusInput == NUMBERVALID 
                && m_iNumberStatusPor == NUMBERZERO
                && m_App.getAppUserView().getUser().hasPermission("sales.EditLines")) {
                ProductInfoExt product = getInputProduct();
                addTicketLine(product, 1.0, product.getPriceSell());
                m_jEditLine.doClick();

            } else if (cTrans == '-' 
                && m_iNumberStatusInput == NUMBERVALID 
                && m_iNumberStatusPor == NUMBERZERO
                && m_App.getAppUserView().getUser().hasPermission("sales.EditLines")) {
                ProductInfoExt product = getInputProduct();
                addTicketLine(product, 1.0, -product.getPriceSell());
                m_jEditLine.doClick();

            } else if (cTrans == '+' 
                && m_iNumberStatusInput == NUMBERVALID 
                && m_iNumberStatusPor == NUMBERVALID
                && m_App.getAppUserView().getUser().hasPermission("sales.EditLines")) {
                ProductInfoExt product = getInputProduct();
                addTicketLine(product, getPorValue(), product.getPriceSell());

            } else if (cTrans == '-' 
                && m_iNumberStatusInput == NUMBERVALID 
                && m_iNumberStatusPor == NUMBERVALID
                && m_App.getAppUserView().getUser().hasPermission("sales.EditLines")) {
                ProductInfoExt product = getInputProduct();
                addTicketLine(product, getPorValue(), -product.getPriceSell());

            } else if (cTrans == ' ' || cTrans == '=') {
                if (m_oTicket.getLinesCount() > 0) {
                    if (closeTicket(m_oTicket, m_oTicketExt)) {
                        m_ticketsbag.deleteTicket();  
                        String autoLogoff = (m_App.getProperties().getProperty("till.autoLogoff"));
                        if (autoLogoff != null){               
                            if (autoLogoff.equals("true")){                    
                                if ("restaurant".equals(
                                    m_App.getProperties().getProperty("machine.ticketsbag"))
                                    && ("true".equals(m_App.getProperties().getProperty("till.autoLogoffrestaurant")))){
                                    deactivate();
                                    setActiveTicket(null, null); 
                                }else {
                                    ((JRootApp)m_App).closeAppView();   
                                }    
                            }                   
                        };                       
                    } else {
                        refreshTicket();
                    }
                } else {
                    Toolkit.getDefaultToolkit().beep();
                }
            }
        }
    }

    
    private boolean closeTicket(TicketInfo ticket, Object ticketext) {
        if (listener  != null) {
            listener.stop();
        }
        boolean resultok = false;
        
        if (m_App.getAppUserView().getUser().hasPermission("sales.Total")) {  

            warrantyCheck(ticket);
            
            try {

                taxeslogic.calculateTaxes(ticket);
                if (ticket.getTotal()>=0.0){
                    ticket.resetPayments();
                }
                
                if (executeEvent(ticket, ticketext, "ticket.total") == null) {
                    if (listener  != null) {
                        listener.stop();
                    }

                    printTicket("Printer.TicketTotal", ticket, ticketext);
                   
                    JPaymentSelect paymentdialog = ticket.getTicketType() == TicketInfo.RECEIPT_NORMAL
                            ? paymentdialogreceipt
                            : paymentdialogrefund;
                    paymentdialog.setPrintSelected("true".equals(m_jbtnconfig.getProperty("printselected", "true")));
                    
                    paymentdialog.setTransactionID(ticket.getTransactionID());

                    if (paymentdialog.showDialog(ticket.getTotal(), ticket.getCustomer())) {

                        ticket.setPayments(paymentdialog.getSelectedPayments());

                        ticket.setUser(m_App.getAppUserView().getUser().getUserInfo());
                        ticket.setActiveCash(m_App.getActiveCashIndex());
                        ticket.setDate(new Date());

                        if (executeEvent(ticket, ticketext, "ticket.save") == null) {

                            try {
                                dlSales.saveTicket(ticket, m_App.getInventoryLocation());  
                                m_config.setProperty("lastticket.number", Integer.toString(ticket.getTicketId()));
                                m_config.setProperty("lastticket.type", Integer.toString(ticket.getTicketType()));
                                m_config.save();     
                                
                            } catch (BasicException eData) {
                                MessageInf msg = new MessageInf(MessageInf.SGN_NOTICE, AppLocal.getIntString("message.nosaveticket"), eData);
                                msg.show(this);
                            } catch (IOException ex) {
                                Logger.getLogger(JPanelTicket.class.getName()).log(Level.SEVERE, null, ex);
                            }

                            executeEvent(ticket, ticketext, "ticket.close", 
                                    new ScriptArg("print", paymentdialog.isPrintSelected()));

                                printTicket(paymentdialog.isPrintSelected() || warrantyPrint
                                    ? "Printer.Ticket"
                                    : "Printer.Ticket2", ticket, ticketext);  
                                    Notify(AppLocal.getIntString("notify.printing"));                 
   
                            resultok = true;

                            if ("restaurant".equals(m_App.getProperties()
                                .getProperty("machine.ticketsbag")) && !ticket.getOldTicket()) { 
                                restDB.clearCustomerNameInTable(ticketext.toString());
                                restDB.clearWaiterNameInTable(ticketext.toString());
                                restDB.clearTicketIdInTable(ticketext.toString());
                            }                                
                        }
                    }
                }
            } catch (TaxesException e) {
                MessageInf msg = new MessageInf(MessageInf.SGN_WARNING, 
                    AppLocal.getIntString("message.cannotcalculatetaxes"));
                msg.show(this);
                resultok = false;
            }

            m_oTicket.resetTaxes();
            m_oTicket.resetPayments();
           
            jCheckStock.setText("");
    
        }
                
        return resultok;        
    }   

    private boolean warrantyCheck(TicketInfo ticket) {    
        warrantyPrint=false;
        int lines=0;
        while (lines < ticket.getLinesCount()) {             
            if (!warrantyPrint){
                warrantyPrint = ticket.getLine(lines).isProductWarranty();
                return (true);
            }
            lines++;
            }
        return false;
    }
   
    /**
     *
     * @param pTicket
     * @return
     */
    public String getPickupString(TicketInfo pTicket){ 
    if (pTicket == null){    
        return("0");
    }
     String tmpPickupId=Integer.toString(pTicket.getPickupId());
     String pickupSize =(m_App.getProperties().getProperty("till.pickupsize"));    
        if (pickupSize!=null && (Integer.parseInt(pickupSize) >= tmpPickupId.length())){        
            while (tmpPickupId.length()< (Integer.parseInt(pickupSize))){
            tmpPickupId="0"+tmpPickupId;}
        } 
       return (tmpPickupId);      
    }
    
    
    private void printTicket(String sresourcename, TicketInfo ticket, Object ticketext) {

        String sresource = dlSystem.getResourceAsXML(sresourcename);
        if (sresource == null) {
            MessageInf msg = new MessageInf(MessageInf.SGN_WARNING, AppLocal.getIntString("message.cannotprintticket"));
            msg.show(JPanelTicket.this);
        } else {
            if (ticket.getPickupId()== 0){
                try{
                    ticket.setPickupId(dlSales.getNextPickupIndex());
                }catch (BasicException e){
                    ticket.setPickupId(0);
                }
            }
            try {
                ScriptEngine script = ScriptFactory.getScriptEngine(ScriptFactory.VELOCITY);

                if (Boolean.parseBoolean(m_App.getProperties().getProperty("receipt.newlayout"))){
                    script.put("taxes",ticket.getTaxLines());                       
                } else {
                    script.put("taxes", taxcollection);            
                }
                
                script.put("taxeslogic", taxeslogic);
                script.put("ticket", ticket);
                script.put("place", ticketext);
                script.put("warranty", warrantyPrint);
                script.put("pickupid",getPickupString(ticket));

                refreshTicket();                
                
                m_TTP.printTicket(script.eval(sresource).toString(), ticket);

            } catch (    ScriptException | TicketPrinterException e) {
                MessageInf msg = new MessageInf(MessageInf.SGN_WARNING, AppLocal.getIntString("message.cannotprintticket"), e);
                msg.show(JPanelTicket.this);
            }
        }
    }
     public void printTicket(String resource) {
//        printTicket(resource, m_oTicket, m_oTicketExt);
// this method is intended to be called only from JPanelButtons.

        if (resource == null) {
            MessageInf msg = new MessageInf(MessageInf.SGN_WARNING, AppLocal.getIntString("message.cannotexecute"));
            msg.show(this);
        } else {
// JG 5 Jul 17 calculate taxes disabled as causing incorrect tax recalc
//            taxeslogic.calculateTaxes(m_oTicket);
            printTicket(resource, m_oTicket, m_oTicketExt);
        }
    
         Notify(AppLocal.getIntString("notify.printed"));                                    
        j_btnRemotePrt.setEnabled(false);
    }
    
    public void customerAdd(String resource) {
        Notify(AppLocal.getIntString("notify.customeradd"));                                    
    }          
     
     public void customerRemove(String resource) {
        Notify(AppLocal.getIntString("notify.customerremove"));                                    
    }     
     
    public void customerChange(String resource) {
        Notify(AppLocal.getIntString("notify.customerchange"));                                    
    }     
     
    public void Notify(String msg){
        final WebNotification notification = new WebNotification ();
        notification.setIcon ( NotificationIcon.information );
        notification.setDisplayTime ( 4000 );

        final WebClock clock = new WebClock ();
        clock.setClockType ( ClockType.timer );
        clock.setTimeLeft ( 5000 );
//        clock.setTimePattern ( clock );        
        notification.setContent ( msg );

        NotificationManager.showNotification ( notification );
        clock.start ();    
    }    
    
    private void printReport(String resourcefile, TicketInfo ticket, Object ticketext) {

        try {     
         
            JasperReport jr;
           
            InputStream in = getClass().getResourceAsStream(resourcefile + ".ser");
            if (in == null) {      
                // read and compile the report
                JasperDesign jd = JRXmlLoader.load(getClass().getResourceAsStream(resourcefile + ".jrxml"));            
                jr = JasperCompileManager.compileReport(jd);    
            } else {
                try (ObjectInputStream oin = new ObjectInputStream(in)) {
                    jr = (JasperReport) oin.readObject();
                }
            }
          

            Map reportparams = new HashMap();

            try {
                reportparams.put("REPORT_RESOURCE_BUNDLE", ResourceBundle.getBundle(resourcefile + ".properties"));
            } catch (MissingResourceException e) {
            }
            reportparams.put("TAXESLOGIC", taxeslogic); 
            
            Map reportfields = new HashMap();
            reportfields.put("TICKET", ticket);
            reportfields.put("PLACE", ticketext);

            JasperPrint jp = JasperFillManager.fillReport(jr, reportparams, new JRMapArrayDataSource(new Object[] { reportfields } ));
            
            PrintService service = ReportUtils.getPrintService(m_App.getProperties().getProperty("machine.printername"));
            
            JRPrinterAWT300.printPages(jp, 0, jp.getPages().size() - 1, service);
            
        } catch (JRException | IOException | ClassNotFoundException e) {
            MessageInf msg = new MessageInf(MessageInf.SGN_WARNING, AppLocal.getIntString("message.cannotloadreport"), e);
            msg.show(this);
        }               
    }

    private void visorTicketLine(TicketLineInfo oLine){
        if (oLine == null) { 
             m_App.getDeviceTicket().getDeviceDisplay().clearVisor();
        } else {                 
            try {
                ScriptEngine script = ScriptFactory.getScriptEngine(ScriptFactory.VELOCITY);
                script.put("ticketline", oLine);
                m_TTP.printTicket(script.eval(dlSystem.getResourceAsXML("Printer.TicketLine")).toString());

            } catch (    ScriptException | TicketPrinterException e) {
                MessageInf msg = new MessageInf(MessageInf.SGN_WARNING, AppLocal.getIntString("message.cannotprintline"), e);
                msg.show(JPanelTicket.this);
            }
        } 
    }    
    
    
    private Object evalScript(ScriptObject scr, String resource, ScriptArg... args) {
        
        // resource here is guaranteed to be not null
         try {
            scr.setSelectedIndex(m_ticketlines.getSelectedIndex());
            return scr.evalScript(dlSystem.getResourceAsXML(resource), args);                
        } catch (ScriptException e) {
            MessageInf msg = new MessageInf(MessageInf.SGN_WARNING, AppLocal.getIntString("message.cannotexecute"), e);
            msg.show(this);
            return msg;
        } 
    }
        
    /**
     *
     * @param resource
     * @param args
     */
    public void evalScriptAndRefresh(String resource, ScriptArg... args) {

        if (resource == null) {
            MessageInf msg = new MessageInf(MessageInf.SGN_WARNING, AppLocal.getIntString("message.cannotexecute"));
            msg.show(this);            
        } else {
            ScriptObject scr = new ScriptObject(m_oTicket, m_oTicketExt);
            scr.setSelectedIndex(m_ticketlines.getSelectedIndex());
            evalScript(scr, resource, args);   
            refreshTicket();

            setSelectedIndex(scr.getSelectedIndex());
        }
    }

    /**
     *
     */
    private Object executeEventAndRefresh(String eventkey, ScriptArg ... args) {

        String resource = m_jbtnconfig.getEvent(eventkey);

        if (resource == null) {
            return null;
        } else {
            ScriptObject scr = new ScriptObject(m_oTicket, m_oTicketExt);
            scr.setSelectedIndex(m_ticketlines.getSelectedIndex());
            Object result = evalScript(scr, resource, args);   
            refreshTicket();
            setSelectedIndex(scr.getSelectedIndex());
            return result;
        }
    }
   
    
    private Object executeEvent(TicketInfo ticket, Object ticketext, String eventkey, ScriptArg ... args) {
        
        String resource = m_jbtnconfig.getEvent(eventkey);
        if (resource == null) {
            return null;
        } else {
            ScriptObject scr = new ScriptObject(ticket, ticketext);
            return evalScript(scr, resource, args);
        }
    }
    
    /**
     *
     * @param sresourcename
     * @return
     */
    public String getResourceAsXML(String sresourcename) {
        return dlSystem.getResourceAsXML(sresourcename);
    }

    /**
     *
     * @param sresourcename
     * @return
     */
    public BufferedImage getResourceAsImage(String sresourcename) {
        return dlSystem.getResourceAsImage(sresourcename);
    }
    
    private void setSelectedIndex(int i) {
        
        if (i >= 0 && i < m_oTicket.getLinesCount()) {
            m_ticketlines.setSelectedIndex(i);
        } else if (m_oTicket.getLinesCount() > 0) {
            m_ticketlines.setSelectedIndex(m_oTicket.getLinesCount() - 1);
        }    
    }
     
    /**
     *
     */
    public static class ScriptArg {
        private final String key;
        private final Object value;
        
        /**
         *
         * @param key
         * @param value
         */
        public ScriptArg(String key, Object value) {
            this.key = key;
            this.value = value;
        }

        /**
         *
         * @return
         */
        public String getKey() {
            return key;
        }

        /**
         *
         * @return
         */
        public Object getValue() {
            return value;
        }
    }

    
    private String setTempjPrice(String jPrice){
        jPrice = jPrice.replace(".","");
// remove all leading zeros from the string        
        long tempL=Long.parseLong(jPrice);
        jPrice = Long.toString(tempL);
        
        while (jPrice.length()<3){
            jPrice="0"+jPrice;                        
        }
        return (jPrice.length()<= 2)? jPrice : (new StringBuffer(jPrice).insert(jPrice.length()-2,".").toString());
    }

    public void checkStock() {
        int i = m_ticketlines.getSelectedIndex();

        if (i < 0) {
            } else {
                try {            
                    TicketLineInfo line = m_oTicket.getLine(i);
                    String pId = line.getProductID();
                    
                String lName = (m_App.getProperties().getProperty("machine.department"));
                lName = "'" + lName + "'";
                    ProductStock checkProduct;
                    String location = m_App.getInventoryLocation();
                    checkProduct = dlSales.getProductStockState(pId, location);
                    
                    if (checkProduct != null) {
        
                        if (checkProduct.getUnits() <=0) {
                            jCheckStock.setForeground(Color.magenta);
                        } else {
                            jCheckStock.setForeground(Color.darkGray);
                        }

                        double dUnits = checkProduct.getUnits();
                        int iUnits;
                        iUnits = (int) dUnits;
                        jCheckStock.setText(Integer.toString(iUnits));
                    } else {
                        jCheckStock.setText(null);
                    }
                } catch (BasicException ex) {
                    Logger.getLogger(JPanelTicket.class.getName()).log(Level.SEVERE, null, ex);
                }                    
            } 
        }
    
    public void checkCustomer() {
        if (m_oTicket.getCustomer().isVIP() == true) {

            String content;
            String vip;
            String discount;

            if (m_oTicket.getCustomer().isVIP() == true) {
                vip = AppLocal.getIntString("message.vipyes");
            } else {
                vip = AppLocal.getIntString("message.vipno");
            }
            if (m_oTicket.getCustomer().getDiscount() > 0) {
                discount = AppLocal.getIntString("message.discyes") + m_oTicket.getCustomer().getDiscount() + "%";
            } else {
                discount = AppLocal.getIntString("message.discno");
            }
                                
            content = "<html>"+
                "<b>" + AppLocal.getIntString("label.vip") + " : " + "</b>" + vip + "<br>" +
                "<b>" + AppLocal.getIntString("label.discount") + " : " + "</b>" + discount + "<br>";
                    

            JFrame frame = new JFrame();
            JOptionPane.showMessageDialog(frame, 
                content,                             
                "Info", 
                JOptionPane.WARNING_MESSAGE);  
        }        
    }
    
    /**
     *
     */
    public class ScriptObject {
        
        private final TicketInfo ticket;
        private final Object ticketext;
        
        private int selectedindex;
        
        private ScriptObject(TicketInfo ticket, Object ticketext) {
            this.ticket = ticket;
            this.ticketext = ticketext;
        }
        
        /**
         *
         * @return
         */
        public double getInputValue() {
            if (m_iNumberStatusInput == NUMBERVALID && m_iNumberStatusPor == NUMBERZERO) {
                return JPanelTicket.this.getInputValue();
            } else {
                return 0.0;
            }
        }
        
        /**
         *
         * @return
         */
        public int getSelectedIndex() {
            return selectedindex;
        }
        
        /**
         *
         * @param i
         */
        public void setSelectedIndex(int i) {
            selectedindex = i;
        }

        /**
         *
         * @param resourcefile
         */
        public void printReport(String resourcefile) {
            JPanelTicket.this.printReport(resourcefile, ticket, ticketext);
        }
        
        /**
         *
         * @param sresourcename
         */
        public void printTicket(String sresourcename) {
            JPanelTicket.this.printTicket(sresourcename, ticket, ticketext);
            j_btnRemotePrt.setEnabled(false);
        }
        
        /**
         *
         * @param code
         * @param args
         * @return Script object and value
         * @throws ScriptException
         */
        public Object evalScript(String code, ScriptArg... args) throws ScriptException {
            
        ScriptEngine script = ScriptFactory.getScriptEngine(ScriptFactory.BEANSHELL);            

        String sDBUser = m_App.getProperties().getProperty("db.user");
        String sDBPassword = m_App.getProperties().getProperty("db.password");
        
        if (sDBUser != null && sDBPassword != null && sDBPassword.startsWith("crypt:")) {
            AltEncrypter cypher = new AltEncrypter("cypherkey" + sDBUser);
            sDBPassword = cypher.decrypt(sDBPassword.substring(6));
        } 
            script.put("hostname", m_App.getProperties().getProperty("machine.hostname"));
            script.put("dbURL", m_App.getProperties().getProperty("db.URL")+m_App.getProperties().getProperty("db.schema"));
            script.put("dbUser", sDBUser);
            script.put("dbPassword", sDBPassword);
            
            script.put("ticket", ticket);
            script.put("place", ticketext);
            script.put("taxes", taxcollection);
            script.put("taxeslogic", taxeslogic);             
            script.put("user", m_App.getAppUserView().getUser());
            script.put("sales", this);
            script.put("taxesinc",m_jaddtax.isSelected());
            script.put("warranty",warrantyPrint);
            script.put("pickupid",getPickupString(ticket));

            for(ScriptArg arg : args) {
                script.put(arg.getKey(), arg.getValue());
            }             

            return script.eval(code);
        }  
    }
    
    
/** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the FormEditor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        m_jPanContainer = new javax.swing.JPanel();
        m_jOptions = new javax.swing.JPanel();
        m_jPanelBag = new javax.swing.JPanel();
        jTBtnShow = new javax.swing.JToggleButton();
        m_jbtnScale = new javax.swing.JButton();
        jbtnMooring = new javax.swing.JButton();
        m_jButtons = new javax.swing.JPanel();
        btnSplit = new javax.swing.JButton();
        btnReprint1 = new javax.swing.JButton();
        j_btnRemotePrt = new javax.swing.JButton();
        jBtnCustomer = new javax.swing.JButton();
        m_jPanelScripts = new javax.swing.JPanel();
        m_jButtonsExt = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        m_jPanTicket = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        m_jDelete = new javax.swing.JButton();
        m_jList = new javax.swing.JButton();
        m_jEditLine = new javax.swing.JButton();
        jEditAttributes = new javax.swing.JButton();
        jCheckStock = new javax.swing.JButton();
        m_jPanelCentral = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(5, 0), new java.awt.Dimension(5, 0), new java.awt.Dimension(5, 32767));
        m_jTicketId = new javax.swing.JLabel();
        m_jPanTotals = new javax.swing.JPanel();
        m_jLblTotalEuros3 = new javax.swing.JLabel();
        m_jLblTotalEuros2 = new javax.swing.JLabel();
        m_jLblTotalEuros1 = new javax.swing.JLabel();
        m_jSubtotalEuros = new javax.swing.JLabel();
        m_jTaxesEuros = new javax.swing.JLabel();
        m_jTotalEuros = new javax.swing.JLabel();
        m_jContEntries = new javax.swing.JPanel();
        m_jPanEntries = new javax.swing.JPanel();
        jPanel9 = new javax.swing.JPanel();
        m_jaddtax = new com.alee.extended.button.WebSwitch();
        m_jPrice = new javax.swing.JLabel();
        m_jPor = new javax.swing.JLabel();
        m_jEnter = new javax.swing.JButton();
        m_jTax = new javax.swing.JComboBox();
        m_jKeyFactory = new javax.swing.JTextField();
        m_jNumberKeys = new com.openbravo.beans.JNumberKeys();
        catcontainer = new javax.swing.JPanel();

        setBackground(new java.awt.Color(255, 204, 153));
        setOpaque(false);
        setLayout(new java.awt.CardLayout());

        m_jPanContainer.setLayout(new java.awt.BorderLayout());

        m_jOptions.setLayout(new java.awt.BorderLayout());

        m_jPanelBag.setAutoscrolls(true);
        m_jPanelBag.setMaximumSize(new java.awt.Dimension(10, 10));
        m_jPanelBag.setPreferredSize(new java.awt.Dimension(0, 60));

        jTBtnShow.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jTBtnShow.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/resources.png"))); // NOI18N
        jTBtnShow.setPreferredSize(new java.awt.Dimension(80, 45));
        jTBtnShow.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTBtnShowActionPerformed(evt);
            }
        });
        m_jPanelBag.add(jTBtnShow);

        m_jbtnScale.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        m_jbtnScale.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/scale.png"))); // NOI18N
        m_jbtnScale.setText(AppLocal.getIntString("button.scale")); // NOI18N
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("pos_messages"); // NOI18N
        m_jbtnScale.setToolTipText(bundle.getString("tooltip.scale")); // NOI18N
        m_jbtnScale.setFocusPainted(false);
        m_jbtnScale.setFocusable(false);
        m_jbtnScale.setMargin(new java.awt.Insets(8, 14, 8, 14));
        m_jbtnScale.setMaximumSize(new java.awt.Dimension(85, 44));
        m_jbtnScale.setMinimumSize(new java.awt.Dimension(85, 44));
        m_jbtnScale.setPreferredSize(new java.awt.Dimension(85, 45));
        m_jbtnScale.setRequestFocusEnabled(false);
        m_jbtnScale.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jbtnScaleActionPerformed(evt);
            }
        });
        m_jPanelBag.add(m_jbtnScale);

        jbtnMooring.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jbtnMooring.setText(bundle.getString("button.moorings")); // NOI18N
        jbtnMooring.setMargin(new java.awt.Insets(8, 14, 8, 14));
        jbtnMooring.setMaximumSize(new java.awt.Dimension(80, 40));
        jbtnMooring.setMinimumSize(new java.awt.Dimension(80, 40));
        jbtnMooring.setPreferredSize(new java.awt.Dimension(80, 45));
        jbtnMooring.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnMooringActionPerformed(evt);
            }
        });
        m_jPanelBag.add(jbtnMooring);

        m_jButtons.setPreferredSize(new java.awt.Dimension(350, 55));

        btnSplit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/sale_split_sml.png"))); // NOI18N
        btnSplit.setToolTipText(bundle.getString("tooltip.salesplit")); // NOI18N
        btnSplit.setEnabled(false);
        btnSplit.setFocusPainted(false);
        btnSplit.setFocusable(false);
        btnSplit.setMargin(new java.awt.Insets(8, 14, 8, 14));
        btnSplit.setMaximumSize(new java.awt.Dimension(50, 40));
        btnSplit.setMinimumSize(new java.awt.Dimension(50, 40));
        btnSplit.setPreferredSize(new java.awt.Dimension(80, 45));
        btnSplit.setRequestFocusEnabled(false);
        btnSplit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSplitActionPerformed(evt);
            }
        });

        btnReprint1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        btnReprint1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/reprint24.png"))); // NOI18N
        btnReprint1.setToolTipText(bundle.getString("tooltip.reprintLastTicket")); // NOI18N
        btnReprint1.setFocusPainted(false);
        btnReprint1.setFocusable(false);
        btnReprint1.setMargin(new java.awt.Insets(8, 14, 8, 14));
        btnReprint1.setMaximumSize(new java.awt.Dimension(50, 40));
        btnReprint1.setMinimumSize(new java.awt.Dimension(50, 40));
        btnReprint1.setPreferredSize(new java.awt.Dimension(80, 45));
        btnReprint1.setRequestFocusEnabled(false);
        btnReprint1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnReprint1ActionPerformed(evt);
            }
        });

        j_btnRemotePrt.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        j_btnRemotePrt.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/remote_print.png"))); // NOI18N
        j_btnRemotePrt.setText(bundle.getString("button.sendorder")); // NOI18N
        j_btnRemotePrt.setToolTipText(bundle.getString("tooltip.printtoremote")); // NOI18N
        j_btnRemotePrt.setMargin(new java.awt.Insets(0, 4, 0, 4));
        j_btnRemotePrt.setMaximumSize(new java.awt.Dimension(50, 40));
        j_btnRemotePrt.setMinimumSize(new java.awt.Dimension(50, 40));
        j_btnRemotePrt.setPreferredSize(new java.awt.Dimension(80, 45));
        j_btnRemotePrt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                j_btnRemotePrtActionPerformed(evt);
            }
        });

        jBtnCustomer.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jBtnCustomer.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/customer.png"))); // NOI18N
        jBtnCustomer.setToolTipText(bundle.getString("tooltip.salescustomer")); // NOI18N
        jBtnCustomer.setPreferredSize(new java.awt.Dimension(80, 45));
        jBtnCustomer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBtnCustomerActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout m_jButtonsLayout = new javax.swing.GroupLayout(m_jButtons);
        m_jButtons.setLayout(m_jButtonsLayout);
        m_jButtonsLayout.setHorizontalGroup(
            m_jButtonsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(m_jButtonsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jBtnCustomer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnSplit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(j_btnRemotePrt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnReprint1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        m_jButtonsLayout.setVerticalGroup(
            m_jButtonsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(m_jButtonsLayout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(m_jButtonsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(j_btnRemotePrt, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnSplit, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnReprint1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jBtnCustomer, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        m_jPanelBag.add(m_jButtons);

        m_jOptions.add(m_jPanelBag, java.awt.BorderLayout.PAGE_START);

        m_jPanelScripts.setPreferredSize(new java.awt.Dimension(200, 60));
        m_jPanelScripts.setLayout(new java.awt.BorderLayout());

        m_jButtonsExt.setPreferredSize(new java.awt.Dimension(20, 60));

        jPanel1.setMinimumSize(new java.awt.Dimension(235, 50));
        jPanel1.setPreferredSize(new java.awt.Dimension(10, 55));
        m_jButtonsExt.add(jPanel1);

        m_jPanelScripts.add(m_jButtonsExt, java.awt.BorderLayout.PAGE_START);

        m_jOptions.add(m_jPanelScripts, java.awt.BorderLayout.CENTER);
        m_jPanelScripts.getAccessibleContext().setAccessibleDescription("");

        m_jPanContainer.add(m_jOptions, java.awt.BorderLayout.NORTH);

        m_jPanTicket.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        m_jPanTicket.setLayout(new java.awt.BorderLayout());

        jPanel5.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jPanel5.setPreferredSize(new java.awt.Dimension(75, 270));
        jPanel5.setLayout(new java.awt.BorderLayout());

        jPanel2.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 5, 0, 5));
        jPanel2.setPreferredSize(new java.awt.Dimension(70, 250));
        jPanel2.setLayout(new java.awt.GridLayout(0, 1, 5, 5));

        m_jDelete.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/editdelete.png"))); // NOI18N
        m_jDelete.setToolTipText(bundle.getString("tooltip.saleremoveline")); // NOI18N
        m_jDelete.setFocusPainted(false);
        m_jDelete.setFocusable(false);
        m_jDelete.setMargin(new java.awt.Insets(8, 14, 8, 14));
        m_jDelete.setMaximumSize(new java.awt.Dimension(42, 36));
        m_jDelete.setMinimumSize(new java.awt.Dimension(42, 36));
        m_jDelete.setPreferredSize(new java.awt.Dimension(50, 45));
        m_jDelete.setRequestFocusEnabled(false);
        m_jDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jDeleteActionPerformed(evt);
            }
        });
        jPanel2.add(m_jDelete);

        m_jList.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/search32.png"))); // NOI18N
        m_jList.setToolTipText(bundle.getString("tooltip.saleproductfind")); // NOI18N
        m_jList.setFocusPainted(false);
        m_jList.setFocusable(false);
        m_jList.setMargin(new java.awt.Insets(8, 14, 8, 14));
        m_jList.setMaximumSize(new java.awt.Dimension(42, 36));
        m_jList.setMinimumSize(new java.awt.Dimension(42, 36));
        m_jList.setPreferredSize(new java.awt.Dimension(50, 45));
        m_jList.setRequestFocusEnabled(false);
        m_jList.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jListActionPerformed(evt);
            }
        });
        jPanel2.add(m_jList);

        m_jEditLine.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/sale_editline.png"))); // NOI18N
        m_jEditLine.setToolTipText(bundle.getString("tooltip.saleeditline")); // NOI18N
        m_jEditLine.setFocusPainted(false);
        m_jEditLine.setFocusable(false);
        m_jEditLine.setMargin(new java.awt.Insets(8, 14, 8, 14));
        m_jEditLine.setMaximumSize(new java.awt.Dimension(42, 36));
        m_jEditLine.setMinimumSize(new java.awt.Dimension(42, 36));
        m_jEditLine.setPreferredSize(new java.awt.Dimension(50, 45));
        m_jEditLine.setRequestFocusEnabled(false);
        m_jEditLine.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jEditLineActionPerformed(evt);
            }
        });
        jPanel2.add(m_jEditLine);

        jEditAttributes.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/attributes.png"))); // NOI18N
        jEditAttributes.setToolTipText(bundle.getString("tooltip.saleattributes")); // NOI18N
        jEditAttributes.setFocusPainted(false);
        jEditAttributes.setFocusable(false);
        jEditAttributes.setMargin(new java.awt.Insets(8, 14, 8, 14));
        jEditAttributes.setMaximumSize(new java.awt.Dimension(42, 36));
        jEditAttributes.setMinimumSize(new java.awt.Dimension(42, 36));
        jEditAttributes.setPreferredSize(new java.awt.Dimension(50, 45));
        jEditAttributes.setRequestFocusEnabled(false);
        jEditAttributes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jEditAttributesActionPerformed(evt);
            }
        });
        jPanel2.add(jEditAttributes);

        jCheckStock.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jCheckStock.setForeground(new java.awt.Color(76, 197, 237));
        jCheckStock.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/info.png"))); // NOI18N
        jCheckStock.setToolTipText(bundle.getString("tooltip.salecheckstock")); // NOI18N
        jCheckStock.setFocusPainted(false);
        jCheckStock.setFocusable(false);
        jCheckStock.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jCheckStock.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        jCheckStock.setMargin(new java.awt.Insets(8, 4, 8, 4));
        jCheckStock.setMaximumSize(new java.awt.Dimension(42, 36));
        jCheckStock.setMinimumSize(new java.awt.Dimension(42, 36));
        jCheckStock.setPreferredSize(new java.awt.Dimension(80, 45));
        jCheckStock.setRequestFocusEnabled(false);
        jCheckStock.setVerticalTextPosition(javax.swing.SwingConstants.TOP);
        jCheckStock.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jCheckStockMouseClicked(evt);
            }
        });
        jCheckStock.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckStockActionPerformed(evt);
            }
        });
        jPanel2.add(jCheckStock);

        jPanel5.add(jPanel2, java.awt.BorderLayout.NORTH);

        m_jPanTicket.add(jPanel5, java.awt.BorderLayout.LINE_START);

        m_jPanelCentral.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        m_jPanelCentral.setPreferredSize(new java.awt.Dimension(450, 240));
        m_jPanelCentral.setLayout(new java.awt.BorderLayout());

        jPanel4.setLayout(new java.awt.BorderLayout());
        jPanel4.add(filler2, java.awt.BorderLayout.LINE_START);

        m_jTicketId.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        m_jTicketId.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        m_jTicketId.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        m_jTicketId.setOpaque(true);
        m_jTicketId.setPreferredSize(new java.awt.Dimension(300, 40));
        m_jTicketId.setRequestFocusEnabled(false);
        m_jTicketId.setVerticalTextPosition(javax.swing.SwingConstants.TOP);
        jPanel4.add(m_jTicketId, java.awt.BorderLayout.CENTER);

        m_jPanTotals.setPreferredSize(new java.awt.Dimension(375, 60));
        m_jPanTotals.setLayout(new java.awt.GridLayout(2, 3, 4, 0));

        m_jLblTotalEuros3.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        m_jLblTotalEuros3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        m_jLblTotalEuros3.setLabelFor(m_jSubtotalEuros);
        m_jLblTotalEuros3.setText(AppLocal.getIntString("label.subtotalcash")); // NOI18N
        m_jPanTotals.add(m_jLblTotalEuros3);

        m_jLblTotalEuros2.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        m_jLblTotalEuros2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        m_jLblTotalEuros2.setLabelFor(m_jSubtotalEuros);
        m_jLblTotalEuros2.setText(AppLocal.getIntString("label.taxcash")); // NOI18N
        m_jPanTotals.add(m_jLblTotalEuros2);

        m_jLblTotalEuros1.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        m_jLblTotalEuros1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        m_jLblTotalEuros1.setLabelFor(m_jTotalEuros);
        m_jLblTotalEuros1.setText(AppLocal.getIntString("label.totalcash")); // NOI18N
        m_jPanTotals.add(m_jLblTotalEuros1);

        m_jSubtotalEuros.setBackground(m_jEditLine.getBackground());
        m_jSubtotalEuros.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        m_jSubtotalEuros.setForeground(m_jEditLine.getForeground());
        m_jSubtotalEuros.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        m_jSubtotalEuros.setLabelFor(m_jSubtotalEuros);
        m_jSubtotalEuros.setToolTipText(bundle.getString("tooltip.salesubtotal")); // NOI18N
        m_jSubtotalEuros.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(153, 153, 153), 1, true));
        m_jSubtotalEuros.setMaximumSize(new java.awt.Dimension(125, 25));
        m_jSubtotalEuros.setMinimumSize(new java.awt.Dimension(80, 25));
        m_jSubtotalEuros.setPreferredSize(new java.awt.Dimension(80, 25));
        m_jSubtotalEuros.setRequestFocusEnabled(false);
        m_jPanTotals.add(m_jSubtotalEuros);

        m_jTaxesEuros.setBackground(m_jEditLine.getBackground());
        m_jTaxesEuros.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        m_jTaxesEuros.setForeground(m_jEditLine.getForeground());
        m_jTaxesEuros.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        m_jTaxesEuros.setLabelFor(m_jTaxesEuros);
        m_jTaxesEuros.setToolTipText(bundle.getString("tooltip.saletax")); // NOI18N
        m_jTaxesEuros.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(153, 153, 153), 1, true));
        m_jTaxesEuros.setMaximumSize(new java.awt.Dimension(125, 25));
        m_jTaxesEuros.setMinimumSize(new java.awt.Dimension(80, 25));
        m_jTaxesEuros.setPreferredSize(new java.awt.Dimension(80, 25));
        m_jTaxesEuros.setRequestFocusEnabled(false);
        m_jPanTotals.add(m_jTaxesEuros);

        m_jTotalEuros.setBackground(m_jEditLine.getBackground());
        m_jTotalEuros.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        m_jTotalEuros.setForeground(m_jEditLine.getForeground());
        m_jTotalEuros.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        m_jTotalEuros.setLabelFor(m_jTotalEuros);
        m_jTotalEuros.setToolTipText(bundle.getString("tooltip.saletotal")); // NOI18N
        m_jTotalEuros.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(153, 153, 153), 1, true));
        m_jTotalEuros.setMaximumSize(new java.awt.Dimension(125, 25));
        m_jTotalEuros.setMinimumSize(new java.awt.Dimension(80, 25));
        m_jTotalEuros.setPreferredSize(new java.awt.Dimension(100, 25));
        m_jTotalEuros.setRequestFocusEnabled(false);
        m_jPanTotals.add(m_jTotalEuros);

        jPanel4.add(m_jPanTotals, java.awt.BorderLayout.LINE_END);

        m_jPanelCentral.add(jPanel4, java.awt.BorderLayout.SOUTH);

        m_jPanTicket.add(m_jPanelCentral, java.awt.BorderLayout.CENTER);

        m_jPanContainer.add(m_jPanTicket, java.awt.BorderLayout.CENTER);

        m_jContEntries.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        m_jContEntries.setMinimumSize(new java.awt.Dimension(300, 350));
        m_jContEntries.setLayout(new java.awt.BorderLayout());

        m_jPanEntries.setPreferredSize(new java.awt.Dimension(300, 350));
        m_jPanEntries.setLayout(new javax.swing.BoxLayout(m_jPanEntries, javax.swing.BoxLayout.Y_AXIS));

        jPanel9.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));

        m_jaddtax.setBorder(null);
        m_jaddtax.setToolTipText(bundle.getString("tooltip.switchtax")); // NOI18N
        m_jaddtax.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        m_jaddtax.setPreferredSize(new java.awt.Dimension(60, 30));
        m_jaddtax.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jaddtaxActionPerformed(evt);
            }
        });

        m_jPrice.setFont(new java.awt.Font("Arial", 1, 16)); // NOI18N
        m_jPrice.setForeground(new java.awt.Color(76, 197, 237));
        m_jPrice.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        m_jPrice.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(76, 197, 237)), javax.swing.BorderFactory.createEmptyBorder(1, 4, 1, 4)));
        m_jPrice.setOpaque(true);
        m_jPrice.setPreferredSize(new java.awt.Dimension(100, 25));
        m_jPrice.setRequestFocusEnabled(false);

        m_jPor.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        m_jPor.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        m_jPor.setRequestFocusEnabled(false);

        m_jEnter.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/barcode.png"))); // NOI18N
        m_jEnter.setToolTipText(bundle.getString("tooltip.salebarcode")); // NOI18N
        m_jEnter.setFocusPainted(false);
        m_jEnter.setFocusable(false);
        m_jEnter.setPreferredSize(new java.awt.Dimension(80, 45));
        m_jEnter.setRequestFocusEnabled(false);
        m_jEnter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jEnterActionPerformed(evt);
            }
        });

        m_jTax.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        m_jTax.setToolTipText(bundle.getString("tooltip.salestaxswitch")); // NOI18N
        m_jTax.setFocusable(false);
        m_jTax.setPreferredSize(new java.awt.Dimension(28, 25));
        m_jTax.setRequestFocusEnabled(false);

        m_jKeyFactory.setEditable(false);
        m_jKeyFactory.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        m_jKeyFactory.setForeground(javax.swing.UIManager.getDefaults().getColor("Panel.background"));
        m_jKeyFactory.setAutoscrolls(false);
        m_jKeyFactory.setBorder(null);
        m_jKeyFactory.setCaretColor(javax.swing.UIManager.getDefaults().getColor("Panel.background"));
        m_jKeyFactory.setMinimumSize(new java.awt.Dimension(0, 0));
        m_jKeyFactory.setPreferredSize(new java.awt.Dimension(1, 1));
        m_jKeyFactory.setRequestFocusEnabled(false);
        m_jKeyFactory.setVerifyInputWhenFocusTarget(false);
        m_jKeyFactory.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                m_jKeyFactoryKeyTyped(evt);
            }
        });

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(m_jPor)
                    .addComponent(m_jKeyFactory, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 7, Short.MAX_VALUE)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addComponent(m_jaddtax, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(7, 7, 7)
                        .addComponent(m_jTax, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addComponent(m_jPrice, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(5, 5, 5)))
                .addComponent(m_jEnter, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addComponent(m_jEnter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addComponent(m_jPrice, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 7, Short.MAX_VALUE)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(m_jTax, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(m_jaddtax, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel9Layout.createSequentialGroup()
                .addComponent(m_jPor)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(m_jKeyFactory, javax.swing.GroupLayout.PREFERRED_SIZE, 1, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        m_jPanEntries.add(jPanel9);

        m_jNumberKeys.setMinimumSize(new java.awt.Dimension(300, 300));
        m_jNumberKeys.setPreferredSize(new java.awt.Dimension(250, 250));
        m_jNumberKeys.addJNumberEventListener(new com.openbravo.beans.JNumberEventListener() {
            public void keyPerformed(com.openbravo.beans.JNumberEvent evt) {
                m_jNumberKeysKeyPerformed(evt);
            }
        });
        m_jPanEntries.add(m_jNumberKeys);

        m_jContEntries.add(m_jPanEntries, java.awt.BorderLayout.NORTH);

        m_jPanContainer.add(m_jContEntries, java.awt.BorderLayout.LINE_END);

        catcontainer.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        catcontainer.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        catcontainer.setLayout(new java.awt.BorderLayout());
        m_jPanContainer.add(catcontainer, java.awt.BorderLayout.SOUTH);

        add(m_jPanContainer, "ticket");
    }// </editor-fold>//GEN-END:initComponents

    private void m_jbtnScaleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jbtnScaleActionPerformed

        stateTransition('\u00a7');
        
    }//GEN-LAST:event_m_jbtnScaleActionPerformed

    private void m_jEditLineActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jEditLineActionPerformed

        count = (int) m_oTicket.getArticlesCount();     // get existing line value
            
        int i = m_ticketlines.getSelectedIndex();
        
        if (i < 0){
            Toolkit.getDefaultToolkit().beep(); // no line selected
        } else {
            try {
                TicketLineInfo newline = JProductLineEdit.showMessage(this, m_App, m_oTicket.getLine(i));
                if (newline != null) {
                    paintTicketLine(i, newline);
                }
                    
            } catch (BasicException e) {
                new MessageInf(e).show(this);
            }
        }

    }//GEN-LAST:event_m_jEditLineActionPerformed

    private void m_jEnterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jEnterActionPerformed

        stateTransition('\n');

    }//GEN-LAST:event_m_jEnterActionPerformed

    private void m_jNumberKeysKeyPerformed(com.openbravo.beans.JNumberEvent evt) {//GEN-FIRST:event_m_jNumberKeysKeyPerformed

        stateTransition(evt.getKey());

        j_btnRemotePrt.setEnabled(true);
        j_btnRemotePrt.revalidate();
         
    }//GEN-LAST:event_m_jNumberKeysKeyPerformed

    private void m_jKeyFactoryKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_m_jKeyFactoryKeyTyped

        m_jKeyFactory.setText(null);

        stateTransition(evt.getKeyChar());

    }//GEN-LAST:event_m_jKeyFactoryKeyTyped

    private void m_jDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jDeleteActionPerformed

        int i = m_ticketlines.getSelectedIndex();
        
        if (i < 0){
            Toolkit.getDefaultToolkit().beep();
        } else {               
            removeTicketLine(i);
            jCheckStock.setText("");
        }     
    }//GEN-LAST:event_m_jDeleteActionPerformed

    private void m_jListActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jListActionPerformed

        ProductInfoExt prod = JProductFinder.showMessage(JPanelTicket.this, dlSales);    
        if (prod != null) {
            buttonTransition(prod);
        }
        
    }//GEN-LAST:event_m_jListActionPerformed

    private void jEditAttributesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jEditAttributesActionPerformed
       if (listener  != null) {
            listener.stop();
        } 
        int i = m_ticketlines.getSelectedIndex();
        if (i < 0) {
            Toolkit.getDefaultToolkit().beep(); // no line selected
        } else {
            try {
                TicketLineInfo line = m_oTicket.getLine(i);
                JProductAttEdit2 attedit = JProductAttEdit2.getAttributesEditor(this, m_App.getSession());
                attedit.editAttributes(line.getProductAttSetId(), line.getProductAttSetInstId());
                attedit.setVisible(true);
                if (attedit.isOK()) {
                    line.setProductAttSetInstId(attedit.getAttributeSetInst());
                    line.setProductAttSetInstDesc(attedit.getAttributeSetInstDescription());
                    paintTicketLine(i, line);
                }
            } catch (BasicException ex) {
                JOptionPane.showMessageDialog(this, 
                    AppLocal.getIntString("message.cannotfindattributes"), 
                    AppLocal.getIntString("message.title"), 
                    JOptionPane.INFORMATION_MESSAGE);
            }
        }

        if (listener  != null){           
            listener.restart(); 
        }
}//GEN-LAST:event_jEditAttributesActionPerformed

    private void jbtnMooringActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnMooringActionPerformed
// Display vessel selection box on screen if reply is good add to the ticket
       if (listener  != null) {
            listener.stop();
        } 
       JMooringDetails mooring = JMooringDetails.getMooringDetails(this, m_App.getSession());
       mooring.setVisible(true);
       if (mooring.isCreate()){ 
           if (((mooring.getVesselDays()>0 )) &&  ((mooring.getVesselSize()>1))){          
            try{
                ProductInfoExt vProduct = dlSales.getProductInfoByCode("BFeesDay1");
                vProduct.setName("Berth Fees 1st Day " + mooring.getVesselName());
                addTicketLine(vProduct, mooring.getVesselSize(), vProduct.getPriceSell());

                if (mooring.getVesselDays()>1){
                    vProduct = dlSales.getProductInfoByCode("BFeesDay2");
                    vProduct.setName("Additional Days " +(mooring.getVesselDays()-1));
                    addTicketLine(vProduct, mooring.getVesselSize() * (mooring.getVesselDays()-1), vProduct.getPriceSell());               
                }

                if (mooring.getVesselPower()){
                    vProduct = dlSales.getProductInfoByCode("PowerSupplied");
                    addTicketLine(vProduct, mooring.getVesselDays(), vProduct.getPriceSell());               
                }         
           }catch (BasicException e){}
            }
        }
               refreshTicket(); 
    }//GEN-LAST:event_jbtnMooringActionPerformed

    private void j_btnRemotePrtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_j_btnRemotePrtActionPerformed
        
        String rScript = (dlSystem.getResourceAsText("script.SendOrder"));
        
        try {                       
            
            ScriptEngine scriptEngine = ScriptFactory.getScriptEngine(ScriptFactory.BEANSHELL);
            scriptEngine.put("ticket", m_oTicket);  
            scriptEngine.put("place",  m_oTicketExt);             
            scriptEngine.put("user", m_App.getAppUserView().getUser());
            scriptEngine.put("sales", this);
            scriptEngine.put("pickupid", m_oTicket.getPickupId());

            Object result = scriptEngine.eval(rScript);

        } catch (ScriptException ex) {
            Logger.getLogger(JPanelTicket.class.getName()).log(Level.ALL, null, ex);
        }

        remoteOrderDisplay();
        
    }//GEN-LAST:event_j_btnRemotePrtActionPerformed

    private void btnReprint1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnReprint1ActionPerformed
        if (m_config.getProperty("lastticket.number") != null) {
            try {
                TicketInfo ticket = dlSales.loadTicket(
                        Integer.parseInt((m_config.getProperty("lastticket.type"))), 
                        Integer.parseInt((m_config.getProperty("lastticket.number"))));
                if (ticket == null) {
                    JFrame frame = new JFrame();
                    JOptionPane.showMessageDialog(frame, 
                            AppLocal.getIntString("message.notexiststicket"), 
                            AppLocal.getIntString("message.notexiststickettitle"), 
                            JOptionPane.WARNING_MESSAGE);
                } else {
                    m_ticket = ticket;
                    try {
                        taxeslogic.calculateTaxes(m_ticket);
                        //TicketTaxInfo[] taxlist = m_ticket.getTaxLines();
                    } catch (TaxesException ex) {
                        LOGGER.log(Level.SEVERE, "", ex);
                    }
                    printTicket("Printer.ReprintTicket", m_ticket, null);                    
                    Notify("'Printed'");
                }
            } catch (BasicException e) {
                MessageInf msg = new MessageInf(MessageInf.SGN_WARNING, AppLocal.getIntString("message.cannotloadticket"), e);
                msg.show(this);
            }
        }
    }//GEN-LAST:event_btnReprint1ActionPerformed

    private void btnSplitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSplitActionPerformed

        if (m_oTicket.getLinesCount() > 0) {
            ReceiptSplit splitdialog = ReceiptSplit.getDialog(this,
                dlSystem.getResourceAsXML("Ticket.Line"), dlSales, dlCustomers, taxeslogic);

            TicketInfo ticket1 = m_oTicket.copyTicket();
            TicketInfo ticket2 = new TicketInfo();
            ticket2.setCustomer(m_oTicket.getCustomer());

            if (splitdialog.showDialog(ticket1, ticket2, m_oTicketExt)) {
                if (closeTicket(ticket2, m_oTicketExt)) { // already checked  that number of lines > 0
                    setActiveTicket(ticket1, m_oTicketExt);// set result ticket
                }
            }
        }

    }//GEN-LAST:event_btnSplitActionPerformed

    private void jCheckStockActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckStockActionPerformed

        if (listener  != null) {
            listener.stop();
        } 
        
        int i = m_ticketlines.getSelectedIndex();
        if (i < 0) {
            Toolkit.getDefaultToolkit().beep();
        } else {
            try {
                TicketLineInfo line = m_oTicket.getLine(i);
                String pId = line.getProductID();
                String location = m_App.getInventoryLocation();
                ProductStock checkProduct;
                checkProduct = dlSales.getProductStockState(pId, location);

                if (checkProduct != null) {

                    if (checkProduct.getUnits() <=0) {
                        jCheckStock.setForeground(Color.magenta);
                    } else {
                        jCheckStock.setForeground(Color.darkGray);
                    }

                    String content;
                    
                    if (!location.equals(checkProduct.getLocation())) {
                        content = AppLocal.getIntString("message.location.current");
                        JFrame frame = new JFrame();
                        JOptionPane.showMessageDialog(frame,
                            content,
                            "Info",
                            JOptionPane.INFORMATION_MESSAGE);                        
                    } else {
                        double dUnits = checkProduct.getUnits();
                        int iUnits;
                        iUnits = (int) dUnits;

                        jCheckStock.setText(Integer.toString(iUnits));
                    }

                } else {
                    jCheckStock.setText(null);
                }
            } catch (BasicException ex) {
                    Logger.getLogger(JPanelTicket.class.getName()).log(Level.SEVERE, null, ex);
            }        
        }       
       
        if (listener  != null){           
            listener.restart(); 
        }
    }//GEN-LAST:event_jCheckStockActionPerformed

    private void jCheckStockMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jCheckStockMouseClicked
        if (evt.getClickCount()==2) {
            if (listener  != null) {
                listener.stop();
            } 
   
            int i = m_ticketlines.getSelectedIndex();
            if (i < 0) {
                Toolkit.getDefaultToolkit().beep();
            } else {
                try {
                    TicketLineInfo line = m_oTicket.getLine(i);
                    String pId = line.getProductID();
                    String location = m_App.getInventoryLocation();
                    ProductStock checkProduct;
                    checkProduct = dlSales.getProductStockState(pId, location);
                   
                    Double pMin;
                    Double pMax;
                    Double pUnits;
                    Date pMemoDate;
                    String content;
                    
                    if (!location.equals(checkProduct.getLocation())) {
                        content = AppLocal.getIntString("message.location.current");
                        JFrame frame = new JFrame();
                        JOptionPane.showMessageDialog(frame,
                            content,
                            "Info",
                            JOptionPane.INFORMATION_MESSAGE);                      
                    } else {
                        if (checkProduct.getMinimum() != null) {
                            pMin = checkProduct.getMinimum();
                        } else {
                            pMin = 0.;
                        }
                        if (checkProduct.getMaximum() != null) {
                            pMax = checkProduct.getMaximum();
                        } else {
                            pMax = 0.;
                        }
                        if (checkProduct.getUnits() != null) {
                            pUnits = checkProduct.getUnits();
                        } else {
                            pUnits = 0.;
                        }
                        if (checkProduct.getMemoDate() != null) {
                            pMemoDate = checkProduct.getMemoDate();
                        } else {
                            pMemoDate = null;
                        }

                        content = "<html>"+
                            "<b>" + AppLocal.getIntString("label.currentstock") +
                            " : " + "</b>" + checkProduct.getUnits() + "<br>" +
                            "<b>" + AppLocal.getIntString("label.maximum") +
                            " : " + "</b>" + pMax + "<br>" +
                            "<b>" + AppLocal.getIntString("label.minimum") +
                            " : " + "</b>" + pMin + "<br>" +
                            "<b>" + AppLocal.getIntString("label.proddate") +
                            " : " + "</b>" + pMemoDate + "<br>";

                        JFrame frame = new JFrame();
                        JOptionPane.showMessageDialog(frame,
                            content,
                            "Info",
                            JOptionPane.INFORMATION_MESSAGE);
                    }
                } catch (BasicException ex) {
                    Logger.getLogger(JPanelTicket.class.getName()).log(Level.SEVERE, null, ex);
                }
            }       
      
            if (listener  != null){           
                listener.restart(); 
            }       
        }
    }//GEN-LAST:event_jCheckStockMouseClicked

    private void m_jaddtaxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jaddtaxActionPerformed
        m_jKeyFactory.requestFocus();
    }//GEN-LAST:event_m_jaddtaxActionPerformed

    private void jTBtnShowActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTBtnShowActionPerformed
        if (jTBtnShow.isSelected()) {
            m_jPanelScripts.setVisible(true);
            m_jButtonsExt.setVisible(true);            
        } else {
            m_jPanelScripts.setVisible(false);
            m_jButtonsExt.setVisible(false);            
        }
            refreshTicket();        
        m_jKeyFactory.requestFocus();
    }//GEN-LAST:event_jTBtnShowActionPerformed

    private void jBtnCustomerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtnCustomerActionPerformed
        if (listener  != null) {
            listener.stop();
        }
        Object[] options = {"Create", "Find","Cancel"};
        
        int n = JOptionPane.showOptionDialog(null,
            AppLocal.getIntString("message.customeradd"),
            AppLocal.getIntString("label.customer"),            
            JOptionPane.YES_NO_CANCEL_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            options,
            options[2]);

        if (n == 0) {
            JDialogNewCustomer dialog = JDialogNewCustomer.getDialog(this,m_App);
            dialog.setVisible(true);

            CustomerInfoExt m_customerInfo = dialog.getSelectedCustomer();
            if (dialog.getSelectedCustomer()!=null){
                try {
                    m_oTicket.setCustomer(dlSales.loadCustomerExt
                        (dialog.getSelectedCustomer().getId()));
                } catch (BasicException ex) {
                    Logger.getLogger(JPanelTicket.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }        
        
        if (n == 1) {
            JCustomerFinder finder = JCustomerFinder.getCustomerFinder(this, dlCustomers);

            if (m_oTicket.getCustomerId() == null) {
                finder.setAppView(m_App);
                finder.search(m_oTicket.getCustomer());
                finder.executeSearch();
                finder.setVisible(true);

                if (finder.getSelectedCustomer() != null) {
                    try {
                        m_oTicket.setCustomer(dlSales.loadCustomerExt
                            (finder.getSelectedCustomer().getId()));
                        if ("restaurant".equals(m_App.getProperties().getProperty("machine.ticketsbag"))) {
                            restDB.setCustomerNameInTableByTicketId(dlSales.loadCustomerExt
                                (finder.getSelectedCustomer().getId()).toString(), m_oTicket.getId());
                        }

                        checkCustomer();

                        m_jTicketId.setText(m_oTicket.getName(m_oTicketExt));

                    } catch (BasicException e) {
                        MessageInf msg = new MessageInf(MessageInf.SGN_WARNING,
                            AppLocal.getIntString("message.cannotfindcustomer"), e);
                        msg.show(this);
                    }
                } else {
                    restDB.setCustomerNameInTableByTicketId(null, m_oTicket.getId());
                    m_oTicket.setCustomer(null);
                    Notify("notify.customerremove");
                }

            } else {
                if (JOptionPane.showConfirmDialog(this,
                    AppLocal.getIntString("message.customerchange"),
                    AppLocal.getIntString("title.editor"),
                    JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION){

                finder.setAppView(m_App);
                finder.search(m_oTicket.getCustomer());
                finder.executeSearch();
                finder.setVisible(true);

                if (finder.getSelectedCustomer() != null) {
                    try {
                        m_oTicket.setCustomer(dlSales.loadCustomerExt
                            (finder.getSelectedCustomer().getId()));
                        if ("restaurant".equals(m_App.getProperties().getProperty("machine.ticketsbag"))) {
                            restDB.setCustomerNameInTableByTicketId(dlSales.loadCustomerExt
                                (finder.getSelectedCustomer().getId()).toString(), m_oTicket.getId());
                        }

                        checkCustomer();

                        m_jTicketId.setText(m_oTicket.getName());

                    } catch (BasicException e) {
                        MessageInf msg = new MessageInf(MessageInf.SGN_WARNING,
                            AppLocal.getIntString("message.cannotfindcustomer"), e);
                        msg.show(this);
                    }
                } else {
                    restDB.setCustomerNameInTableByTicketId(null, m_oTicket.getId());
                    m_oTicket.setCustomer(null);
                }
            }    
            }
        }
   
        refreshTicket();
        
    }//GEN-LAST:event_jBtnCustomerActionPerformed
                                       
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnReprint1;
    private javax.swing.JButton btnSplit;
    private javax.swing.JPanel catcontainer;
    private javax.swing.Box.Filler filler2;
    private javax.swing.JButton jBtnCustomer;
    private javax.swing.JButton jCheckStock;
    private javax.swing.JButton jEditAttributes;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JToggleButton jTBtnShow;
    private javax.swing.JButton j_btnRemotePrt;
    private javax.swing.JButton jbtnMooring;
    private javax.swing.JPanel m_jButtons;
    private javax.swing.JPanel m_jButtonsExt;
    private javax.swing.JPanel m_jContEntries;
    private javax.swing.JButton m_jDelete;
    private javax.swing.JButton m_jEditLine;
    private javax.swing.JButton m_jEnter;
    private javax.swing.JTextField m_jKeyFactory;
    private javax.swing.JLabel m_jLblTotalEuros1;
    private javax.swing.JLabel m_jLblTotalEuros2;
    private javax.swing.JLabel m_jLblTotalEuros3;
    private javax.swing.JButton m_jList;
    private com.openbravo.beans.JNumberKeys m_jNumberKeys;
    private javax.swing.JPanel m_jOptions;
    private javax.swing.JPanel m_jPanContainer;
    private javax.swing.JPanel m_jPanEntries;
    private javax.swing.JPanel m_jPanTicket;
    private javax.swing.JPanel m_jPanTotals;
    private javax.swing.JPanel m_jPanelBag;
    private javax.swing.JPanel m_jPanelCentral;
    private javax.swing.JPanel m_jPanelScripts;
    private javax.swing.JLabel m_jPor;
    private javax.swing.JLabel m_jPrice;
    private javax.swing.JLabel m_jSubtotalEuros;
    private javax.swing.JComboBox m_jTax;
    private javax.swing.JLabel m_jTaxesEuros;
    private javax.swing.JLabel m_jTicketId;
    private javax.swing.JLabel m_jTotalEuros;
    private com.alee.extended.button.WebSwitch m_jaddtax;
    private javax.swing.JButton m_jbtnScale;
    // End of variables declaration//GEN-END:variables

/* Remote Orders Display
    We only know about KrOS POS orders and won't try and handle any 
    that are injected from an external source
*/    

    public void remoteOrderDisplay() {
        remoteOrderDisplay(remoteOrderId(), 1, true);
    }

    public void remoteOrderDisplay(String id) {
        remoteOrderDisplay(id, 1, true);        
    }        

    public void remoteOrderDisplay(Integer display) {
        remoteOrderDisplay(remoteOrderId(), display, false);        
    }

    public String remoteOrderId(){

        String id = "";

        if ((m_oTicket.getCustomer() != null)) {
            return m_oTicket.getCustomer().getName();
        } else if (m_oTicketExt!=null){
            return m_oTicketExt.toString();
        } else {
            if (m_oTicket.getPickupId() == 0) {
                try {
                    m_oTicket.setPickupId(dlSales.getNextPickupIndex());
                } catch (BasicException e) {
                    m_oTicket.setPickupId(0);
                }
            }            

            return getPickupString(m_oTicket);                            
        }
    } 

    public void remoteOrderDisplay(String id, Integer display, boolean primary) {

        try {
            // may use later if/when external order injection
            // String orderUUID = UUID.randomUUID().toString();

            dlSystem.deleteOrder(id);
        } catch (BasicException ex) {
            Logger.getLogger(JPanelTicket.class.getName()).log(Level.SEVERE, null, ex);
        }

        for (int i = 0; i < m_oTicket.getLinesCount(); i++) {           
            try {
                if (primary){
                    if ((m_oTicket.getLine(i).getProperty("display")==null)  
                            || ("".equals(m_oTicket.getLine(i).getProperty("display")))){
                        display = 1;
                    }else{
                        display = Integer.parseInt(m_oTicket.getLine(i).getProperty("display"));
                    }
                }

                dlSystem.addOrder(getPickupString(m_oTicket)
                        , (int) m_oTicket.getLine(i).getMultiply()
                        , m_oTicket.getLine(i).getProductName()
                        , m_oTicket.getLine(i).getProductAttSetInstDesc()
                        , m_oTicket.getLine(i).getProperty("notes")
                        , id
                        , null
                        , display
                        , null
                        , null);                

/* this block for future - right now we're deleting all ticketlines
    and resending for consistency with actual ticketlines
                dlSystem.updateOrder(getPickupString(m_oTicket)
                        , (int) m_oTicket.getLine(i).getMultiply()
                        , m_oTicket.getLine(i).getProductName()
                        , m_oTicket.getLine(i).getProductAttSetInstDesc()
                        , m_oTicket.getLine(i).getProperty("notes")
                        , id
                        , null
                        , display
                        , null
                        , null);                
*/                
                
            } catch (BasicException ex) {
                Logger.getLogger(JPanelTicket.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }    
     
}
