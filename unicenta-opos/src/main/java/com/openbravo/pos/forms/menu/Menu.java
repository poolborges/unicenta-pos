/*
 * Copyright (C) 2022 Paulo Borges
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.openbravo.pos.forms.menu;


/**
 *
 * @author poolb
 */
public interface Menu {
    public MenuGroup addGroup(String key);

    public interface MenuGroup {
        public void addPanel(String icon, String key, String classname);
        public void addExecution(String icon, String key, String classname);
        public Submenu addSubmenu(String icon, String key, String classname);
        public void addChangePasswordAction();
        public void addExitAction();
    }

    public interface Submenu {
        public void addTitle(String key);
        public void addPanel(String icon, String key, String classname);
        public void addExecution(String icon, String key, String classname);
        public Submenu addSubmenu(String icon, String key, String classname);
    }
}
