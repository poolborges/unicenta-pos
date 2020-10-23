//    KrOS POS  - Open Source Point Of Sale
//    Copyright (c) 2009-2018 uniCenta & previous Openbravo POS works
//
//    This program is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    (at your option) any later version.
//
//     This program is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with this program.  If not, see <http://www.gnu.org/licenses/>.

package com.openbravo.data.loader;


import com.openbravo.pos.forms.AppConfig;
import java.io.*;

/**
 *
 * @author JG uniCenta
 */
public class CompanyDetails {
    private String db_url;
    private String db_user;       
    private String db_password;
    private File m_config;
    private Session session;

    /**
     *
     */
    public CompanyDetails() {          
   
       AppConfig config = new AppConfig(m_config);
}

    /**
     *
     * @param config
     */
    public void loadProperties(AppConfig config) {
         
        db_url=(config.getProperty("db.url"));
        db_user=(config.getProperty("db_user"));
        db_password=(config.getProperty("db.password"));
}

    /**
     *
     * @return
     */
    public String getUser() {
        return db_user;
    }
}