/*
 * Copyright (C) 2022 KiolOS<https://github.com/kriolos>
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

package com.openbravo.pos.util;

/**
*
* @author Xibergy Systems
*/

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 *
 * @author JG uniCenta
 */
public class PropertyUtils {

    private Properties m_propsconfig;
    private File configFile;
    private final String APP_ID = "upos-app";

    /**
     *
     */
    public PropertyUtils() {
        init(getDefaultConfig());
    }

    private void init(File configfile) {
        this.configFile = configfile;
        load();
    }

    private File getDefaultConfig() {
        return new File(new File("C:\\Documents and Settings\\jack"), "unicentaopos.properties");
    }

    private void load() {
        // Load Properties
        try {
            InputStream in = new FileInputStream(configFile);
            if (in != null) {
                m_propsconfig = new Properties();
                m_propsconfig.load(in);
                in.close();
            }
        } catch (IOException e) {
        }
    }

    /**
     *
     * @param sKey
     * @return
     */
    public String getProperty(String sKey) {
        return m_propsconfig.getProperty(sKey);
    }

    /**
     *
     * @return
     */
    public String getDriverName() {
        return m_propsconfig.getProperty("db.driver");
    }

    /**
     *
     * @return
     */
    public String getUrl() {
        return m_propsconfig.getProperty("db.URL");
    }

    /**
     *
     * @return
     */
    public String getDBUser() {
        return m_propsconfig.getProperty("db.user");
    }

    /**
     *
     * @return
     */
    public String getDBPassword() {
        String m_password = "[color=#FF0000]YourDBPassword[/color]";
        return m_password;
    }
}
