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

package com.openbravo.pos.menu;

import com.openbravo.pos.forms.AppLocal;
import com.openbravo.pos.forms.AppUserView;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;

/**
 *
 * @author adrianromero
 */
public class MenuExecAction extends AbstractAction {

    private final AppUserView appUserView;
    private final String m_sMyView;

    public MenuExecAction(AppUserView app, String icon, String keytext, String sMyView) {
        this.putValue(Action.SMALL_ICON, new ImageIcon(MenuExecAction.class.getResource(icon)));
        this.putValue(Action.NAME, AppLocal.getIntString(keytext));
        this.putValue(AppUserView.ACTION_TASKNAME, sMyView);
        this.appUserView = app;
        this.m_sMyView = sMyView;
    }
    @Override
    public void actionPerformed(ActionEvent evt) {
        this.appUserView.executeTask(m_sMyView);            
    }     
}
