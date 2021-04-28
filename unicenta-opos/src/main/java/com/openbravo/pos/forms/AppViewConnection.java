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
package com.openbravo.pos.forms;

import com.openbravo.basic.BasicException;
import com.openbravo.data.loader.Session;
import com.openbravo.pos.util.AltEncrypter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

/**
 *
 * @author adrianromero
 */
public class AppViewConnection {

    private static final int MAX_BD = 10;

    private AppViewConnection() {
    }

    public static Session createSession(AppProperties props) throws BasicException {

        try {
            String dbURL = "";
            String sDBUser = "";
            String sDBPassword = "";
            DBProperties dbProperties = null;

            /* Check Locking
            File filePath = new File(AppConfig.getInstance().getAppDataDirectory(), AppLocal.getLockFileName());
            if (!filePath.exists()) {
            }
             */
            if ("true".equals(props.getProperty("db.multi"))) {
                
                List<String> dbNames = findAllDB(props);

                String chosedDbName = choseDB(props, dbNames.toArray(), "db.name");

                String[] dbNameParts = chosedDbName.split(".");
                String dbID = "";
                //get Default
                if (dbNameParts != null && dbNameParts.length == 2) {
                    String[] dbIdSlit = dbNameParts[0].split("db");
                    if (dbIdSlit != null && dbIdSlit.length == 2) {
                        dbID = dbIdSlit[1];
                    }
                }
                dbProperties = getDBProperties(props, dbID);

            } else {
                dbProperties = getDBProperties(props, "");
            }

            sDBUser = dbProperties.sDBUser;
            sDBPassword = dbProperties.sDBPassword;
            dbURL = dbProperties.dbURL;

            return new Session(dbURL, sDBUser, sDBPassword);

        } catch (SQLException eSQL) {
            throw new BasicException(AppLocal.getIntString("message.databaseconnectionerror"), eSQL);
        }
    }

    private static List<String> findAllDB(AppProperties props) {

        List<String> dbNames = new ArrayList<>();

        dbNames.add("db.name"); // Add defaul Data base

        for (int count = 1; count < 10; count++) {

            String curDbKey = "db" + count + ".name";
            String curName = props.getProperty(curDbKey);

            if (curName != null) {
                dbNames.add(curDbKey);
            }
        }

        return dbNames;
    }

    private static String choseDB(AppProperties props, Object[] dbs, String defaultDb) {

        ImageIcon icon = new ImageIcon("/com/openbravo/images/app_logo_48x48");
        Object chosedDbName = JOptionPane.showInputDialog(
                null,
                AppLocal.getIntString("message.databasechoose"),
                "Selection",
                JOptionPane.OK_OPTION,
                icon,
                dbs,
                defaultDb);

        return (String) chosedDbName;
    }

    private static DBProperties getDBProperties(AppProperties props, String dbID) {

        final DBProperties dbProps = new DBProperties();

        dbProps.sDBUser = props.getProperty("db" + dbID + ".user");
        dbProps.sDBPassword = props.getProperty("db" + dbID + ".password");
        if (dbProps.sDBUser != null && dbProps.sDBPassword != null && dbProps.sDBPassword.startsWith("crypt:")) {
            AltEncrypter cypher = new AltEncrypter("cypherkey" + dbProps.sDBUser);
            dbProps.sDBPassword = cypher.decrypt(dbProps.sDBPassword.substring(6));
        }

        dbProps.dbURL = props.getProperty("db" + dbID + ".URL")
                + props.getProperty("db" + dbID + ".schema")
                + props.getProperty("db" + dbID + ".options");

        return dbProps;
    }

    private static class DBProperties {

        public String dbURL = null;
        public String sDBUser = null;
        public String sDBPassword = null;

        public String toString() {
            return "URL: " + dbURL + ", USER: " + sDBUser + ", PASS: " + sDBPassword;
        }
    }
}
