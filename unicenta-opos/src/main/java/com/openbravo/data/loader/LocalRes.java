//    KrOS POS  - Open Source Point Of Sale
//    Copyright (c) 2009-2018 uniCenta & previous Openbravo POS works
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

package com.openbravo.data.loader;

import java.util.MissingResourceException;
import com.openbravo.beans.LocaleResources;

/**
 *
 * @author JG uniCenta
 */
public class LocalRes {
    
    // private static ResourceBundle m_Intl;
    private static LocaleResources m_resources;
    
    static {
        m_resources = new LocaleResources();
        m_resources.addBundleName("data_messages");
    }
    
    /** Creates a new instance of LocalRes */
    private LocalRes() {
    }
       
    /**
     *
     * @param sKey
     * @return
     */
    public static String getIntString(String sKey) {      
        return m_resources.getString(sKey);
    }         
}
