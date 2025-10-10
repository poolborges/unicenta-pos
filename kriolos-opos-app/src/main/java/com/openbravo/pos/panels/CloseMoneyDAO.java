/*
 * Copyright (C) 2025 Paulo Borges
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
package com.openbravo.pos.panels;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import org.openide.util.Exceptions;

/**
 *
 * @author psb
 */
public class CloseMoneyDAO {

    private final Connection connectionDB;

    public CloseMoneyDAO(Connection connectionDB) {
        this.connectionDB = connectionDB;
    }

    public int getNumOfNoSales(Date startDate) {

        int numOfNoSales = 0;

        String SQL = "SELECT * "
                + "FROM draweropened "
                + "WHERE TICKETID = 'No Sale' AND OPENDATE > ?";

        try (PreparedStatement preparedStatement = connectionDB.prepareStatement(SQL)) {
            preparedStatement.setTimestamp(1, new Timestamp(startDate.getTime()));
            try (ResultSet resultSet = preparedStatement.executeQuery()) {

                while (resultSet.next()) {
                    numOfNoSales++;
                }
            }
        }
        catch (SQLException ex) {
            Exceptions.printStackTrace(ex);
        }

        return numOfNoSales;
    }

    public int getNumOfRemovedLines(Date startDate) {

        int numOfLinesRemoved = 0;

        // Get Ticket DELETES & Line Voids            
        String SQL = "SELECT * "
                + "FROM lineremoved "
                + "WHERE REMOVEDDATE > ?";

        try (PreparedStatement preparedStatement = connectionDB.prepareStatement(SQL)) {
            preparedStatement.setTimestamp(1, new Timestamp(startDate.getTime()));
            try (ResultSet resultSet = preparedStatement.executeQuery()) {

                while (resultSet.next()) {
                    numOfLinesRemoved++;
                }
            }
        }
        catch (SQLException ex) {
            Exceptions.printStackTrace(ex);
        }
        
        return numOfLinesRemoved;
    }
    
    public int getNumOfVoidLines(Date startDate) {

        int numOfLinesRemoved = 0;

        // Get Line Voids            
        String SQL = "SELECT * "
                + "FROM lineremoved "
                + "WHERE TICKETID = 'Void' AND REMOVEDDATE >= ?";

        try (PreparedStatement preparedStatement = connectionDB.prepareStatement(SQL)) {
            preparedStatement.setTimestamp(1, new Timestamp(startDate.getTime()));
            try (ResultSet resultSet = preparedStatement.executeQuery()) {

                while (resultSet.next()) {
                    numOfLinesRemoved++;
                }
            }
        }
        catch (SQLException ex) {
            Exceptions.printStackTrace(ex);
        }
        
        return numOfLinesRemoved;
    }
}
