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

import com.openbravo.pos.menu.JRootMenu;
import com.openbravo.basic.BasicException;
import com.openbravo.data.gui.JMessageDialog;
import com.openbravo.data.gui.MessageInf;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.util.Set;
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

    private final JRootApp appRootPanel;
    private final AppUser appCurrentUser;
    private final DataLogicSystem dataLogicSystem;
    private final JLabel notificatorLabel;

    private Icon menuOpenIcon;
    private Icon menuCloseIcon;

    private final JRootMenu rootMenu;

    /**
     * Creates a JPanel
     *
     * @param appview
     * @param appuser
     */
    public JPrincipalApp(JRootApp appview, AppUser appuser) {

        appRootPanel = appview;
        appCurrentUser = appuser;

        dataLogicSystem = (DataLogicSystem) appRootPanel.getBean("com.openbravo.pos.forms.DataLogicSystem");
        AppUserPermissionsLoader aupLoader = new AppUserPermissionsLoader(dataLogicSystem);
        Set<String> userPermissions = aupLoader.getPermissionsForRole(appCurrentUser.getRole());
        appCurrentUser.fillPermissions(userPermissions);

        initComponents();
        applyComponentOrientation(appRootPanel.getComponentOrientation());

        notificatorLabel = new JLabel();
        notificatorLabel.applyComponentOrientation(getComponentOrientation());
        notificatorLabel.setText(appCurrentUser.getName());
        notificatorLabel.setIcon(appCurrentUser.getIcon());

        //MENU SIDE
        menuColapsePanel.add(Box.createVerticalStrut(50), 0);
        menuContainerPanel.getVerticalScrollBar().setPreferredSize(new Dimension(35, 35));
        rootMenu = new JRootMenu(this, this);
        rootMenu.setRootMenu(menuContainerPanel, dataLogicSystem);
        setMenuIcon();
        assignMenuButtonIcon();

        //MAIN 
        contentTItlePanel.setVisible(false);
        addView(new JPanel(), "<NULL>");
        showView("<NULL>");

    }

    private void setMenuIcon() {
        if (menuColapseButton.getComponentOrientation().isLeftToRight()) {
            menuOpenIcon = new ImageIcon(getClass().getResource(
                    "/com/openbravo/images/menu-right.png"));
            menuCloseIcon = new ImageIcon(getClass().getResource(
                    "/com/openbravo/images/menu-left.png"));
        } else {
            menuOpenIcon = new ImageIcon(getClass().getResource(
                    "/com/openbravo/images/menu-left.png"));
            menuCloseIcon = new ImageIcon(getClass().getResource(
                    "/com/openbravo/images/menu-right.png"));
        }
    }

    private void assignMenuButtonIcon() {
        menuColapseButton.setIcon(menuContainerPanel.isVisible() ? menuCloseIcon : menuOpenIcon);
    }

    private void setMenuVisible(boolean value) {

        menuContainerPanel.setVisible(value);
        assignMenuButtonIcon();
        revalidate();
    }

    public JComponent getNotificator() {
        return notificatorLabel;
    }

    public void activate() {

        setMenuVisible(getBounds().width > 800);
        rootMenu.getViewManager().resetActionfirst();
    }

    public boolean deactivate() {
        if (rootMenu.getViewManager().deactivateLastView()) {
            showView("<NULL>");
            return true;
        } else {
            return false;
        }

    }

    @Override
    public void exitToLogin() {
        appRootPanel.closeAppView();
    }

    private void addView(JComponent component, String sView) {
        contentContainerPanel.add(component, sView);
    }

    private void showView(String sView) {
        CardLayout cl = (CardLayout) (contentContainerPanel.getLayout());
        cl.show(contentContainerPanel, sView);
    }

    @Override
    public AppUser getUser() {
        return appCurrentUser;
    }

    @Override
    public void showTask(String sTaskClass) {

        LOGGER.info("Show View for class: " + sTaskClass);
        try {
            appRootPanel.waitCursorBegin();

            if (appCurrentUser.hasPermission(sTaskClass)) {

                JPanelView viewPanel = rootMenu.getViewManager().getCreatedViews().get(sTaskClass);
                if (viewPanel == null) {

                    viewPanel = rootMenu.getViewManager().getPreparedViews().get(sTaskClass);

                    if (viewPanel == null) {

                        try {
                            viewPanel = (JPanelView) appRootPanel.getBean(sTaskClass);
                        } catch (BeanFactoryException e) {
                            LOGGER.log(Level.SEVERE, "Exception on get a JPanelView Bean for class: " + sTaskClass, e);
                            viewPanel = new JPanelNull(appRootPanel, e);
                        }
                    }

                    rootMenu.getViewManager().getCreatedViews().put(sTaskClass, viewPanel);
                }

                if (!rootMenu.getViewManager().checkIfLastView(viewPanel)) {

                    if (rootMenu.getViewManager().getLastView() != null) {
                        LOGGER.info("Call 'deactivate' on class: " + rootMenu.getViewManager().getLastView().getClass().getName());
                        rootMenu.getViewManager().getLastView().deactivate();
                    }

                    viewPanel.getComponent().applyComponentOrientation(getComponentOrientation());
                    addView(viewPanel.getComponent(), sTaskClass);

                    LOGGER.info("Call 'activate' on class: " + sTaskClass);
                    viewPanel.activate();

                    rootMenu.getViewManager().setLastView(viewPanel);

                    setMenuVisible(getBounds().width > 800);

                    showView(sTaskClass);
                    String sTitle = viewPanel.getTitle();
                    if (sTitle != null && !sTitle.isBlank()) {
                        contentTItlePanel.setVisible(true);
                        contentTitleLabel.setText(sTitle);
                    } else {
                        contentTItlePanel.setVisible(false);
                        contentTitleLabel.setText("");
                    }
                } else {
                    LOGGER.log(Level.INFO, "Already open: " + sTaskClass + ", Instance: " + viewPanel);
                }
            } else {

                LOGGER.log(Level.INFO, "NO PERMISSION on call class: : " + sTaskClass);
                JMessageDialog.showMessage(this,
                        new MessageInf(MessageInf.SGN_WARNING,
                                AppLocal.getIntString("message.notpermissions"), "<html>"+sTaskClass));
            }
            appRootPanel.waitCursorEnd();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Exception on show class: " + sTaskClass, e);
            JMessageDialog.showMessage(this,
                    new MessageInf(MessageInf.SGN_WARNING,
                            AppLocal.getIntString("message.notactive"), e));
        }
    }

    @Override
    public void executeTask(String sTaskClass) {

        appRootPanel.waitCursorBegin();

        if (appCurrentUser.hasPermission(sTaskClass)) {
            try {
                ProcessAction myProcess = (ProcessAction) appRootPanel.getBean(sTaskClass);

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
        appRootPanel.waitCursorEnd();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        sideMenuPanel = new javax.swing.JPanel();
        menuContainerPanel = new javax.swing.JScrollPane();
        menuColapsePanel = new javax.swing.JPanel();
        menuColapseButton = new javax.swing.JButton();
        contentAreaPanel = new javax.swing.JPanel();
        contentTItlePanel = new javax.swing.JPanel();
        contentTitleLabel = new javax.swing.JLabel();
        contentContainerPanel = new javax.swing.JPanel();

        setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        setLayout(new java.awt.BorderLayout());

        sideMenuPanel.setLayout(new java.awt.BorderLayout());

        menuContainerPanel.setBackground(new java.awt.Color(102, 102, 102));
        menuContainerPanel.setBorder(null);
        menuContainerPanel.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        menuContainerPanel.setPreferredSize(new java.awt.Dimension(250, 2));
        sideMenuPanel.add(menuContainerPanel, java.awt.BorderLayout.LINE_START);

        menuColapsePanel.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        menuColapsePanel.setPreferredSize(new java.awt.Dimension(45, 45));

        menuColapseButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/menu-left.png"))); // NOI18N
        menuColapseButton.setToolTipText(AppLocal.getIntString("tooltip.menu")); // NOI18N
        menuColapseButton.setFocusPainted(false);
        menuColapseButton.setFocusable(false);
        menuColapseButton.setIconTextGap(0);
        menuColapseButton.setMargin(new java.awt.Insets(10, 2, 10, 2));
        menuColapseButton.setMaximumSize(new java.awt.Dimension(45, 32224661));
        menuColapseButton.setMinimumSize(new java.awt.Dimension(32, 32));
        menuColapseButton.setPreferredSize(new java.awt.Dimension(36, 45));
        menuColapseButton.setRequestFocusEnabled(false);
        menuColapseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuColapseButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout menuColapsePanelLayout = new javax.swing.GroupLayout(menuColapsePanel);
        menuColapsePanel.setLayout(menuColapsePanelLayout);
        menuColapsePanelLayout.setHorizontalGroup(
            menuColapsePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, menuColapsePanelLayout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(menuColapseButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        menuColapsePanelLayout.setVerticalGroup(
            menuColapsePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(menuColapsePanelLayout.createSequentialGroup()
                .addContainerGap(88, Short.MAX_VALUE)
                .addComponent(menuColapseButton, javax.swing.GroupLayout.DEFAULT_SIZE, 33, Short.MAX_VALUE)
                .addContainerGap(188, Short.MAX_VALUE))
        );

        sideMenuPanel.add(menuColapsePanel, java.awt.BorderLayout.LINE_END);

        add(sideMenuPanel, java.awt.BorderLayout.LINE_START);

        contentAreaPanel.setPreferredSize(new java.awt.Dimension(200, 40));
        contentAreaPanel.setLayout(new java.awt.BorderLayout());

        contentTItlePanel.setLayout(new java.awt.BorderLayout());

        contentTitleLabel.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        contentTitleLabel.setForeground(new java.awt.Color(0, 168, 223));
        contentTitleLabel.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 1, 0, java.awt.Color.darkGray), javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        contentTitleLabel.setMaximumSize(new java.awt.Dimension(100, 35));
        contentTitleLabel.setMinimumSize(new java.awt.Dimension(30, 25));
        contentTitleLabel.setPreferredSize(new java.awt.Dimension(100, 35));
        contentTItlePanel.add(contentTitleLabel, java.awt.BorderLayout.NORTH);

        contentAreaPanel.add(contentTItlePanel, java.awt.BorderLayout.NORTH);

        contentContainerPanel.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        contentContainerPanel.setLayout(new java.awt.CardLayout());
        contentAreaPanel.add(contentContainerPanel, java.awt.BorderLayout.CENTER);

        add(contentAreaPanel, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

private void menuColapseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuColapseButtonActionPerformed

    setMenuVisible(!menuContainerPanel.isVisible());

}//GEN-LAST:event_menuColapseButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel contentAreaPanel;
    private javax.swing.JPanel contentContainerPanel;
    private javax.swing.JPanel contentTItlePanel;
    private javax.swing.JLabel contentTitleLabel;
    private javax.swing.JButton menuColapseButton;
    private javax.swing.JPanel menuColapsePanel;
    private javax.swing.JScrollPane menuContainerPanel;
    private javax.swing.JPanel sideMenuPanel;
    // End of variables declaration//GEN-END:variables

}
