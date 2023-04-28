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

import com.openbravo.pos.forms.AppUser;

/**
 *
 * @author adrianromero
 */
public interface AppUserView {

    /**
     *
     */
    public static final String ACTION_TASKNAME = "taskname";

    /**
     *  Current User
     * 
     * @return AppUser
     */
    public AppUser getUser();

    /**
     * Show Panel
     * 
     * @param sTaskClass
     */
    public void showTask(String sTaskClass);

    /**
     *
     * @param sTaskClass
     */
    public void executeTask(String sTaskClass);
    
    public void exitToLogin();
}
