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

import com.openbravo.pos.ticket.UserInfo;
import com.openbravo.pos.util.Hashcypher;
import java.util.HashSet;
import java.util.Set;
import javax.swing.Icon;

/**
 *
 * @author adrianromero
 * @author poolborges (update: 2025-10-01)
 */
public class AppUser {
    
    private final String id;
    private final String username;
    private final String card;
    private String password;
    private final String role;
    private final Icon icon;

    private final Set<String> permissions = new HashSet<>();

    public AppUser(String id, String name, String password, String card, String role, Icon icon) {
        this.id = id;
        this.username = name;
        this.password = password;
        this.card = card;
        this.role = role;
        this.icon = icon;
        
        
        // DEFAULT Permissions for all users
        permissions.add("com.openbravo.pos.forms.JPanelMenu");
        permissions.add("Menu.Exit");
    }

    public Icon getIcon() {
        return icon;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return username;
    }

    public void setPassword(String sValue) {
        password = sValue;
    }

    public String getPassword() {
        return password;
    }

    public String getRole() {
        return role;
    }

    public String getCard() {
        return card;
    }

    public boolean authenticate() {
        return password == null || password.equals("") || password.startsWith("empty:");
    }

    public boolean authenticate(String sPwd) {
        return Hashcypher.authenticate(sPwd, password);
    }

    /**
     * Load User's Permissions
     *
     * @param permissions
     */
    public void fillPermissions(Set<String> permissions) {

        if (permissions != null) {
            this.permissions.addAll(permissions);
        }
    }

    /**
     * Validate User's Permissions
     *
     * @param classname
     * @return
     */
    public boolean hasPermission(String classname) {
        return permissions.contains(classname);
    }

    public UserInfo getUserInfo() {
        return new UserInfo(id, username);
    }
}
