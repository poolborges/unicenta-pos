//    uniCenta oPOS  - Touch Friendly Point Of Sale
//    Copyright (c) 2009-2018 uniCenta & previous Openbravo POS works
//    https://unicenta.com
//
//    This file is part of uniCenta oPOS
//
//    uniCenta oPOS is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    (at your option) any later version.
//
//   uniCenta oPOS is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with uniCenta oPOS.  If not, see <http://www.gnu.org/licenses/>

package com.openbravo.pos.forms;

import com.openbravo.beans.LocaleResources;

/**
 * @author Jack Gerrard
 * @author adrianromero
 */
public class AppLocal {
    

    public static final String APP_NAME = "uniCenta oPOS";
    public static final String APP_ID = "unicentaopos";
    public static final String APP_VERSION = "4.5";

    private static final LocaleResources m_resources;
    
    static {
        m_resources = new LocaleResources();
        m_resources.addBundleName("pos_messages");
        m_resources.addBundleName("erp_messages");
    }
    
    /** Creates a new instance of AppLocal */
    private AppLocal() {
    }
    
    /**
     *
     * @param sKey local values
     * @return string values
     */
    public static String getIntString(String sKey) {
        return m_resources.getString(sKey);
    }
    
    /**
     *
     * @param sKey local values
     * @param sValues string values
     * @return string values
     */
    public static String getIntString(String sKey, Object ... sValues) {
        return m_resources.getString(sKey, sValues);
    }
}
