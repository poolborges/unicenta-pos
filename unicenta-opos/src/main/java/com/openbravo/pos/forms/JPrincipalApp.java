//    KrOS POS Open Source Point Of Sale
//    Copyright (c) 2009-2018 uniCenta & previous Openbravo POS works
//    /product
//
//    .
//
//    This program is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    (at your option) any later version.
//
//     This program is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with Openbravo POS.  If not, see <http://www.gnu.org/licenses/>.

package com.openbravo.pos.forms;

import com.openbravo.basic.BasicException;
import com.openbravo.data.gui.JMessageDialog;
import com.openbravo.data.gui.MessageInf;
import com.openbravo.pos.customers.CustomerInfo;
import com.openbravo.pos.scripting.ScriptEngine;
import com.openbravo.pos.scripting.ScriptException;
import com.openbravo.pos.scripting.ScriptFactory;
import com.openbravo.pos.util.Hashcypher;
import com.openbravo.pos.util.StringUtils;
import java.awt.CardLayout;
import java.awt.Color;
// import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import org.jdesktop.swingx.JXTaskPane;
import org.jdesktop.swingx.JXTaskPaneContainer;
import org.jdesktop.swingx.painter.MattePainter;
// import org.jdesktop.swingx.painter.MattePainter;

/**
 *
 * @author adrianromero
 */
public class JPrincipalApp extends javax.swing.JPanel implements AppUserView {

    private static final Logger logger = Logger.getLogger("com.openbravo.pos.forms.JPrincipalApp");
    
    private final JRootApp m_appview;
    private final AppUser m_appuser;
    
    private DataLogicSystem m_dlSystem;
    
    private JLabel m_principalnotificator;
    
    private JPanelView m_jLastView;    
    private Action m_actionfirst;
    
    private Map<String, JPanelView> m_aPreparedViews; // Prepared views   
    private Map<String, JPanelView> m_aCreatedViews;
    
    private Icon menu_open;
    private Icon menu_close;
        
    //HS Updates
    private CustomerInfo customerInfo;
        
    /** Creates new form JPrincipalApp
     * @param appview
     * @param appuser */
    public JPrincipalApp(JRootApp appview, AppUser appuser) {
        
        m_appview = appview; 
        m_appuser = appuser;
                   
        m_dlSystem = (DataLogicSystem) m_appview.getBean("com.openbravo.pos.forms.DataLogicSystem");
        
        m_appuser.fillPermissions(m_dlSystem);
               
        m_actionfirst = null;
        m_jLastView = null;
     
        m_aPreparedViews = new HashMap<>();
        m_aCreatedViews = new HashMap<>();
                
        initComponents();
        
        jPanel2.add(Box.createVerticalStrut(50), 0);
        m_jPanelLeft.getVerticalScrollBar().setPreferredSize(new Dimension(35, 35));
        
        applyComponentOrientation(appview.getComponentOrientation());
       
        m_principalnotificator = new JLabel();
        m_principalnotificator.applyComponentOrientation(getComponentOrientation());
        m_principalnotificator.setText(m_appuser.getName());
        m_principalnotificator.setIcon(m_appuser.getIcon());
        
        if (jButton1.getComponentOrientation().isLeftToRight()) {
            menu_open = new javax.swing.ImageIcon(getClass().getResource(
                "/com/openbravo/images/menu-right.png"));
            menu_close = new javax.swing.ImageIcon(getClass().getResource(
                "/com/openbravo/images/menu-left.png"));
        } else {
            menu_open = new javax.swing.ImageIcon(getClass().getResource(
                "/com/openbravo/images/menu-left.png"));
            menu_close = new javax.swing.ImageIcon(getClass().getResource(
                "/com/openbravo/images/menu-right.png"));
        }
        assignMenuButtonIcon();        
                
        m_jPanelTitle.setVisible(false);       
        m_jPanelContainer.add(new JPanel(), "<NULL>");

        showView("<NULL>");
        
        try {

            m_jPanelLeft.setViewportView(getScriptMenu(
            m_dlSystem.getResourceAsText("Menu.Root")));
            
        } catch (ScriptException e) {
            logger.log(Level.SEVERE, "Cannot read Menu.Root resource. Trying default menu.", e);
            try {
                m_jPanelLeft.setViewportView(getScriptMenu(
                    StringUtils.readResource("/com/openbravo/pos/templates/Menu.Root.txt")));
            } catch (    IOException | ScriptException ex) {
                logger.log(Level.SEVERE, "Cannot read default menu", ex);
            }
        }               
    }

    private Component getScriptMenu(String menutext) throws ScriptException {

        ScriptMenu menu = new ScriptMenu();

        ScriptEngine eng = ScriptFactory.getScriptEngine(ScriptFactory.BEANSHELL);
        eng.put("menu", menu);
        eng.eval(menutext);
        return menu.getTaskPane();
    }
    
    private void assignMenuButtonIcon() {
        jButton1.setIcon(m_jPanelLeft.isVisible()
                ? menu_close
                : menu_open);
    }
    
    /**
     *
     */
    public class ScriptMenu {
        private final JXTaskPaneContainer taskPane;
        
        private ScriptMenu() {
            taskPane = new JXTaskPaneContainer();
            taskPane.applyComponentOrientation(getComponentOrientation());
        }
        
        /**
         *
         * @param key
         * @return
         */
        public ScriptGroup addGroup(String key) {
            ScriptGroup group = new ScriptGroup(key);
            taskPane.add(group.getTaskGroup());
            return group;
        }
            
        /**
         *
         * @return
        */
        public JXTaskPaneContainer getTaskPane() {  
            return taskPane;
        }
    }
    
    /**
     *
     */
    public class ScriptGroup {

        private final JXTaskPane taskGroup;
        
        private ScriptGroup(String key) {
            taskGroup = new JXTaskPane();
            taskGroup.applyComponentOrientation(getComponentOrientation());
            taskGroup.setFocusable(false);
            taskGroup.setRequestFocusEnabled(false);
            taskGroup.setTitle(AppLocal.getIntString(key));
            taskGroup.setVisible(false);
            taskGroup.setFont(new java.awt.Font("Arial",0,16));
                       
        }
        
        /**
         *
         * @param icon
         * @param key
         * @param classname
         */
        public void addPanel(String icon, String key, String classname) {            
            addAction(new MenuPanelAction(m_appview, icon, key, classname));
        }        

        /**
         *
         * @param icon
         * @param key
         * @param classname
         */
        public void addExecution(String icon, String key, String classname) {
            addAction(new MenuExecAction(m_appview, icon, key, classname));
        }        

        /**
         *
         * @param icon
         * @param key
         * @param classname
         * @return
         */
        public ScriptSubmenu addSubmenu(String icon, String key, String classname) {
            ScriptSubmenu submenu = new ScriptSubmenu(key); 
            m_aPreparedViews.put(classname, new JPanelMenu(submenu.getMenuDefinition()));
            addAction(new MenuPanelAction(m_appview, icon, key, classname));
            return submenu;
        }        

        /**
         *
         */
        public void addChangePasswordAction() {            
            addAction(new ChangePasswordAction("/com/openbravo/images/password.png", "Menu.ChangePassword"));
        }       

        /**
         *
         */
        public void addExitAction() {            
            addAction(new ExitAction("/com/openbravo/images/logout.png", "Menu.Exit"));
        }
        
        private void addAction(Action act) {
            
            if (m_appuser.hasPermission((String) act.getValue(AppUserView.ACTION_TASKNAME))) {
                Component c = taskGroup.add(act);
                c.applyComponentOrientation(getComponentOrientation());
                c.setFocusable(false);
                
                taskGroup.setVisible(true);

                if (m_actionfirst == null) {
                    m_actionfirst = act;
                }
            }
        }
        

        /**
         *
         * @return
         */
        public JXTaskPane getTaskGroup() {
            return taskGroup;
        }   
    }
    
    /**
     *
     */
    public class ScriptSubmenu {
        private final MenuDefinition menudef;
        
        private ScriptSubmenu(String key) {
            menudef = new MenuDefinition(key);
        }
        
        /**
         *
         * @param key
         */
        public void addTitle(String key) {
            menudef.addMenuTitle(key);
        }
        
        /**
         *
         * @param icon
         * @param key
         * @param classname
         */
        public void addPanel(String icon, String key, String classname) {
            menudef.addMenuItem(new MenuPanelAction(m_appview, icon, key, classname));
        }

        /**
         *
         * @param icon
         * @param key
         * @param classname
         */
        public void addExecution(String icon, String key, String classname) {
            menudef.addMenuItem(new MenuExecAction(m_appview, icon, key, classname));
        }                

        /**
         *
         * @param icon
         * @param key
         * @param classname
         * @return
         */
        public ScriptSubmenu addSubmenu(String icon, String key, String classname) {
            ScriptSubmenu submenu = new ScriptSubmenu(key); 
            m_aPreparedViews.put(classname, new JPanelMenu(submenu.getMenuDefinition()));
            menudef.addMenuItem(new MenuPanelAction(m_appview, icon, key, classname));
            return submenu;
        } 

        /**
         *
         */
        public void addChangePasswordAction() {            
            menudef.addMenuItem(new ChangePasswordAction("/com/openbravo/images/password.png", "Menu.ChangePassword"));
        }        

        /**
         *
         */
        public void addExitAction() {
            menudef.addMenuItem(new ExitAction("/com/openbravo/images/logout.png", "Menu.Exit"));
        }

        /**
         *
         * @return
         */
        public MenuDefinition getMenuDefinition() {
            return menudef;
        }
    }
    
    private void setMenuVisible(boolean value) {
        
        m_jPanelLeft.setVisible(value);
        assignMenuButtonIcon();
        revalidate();
    }
        
    /**
     *
     * @return
     */
    public JComponent getNotificator() {
        return m_principalnotificator;
    }
    
    /**
     *
     */
    public void activate() {
        
        setMenuVisible(getBounds().width > 800);
        
        if (m_actionfirst != null) {
            m_actionfirst.actionPerformed(null);
            m_actionfirst = null;
        }
    }
    
    /**
     *
     * @return
     */
    public boolean deactivate() {
        if (m_jLastView == null) {
            return true;
        } else if (m_jLastView.deactivate()) {
            m_jLastView = null;
            showView("<NULL>");       
            return true;
        } else {
            return false;
        }
        
    }
    
    private class ExitAction extends AbstractAction {
        
        public ExitAction(String icon, String keytext) {
            putValue(Action.SMALL_ICON, new ImageIcon(JPrincipalApp.class.getResource(icon)));
            putValue(Action.NAME, AppLocal.getIntString(keytext));
            putValue(AppUserView.ACTION_TASKNAME, keytext);
        }
        @Override
        public void actionPerformed(ActionEvent evt) {
            m_appview.closeAppView();
        }
    }
    
    /**
     *
     */
    public void exitToLogin() {
        m_appview.closeAppView();
    }    
    
    
    private class ChangePasswordAction extends AbstractAction {
        public ChangePasswordAction(String icon, String keytext) {
            putValue(Action.SMALL_ICON, new ImageIcon(JPrincipalApp.class.getResource(icon)));
            putValue(Action.NAME, AppLocal.getIntString(keytext));
            putValue(AppUserView.ACTION_TASKNAME, keytext);

        }
        @Override
        public void actionPerformed(ActionEvent evt) {
                       
            String sNewPassword = Hashcypher.changePassword(JPrincipalApp.this, m_appuser.getPassword());
            if (sNewPassword != null) {
                try {
                    
                    m_dlSystem.execChangePassword(new Object[] {sNewPassword, m_appuser.getId()});
                    m_appuser.setPassword(sNewPassword);
                } catch (BasicException e) {
                    JMessageDialog.showMessage(JPrincipalApp.this
                        , new MessageInf(MessageInf.SGN_WARNING
                        , AppLocal.getIntString("message.cannotchangepassword")));             
                }
            }
        }
    }
 
    
    
    private void showView(String sView) {
        CardLayout cl = (CardLayout)(m_jPanelContainer.getLayout());
        cl.show(m_jPanelContainer, sView);       
    }
    
    /**
     *
     * @return
     */
    @Override
    public AppUser getUser() {
        return m_appuser;
    }
    
    /**
     *
     * @param sTaskClass
     */
    @Override
    public void showTask(String sTaskClass) {
         
        customerInfo = new CustomerInfo("");
        customerInfo.setName("");
         
        m_appview.waitCursorBegin();       
         
        if (m_appuser.hasPermission(sTaskClass)) {            
            
            JPanelView m_jMyView = (JPanelView) m_aCreatedViews.get(sTaskClass);

            if (m_jLastView == null 
                || (m_jMyView != m_jLastView 
                && m_jLastView.deactivate())) {

                if (m_jMyView == null) {   
                    
                    m_jMyView = m_aPreparedViews.get(sTaskClass);

                    if (m_jMyView == null) {   

                        try {
                            m_jMyView = (JPanelView) m_appview.getBean(sTaskClass);
                        } catch (BeanFactoryException e) {
                            m_jMyView = new JPanelNull(m_appview, e);
                        }
                    }
                    
                    m_jMyView.getComponent().applyComponentOrientation(getComponentOrientation());
                    m_jPanelContainer.add(m_jMyView.getComponent(), sTaskClass);
                    m_aCreatedViews.put(sTaskClass, m_jMyView);
                }
                
                try {
                    m_jMyView.activate();
                } catch (BasicException e) {
                    JMessageDialog.showMessage(this
                        , new MessageInf(MessageInf.SGN_WARNING
                        , AppLocal.getIntString("message.notactive"), e));            
                }

                m_jLastView = m_jMyView;

                setMenuVisible(getBounds().width > 800);
                setMenuVisible(false);

                showView(sTaskClass);   
                String sTitle = m_jMyView.getTitle();
                m_jPanelTitle.setVisible(sTitle != null);
                m_jTitle.setText(sTitle);       
            }
        } else  {

            JMessageDialog.showMessage(this
                , new MessageInf(MessageInf.SGN_WARNING
                , AppLocal.getIntString("message.notpermissions")));            
        }
        m_appview.waitCursorEnd();       
    }
    
    /**
     *
     * @param sTaskClass
     */
    @Override
    public void executeTask(String sTaskClass) {
        
        m_appview.waitCursorBegin();       

        if (m_appuser.hasPermission(sTaskClass)) {
            try {
                ProcessAction myProcess = (ProcessAction) m_appview.getBean(sTaskClass);

                try {
                    MessageInf m = myProcess.execute();    
                    if (m != null) {
                        JMessageDialog.showMessage(JPrincipalApp.this, m);
                    }
                } catch (BasicException eb) {
                    JMessageDialog.showMessage(JPrincipalApp.this, new MessageInf(eb));            
                }
            } catch (BeanFactoryException e) {
                JMessageDialog.showMessage(JPrincipalApp.this
                    , new MessageInf(MessageInf.SGN_WARNING
                    , AppLocal.getIntString("label.LoadError"), e));            
            }                    
        } else  {
            JMessageDialog.showMessage(JPrincipalApp.this
                , new MessageInf(MessageInf.SGN_WARNING
                , AppLocal.getIntString("message.notpermissions")));            
        }
        m_appview.waitCursorEnd();    
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        m_jPanelLeft = new javax.swing.JScrollPane();
        jPanel2 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        m_jPanelRight = new javax.swing.JPanel();
        m_jPanelTitle = new javax.swing.JPanel();
        m_jTitle = new javax.swing.JLabel();
        m_jPanelContainer = new javax.swing.JPanel();

        setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        setLayout(new java.awt.BorderLayout());

        jPanel1.setLayout(new java.awt.BorderLayout());

        m_jPanelLeft.setBackground(new java.awt.Color(102, 102, 102));
        m_jPanelLeft.setBorder(null);
        m_jPanelLeft.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        m_jPanelLeft.setPreferredSize(new java.awt.Dimension(250, 2));
        jPanel1.add(m_jPanelLeft, java.awt.BorderLayout.LINE_START);

        jPanel2.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jPanel2.setPreferredSize(new java.awt.Dimension(45, 45));

        jButton1.setToolTipText(AppLocal.getIntString("tooltip.menu")); // NOI18N
        jButton1.setFocusPainted(false);
        jButton1.setFocusable(false);
        jButton1.setIconTextGap(0);
        jButton1.setMargin(new java.awt.Insets(10, 2, 10, 2));
        jButton1.setMaximumSize(new java.awt.Dimension(45, 32224661));
        jButton1.setMinimumSize(new java.awt.Dimension(32, 32));
        jButton1.setPreferredSize(new java.awt.Dimension(36, 45));
        jButton1.setRequestFocusEnabled(false);
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap(88, Short.MAX_VALUE)
                .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, 33, Short.MAX_VALUE)
                .addContainerGap(188, Short.MAX_VALUE))
        );

        jPanel1.add(jPanel2, java.awt.BorderLayout.LINE_END);

        add(jPanel1, java.awt.BorderLayout.LINE_START);

        m_jPanelRight.setPreferredSize(new java.awt.Dimension(200, 40));
        m_jPanelRight.setLayout(new java.awt.BorderLayout());

        m_jPanelTitle.setLayout(new java.awt.BorderLayout());

        m_jTitle.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        m_jTitle.setForeground(new java.awt.Color(0, 168, 223));
        m_jTitle.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 1, 0, java.awt.Color.darkGray), javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        m_jTitle.setMaximumSize(new java.awt.Dimension(100, 35));
        m_jTitle.setMinimumSize(new java.awt.Dimension(30, 25));
        m_jTitle.setPreferredSize(new java.awt.Dimension(100, 35));
        m_jPanelTitle.add(m_jTitle, java.awt.BorderLayout.NORTH);

        m_jPanelRight.add(m_jPanelTitle, java.awt.BorderLayout.NORTH);

        m_jPanelContainer.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        m_jPanelContainer.setLayout(new java.awt.CardLayout());
        m_jPanelRight.add(m_jPanelContainer, java.awt.BorderLayout.CENTER);

        add(m_jPanelRight, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed

    setMenuVisible(!m_jPanelLeft.isVisible());
    
}//GEN-LAST:event_jButton1ActionPerformed
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel m_jPanelContainer;
    private javax.swing.JScrollPane m_jPanelLeft;
    private javax.swing.JPanel m_jPanelRight;
    private javax.swing.JPanel m_jPanelTitle;
    private javax.swing.JLabel m_jTitle;
    // End of variables declaration//GEN-END:variables
    
}