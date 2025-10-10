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
package com.openbravo.pos.sales.restaurant;

import com.openbravo.data.loader.Session;
import com.openbravo.pos.forms.AppView;
import com.openbravo.pos.forms.DataLogicSystem;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author JDL
 */
public class RestaurantDBUtils {

    private final static Logger LOGGER = Logger.getLogger(RestaurantDBUtils.class.getName());

    private Session dbSession;


    private AppView m_App;

    protected DataLogicSystem dlSystem;

    /**
     *
     * @param oApp
     */
    public RestaurantDBUtils(AppView oApp) {
        m_App = oApp;

        try {
            dbSession = m_App.getSession();
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, null, e);
        }
    }

    /**
     *
     * @param newTable
     * @param ticketID
     */
    public void moveCustomer(String newTable, String ticketID) {
        String oldTable = getTableDetails(ticketID);

        if (countTicketIdInTable(ticketID) > 1) {
            setCustomerNameInTable(getCustomerNameInTable(oldTable), newTable);
            setWaiterNameInTable(getWaiterNameInTable(oldTable), newTable);
            setTicketIdInTable(ticketID, newTable);

            oldTable = getTableMovedName(ticketID);
            boolean hasUpdated = oldTable == null ? newTable != null : !oldTable.equals(newTable);
            if ((oldTable != null) && (hasUpdated)) {
                clearCustomerNameInTable(oldTable);
                clearWaiterNameInTable(oldTable);
                clearTicketIdInTable(oldTable);
                clearTableMovedFlag(oldTable);
            } else {
                oldTable = getTableMovedName(ticketID);
                clearTableMovedFlag(oldTable);
            }
        }
    }

    /**
     *
     * @param custName
     * @param tableName
     */
    public void setCustomerNameInTable(String custName, String tableName) {
        try (Connection con = dbSession.getConnection()) {
            String SQL = "UPDATE places SET CUSTOMER=? WHERE NAME=?";
            PreparedStatement pstmt = con.prepareStatement(SQL);
            pstmt.setString(1, custName);
            pstmt.setString(2, tableName);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "", e);
        }
    }

    /**
     *
     * @param custName
     * @param tableID
     */
    public void setCustomerNameInTableById(String custName, String tableID) {
        try (Connection con = dbSession.getConnection()) {
            String SQL = "UPDATE places SET CUSTOMER=? WHERE ID=?";
            PreparedStatement pstmt = con.prepareStatement(SQL);
            pstmt.setString(1, custName);
            pstmt.setString(2, tableID);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "", e);
        }
    }

    /**
     *
     * @param custName
     * @param ticketID
     */
    public void setCustomerNameInTableByTicketId(String custName, String ticketID) {
        try (Connection con = dbSession.getConnection()) {
            String SQL = "UPDATE places SET CUSTOMER=? WHERE TICKETID=?";
            PreparedStatement pstmt = con.prepareStatement(SQL);
            pstmt.setString(1, custName);
            pstmt.setString(2, ticketID);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "", e);
        }
    }

    /**
     *
     * @param tableName
     * @return
     */
    public String getCustomerNameInTable(String tableName) {
        String customerName = "";
        try (Connection con = dbSession.getConnection()) {
            String SQL = "SELECT customer FROM places WHERE NAME=?";
            try (PreparedStatement pstmt = con.prepareStatement(SQL)) {
                pstmt.setString(1, tableName);

                try (ResultSet resultSet = pstmt.executeQuery()) {
                    if (resultSet.next()) {
                        customerName = resultSet.getString("CUSTOMER");
                    }
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Exception get customer name in table: " + tableName, e);
        }

        return customerName;
    }

    /**
     *
     * @param tableId
     * @return
     */
    public String getCustomerNameInTableById(String tableId) {
        try (Connection con = dbSession.getConnection()) {
            String SQL = "SELECT customer FROM places WHERE ID='" + tableId + "'";
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(SQL);
            if (rs.next()) {
                String customer = rs.getString("CUSTOMER");
                return (customer);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "", e);
        }

        return "";
    }

    /**
     *
     * @param tableName
     */
    public void clearCustomerNameInTable(String tableName) {
        try (Connection con = dbSession.getConnection()) {
            String SQL = "UPDATE places SET CUSTOMER=null WHERE NAME=?";
            PreparedStatement pstmt = con.prepareStatement(SQL);
            pstmt.setString(1, tableName);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "", e);
        }
    }

    /**
     *
     * @param tableID
     */
    public void clearCustomerNameInTableById(String tableID) {
        try (Connection con = dbSession.getConnection()) {
            String SQL = "UPDATE places SET CUSTOMER=null WHERE ID=?";
            PreparedStatement pstmt = con.prepareStatement(SQL);
            pstmt.setString(1, tableID);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "", e);
        }
    }

    /**
     *
     * @param waiterName
     * @param tableName
     */
    public void setWaiterNameInTable(String waiterName, String tableName) {
        try (Connection con = dbSession.getConnection()) {
            String SQL = "UPDATE places SET WAITER=? WHERE NAME=?";
            PreparedStatement pstmt = con.prepareStatement(SQL);
            pstmt.setString(1, waiterName);
            pstmt.setString(2, tableName);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "", e);
        }
    }

    /**
     *
     * @param waiterName
     * @param tableID
     */
    public void setWaiterNameInTableById(String waiterName, String tableID) {
        try (Connection con = dbSession.getConnection()) {
            String SQL = "UPDATE places SET WAITER=? WHERE ID=?";
            PreparedStatement pstmt = con.prepareStatement(SQL);
            pstmt.setString(1, waiterName);
            pstmt.setString(2, tableID);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "", e);
        }
    }

    /**
     *
     * @param tableName
     * @return
     */
    public String getWaiterNameInTable(String tableName) {
        try (Connection con = dbSession.getConnection()) {
            String SQL = "SELECT waiter FROM places WHERE NAME='" + tableName + "'";
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(SQL);

            if (rs.next()) {
                String waiter = rs.getString("WAITER");
                return (waiter);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "", e);
        }

        return "";
    }

    /**
     *
     * @param tableID
     * @return
     */
    public String getWaiterNameInTableById(String tableID) {
        try (Connection con = dbSession.getConnection()) {
            String SQL = "SELECT waiter FROM places WHERE ID='" + tableID + "'";
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(SQL);

            if (rs.next()) {
                String waiter = rs.getString("WAITER");
                return (waiter);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "", e);
        }

        return "";
    }

    /**
     *
     * @param tableName
     */
    public void clearWaiterNameInTable(String tableName) {
        try (Connection con = dbSession.getConnection()) {
            String SQL = "UPDATE places SET WAITER=null WHERE NAME=?";
            PreparedStatement pstmt = con.prepareStatement(SQL);
            pstmt.setString(1, tableName);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "", e);
        }
    }

    /**
     *
     * @param tableID
     */
    public void clearWaiterNameInTableById(String tableID) {
        try (Connection con = dbSession.getConnection()) {
            String SQL = "UPDATE places SET WAITER=null WHERE ID=?";
            PreparedStatement pstmt = con.prepareStatement(SQL);
            pstmt.setString(1, tableID);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "", e);
        }
    }

    /**
     *
     * @param ID
     * @return
     */
    public String getTicketIdInTable(String ID) {
        try (Connection con = dbSession.getConnection()) {
            String SQL = "SELECT TICKETID FROM places WHERE ID='" + ID + "'";
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(SQL);

            if (rs.next()) {
                String customer = rs.getString("TICKETID");
                return (customer);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "", e);
        }

        return "";
    }

    /**
     *
     * @param TicketID
     * @param tableName
     */
    public void setTicketIdInTable(String TicketID, String tableName) {
        try (Connection con = dbSession.getConnection()) {
            String SQL = "UPDATE places SET TICKETID=? WHERE NAME=?";
            PreparedStatement pstmt = con.prepareStatement(SQL);
            pstmt.setString(1, TicketID);
            pstmt.setString(2, tableName);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "", e);
        }
    }

    /**
     *
     * @param tableName
     */
    public void clearTicketIdInTable(String tableName) {
        try (Connection con = dbSession.getConnection()) {
            String SQL = "UPDATE places SET TICKETID=null WHERE NAME=?";
            PreparedStatement pstmt = con.prepareStatement(SQL);
            pstmt.setString(1, tableName);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "", e);
        }
    }

    /**
     *
     * @param tableID
     */
    public void clearTicketIdInTableById(String tableID) {
        try (Connection con = dbSession.getConnection()) {
            String SQL = "UPDATE places SET TICKETID=null WHERE ID=?";
            PreparedStatement pstmt = con.prepareStatement(SQL);
            pstmt.setString(1, tableID);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "", e);
        }
    }

    /**
     *
     * @param ticketID
     * @return
     */
    public Integer countTicketIdInTable(String ticketID) {
        try (Connection con = dbSession.getConnection()) {
            String SQL = "SELECT COUNT(*) AS RECORDCOUNT FROM places WHERE TICKETID='" + ticketID + "'";
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(SQL);

            if (rs.next()) {
                Integer count = rs.getInt("RECORDCOUNT");
                return (count);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "", e);
        }

        return 0;
    }

    /**
     *
     * @param ticketID
     * @return
     */
    public String getTableDetails(String ticketID) {
        try (Connection con = dbSession.getConnection()) {
            String SQL = "SELECT NAME FROM places WHERE TICKETID='" + ticketID + "'";
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(SQL);

            if (rs.next()) {
                String name = rs.getString("NAME");
                return (name);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "", e);
        }

        return "";
    }

    /**
     *
     * @param tableID
     */
    public void setTableMovedFlag(String tableID) {
        try (Connection con = dbSession.getConnection()) {
            String SQL = "UPDATE places SET TABLEMOVED='true' WHERE ID=?";
            PreparedStatement pstmt = con.prepareStatement(SQL);
            pstmt.setString(1, tableID);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "", e);
        }
    }

    /**
     *
     * @param ticketID
     * @return
     */
    public String getTableMovedName(String ticketID) {
        try (Connection con = dbSession.getConnection()) {
            String SQL = "SELECT NAME FROM places WHERE TICKETID='" + ticketID + "' AND TABLEMOVED ='true'";
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(SQL);

            if (rs.next()) {
                String name = rs.getString("NAME");
                return (name);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "", e);
        }

        return null;
    }

    /**
     *
     * @param ticketID
     * @return
     */
    public Boolean getTableMovedFlag(String ticketID) {
        try (Connection con = dbSession.getConnection()) {
            String SQL = "SELECT TABLEMOVED FROM places WHERE TICKETID='" + ticketID + "'";
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(SQL);

            if (rs.next()) {
                return (rs.getBoolean("TABLEMOVED"));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "", e);
        }

        return false;
    }

    /**
     *
     * @param tableID
     */
    public void clearTableMovedFlag(String tableID) {
        try (Connection con = dbSession.getConnection()) {
            String SQL = "UPDATE places SET TABLEMOVED='false' WHERE NAME=?";
            PreparedStatement pstmt = con.prepareStatement(SQL);
            pstmt.setString(1, tableID);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "", e);
        }
    }
}
