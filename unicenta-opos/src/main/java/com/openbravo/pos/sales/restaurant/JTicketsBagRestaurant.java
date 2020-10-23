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
//    KrOS POS  - Open Source Point Of Sale
//    Copyright (c) 2009-2018 uniCenta & previous Openbravo POS works
//    
//

package com.openbravo.pos.sales.restaurant;

import com.alee.extended.time.ClockType;
import com.alee.extended.time.WebClock;
import com.alee.managers.notification.NotificationIcon;
import com.alee.managers.notification.NotificationManager;
import com.alee.managers.notification.WebNotification;
import com.openbravo.basic.BasicException;
import com.openbravo.beans.JNumberPop;
import com.openbravo.data.gui.JMessageDialog;
import com.openbravo.data.gui.ListKeyed;
import com.openbravo.data.gui.MessageInf;
import com.openbravo.data.loader.SentenceList;
import com.openbravo.pos.forms.AppLocal;
import com.openbravo.pos.forms.AppView;
import com.openbravo.pos.forms.DataLogicSales;
import com.openbravo.pos.forms.DataLogicSystem;
import com.openbravo.pos.forms.JRootApp;
import com.openbravo.pos.printer.DeviceTicket;
import com.openbravo.pos.printer.TicketParser;
import com.openbravo.pos.printer.TicketPrinterException;
import com.openbravo.pos.sales.DataLogicReceipts;
import com.openbravo.pos.sales.JPanelTicket;
import com.openbravo.pos.sales.TaxesLogic;
import com.openbravo.pos.scripting.ScriptEngine;
import com.openbravo.pos.scripting.ScriptException;
import com.openbravo.pos.scripting.ScriptFactory;
import com.openbravo.pos.ticket.TicketInfo;
import com.openbravo.pos.ticket.TicketLineInfo;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;

/**
 *
 * @author JG uniCenta
 */
public class JTicketsBagRestaurant extends javax.swing.JPanel {
    
    private final AppView m_App;
    private final JTicketsBagRestaurantMap m_restaurant;
    private List<TicketLineInfo> m_aLines;
    private TicketLineInfo line;
    private TicketInfo ticket;
    private final Object ticketExt; 
    private DataLogicSystem m_dlSystem = null;
    private final DeviceTicket m_TP;   
    private final TicketParser m_TTP2; 
    private final RestaurantDBUtils restDB;    
    
    private final DataLogicSystem dlSystem = null;
    private final DataLogicReceipts dlReceipts = null;
    private DataLogicSales dlSales = null;
    
    private TicketParser m_TTP; 
    
    private SentenceList senttax;
    private ListKeyed taxcollection;
    private TaxesLogic taxeslogic;
    
   
    
    /** Creates new form JTicketsBagRestaurantMap
     * @param app
     * @param restaurant */
    public JTicketsBagRestaurant(AppView app, JTicketsBagRestaurantMap restaurant) {
        super();
        m_App = app;
        m_restaurant = restaurant;
        
        initComponents();

        ticketExt = null;  
        
        restDB = new  RestaurantDBUtils(m_App); 
        
        m_dlSystem = (DataLogicSystem) m_App.getBean("com.openbravo.pos.forms.DataLogicSystem");
        dlSales = (DataLogicSales) m_App.getBean("com.openbravo.pos.forms.DataLogicSales");        
        DataLogicReceipts m_dlReceipts = (DataLogicReceipts) m_App.getBean("com.openbravo.pos.sales.DataLogicReceipts");
        
        m_TP = new DeviceTicket();
        m_TTP2 = new TicketParser(m_App.getDeviceTicket(), m_dlSystem);     
        
//        j_btnKitchen.setVisible(false);
        j_btnKitchen.setVisible(true);        

        m_TablePlan.setVisible(m_App.getAppUserView().getUser().
                hasPermission("sales.TablePlan"));        
        
    }

    /**
     *
     */
    public void activate() {
        
        m_DelTicket.setEnabled(m_App.getAppUserView().getUser()
                .hasPermission("com.openbravo.pos.sales.JPanelTicketEdits"));

        m_TablePlan.setEnabled(m_App.getAppUserView().getUser()
                .hasPermission("com.openbravo.pos.sales.JPanelTicketEdits"));
        m_TablePlan.setVisible(true);
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

    /**
     *
     * @param resource
     */
    public void printTicket(String resource) {
        printTicket(resource, ticket, m_restaurant.getTable());
        printNotify();
        j_btnKitchen.setEnabled(false);        
    } 
    
 private void printTicket(String sresourcename, TicketInfo ticket, String table) {
    if (ticket != null) {

        if (ticket.getPickupId()== 0){
            try{
                ticket.setPickupId(dlSales.getNextPickupIndex());
            }catch (BasicException e){
                ticket.setPickupId(0);
            }
        }        
                
        try {
            ScriptEngine script = ScriptFactory.getScriptEngine(ScriptFactory.VELOCITY);

            script.put("ticket", ticket);              
            script.put("place",m_restaurant.getTableName());
            script.put("pickupid",getPickupString(ticket));            
            
            m_TTP2.printTicket(script.eval(m_dlSystem.getResourceAsXML(sresourcename)).toString()); 

        } catch ( ScriptException | TicketPrinterException e) {
            JMessageDialog.showMessage(this, 
                new MessageInf(MessageInf.SGN_NOTICE, 
                AppLocal.getIntString("message.cannotprint"), e));
        }
    } 
 }

    public void printNotify(){
        final WebNotification notificationPopup = new WebNotification ();
        notificationPopup.setIcon ( NotificationIcon.information );
        notificationPopup.setDisplayTime ( 4000 );

        final WebClock clock = new WebClock ();
        clock.setClockType ( ClockType.timer );
        clock.setTimeLeft ( 5000 );
        clock.setTimePattern ( "'Printed successfully'" );
        notificationPopup.setContent ( clock );

        NotificationManager.showNotification ( notificationPopup );
        clock.start ();    
    }  
    
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        m_TablePlan = new javax.swing.JButton();
        m_MoveTable = new javax.swing.JButton();
        m_DelTicket = new javax.swing.JButton();
        j_btnKitchen = new javax.swing.JButton();

        setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        setMinimumSize(new java.awt.Dimension(250, 50));
        setPreferredSize(new java.awt.Dimension(350, 50));
        setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        m_TablePlan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/tables.png"))); // NOI18N
        m_TablePlan.setToolTipText("Go to Table Plan");
        m_TablePlan.setFocusPainted(false);
        m_TablePlan.setFocusable(false);
        m_TablePlan.setMargin(new java.awt.Insets(0, 4, 0, 4));
        m_TablePlan.setMaximumSize(new java.awt.Dimension(50, 40));
        m_TablePlan.setMinimumSize(new java.awt.Dimension(50, 40));
        m_TablePlan.setPreferredSize(new java.awt.Dimension(80, 45));
        m_TablePlan.setRequestFocusEnabled(false);
        m_TablePlan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_TablePlanActionPerformed(evt);
            }
        });
        add(m_TablePlan);

        m_MoveTable.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/movetable.png"))); // NOI18N
        m_MoveTable.setToolTipText("Move Table");
        m_MoveTable.setFocusPainted(false);
        m_MoveTable.setFocusable(false);
        m_MoveTable.setMargin(new java.awt.Insets(0, 4, 0, 4));
        m_MoveTable.setMaximumSize(new java.awt.Dimension(50, 40));
        m_MoveTable.setMinimumSize(new java.awt.Dimension(50, 40));
        m_MoveTable.setPreferredSize(new java.awt.Dimension(80, 45));
        m_MoveTable.setRequestFocusEnabled(false);
        m_MoveTable.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_MoveTableActionPerformed(evt);
            }
        });
        add(m_MoveTable);

        m_DelTicket.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/sale_delete.png"))); // NOI18N
        m_DelTicket.setToolTipText("Delete Current Order");
        m_DelTicket.setFocusPainted(false);
        m_DelTicket.setFocusable(false);
        m_DelTicket.setMargin(new java.awt.Insets(0, 4, 0, 4));
        m_DelTicket.setMaximumSize(new java.awt.Dimension(50, 40));
        m_DelTicket.setMinimumSize(new java.awt.Dimension(50, 40));
        m_DelTicket.setPreferredSize(new java.awt.Dimension(80, 45));
        m_DelTicket.setRequestFocusEnabled(false);
        m_DelTicket.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_DelTicketActionPerformed(evt);
            }
        });
        add(m_DelTicket);

        j_btnKitchen.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/printer24.png"))); // NOI18N
        j_btnKitchen.setToolTipText("Send to Kichen Printer");
        j_btnKitchen.setMargin(new java.awt.Insets(0, 4, 0, 4));
        j_btnKitchen.setMaximumSize(new java.awt.Dimension(50, 40));
        j_btnKitchen.setMinimumSize(new java.awt.Dimension(50, 40));
        j_btnKitchen.setPreferredSize(new java.awt.Dimension(80, 45));
        j_btnKitchen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                j_btnKitchenActionPerformed(evt);
            }
        });
        add(j_btnKitchen);
        j_btnKitchen.getAccessibleContext().setAccessibleDescription("Send to Remote Printer");
    }// </editor-fold>//GEN-END:initComponents

    private void m_MoveTableActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_MoveTableActionPerformed

// JG 6 Nov 13 - clear Customer from orignal table - Thanks David Kurniawan
        restDB.clearCustomerNameInTableById(m_restaurant.getTable());
        restDB.clearWaiterNameInTableById(m_restaurant.getTable());

        restDB.setTableMovedFlag(m_restaurant.getTable());
        m_restaurant.moveTicket();                 
             
    }//GEN-LAST:event_m_MoveTableActionPerformed

    @SuppressWarnings("empty-statement")
    private void m_DelTicketActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_DelTicketActionPerformed
       boolean pinOK = false;

//        if (ticket != null) {

            if (m_App.getProperties().getProperty("override.check").equals("true")) {
                Integer secret = Integer.parseInt(m_App.getProperties().getProperty("override.pin"));
                Integer iValue = JNumberPop.showEditNumber(this, AppLocal.getIntString("title.override.enterpin")); 

                if (iValue == null ? secret == null : iValue.equals(secret)) {
                    pinOK = true;
                    int res = JOptionPane.showConfirmDialog(this
                        , AppLocal.getIntString("message.wannadelete")
                        , AppLocal.getIntString("title.editor")
                        , JOptionPane.YES_NO_OPTION
                        , JOptionPane.QUESTION_MESSAGE);
        
                    if (res == JOptionPane.YES_OPTION) {
                        restDB.clearCustomerNameInTableById(m_restaurant.getTable());
                        restDB.clearWaiterNameInTableById(m_restaurant.getTable());
                        restDB.clearTicketIdInTableById(m_restaurant.getTable());
                        m_restaurant.deleteTicket();
                    }
                } else {
                    pinOK = false;
                    JOptionPane.showMessageDialog(this, AppLocal.getIntString("message.override.badpin"));                                        
                }
            }

            int res = JOptionPane.showConfirmDialog(this
                , AppLocal.getIntString("message.wannadelete")
                , AppLocal.getIntString("title.editor")
                , JOptionPane.YES_NO_OPTION
                , JOptionPane.QUESTION_MESSAGE);
        
            if (res == JOptionPane.YES_OPTION) {
                restDB.clearCustomerNameInTableById(m_restaurant.getTable());
                restDB.clearWaiterNameInTableById(m_restaurant.getTable());
                restDB.clearTicketIdInTableById(m_restaurant.getTable());
                m_restaurant.deleteTicket();
            }          
    }//GEN-LAST:event_m_DelTicketActionPerformed

    private void m_TablePlanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_TablePlanActionPerformed
    // outta here back to TableMap   
        m_restaurant.newTicket();            
    }//GEN-LAST:event_m_TablePlanActionPerformed
    
    @SuppressWarnings("empty-statement")
    private void j_btnKitchenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_j_btnKitchenActionPerformed
        ticket = m_restaurant.getActiveTicket();
        String rScript = (m_dlSystem.getResourceAsText("script.SendOrder"));

            
        try {  
            ScriptEngine scriptEngine = ScriptFactory.getScriptEngine(ScriptFactory.BEANSHELL);
            scriptEngine.put("ticket", ticket); 
            scriptEngine.put("place",m_restaurant.getTableName());            
            scriptEngine.put("user", m_App.getAppUserView().getUser());
            scriptEngine.put("sales", this);
            scriptEngine.put("pickupid", ticket.getPickupId());
            
            Object result = scriptEngine.eval(rScript);
            
        } catch (ScriptException ex) {
            Logger.getLogger(JPanelTicket.class.getName()).log(Level.SEVERE, null, ex);
        }
         // Autologoff after sales            
            String autoLogoff = (m_App.getProperties().getProperty("till.autoLogoff"));
            String autoLogoffRestaurant =(m_App.getProperties().getProperty("till.autoLogoffrestaurant"));
            if (autoLogoff != null){
                if (autoLogoff.equals("true")){  
         // check how far to logoof to ie tables or application
                    if (autoLogoffRestaurant == null){
                        ((JRootApp)m_App).closeAppView();
                    }else if (autoLogoffRestaurant.equals("true")){                        
                        m_restaurant.newTicket();  
                    }else{
                        ((JRootApp)m_App).closeAppView();
                    }    
                }    
            }
    }//GEN-LAST:event_j_btnKitchenActionPerformed
  
      
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton j_btnKitchen;
    private javax.swing.JButton m_DelTicket;
    private javax.swing.JButton m_MoveTable;
    private javax.swing.JButton m_TablePlan;
    // End of variables declaration//GEN-END:variables

    
}