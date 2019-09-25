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
//    along with uniCenta oPOS.  If not, see <http://www.gnu.org/licenses/>.

package com.openbravo.pos.forms;

import com.openbravo.basic.BasicException;
import com.openbravo.data.loader.Session;
import com.openbravo.pos.util.AltEncrypter;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

/**
 *
 * @author adrianromero
 */
public class AppViewConnection {
    
    /** Creates a new instance of AppViewConnection */
    private AppViewConnection() {
    }
    
    /**
     *
     * @param props
     * @return
     * @throws BasicException
     */
    public static Session createSession(AppProperties props) throws BasicException {

        try {
            String dbURL=null;
            String sDBUser=null;
            String sDBPassword=null;
            String sUserPath = System.getProperty("user.home"); 
            String filePath = sUserPath + "\\open.db";            
            
            if (isJavaWebStart()) {
                Class.forName(props.getProperty("db.driver"), true, Thread.currentThread().getContextClassLoader());
            } else {
                ClassLoader cloader = new URLClassLoader(new URL[] {
                    new File(props.getProperty("db.driverlib")).toURI().toURL()});
                DriverManager.registerDriver(new DriverWrapper((Driver) 
                        Class.forName(props.getProperty("db.driver"), 
                                true, cloader).newInstance()));
            }

            if("true".equals(props.getProperty("db.multi"))) {
                if (!Files.exists(Paths.get(filePath))) {
                    ImageIcon icon = new ImageIcon("/com/openbravo/images/unicentaopos.png");
                    Object[] dbs = {
                    "0 - " + props.getProperty("db.name"),
                    "1 - " + props.getProperty("db1.name")};
        
                    Object s = (Object)JOptionPane.showInputDialog(
                        null, AppLocal.getIntString("message.databasechoose"),
                        "Selection", JOptionPane.OK_OPTION,
                        icon, dbs, props.getProperty("db.name"));
            
                    if (s.toString().startsWith("1")) {
                        sDBUser = props.getProperty("db1.user");
                        sDBPassword = props.getProperty("db1.password");
                        if (sDBUser != null && sDBPassword != null && sDBPassword.startsWith("crypt:")) {
                            AltEncrypter cypher = new AltEncrypter("cypherkey" + sDBUser);
                            sDBPassword = cypher.decrypt(sDBPassword.substring(6));
                        }
                        dbURL = props.getProperty("db1.URL") +
                        props.getProperty("db1.schema") +
                        props.getProperty("db1.options");
                    } else {
                        sDBUser = props.getProperty("db.user");
                        sDBPassword = props.getProperty("db.password");
                        if (sDBUser != null && sDBPassword != null && sDBPassword.startsWith("crypt:")) {
                            AltEncrypter cypher = new AltEncrypter("cypherkey" + sDBUser);
                            sDBPassword = cypher.decrypt(sDBPassword.substring(6));
                        }
                        dbURL = props.getProperty("db.URL") +
                        props.getProperty("db.schema") +
                        props.getProperty("db.options");                        
                    }
                } else {
                    sDBUser = props.getProperty("db.user");
                    sDBPassword = props.getProperty("db.password");
                    if (sDBUser != null && sDBPassword != null && sDBPassword.startsWith("crypt:")) {
                        AltEncrypter cypher = new AltEncrypter("cypherkey" + sDBUser);
                        sDBPassword = cypher.decrypt(sDBPassword.substring(6));
                    }
                    dbURL = props.getProperty("db.URL") +
                    props.getProperty("db.schema") +
                    props.getProperty("db.options");                    
                }    

            } else {
                sDBUser = props.getProperty("db.user");
                sDBPassword = props.getProperty("db.password");
                if (sDBUser != null && sDBPassword != null && sDBPassword.startsWith("crypt:")) {
                    AltEncrypter cypher = new AltEncrypter("cypherkey" + sDBUser);
                    sDBPassword = cypher.decrypt(sDBPassword.substring(6));
                }
                dbURL = props.getProperty("db.URL") +
                props.getProperty("db.schema") +
                props.getProperty("db.options");                
            }

            return new Session(dbURL, sDBUser,sDBPassword);
                
        } catch (InstantiationException | IllegalAccessException | MalformedURLException | ClassNotFoundException e) {
            throw new BasicException(AppLocal.getIntString("message.databasedrivererror"), e);
        } catch (SQLException eSQL) {
            throw new BasicException(AppLocal.getIntString("message.databaseconnectionerror"), eSQL);
        }
    }

    private static boolean isJavaWebStart() {

        try {
            Class.forName("javax.jnlp.ServiceManager");
            return true;
        } catch (ClassNotFoundException ue) {
            return false;
        }
    }
}