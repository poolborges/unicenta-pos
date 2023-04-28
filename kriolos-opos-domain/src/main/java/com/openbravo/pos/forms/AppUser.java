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

import com.openbravo.pos.forms.DataLogicSystem;
import com.openbravo.pos.ticket.UserInfo;
import com.openbravo.pos.util.Hashcypher;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Icon;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 *
 * @author adrianromero
 */
public class AppUser {

    private static final Logger logger = Logger.getLogger("com.openbravo.pos.forms.AppUser");

    private static SAXParser m_sp = null;
    private final String m_sId;
    private final String m_sName;
    private final String m_sCard;
    private String m_sPassword;
    private final String m_sRole;
    private final Icon m_Icon;

    private final Set<String> m_apermissions = new HashSet<>();


    /**
     * Creates a new instance of AppUser
     *
     * @param id
     * @param name
     * @param card
     * @param password
     * @param icon
     * @param role
     */
    public AppUser(String id, String name, String password, String card, String role, Icon icon) {
        m_sId = id;
        m_sName = name;
        m_sPassword = password;
        m_sCard = card;
        m_sRole = role;
        m_Icon = icon;
    }

    /**
     * Gets the User's button icon
     */
    public Icon getIcon() {
        return m_Icon;
    }

    /**
     * Get the user's ID
     */
    public String getId() {
        return m_sId;
    }

    /**
     * Get the User's Name
     */
    public String getName() {
        return m_sName;
    }

    /**
     * Set the User's Password
     */
    public void setPassword(String sValue) {
        m_sPassword = sValue;
    }

    /**
     * Get the User's Password
     */
    public String getPassword() {
        return m_sPassword;
    }

    /**
     * Get the User's Role
     */
    public String getRole() {
        return m_sRole;
    }

    /**
     * Get the User's Card
     */
    public String getCard() {
        return m_sCard;
    }

    /**
     * Validate User's Password
     *
     * @return
     */
    public boolean authenticate() {
        return m_sPassword == null || m_sPassword.equals("") || m_sPassword.startsWith("empty:");
    }

    /**
     * Eval User's Password
     *
     * @param sPwd
     * @return
     */
    public boolean authenticate(String sPwd) {
        return Hashcypher.authenticate(sPwd, m_sPassword);
    }

    /**
     * Load User's Permissions
     *
     * @param dlSystem
     */
    public void fillPermissions(DataLogicSystem dlSystem) {

        // Permissions for all users
        m_apermissions.add("com.openbravo.pos.forms.JPanelMenu");
        m_apermissions.add("Menu.Exit");

        String sRolePermisions = dlSystem.findRolePermissions(m_sRole);

        if (sRolePermisions != null) {
            try {
                if (m_sp == null) {
                    SAXParserFactory spf = SAXParserFactory.newInstance();
                    m_sp = spf.newSAXParser();
                }
                m_sp.parse(new InputSource(new StringReader(sRolePermisions)), new ConfigurationHandler());

            } catch (ParserConfigurationException ePC) {
                logger.log(Level.WARNING, "exception.parserconfig", ePC);
            } catch (SAXException eSAX) {
                logger.log(Level.WARNING, "exception.xmlfile", eSAX);
            } catch (IOException eIO) {
                logger.log(Level.WARNING, "exception.iofile", eIO);
            }
        }

    }

    /**
     * Validate User's Permissions
     *
     * @param classname
     * @return
     */
    public boolean hasPermission(String classname) {

        return (m_apermissions == null) ? false : m_apermissions.contains(classname);
    }

    /**
     * Get User's ID/Name
     *
     * @return
     */
    public UserInfo getUserInfo() {
        return new UserInfo(m_sId, m_sName);
    }

    private class ConfigurationHandler extends DefaultHandler {

        @Override
        public void startDocument() throws SAXException {
        }

        @Override
        public void endDocument() throws SAXException {
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            if ("class".equals(qName)) {
                m_apermissions.add(attributes.getValue("name"));
            }
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
        }
    }

}
