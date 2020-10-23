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

package com.openbravo.pos.sales;

import com.openbravo.pos.forms.AppView;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Administrator
 */
public class KitchenDisplay {

    private final static Logger LOGGER = Logger.getLogger(JPanelTicket.class.getName());

    private final AppView m_App;

    /**
     *
     * @param oApp
     */
    public KitchenDisplay(AppView oApp) {
        m_App = oApp;
    }

    /**
     *
     * @param ID
     * @param table
     * @param pickupID
     * @param product
     * @param multiply
     * @param attributes
     */
    public void addRecord(String ID, String table, String pickupID, String product, String multiply, String attributes) {
        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        Date date = new Date();

        try {
            String SQL = "INSERT INTO KITCHENDISPLAY (ID, ORDERTIME, PLACE, PICKUPID, PRODUCT, MULTIPLY, ATTRIBUTES) VALUES (?, ?, ?, ?, ?, ?, ?) ";
            PreparedStatement pstmt = m_App.getSession().getConnection().prepareStatement(SQL);
            pstmt.setString(1, ID);
            pstmt.setString(2, dateFormat.format(date));
            pstmt.setString(3, table);
            pstmt.setString(4, pickupID);
            pstmt.setString(5, product);
            pstmt.setString(6, multiply);
            pstmt.setString(7, attributes);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "", e);
        }
    }

}
