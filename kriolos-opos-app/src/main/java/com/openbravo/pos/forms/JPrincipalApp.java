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
import com.openbravo.data.gui.JMessageDialog;
import com.openbravo.data.gui.MessageInf;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;

/**
 *
 * @author adrianromero
 */
public class JPrincipalApp extends JPanel implements AppUserView {

    private static final Logger LOGGER = Logger.getLogger(JPrincipalApp.class.getName());
    private static final long serialVersionUID = 1L;

    private final JRootApp m_appview;
    private final AppUser m_appuser;
    private final DataLogicSystem m_dlSystem;
    private final JLabel m_principalnotificator;

    private Icon menu_open;
    private Icon menu_close;

    private final JRootMenu rMenu;

    /**
     * Creates a JPanel
     *
     * @param appview
     * @param appuser
     */
    public JPrincipalApp(JRootApp appview, AppUser appuser) {

        m_appview = appview;
        m_appuser = appuser;

        m_dlSystem = (DataLogicSystem) m_appview.getBean("com.openbravo.pos.forms.DataLogicSystem");

        m_appuser.fillPermissions(m_dlSystem);

        initComponents();
        applyComponentOrientation(m_appview.getComponentOrientation());

        m_principalnotificator = new JLabel();
        m_principalnotificator.applyComponentOrientation(getComponentOrientation());
        m_principalnotificator.setText(m_appuser.getName());
        m_principalnotificator.setIcon(m_appuser.getIcon());

        //MENU SIDE
        colapseHPanel.add(Box.createVerticalStrut(50), 0);
        m_jPanelMenu.getVerticalScrollBar().setPreferredSize(new Dimension(35, 35));
        rMenu = new JRootMenu(this, this);
        rMenu.setRootMenu(m_jPanelMenu, m_dlSystem);
        setMenuIcon();
        assignMenuButtonIcon();

        //MAIN 
        m_jPanelTitle.setVisible(false);
        addView(new JPanel(), "<NULL>");
        showView("<NULL>");

    }

    private void setMenuIcon() {
        if (colapseButton.getComponentOrientation().isLeftToRight()) {
            menu_open = new ImageIcon(getClass().getResource(
                    "/com/openbravo/images/menu-right.png"));
            menu_close = new ImageIcon(getClass().getResource(
                    "/com/openbravo/images/menu-left.png"));
        } else {
            menu_open = new ImageIcon(getClass().getResource(
                    "/com/openbravo/images/menu-left.png"));
            menu_close = new ImageIcon(getClass().getResource(
                    "/com/openbravo/images/menu-right.png"));
        }
    }

    private void assignMenuButtonIcon() {
        colapseButton.setIcon(m_jPanelMenu.isVisible() ? menu_close : menu_open);
    }

    private void setMenuVisible(boolean value) {

        m_jPanelMenu.setVisible(value);
        assignMenuButtonIcon();
        revalidate();
    }

    public JComponent getNotificator() {
        return m_principalnotificator;
    }

    public void activate() {

        setMenuVisible(getBounds().width > 800);
        rMenu.getViewManager().resetActionfirst();
    }

    public boolean deactivate() {
        if (rMenu.getViewManager().deactivateLastView()) {
            showView("<NULL>");
            return true;
        } else {
            return false;
        }

    }

    @Override
    public void exitToLogin() {
        m_appview.closeAppView();
    }

    private void addView(JComponent component, String sView) {
        m_jPanelContainer.add(component, sView);
    }

    private void showView(String sView) {
        CardLayout cl = (CardLayout) (m_jPanelContainer.getLayout());
        cl.show(m_jPanelContainer, sView);
    }

    @Override
    public AppUser getUser() {
        return m_appuser;
    }

    @Override
    public void showTask(String sTaskClass) {

        LOGGER.info("Show View for class: " + sTaskClass);
        try {
            m_appview.waitCursorBegin();

            if (m_appuser.hasPermission(sTaskClass)) {

                JPanelView viewPanel = rMenu.getViewManager().getCreatedViews().get(sTaskClass);
                if (viewPanel == null) {

                    viewPanel = rMenu.getViewManager().getPreparedViews().get(sTaskClass);

                    if (viewPanel == null) {

                        try {
                            viewPanel = (JPanelView) m_appview.getBean(sTaskClass);
                        } catch (BeanFactoryException e) {
                            LOGGER.log(Level.SEVERE, "Exception on get a JPanelView Bean for class: " + sTaskClass, e);
                            viewPanel = new JPanelNull(m_appview, e);
                        }
                    }

                    rMenu.getViewManager().getCreatedViews().put(sTaskClass, viewPanel);
                }

                if (!rMenu.getViewManager().checkIfLastView(viewPanel)) {

                    if (rMenu.getViewManager().getLastView() != null) {
                        LOGGER.info("Call 'deactivate' on class: " + rMenu.getViewManager().getLastView().getClass().getName());
                        rMenu.getViewManager().getLastView().deactivate();
                    }

                    viewPanel.getComponent().applyComponentOrientation(getComponentOrientation());
                    addView(viewPanel.getComponent(), sTaskClass);

                    LOGGER.info("Call 'activate' on class: " + sTaskClass);
                    viewPanel.activate();

                    rMenu.getViewManager().setLastView(viewPanel);

                    setMenuVisible(getBounds().width > 800);

                    showView(sTaskClass);
                    String sTitle = viewPanel.getTitle();
                    if (sTitle != null && !sTitle.isBlank()) {
                        m_jPanelTitle.setVisible(true);
                        m_jTitle.setText(sTitle);
                    } else {
                        m_jPanelTitle.setVisible(false);
                        m_jTitle.setText("");
                    }
                } else {
                    LOGGER.log(Level.INFO, "Already open: " + sTaskClass + ", Instance: " + viewPanel);
                }
            } else {

                LOGGER.log(Level.INFO, "NO PERMISSION on call class: : " + sTaskClass);
                JMessageDialog.showMessage(this,
                        new MessageInf(MessageInf.SGN_WARNING,
                                AppLocal.getIntString("message.notpermissions")));
            }
            m_appview.waitCursorEnd();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Exception on show class: " + sTaskClass, e);
            JMessageDialog.showMessage(this,
                    new MessageInf(MessageInf.SGN_WARNING,
                            AppLocal.getIntString("message.notactive"), e));
        }
    }

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
                JMessageDialog.showMessage(JPrincipalApp.this,
                        new MessageInf(MessageInf.SGN_WARNING,
                                AppLocal.getIntString("label.LoadError"), e));
            }
        } else {
            JMessageDialog.showMessage(JPrincipalApp.this,
                    new MessageInf(MessageInf.SGN_WARNING,
                            AppLocal.getIntString("message.notpermissions")));
        }
        m_appview.waitCursorEnd();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        m_jPanelLefSide = new javax.swing.JPanel();
        m_jPanelMenu = new javax.swing.JScrollPane();
        colapseHPanel = new javax.swing.JPanel();
        colapseButton = new javax.swing.JButton();
        m_jPanelRightSide = new javax.swing.JPanel();
        m_jPanelTitle = new javax.swing.JPanel();
        m_jTitle = new javax.swing.JLabel();
        m_jPanelContainer = new javax.swing.JPanel();

        setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        setLayout(new java.awt.BorderLayout());

        m_jPanelLefSide.setLayout(new java.awt.BorderLayout());

        m_jPanelMenu.setBackground(new java.awt.Color(102, 102, 102));
        m_jPanelMenu.setBorder(null);
        m_jPanelMenu.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        m_jPanelMenu.setPreferredSize(new java.awt.Dimension(250, 2));
        m_jPanelLefSide.add(m_jPanelMenu, java.awt.BorderLayout.LINE_START);

        colapseHPanel.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        colapseHPanel.setPreferredSize(new java.awt.Dimension(45, 45));

        colapseButton.setToolTipText(AppLocal.getIntString("tooltip.menu")); // NOI18N
        colapseButton.setFocusPainted(false);
        colapseButton.setFocusable(false);
        colapseButton.setIconTextGap(0);
        colapseButton.setMargin(new java.awt.Insets(10, 2, 10, 2));
        colapseButton.setMaximumSize(new java.awt.Dimension(45, 32224661));
        colapseButton.setMinimumSize(new java.awt.Dimension(32, 32));
        colapseButton.setPreferredSize(new java.awt.Dimension(36, 45));
        colapseButton.setRequestFocusEnabled(false);
        colapseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                colapseButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout colapseHPanelLayout = new javax.swing.GroupLayout(colapseHPanel);
        colapseHPanel.setLayout(colapseHPanelLayout);
        colapseHPanelLayout.setHorizontalGroup(
            colapseHPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, colapseHPanelLayout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(colapseButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        colapseHPanelLayout.setVerticalGroup(
            colapseHPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(colapseHPanelLayout.createSequentialGroup()
                .addContainerGap(88, Short.MAX_VALUE)
                .addComponent(colapseButton, javax.swing.GroupLayout.DEFAULT_SIZE, 33, Short.MAX_VALUE)
                .addContainerGap(188, Short.MAX_VALUE))
        );

        m_jPanelLefSide.add(colapseHPanel, java.awt.BorderLayout.LINE_END);

        add(m_jPanelLefSide, java.awt.BorderLayout.LINE_START);

        m_jPanelRightSide.setPreferredSize(new java.awt.Dimension(200, 40));
        m_jPanelRightSide.setLayout(new java.awt.BorderLayout());

        m_jPanelTitle.setLayout(new java.awt.BorderLayout());

        m_jTitle.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        m_jTitle.setForeground(new java.awt.Color(0, 168, 223));
        m_jTitle.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 1, 0, java.awt.Color.darkGray), javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        m_jTitle.setMaximumSize(new java.awt.Dimension(100, 35));
        m_jTitle.setMinimumSize(new java.awt.Dimension(30, 25));
        m_jTitle.setPreferredSize(new java.awt.Dimension(100, 35));
        m_jPanelTitle.add(m_jTitle, java.awt.BorderLayout.NORTH);

        m_jPanelRightSide.add(m_jPanelTitle, java.awt.BorderLayout.NORTH);

        m_jPanelContainer.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        m_jPanelContainer.setLayout(new java.awt.CardLayout());
        m_jPanelRightSide.add(m_jPanelContainer, java.awt.BorderLayout.CENTER);

        add(m_jPanelRightSide, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

private void colapseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_colapseButtonActionPerformed

    setMenuVisible(!m_jPanelMenu.isVisible());

}//GEN-LAST:event_colapseButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton colapseButton;
    private javax.swing.JPanel colapseHPanel;
    private javax.swing.JPanel m_jPanelContainer;
    private javax.swing.JPanel m_jPanelLefSide;
    private javax.swing.JScrollPane m_jPanelMenu;
    private javax.swing.JPanel m_jPanelRightSide;
    private javax.swing.JPanel m_jPanelTitle;
    private javax.swing.JLabel m_jTitle;
    // End of variables declaration//GEN-END:variables

}
