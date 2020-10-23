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

package com.openbravo.pos.sales.restaurant;

import javax.swing.Timer;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import com.openbravo.basic.BasicException;
import com.openbravo.data.gui.MessageInf;
import com.openbravo.data.gui.NullIcon;
import com.openbravo.data.loader.SentenceList;
import com.openbravo.data.loader.SerializerReadClass;
import com.openbravo.data.loader.StaticSentence;
import com.openbravo.pos.customers.CustomerInfo;
import com.openbravo.pos.forms.AppConfig;
import com.openbravo.pos.forms.AppLocal;
import com.openbravo.pos.forms.AppView;
import com.openbravo.pos.forms.DataLogicSales;
import com.openbravo.pos.forms.DataLogicSystem;
import com.openbravo.pos.sales.DataLogicReceipts;
import com.openbravo.pos.sales.JTicketsBag;
import com.openbravo.pos.sales.SharedTicketInfo;
import com.openbravo.pos.sales.TicketsEditor;
import com.openbravo.pos.ticket.TicketInfo;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 *
 * @author JG uniCenta
 */
public class JTicketsBagRestaurantMap extends JTicketsBag {

    private static class ServerCurrent {

        public ServerCurrent() {
        }
    }

    private java.util.List<Place> m_aplaces;
    private java.util.List<Floor> m_afloors;
    
    private JTicketsBagRestaurant m_restaurantmap;  
    private final JTicketsBagRestaurantRes m_jreservations;   
    private Place m_PlaceCurrent;
    private ServerCurrent m_ServerCurrent;
    private Place m_PlaceClipboard;  
    private CustomerInfo customer;

    private DataLogicReceipts dlReceipts = null;
    private DataLogicSales dlSales = null;
    private DataLogicSystem dlSystem = null;    
    private final RestaurantDBUtils restDB;
    private static final Icon ICO_OCU_SM = new ImageIcon(Place.class.getResource("/com/openbravo/images/edit_group_sm.png"));
    private static final Icon ICO_WAITER = new NullIcon(1, 1);  
    private static final Icon ICO_FRE = new NullIcon(22, 22);
    private String waiterDetails;
    private String customerDetails;
    private String tableName;
    private Boolean transBtns;
    private Boolean actionEnabled = true;    
    private int newX;
    private int newY;    
    private AppView m_app;
    private Boolean showLayout = false;
        
    /** Creates new form JTicketsBagRestaurant
     * @param app
     * @param panelticket */

    public JTicketsBagRestaurantMap(AppView app, TicketsEditor panelticket) {
        
        super(app, panelticket);
      
        restDB = new  RestaurantDBUtils(app);        
        transBtns = AppConfig.getInstance().getBoolean("table.transbtn");
        
        dlReceipts = (DataLogicReceipts) app.getBean("com.openbravo.pos.sales.DataLogicReceipts");
        dlSales = (DataLogicSales) m_App.getBean("com.openbravo.pos.forms.DataLogicSales");
        dlSystem = (DataLogicSystem) m_App.getBean("com.openbravo.pos.forms.DataLogicSystem");        
        
        m_restaurantmap = new JTicketsBagRestaurant(app, this);
        m_PlaceCurrent = null;
        m_PlaceClipboard = null;
        customer = null;
            
        try {
            SentenceList sent = new StaticSentence(
                app.getSession(), 
                "SELECT ID, NAME, IMAGE FROM floors ORDER BY NAME", 
                null, 
                new SerializerReadClass(Floor.class));
            m_afloors = sent.list();
               
        } catch (BasicException eD) {
            m_afloors = new ArrayList<>();
        }
        try {
            SentenceList sent = new StaticSentence(
                app.getSession(), 
                "SELECT ID, NAME, SEATS, X, Y, FLOOR, CUSTOMER, WAITER, TICKETID, TABLEMOVED FROM places ORDER BY FLOOR",
                null, 
                new SerializerReadClass(Place.class));
            m_aplaces = sent.list();
        } catch (BasicException eD) {
            m_aplaces = new ArrayList<>();
        } 
        
        initComponents(); 
        
        m_jbtnSave.setVisible(false);
        
        if (m_afloors.size() > 1) {
            JTabbedPane jTabFloors = new JTabbedPane();
            jTabFloors.applyComponentOrientation(getComponentOrientation());
            jTabFloors.setBorder(new javax.swing.border.EmptyBorder(new Insets(5, 5, 5, 5)));
            jTabFloors.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
            jTabFloors.setFocusable(false);
            jTabFloors.setRequestFocusEnabled(false);
            m_jPanelMap.add(jTabFloors, BorderLayout.CENTER);
            
            m_afloors.stream().map((f) -> {
                f.getContainer().applyComponentOrientation(getComponentOrientation());
                return f;                
            }).forEach((f) -> {
                JScrollPane jScrCont = new JScrollPane();
                jScrCont.applyComponentOrientation(getComponentOrientation());
                JPanel jPanCont = new JPanel();  
                jPanCont.applyComponentOrientation(getComponentOrientation());
                
                jTabFloors.addTab(f.getName(), f.getIcon(), jScrCont);     
                jScrCont.setViewportView(jPanCont);
                jPanCont.add(f.getContainer());
            });
        } else if (m_afloors.size() == 1) {
            Floor f = m_afloors.get(0);
            f.getContainer().applyComponentOrientation(getComponentOrientation());
            
            JPanel jPlaces = new JPanel();
            jPlaces.applyComponentOrientation(getComponentOrientation());
            jPlaces.setLayout(new BorderLayout());
            jPlaces.setBorder(new javax.swing.border.CompoundBorder(
                new javax.swing.border.EmptyBorder(new Insets(5, 5, 5, 5)),
                new javax.swing.border.TitledBorder(f.getName())));
            
            JScrollPane jScrCont = new JScrollPane();
            jScrCont.applyComponentOrientation(getComponentOrientation());
            JPanel jPanCont = new JPanel();
            jPanCont.applyComponentOrientation(getComponentOrientation());
            
            m_jPanelMap.add(jPlaces, BorderLayout.CENTER);
            jPlaces.add(jScrCont, BorderLayout.CENTER);
            jScrCont.setViewportView(jPanCont);            
            jPanCont.add(f.getContainer());
        }   
        
        Floor currfloor = null;
        
        for (Place pl : m_aplaces) {
            int iFloor = 0;
            
            if (currfloor == null || !currfloor.getID().equals(pl.getFloor())) {
                do {
                    currfloor = m_afloors.get(iFloor++);
                } while (!currfloor.getID().equals(pl.getFloor()));
            }

            currfloor.getContainer().add(pl.getButton());
            pl.setButtonBounds();

            if (transBtns) {
                pl.getButton().setOpaque(false);
                pl.getButton().setContentAreaFilled(false);
                pl.getButton().setBorderPainted(false);
            }
            
            pl.getButton().addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent E) {
                if (!actionEnabled) {
                    if (pl.getDiffX() == 0) {
                        pl.setDiffX(pl.getButton().getX() - pl.getX());
                        pl.setDiffY(pl.getButton().getY() - pl.getY());
                    }
                    newX = E.getX() + pl.getButton().getX();
                    newY = E.getY() + pl.getButton().getY();
                    pl.getButton().setBounds(newX + pl.getDiffX(), newY + pl.getDiffY(),
                        pl.getButton().getWidth(), pl.getButton().getHeight());
                        pl.setX(newX);
                        pl.setY(newY);
                    }
                }
            }
            );

            pl.getButton().addActionListener(new MyActionListener(pl));
        }
        
        m_jreservations = new JTicketsBagRestaurantRes(app, this);
        add(m_jreservations, "res");

        showLayout = m_App.getAppUserView().getUser().hasPermission("sales.Layout");
        if (showLayout) {
            m_jbtnLayout.setVisible(true);
            m_jbtnSave.setVisible(false);            
        } else {
            m_jbtnLayout.setVisible(false);
            m_jbtnSave.setVisible(false);            
        }        
        
        if (m_App.getProperties().getProperty("till.autoRefreshTableMap").equals("true")) {
            webLblautoRefresh.setText(java.util.ResourceBundle.getBundle("pos_messages")
                .getString("label.autoRefreshTableMapTimerON"));        
            
            Timer autoRefreshTimer = new Timer(Integer.parseInt(m_App.getProperties()
                .getProperty("till.autoRefreshTimer"))*1000, new tableMapRefresh());
    
            autoRefreshTimer.start();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                autoRefreshTimer.stop();
            } 
        } else {
            webLblautoRefresh.setText(java.util.ResourceBundle.getBundle("pos_messages")
                .getString("label.autoRefreshTableMapTimerOFF"));        
        }

}

    class tableMapRefresh implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            loadTickets();
            printState(); 
        }
    }   
        
    /**
     *
     */
    @Override
    public void activate() {

        showLayout = m_App.getAppUserView().getUser().hasPermission("sales.Layout");
        if (showLayout) {
            m_jbtnLayout.setVisible(true);
            m_jbtnSave.setVisible(false);
        } else {
            m_jbtnLayout.setVisible(false);
            m_jbtnSave.setVisible(false);            
        } 
        
        m_PlaceClipboard = null;
        customer = null;
        loadTickets();        
        printState(); 
        
        m_panelticket.setActiveTicket(null, null); 
        m_restaurantmap.activate();
       
        showView("map");      
    }
    
    /**
     *
     * @return
     */
    @Override
    public boolean deactivate() {
        
        if (viewTables()) {
            m_PlaceClipboard = null;
            customer = null;

            if (m_PlaceCurrent != null) {
                            
                try {
                    dlReceipts.updateSharedTicket(m_PlaceCurrent.getId(), 
                        m_panelticket.getActiveTicket(),
                        m_panelticket.getActiveTicket().getPickupId());
                    dlReceipts.unlockSharedTicket(m_PlaceCurrent.getId(),null);
                } catch (BasicException e) {
                    new MessageInf(e).show(this);
                }                                  
 
                m_PlaceCurrent = null;

            }
            printState();     
            m_panelticket.setActiveTicket(null, null); 

            return true;
        } else {
            return false;
        }
    }

    /**
     *
     * @return
     */
    @Override
    protected JComponent getBagComponent() {
        return m_restaurantmap;
    }

    /**
     *
     * @return
     */
    @Override
    protected JComponent getNullComponent() {
        return this;
    }

    /**
     *
     * @return
     */
    public TicketInfo getActiveTicket() {
        return m_panelticket.getActiveTicket();
    }

    /**
     *
     */
    public void moveTicket() {
        if (m_PlaceCurrent != null) {
                          
            try {
                dlReceipts.updateRSharedTicket(m_PlaceCurrent.getId(), 
                    m_panelticket.getActiveTicket(),m_panelticket.getActiveTicket().getPickupId());
            } catch (BasicException e) {
                new MessageInf(e).show(this);
            }      
            
            m_PlaceClipboard = m_PlaceCurrent;    
            
            customer = null;
            m_PlaceCurrent = null;
        }
        
        printState();
        m_panelticket.setActiveTicket(null, null);
    }
    
    /**
     *
     * @param c
     * @return
     */
    public boolean viewTables(CustomerInfo c) {
        if (m_jreservations.deactivate()) {
            showView("map");
            m_PlaceClipboard = null;    
            customer = c;     
            printState();
            return true;
        } else {
            return false;
        }        
    }
    
    /**
     *
     * @return
     */
    public boolean viewTables() {
        return viewTables(null);
    }
        
    /**
     *
     */
    public void newTicket() {

        if (m_PlaceCurrent != null) {

            try {
                String m_lockState = null;
                m_lockState = dlReceipts.getLockState(m_PlaceCurrent.getId(), m_lockState);
                dlReceipts.getSharedTicket(m_PlaceCurrent.getId());

                if ("override".equals(m_lockState)
                        || "locked".equals(m_lockState)) {
                    dlReceipts.updateSharedTicket(m_PlaceCurrent.getId(),
                        m_panelticket.getActiveTicket(),
                        m_panelticket.getActiveTicket().getPickupId());
                    dlReceipts.unlockSharedTicket(m_PlaceCurrent.getId(), null);
                    m_PlaceCurrent = null;                        
                } else {
                    JOptionPane.showMessageDialog(null
                        , AppLocal.getIntString("message.sharedticketlockoverriden")
                        , AppLocal.getIntString("title.editor")
                        , JOptionPane.INFORMATION_MESSAGE);                        
                }
            } catch (BasicException ex) {
                Logger.getLogger(JTicketsBagRestaurantMap.class.getName()).log(Level.SEVERE, null,ex);
            }
        }
        
        printState();     
        m_panelticket.setActiveTicket(null, null);     
    }
    
    /**
     *
     * @return
     */
    public String getTable() {
        String id = null;
        if (m_PlaceCurrent != null) {
            id = m_PlaceCurrent.getId();
    }
        return(id);
    }
    
    /**
     *
     * @return
     */
    public String getTableName() {
        String stableName = null;
        if (m_PlaceCurrent != null) {
            stableName = m_PlaceCurrent.getName();
    }
        return(stableName);
    }

    /**
     *
     */
    @Override
    public void deleteTicket() {
        
        if (m_PlaceCurrent != null) {
            String id = m_PlaceCurrent.getId();
            try {
                dlReceipts.deleteSharedTicket(id);
            } catch (BasicException e) {
                new MessageInf(e).show(this);
            }       
            
            m_PlaceCurrent.setPeople(false);
            m_PlaceCurrent = null;
        }        
        
        printState();     
        m_panelticket.setActiveTicket(null, null); 
    }

    /**
     *
     */
    public void changeServer() {

        if (m_ServerCurrent != null) {
        }
    }
    
    /**
     *
     */
    public void loadTickets() {
       
        Set<String> atickets = new HashSet<>();
        
        try {
            java.util.List<SharedTicketInfo> l = dlReceipts.getSharedTicketList();
            l.stream().forEach((ticket) -> {
                atickets.add(ticket.getId());
            });
        } catch (BasicException e) {
            new MessageInf(e).show(this);
        }            
            
        m_aplaces.stream().forEach((table) -> {
            table.setPeople(atickets.contains(table.getId()));
        });
    }
    
/*
 *  Populate the floor plans and tables    
*/
    private void printState() {
        String sDB;
        sDB = m_App.getProperties().getProperty("db.engine");        
        
        if (m_PlaceClipboard == null) {
            if (customer == null) {
                m_jText.setText(null);

                m_aplaces.stream().map((place) -> {
                    place.getButton().setEnabled(true);
                    return place;
                }).map((place) -> {
                    if (m_App.getProperties().getProperty("table.tablecolour")== null){
                        tableName="<style=font-size:9px;font-weight:bold;><font color = black>"
                                + place.getName()+"</font></style>";  
                    }else{
                        tableName="<style=font-size:9px;font-weight:bold;><font color ="
                                + m_App.getProperties().getProperty("table.tablecolour")+ ">"
                                + place.getName()+"</font></style>";     
                    }
                    return place;            
                }).map((place) -> {
                    if (Boolean.parseBoolean(m_App.getProperties().getProperty("table.showwaiterdetails"))){
                        if (m_App.getProperties().getProperty("table.waitercolour")== null){
                            waiterDetails = (restDB.getWaiterNameInTable(place.getName()) ==null)? ""
                                    :"<style=font-size:9px;font-weight:bold;><font color = red>"
                                    + restDB.getWaiterNameInTableById(place.getId())+"</font></style><br>";
                        }else{
                            waiterDetails = (restDB.getWaiterNameInTable(place.getName()) ==null)? ""
                                    :"<style=font-size:9px;font-weight:bold;><font color ="
                                    + m_App.getProperties().getProperty("table.waitercolour")+ ">"
                                    + restDB.getWaiterNameInTableById(place.getId())+"</font></style><br>";
                        }
                        place.getButton().setIcon(ICO_OCU_SM);
                    } else {
                        waiterDetails = ""; 
                    }
                    return place;           
                }).map((place) -> {
                    if (Boolean.parseBoolean(m_App.getProperties().getProperty("table.showcustomerdetails"))){
                        place.getButton().setIcon((Boolean.parseBoolean(m_App.getProperties().getProperty("table.showwaiterdetails"))
                                && (restDB.getCustomerNameInTable(place.getName()) !=null))
                                ? ICO_WAITER:ICO_OCU_SM);
                        if (m_App.getProperties().getProperty("table.customercolour")== null){                
                            customerDetails = (restDB.getCustomerNameInTable(place.getName()) ==null)? ""
                                    :"<style=font-size:9px;font-weight:bold;><font color = blue>"
                                    + restDB.getCustomerNameInTableById(place.getId())+"</font></style><br>";
                        }else{
                            customerDetails = (restDB.getCustomerNameInTable(place.getName()) ==null)? ""
                                    :"<style=font-size:9px;font-weight:bold;><font color ="
                                    + m_App.getProperties().getProperty("table.customercolour")+ ">"
                                    + restDB.getCustomerNameInTableById(place.getId())+"</font></style><br>";
                        }
                    } else {
                        customerDetails = ""; 
                    }
                    return place;
                }).map((place) -> { 
                    if ((Boolean.parseBoolean(m_App.getProperties().getProperty("table.showwaiterdetails")))
                            || (Boolean.parseBoolean(m_App.getProperties().getProperty("table.showcustomerdetails")))) {
                        place.getButton().setText("<html><center>"
                                + customerDetails + waiterDetails  +tableName+"</html>" );
                    }else{
                        if (m_App.getProperties().getProperty("table.tablecolour")== null){
                            tableName="<style=font-size:10px;font-weight:bold;><font color = black>"
                                    + place.getName()+"</font></style>";  
                        }else{
                            tableName="<style=font-size:10px;font-weight:bold;><font color ="
                                    + m_App.getProperties().getProperty("table.tablecolour")+ ">"
                                    + place.getName()+"</font></style>";     
                        }
                        
                        place.getButton().setText("<html><center>"+tableName+"</html>");                             
                    }
                    return place;
                }).filter((place) -> (!place.hasPeople())).forEach((place) -> {
                    place.getButton().setIcon(ICO_FRE);
                });
                
                m_jbtnReservations.setEnabled(true);
            } else {
                m_jText.setText(AppLocal.getIntString("label.restaurantcustomer"
                    , new Object[] {customer.getName()
                    }
                ));

                m_aplaces.stream().forEach((place) -> {
                    place.getButton().setEnabled(!place.hasPeople());
                });
                m_jbtnReservations.setEnabled(false);
            }
        } else {
            m_jText.setText(AppLocal.getIntString("label.restaurantmove"
                , new Object[] {m_PlaceClipboard.getName()
                }
            ));

            m_aplaces.stream().forEach((place) -> {
                place.getButton().setEnabled(true);
            });  

            m_jbtnReservations.setEnabled(false);
        }       
    }
    
    private TicketInfo getTicketInfo(Place place) {

        try {
            return dlReceipts.getSharedTicket(place.getId());
        } catch (BasicException e) {
            new MessageInf(e).show(JTicketsBagRestaurantMap.this);
            return null;
        }
    }
    
    private void setActivePlace(Place place, TicketInfo ticket) {
        m_PlaceCurrent = place;
        m_panelticket.setActiveTicket(ticket, m_PlaceCurrent.getName());

        try {
            dlReceipts.lockSharedTicket(m_PlaceCurrent.getId(),"locked");
        } catch (BasicException ex) {
            Logger.getLogger(JTicketsBagRestaurantMap.class.getName()).log(Level.SEVERE, null, ex);
        }        
    } 

    private void showView(String view) {
        CardLayout cl = (CardLayout)(getLayout());
        cl.show(this, view);  
    }

    private class MyActionListener implements ActionListener {

        private final Place m_place;
        public MyActionListener(Place place) {
            m_place = place;
        }
        @Override
        public void actionPerformed(ActionEvent evt) {    
        m_App.getAppUserView().getUser();    
        
        if (!actionEnabled) {
            m_place.setDiffX(0);
        } else {

        if (m_PlaceClipboard == null) {  
            TicketInfo ticket = getTicketInfo(m_place);
            if (ticket == null) {                                               
                ticket = new TicketInfo();
                ticket.setUser(m_App.getAppUserView().getUser().getUserInfo());
                try {
                    dlReceipts.insertSharedTicket(m_place.getId(), ticket, ticket.getPickupId());               
                } catch (BasicException e) {
                    new MessageInf(e).show(JTicketsBagRestaurantMap.this);
                }
                m_place.setPeople(true);                   
                setActivePlace(m_place, ticket);   
            } else {                                                            
                String m_lockState = null;
                try {
                    m_lockState = dlReceipts.getLockState(m_place.getId(), m_lockState);
                    if ("locked".equals(m_lockState)) {
                        JOptionPane.showMessageDialog(null, 
                            AppLocal.getIntString("message.sharedticketlock")); 
                        if (m_App.getAppUserView().getUser().hasPermission("sales.Override")) {                    
                            int res = JOptionPane.showConfirmDialog(null
                                , AppLocal.getIntString("message.sharedticketlockoverride")
                                , AppLocal.getIntString("title.editor")
                                , JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                            if (res == JOptionPane.YES_OPTION) {                
                                m_place.setPeople(true);                                             
                                m_PlaceClipboard = null;
                                setActivePlace(m_place, ticket);
                                dlReceipts.lockSharedTicket(m_PlaceCurrent.getId(),"locked");                                        
                            }                        
                        }
                    } else {                                                    
                        String m_user = m_App.getAppUserView().getUser().getName();
                        String ticketuser = m_place.getWaiter();
                        if (m_user.equals(ticketuser) ||
                            m_App.getAppUserView().getUser().hasPermission("sales.Override")) {
                            m_place.setPeople(true);                                             
                            m_PlaceClipboard = null;
                            m_lockState = "locked";
                            setActivePlace(m_place, ticket);
                        } else {
                            JOptionPane.showMessageDialog(null
                                , AppLocal.getIntString("message.sharedticket")
                                , AppLocal.getIntString("title.editor")
                                , JOptionPane.OK_OPTION);
                        }
                    }
                } catch (BasicException ex) {
                    Logger.getLogger(JTicketsBagRestaurantMap.class.getName()).log(Level.SEVERE, null, ex);
                }
            }    
        }
// This block handles Merge
        if (m_PlaceClipboard != null) {  
            TicketInfo ticketclip = getTicketInfo(m_PlaceClipboard);
            if (ticketclip != null) {
                if (m_PlaceClipboard == m_place) {
                    Place placeclip = m_PlaceClipboard;                       
                    m_PlaceClipboard = null;
                    customer = null;
                    printState();
                    setActivePlace(placeclip, ticketclip);
                } 
                if (m_place.hasPeople()) {
                    TicketInfo ticket = getTicketInfo(m_place);
                    if (ticket != null) {
                        if (JOptionPane.showConfirmDialog(JTicketsBagRestaurantMap.this,
                            AppLocal.getIntString("message.mergetablequestion"), 
                            AppLocal.getIntString("message.mergetable"), 
                            JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {                                 
                            try {
                                m_PlaceClipboard.setPeople(false);
                                if (ticket.getCustomer() == null) {
                                    ticket.setCustomer(ticketclip.getCustomer());
                                }
                                ticketclip.getLines().stream().forEach((line) -> {
                                    ticket.addLine(line);
                                });
                                dlReceipts.updateRSharedTicket(m_place.getId(), 
                                    ticket, ticket.getPickupId());
                                dlReceipts.deleteSharedTicket(m_PlaceClipboard.getId());   
                            } catch (BasicException e) {
                                new MessageInf(e).show(JTicketsBagRestaurantMap.this);
                            }
                            m_PlaceClipboard = null;
                            customer = null;
                            restDB.clearCustomerNameInTable(restDB.getTableDetails(ticketclip.getId()));
                            restDB.clearWaiterNameInTable(restDB.getTableDetails(ticketclip.getId()));
                            restDB.clearTableMovedFlag(restDB.getTableDetails(ticketclip.getId()));
                            restDB.clearTicketIdInTable(restDB.getTableDetails(ticketclip.getId()));
                            printState();
                            setActivePlace(m_place, ticket);
                        } else { 
                            Place placeclip = m_PlaceClipboard;                       
                            m_PlaceClipboard = null;
                            customer = null;
                            printState();
                            setActivePlace(placeclip, ticketclip);                                   
                        }
                    } else {
                        new MessageInf(MessageInf.SGN_WARNING, 
                            AppLocal.getIntString("message.tableempty"))
                            .show(JTicketsBagRestaurantMap.this);
                        m_place.setPeople(false);                            
                    }                                
                } else { 
                    TicketInfo ticket = getTicketInfo(m_place);
                    if (ticket == null) {
                        try {
                            dlReceipts.insertRSharedTicket(m_place.getId(), 
                            ticketclip, ticketclip.getPickupId());
                            m_place.setPeople(true);                              
                            dlReceipts.deleteSharedTicket(m_PlaceClipboard.getId());
                            m_PlaceClipboard.setPeople(false);
                        } catch (BasicException e) {
                            new MessageInf(e).show(JTicketsBagRestaurantMap.this);
                        }
                        m_PlaceClipboard = null;
                        customer = null;
                        printState();
                        setActivePlace(m_place, ticketclip);                            
                    } else {
                        new MessageInf(MessageInf.SGN_WARNING, 
                            AppLocal.getIntString("message.tablefull"))
                                .show(JTicketsBagRestaurantMap.this);
                            m_PlaceClipboard.setPeople(true);
                            printState();
                    }
                }
            } else {
                new MessageInf(MessageInf.SGN_WARNING, 
                    AppLocal.getIntString("message.tableempty")).show(JTicketsBagRestaurantMap.this);
                m_PlaceClipboard.setPeople(false);
                m_PlaceClipboard = null;
                customer = null;
                printState();
            }
        }} 
    }
    }

    /**
     *
     * @param btnText
     */
    public void setButtonTextBags(String btnText){
      m_PlaceClipboard.setButtonText(btnText);
  } 

    
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        m_jPanelMap = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        m_jbtnReservations = new javax.swing.JButton();
        m_jbtnRefresh = new javax.swing.JButton();
        m_jText = new javax.swing.JLabel();
        m_jbtnLayout = new javax.swing.JButton();
        m_jbtnSave = new javax.swing.JButton();
        webLblautoRefresh = new com.alee.laf.label.WebLabel();

        setLayout(new java.awt.CardLayout());

        m_jPanelMap.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        m_jPanelMap.setLayout(new java.awt.BorderLayout());

        jPanel1.setLayout(new java.awt.BorderLayout());

        jPanel2.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jPanel2.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        m_jbtnReservations.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        m_jbtnReservations.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/date.png"))); // NOI18N
        m_jbtnReservations.setText(AppLocal.getIntString("button.reservations")); // NOI18N
        m_jbtnReservations.setToolTipText("Open Reservations screen");
        m_jbtnReservations.setFocusPainted(false);
        m_jbtnReservations.setFocusable(false);
        m_jbtnReservations.setMargin(new java.awt.Insets(8, 14, 8, 14));
        m_jbtnReservations.setMaximumSize(new java.awt.Dimension(133, 40));
        m_jbtnReservations.setMinimumSize(new java.awt.Dimension(133, 40));
        m_jbtnReservations.setPreferredSize(new java.awt.Dimension(133, 45));
        m_jbtnReservations.setRequestFocusEnabled(false);
        m_jbtnReservations.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jbtnReservationsActionPerformed(evt);
            }
        });
        jPanel2.add(m_jbtnReservations);

        m_jbtnRefresh.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        m_jbtnRefresh.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/reload.png"))); // NOI18N
        m_jbtnRefresh.setText(AppLocal.getIntString("button.reloadticket")); // NOI18N
        m_jbtnRefresh.setToolTipText("Reload table information");
        m_jbtnRefresh.setFocusPainted(false);
        m_jbtnRefresh.setFocusable(false);
        m_jbtnRefresh.setMargin(new java.awt.Insets(8, 14, 8, 14));
        m_jbtnRefresh.setMaximumSize(new java.awt.Dimension(100, 40));
        m_jbtnRefresh.setMinimumSize(new java.awt.Dimension(100, 40));
        m_jbtnRefresh.setPreferredSize(new java.awt.Dimension(100, 45));
        m_jbtnRefresh.setRequestFocusEnabled(false);
        m_jbtnRefresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jbtnRefreshActionPerformed(evt);
            }
        });
        jPanel2.add(m_jbtnRefresh);

        m_jText.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jPanel2.add(m_jText);

        m_jbtnLayout.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        m_jbtnLayout.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/movetable.png"))); // NOI18N
        m_jbtnLayout.setText(AppLocal.getIntString("button.layout")); // NOI18N
        m_jbtnLayout.setToolTipText("");
        m_jbtnLayout.setFocusPainted(false);
        m_jbtnLayout.setFocusable(false);
        m_jbtnLayout.setMargin(new java.awt.Insets(8, 14, 8, 14));
        m_jbtnLayout.setMaximumSize(new java.awt.Dimension(100, 40));
        m_jbtnLayout.setMinimumSize(new java.awt.Dimension(100, 40));
        m_jbtnLayout.setPreferredSize(new java.awt.Dimension(100, 45));
        m_jbtnLayout.setRequestFocusEnabled(false);
        m_jbtnLayout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jbtnLayoutActionPerformed(evt);
            }
        });
        jPanel2.add(m_jbtnLayout);

        m_jbtnSave.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        m_jbtnSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/filesave.png"))); // NOI18N
        m_jbtnSave.setText(AppLocal.getIntString("button.save")); // NOI18N
        m_jbtnSave.setToolTipText("");
        m_jbtnSave.setFocusPainted(false);
        m_jbtnSave.setFocusable(false);
        m_jbtnSave.setMargin(new java.awt.Insets(8, 14, 8, 14));
        m_jbtnSave.setMaximumSize(new java.awt.Dimension(100, 40));
        m_jbtnSave.setMinimumSize(new java.awt.Dimension(100, 40));
        m_jbtnSave.setPreferredSize(new java.awt.Dimension(100, 45));
        m_jbtnSave.setRequestFocusEnabled(false);
        m_jbtnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jbtnSaveActionPerformed(evt);
            }
        });
        jPanel2.add(m_jbtnSave);

        jPanel1.add(jPanel2, java.awt.BorderLayout.LINE_START);

        webLblautoRefresh.setBackground(new java.awt.Color(255, 51, 51));
        webLblautoRefresh.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("pos_messages"); // NOI18N
        webLblautoRefresh.setText(bundle.getString("label.autoRefreshTableMapTimerON")); // NOI18N
        webLblautoRefresh.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jPanel1.add(webLblautoRefresh, java.awt.BorderLayout.CENTER);

        m_jPanelMap.add(jPanel1, java.awt.BorderLayout.NORTH);

        add(m_jPanelMap, "map");
    }// </editor-fold>//GEN-END:initComponents

    private void m_jbtnRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jbtnRefreshActionPerformed
        m_PlaceClipboard = null;
        customer = null;
        loadTickets();     
        printState();   
    }//GEN-LAST:event_m_jbtnRefreshActionPerformed

    private void m_jbtnReservationsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jbtnReservationsActionPerformed
        showView("res");
        m_jreservations.activate();
    }//GEN-LAST:event_m_jbtnReservationsActionPerformed

    private void m_jbtnLayoutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jbtnLayoutActionPerformed
        if (java.util.ResourceBundle.getBundle("pos_messages")
                .getString("button.layout").equals(m_jbtnLayout.getText())) {
            actionEnabled = false;
            m_jbtnSave.setVisible(true);
            m_jbtnLayout.setText(java.util.ResourceBundle
                    .getBundle("pos_messages").getString("button.disablelayout"));
            
            for (Place pl : m_aplaces) {
                if (transBtns) {
                    pl.getButton().setOpaque(true);
                    pl.getButton().setContentAreaFilled(true);
                    pl.getButton().setBorderPainted(true);
                }
            }
        } else {
            actionEnabled = true;
            m_jbtnSave.setVisible(false);
            m_jbtnLayout.setText(java.util.ResourceBundle
                    .getBundle("pos_messages").getString("button.layout"));

            for (Place pl : m_aplaces) {
                if (transBtns) {
                    pl.getButton().setOpaque(false);
                    pl.getButton().setContentAreaFilled(false);
                    pl.getButton().setBorderPainted(false);
                }
            }
        }
    }//GEN-LAST:event_m_jbtnLayoutActionPerformed

    private void m_jbtnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jbtnSaveActionPerformed
        for (Place pl : m_aplaces) {
            try {
                dlSystem.updatePlaces(pl.getX(), pl.getY(), pl.getId());
            } catch (BasicException ex) {
                Logger.getLogger(JTicketsBagRestaurantMap.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_m_jbtnSaveActionPerformed
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel m_jPanelMap;
    private javax.swing.JLabel m_jText;
    private javax.swing.JButton m_jbtnLayout;
    private javax.swing.JButton m_jbtnRefresh;
    private javax.swing.JButton m_jbtnReservations;
    private javax.swing.JButton m_jbtnSave;
    private com.alee.laf.label.WebLabel webLblautoRefresh;
    // End of variables declaration//GEN-END:variables
    
}
