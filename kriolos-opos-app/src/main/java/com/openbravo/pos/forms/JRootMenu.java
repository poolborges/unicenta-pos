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

import com.openbravo.beans.JPasswordDialog;
import com.openbravo.data.gui.JMessageDialog;
import com.openbravo.data.gui.MessageInf;
import com.openbravo.pos.forms.menu.Menu;
import com.openbravo.pos.scripting.ScriptEngine;
import com.openbravo.pos.scripting.ScriptException;
import com.openbravo.pos.scripting.ScriptFactory;
import com.openbravo.pos.util.StringUtils;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JScrollPane;
import org.jdesktop.swingx.JXTaskPane;
import org.jdesktop.swingx.JXTaskPaneContainer;

/**
 *
 * @author poolborges
 */
public class JRootMenu {

    private static final Logger LOGGER = Logger.getLogger("com.openbravo.pos.forms.JRootMenu");

    private final Component parent;
    private final AppUserView appview;
    private final ViewManager viewManager = new ViewManager();

    public JRootMenu(Component _parent, AppUserView _appview) {
        parent = _parent;
        appview = _appview;
    }
    
    public ViewManager getViewManager(){
        return viewManager;
    }

    public void setRootMenu(JScrollPane m_jPanelLeft, DataLogicSystem m_dlSystem) {
        try {

            String menuScrip = m_dlSystem.getResourceAsText("Menu.Root");
            Component menuComponent = getScriptMenu(menuScrip);

            if (menuComponent != null) {
                m_jPanelLeft.setViewportView(menuComponent);
            } else {
                String pah = "/com/openbravo/pos/templates/Menu.Root.bs";
                LOGGER.log(Level.FINE, "Root.Men lookup classpath: " + pah);
                menuScrip = StringUtils.readResource(pah);
                menuComponent = getScriptMenu(menuScrip);
                if (menuComponent != null) {
                    m_jPanelLeft.setViewportView(menuComponent);
                } else {
                    LOGGER.log(Level.SEVERE, "Failed on build Root.Menu from class path: " + pah);
                }
            }

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Exception on setup Root.Menu", e);
        }
    }

    private Component getScriptMenu(String menutext) {

        Component menuComponent = null;

        if (menutext == null || menutext.isBlank()) {
            LOGGER.log(Level.SEVERE, "Script content is blank/emp");
            return menuComponent;
        }

        try {
            ScriptMenu menu = new ScriptMenu(parent, appview);

            ScriptEngine eng = ScriptFactory.getScriptEngine(ScriptFactory.BEANSHELL);
            eng.put("menu", menu);
            eng.eval(menutext);
            menuComponent = menu.getTaskPane();
        } catch (ScriptException ex) {
            LOGGER.log(Level.SEVERE, "Exception on eval Menu.Root ", ex);
            LOGGER.log(Level.WARNING, "Exception on eval Menu.Root ", menutext);
        }

        return menuComponent;
    }

    public class ScriptMenu implements Menu{

        private final JXTaskPaneContainer taskPane;
        private final Component parent;
        private final AppUserView appview;

        public ScriptMenu(Component _parent, AppUserView _appview) {
            parent = _parent;
            appview = _appview;
            taskPane = new JXTaskPaneContainer();
            taskPane.applyComponentOrientation(parent.getComponentOrientation());
        }

        @Override
        public ScriptGroup addGroup(String key) {
            ScriptGroup group = new ScriptGroup(key, parent, appview);
            taskPane.add(group.getTaskGroup());
            return group;
        }
        
        public void setSystemAction() {
            /*
            addAction(new ChangePasswordAction(parent, appview,
                    "/com/openbravo/images/password.png", "Menu.ChangePassword"));
            
            addAction(new ExitAction(parent, appview,
                    "/com/openbravo/images/logout.png", "Menu.Exit"));
            
            
            menudef.addMenuItem(new ChangePasswordAction(parent, m_appview,
                    "/com/openbravo/images/password.png", "Menu.ChangePassword"));
            
            
            menudef.addMenuItem(new ExitAction(parent, m_appview,
                    "/com/openbravo/images/logout.png", "Menu.Exit"));
            */
        }
        

        public JXTaskPaneContainer getTaskPane() {
            return taskPane;
        }
    }

    public class ScriptGroup implements Menu.MenuGroup{

        private final JXTaskPane taskGroup;
        private final AppUserView m_appview;
        private final Component parent;

        private ScriptGroup(String key, Component _parent, AppUserView _appview) {
            m_appview = _appview;
            parent = _parent;
            taskGroup = new JXTaskPane();
            taskGroup.applyComponentOrientation(parent.getComponentOrientation());
            taskGroup.setFocusable(false);
            taskGroup.setRequestFocusEnabled(false);
            taskGroup.setTitle(AppLocal.getIntString(key));
            taskGroup.setVisible(false);
            taskGroup.setFont(new java.awt.Font("Arial", 0, 16));
        }

        @Override
        public void addPanel(String icon, String key, String classname) {
            addAction(new MenuPanelAction(m_appview, icon, key, classname));
        }

        @Override
        public void addExecution(String icon, String key, String classname) {
            addAction(new MenuExecAction(m_appview, icon, key, classname));
        }

        @Override
        public ScriptSubmenu addSubmenu(String icon, String key, String classname) {
            ScriptSubmenu submenu = new ScriptSubmenu(m_appview, key);
            viewManager.getPreparedViews().put(classname, new JPanelMenu(submenu.getMenuDefinition()));
            addAction(new MenuPanelAction(m_appview, icon, key, classname));
            return submenu;
        }

        private void addAction(Action act) {

            AppUser m_appuser = m_appview.getUser();
            if (m_appuser.hasPermission((String) act.getValue(AppUserView.ACTION_TASKNAME))) {
                Component c = taskGroup.add(act);
                c.applyComponentOrientation(parent.getComponentOrientation());
                c.setFocusable(false);

                taskGroup.setVisible(true);
                if (viewManager.getActionfirst() == null) {
                    viewManager.setActionfirst(act);
                }
            }
        }
        
        public JXTaskPane getTaskGroup() {
            return taskGroup;
        }

        @Override
        public void addChangePasswordAction() {
            
        }

        @Override
        public void addExitAction() {
            
        }
    }

    public class ScriptSubmenu implements Menu.Submenu{

        private final MenuDefinition menudef;
        private final AppUserView m_appview;

        private ScriptSubmenu(AppUserView _appview, String key) {
            m_appview = _appview;
            menudef = new MenuDefinition(key);
        }

        @Override
        public void addTitle(String key) {
            menudef.addMenuTitle(key);
        }

        @Override
        public void addPanel(String icon, String key, String classname) {
            menudef.addMenuItem(new MenuPanelAction(m_appview, icon, key, classname));
        }

        @Override
        public void addExecution(String icon, String key, String classname) {
            menudef.addMenuItem(new MenuExecAction(m_appview, icon, key, classname));
        }

        @Override
        public ScriptSubmenu addSubmenu(String icon, String key, String classname) {
            ScriptSubmenu submenu = new ScriptSubmenu(m_appview, key);
            viewManager.getPreparedViews().put(classname, new JPanelMenu(submenu.getMenuDefinition()));
            menudef.addMenuItem(new MenuPanelAction(m_appview, icon, key, classname));
            return submenu;
        }

        public MenuDefinition getMenuDefinition() {
            return menudef;
        }
    }

    private class ChangePasswordAction extends AbstractAction {

        private final Component parent;
        private final AppUserView appview;

        private ChangePasswordAction(Component parentP, AppUserView appviewP, String icon, String keytext) {
            parent = parentP;
            appview = appviewP;
            putValue(Action.SMALL_ICON, new ImageIcon(JRootMenu.class.getResource(icon)));
            putValue(Action.NAME, AppLocal.getIntString(keytext));
            putValue(AppUserView.ACTION_TASKNAME, keytext);

        }

        @Override
        public void actionPerformed(ActionEvent evt) {

            try {
                AppUser m_appuser = appview.getUser();
                String sNewPassword = JPasswordDialog.changePassword(parent, m_appuser.getPassword());
                /*
                if (sNewPassword != null) {
                    DataLogicSystem m_dlSystem = (DataLogicSystem) appview.getBean("com.openbravo.pos.forms.DataLogicSystem");
                    m_dlSystem.execChangePassword(new Object[]{sNewPassword, m_appuser.getId()});
                    m_appuser.setPassword(sNewPassword);
                }
                 */
            } catch (Exception e) {
                JMessageDialog.showMessage(parent,
                        new MessageInf(MessageInf.SGN_WARNING,
                                AppLocal.getIntString("message.cannotchangepassword")));
            }
        }
    }

    private class ExitAction extends AbstractAction {

        private final AppUserView m_appview;

        public ExitAction(Component _parent, AppUserView _appview, String icon, String keytext) {
            m_appview = _appview;
            putValue(Action.SMALL_ICON, new ImageIcon(JRootMenu.class.getResource(icon)));
            putValue(Action.NAME, AppLocal.getIntString(keytext));
            putValue(AppUserView.ACTION_TASKNAME, keytext);
        }

        @Override
        public void actionPerformed(ActionEvent evt) {
            appview.exitToLogin();
        }
    }

    public class ViewManager {

        private JPanelView m_jLastView;
        private Action m_actionfirst;

        private final Map<String, JPanelView> m_aPreparedViews; // Prepared views   
        private final Map<String, JPanelView> m_aCreatedViews;

        public ViewManager() {

            m_aPreparedViews = new HashMap<>();
            m_aCreatedViews = new HashMap<>();
            m_actionfirst = null;
            m_jLastView = null;
        }

        public JPanelView getLastView() {
            return m_jLastView;
        }

        public Action getActionfirst() {
            return m_actionfirst;
        }
        
        public void setActionfirst(Action actionfirst) {
            m_actionfirst = actionfirst;
        }

        public void resetActionfirst() {
            if (m_actionfirst != null) {
                m_actionfirst.actionPerformed(null);
                m_actionfirst = null;
            }
        }

        public boolean deactivateLastView() {
            if (m_jLastView == null) {
                return true;
            } else if (m_jLastView.deactivate()) {
                m_jLastView = null;
                return true;
            } else {
                return false;
            }
        }

        public boolean checkIfLastView(JPanelView m_jMyView) {
            return m_jMyView == m_jLastView;
        }

        public void setLastView(JPanelView m_jMyView) {
            m_jLastView = m_jMyView;
        }

        public Map<String, JPanelView> getPreparedViews() {
            return m_aPreparedViews;
        }

        public Map<String, JPanelView> getCreatedViews() {
            return m_aCreatedViews;
        }
    }
}
