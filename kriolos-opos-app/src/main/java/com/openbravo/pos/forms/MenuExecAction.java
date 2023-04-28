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

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;

/**
 *
 * @author adrianromero
 */
public class MenuExecAction extends AbstractAction {

    private final AppUserView m_App;
    private final String m_sMyView;
    
    /** Creates a new instance of MenuExecAction
     * @param app
     * @param icon
     * @param keytext
     * @param sMyView */
    public MenuExecAction(AppUserView app, String icon, String keytext, String sMyView) {
        putValue(Action.SMALL_ICON, new ImageIcon(MenuExecAction.class.getResource(icon)));
        putValue(Action.NAME, AppLocal.getIntString(keytext));
        putValue(AppUserView.ACTION_TASKNAME, sMyView);
        m_App = app;
        m_sMyView = sMyView;
    }
    @Override
    public void actionPerformed(ActionEvent evt) {
        m_App.executeTask(m_sMyView);            
    }     
}
