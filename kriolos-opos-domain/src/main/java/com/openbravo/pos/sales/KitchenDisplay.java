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
package com.openbravo.pos.sales;

import com.openbravo.data.loader.DataWriteUtils;
import com.openbravo.pos.forms.AppView;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;


public class KitchenDisplay {

    private final static Logger LOGGER = Logger.getLogger(KitchenDisplay.class.getName());

    private final AppView m_App;

    /**
     *
     * @param oApp
     */
    public KitchenDisplay(AppView oApp) {
        m_App = oApp;
    }

    /**
     * Inset data into KITCHENDISPLAY 
     * This data will be use by Kitchen Display System (KDS)
     * 
     * @param id
     * @param table
     * @param pickupID
     * @param product
     * @param multiply
     * @param attributes
     */
    public void addRecord(String id, String table, String pickupID, String product, String multiply, String attributes) {
       
        String SQL = "INSERT INTO KITCHENDISPLAY (ID, ORDERTIME, PLACE, PICKUPID, PRODUCT, MULTIPLY, ATTRIBUTES) VALUES (?, ?, ?, ?, ?, ?, ?) ";
        
        /*
        Parameter Breakdown
            ID: Unique key for the kitchen display row.
            ORDERTIME: sql DATETIME when the order was placed.
            PLACE: Table, room, or location identifier.
            PICKUPID: Order or receipt sequence number for pickup.
            PRODUCT: Name or ID of the menu item.
            MULTIPLY: Quantity of the product ordered.
            ATTRIBUTES: Extra modifiers or notes for the item.
        */

        try {
            Connection conDB = m_App.getSession().getConnection();
            LocalDateTime now = LocalDateTime.now();

            try (PreparedStatement pstmt = conDB.prepareStatement(SQL)) {
                //Diable auto-commit because manual end the end
                pstmt.getConnection().setAutoCommit(false);
                pstmt.setString(1, id);
                pstmt.setObject(2, now);
                pstmt.setString(3, table);
                pstmt.setString(4, pickupID);
                pstmt.setString(5, product);
                pstmt.setString(6, multiply);
                pstmt.setString(7, attributes);
                pstmt.executeUpdate();
                pstmt.getConnection().commit();
            }
            catch (SQLException ex) {
                try {
                    conDB.rollback();
                }
                catch (SQLException exRoolB) {
                    LOGGER.log(Level.WARNING, "Exception roolback KDS  with ID: " + id, exRoolB);
                }
                LOGGER.log(Level.SEVERE, "Exception insert data into KDS table with ID: " + id, ex);
            }
        }
        catch (SQLException ex) {
             LOGGER.log(Level.SEVERE, "Exception get DB Connection, to Insert into  KDS table with ID: " + id, ex);
        }

    }

}
