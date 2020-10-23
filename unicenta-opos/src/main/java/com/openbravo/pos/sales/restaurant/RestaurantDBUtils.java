/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
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
    
    private Session s;
    private Connection con;  
    private Statement stmt;
    private PreparedStatement pstmt;
    private String SQL;
    private ResultSet rs;
    private AppView m_App;

    protected DataLogicSystem dlSystem;

    /**
     *
     * @param oApp
     */
    public RestaurantDBUtils(AppView oApp) {
        m_App=oApp;
        
        try{
            s=m_App.getSession();
            con=s.getConnection();                      
        }
//        catch (Exception e){System.out.print("No session or connection");
        catch (Exception e){ }
    }

    /**
     *
     * @param newTable
     * @param ticketID
     */
    public void moveCustomer(String newTable, String ticketID){
      String oldTable=getTableDetails(ticketID); 

        if (countTicketIdInTable(ticketID)>1){
            setCustomerNameInTable(getCustomerNameInTable(oldTable),newTable);
            setWaiterNameInTable(getWaiterNameInTable(oldTable),newTable);  
            setTicketIdInTable(ticketID,newTable);

            oldTable = getTableMovedName(ticketID);  
            if ((oldTable != null) && (oldTable != newTable)){
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
    public void setCustomerNameInTable(String custName, String tableName){
        try{
            SQL = "UPDATE places SET CUSTOMER=? WHERE NAME=?";
            pstmt=con.prepareStatement(SQL);
            pstmt.setString(1,custName); 
            pstmt.setString(2,tableName);    
            pstmt.executeUpdate();
        }catch(SQLException e){
            LOGGER.log(Level.SEVERE, "", e);
        }
    }

    /**
     *
     * @param custName
     * @param tableID
     */
    public void setCustomerNameInTableById(String custName, String tableID){
        try{
            SQL = "UPDATE places SET CUSTOMER=? WHERE ID=?";
            pstmt=con.prepareStatement(SQL);
            pstmt.setString(1,custName); 
            pstmt.setString(2,tableID);    
            pstmt.executeUpdate();
        }catch(SQLException e){
            LOGGER.log(Level.SEVERE, "", e);
        }
    }

    /**
     *
     * @param custName
     * @param ticketID
     */
    public void setCustomerNameInTableByTicketId(String custName, String ticketID){
        try{
            SQL = "UPDATE places SET CUSTOMER=? WHERE TICKETID=?";
            pstmt=con.prepareStatement(SQL);
            pstmt.setString(1,custName); 
            pstmt.setString(2,ticketID);    
            pstmt.executeUpdate();
        }catch(SQLException e){
            LOGGER.log(Level.SEVERE, "", e);
        }
}

    /**
     *
     * @param tableName
     * @return
     */
    public String getCustomerNameInTable(String tableName){
        try{
            SQL = "SELECT customer FROM places WHERE NAME='"+ tableName + "'";   
            stmt = (Statement) con.createStatement();  
            rs = stmt.executeQuery(SQL);

            if (rs.next()){
                String customer =rs.getString("CUSTOMER");
                return(customer); 
            }    
        }catch(SQLException e){
            LOGGER.log(Level.SEVERE, "", e);
        }   
        
        return "";
  }

    /**
     *
     * @param tableId
     * @return
     */
    public String getCustomerNameInTableById(String tableId){
        try{
            SQL = "SELECT customer FROM places WHERE ID='"+ tableId + "'";   
            stmt = (Statement) con.createStatement();  
            rs = stmt.executeQuery(SQL);
            if (rs.next()){
                String customer =rs.getString("CUSTOMER");
                return(customer); }    
        }catch(SQLException e){
            LOGGER.log(Level.SEVERE, "", e);
        }   

        return "";
  }

    /**
     *
     * @param tableName
     */
    public void clearCustomerNameInTable(String tableName){
        try{
            SQL = "UPDATE places SET CUSTOMER=null WHERE NAME=?";
            pstmt=con.prepareStatement(SQL);
            pstmt.setString(1,tableName);     
            pstmt.executeUpdate();
        }catch(SQLException e){
            LOGGER.log(Level.SEVERE, "", e);
        }
    }

    /**
     *
     * @param tableID
     */
    public void clearCustomerNameInTableById(String tableID){
        try{
            SQL = "UPDATE places SET CUSTOMER=null WHERE ID=?";
            pstmt=con.prepareStatement(SQL);
            pstmt.setString(1,tableID);     
            pstmt.executeUpdate();
        }catch(SQLException e){
            LOGGER.log(Level.SEVERE, "", e);
        }
    }

    /**
     *
     * @param waiterName
     * @param tableName
     */
    public void setWaiterNameInTable(String waiterName, String tableName){
        try{
            SQL = "UPDATE places SET WAITER=? WHERE NAME=?";
            pstmt=con.prepareStatement(SQL);
            pstmt.setString(1,waiterName); 
            pstmt.setString(2,tableName);    
            pstmt.executeUpdate();
        }catch(SQLException e){
            LOGGER.log(Level.SEVERE, "", e);
        }
    }
  
    /**
     *
     * @param waiterName
     * @param tableID
     */
    public void setWaiterNameInTableById(String waiterName, String tableID){
        try{
            SQL = "UPDATE places SET WAITER=? WHERE ID=?";
            pstmt=con.prepareStatement(SQL);
            pstmt.setString(1,waiterName); 
            pstmt.setString(2,tableID);    
            pstmt.executeUpdate();
        }catch(SQLException e){
            LOGGER.log(Level.SEVERE, "", e);
        }
    }

    /**
     *
     * @param tableName
     * @return
     */
    public String getWaiterNameInTable(String tableName){
        try{
            SQL = "SELECT waiter FROM places WHERE NAME='"+ tableName + "'";   
            stmt = (Statement) con.createStatement();  
            rs = stmt.executeQuery(SQL);

            if (rs.next()){
                String waiter =rs.getString("WAITER");
                return(waiter);
            }    
        }catch(SQLException e){
            LOGGER.log(Level.SEVERE, "", e);
        }   

       return "";
    }

    /**
     *
     * @param tableID
     * @return
     */
    public String getWaiterNameInTableById(String tableID){
        try{
            SQL = "SELECT waiter FROM places WHERE ID='"+ tableID + "'";   
            stmt = (Statement) con.createStatement();  
            rs = stmt.executeQuery(SQL);

            if (rs.next()){
                String waiter =rs.getString("WAITER");
                return(waiter);
            }    
        }catch(SQLException e){
            LOGGER.log(Level.SEVERE, "", e);
        }   

        return "";
    }

    /**
     *
     * @param tableName
     */
    public void clearWaiterNameInTable(String tableName){
        try{
            SQL = "UPDATE places SET WAITER=null WHERE NAME=?";
            pstmt=con.prepareStatement(SQL);
            pstmt.setString(1,tableName);     
            pstmt.executeUpdate();
        }catch(SQLException e){
            LOGGER.log(Level.SEVERE, "", e);
        }
    }

    /**
     *
     * @param tableID
     */
    public void clearWaiterNameInTableById(String tableID){
        try{
            SQL = "UPDATE places SET WAITER=null WHERE ID=?";
            pstmt=con.prepareStatement(SQL);
            pstmt.setString(1,tableID);     
            pstmt.executeUpdate();
        }catch(SQLException e){
            LOGGER.log(Level.SEVERE, "", e);
        }
    }

    /**
     *
     * @param ID
     * @return
     */
    public String getTicketIdInTable(String ID){
        try{
            SQL = "SELECT TICKETID FROM places WHERE ID='"+ ID + "'";   
            stmt = (Statement) con.createStatement();  
            rs = stmt.executeQuery(SQL);
        
            if (rs.next()){
                String customer =rs.getString("TICKETID");
                return(customer);
            }    
        }catch(SQLException e){
            LOGGER.log(Level.SEVERE, "", e);
        }   

        return "";
    }  

    /**
     *
     * @param TicketID
     * @param tableName
     */
    public void setTicketIdInTable(String TicketID, String tableName){
        try{
            SQL = "UPDATE places SET TICKETID=? WHERE NAME=?";
            pstmt=con.prepareStatement(SQL);
            pstmt.setString(1,TicketID); 
            pstmt.setString(2,tableName);    
            pstmt.executeUpdate();
        }catch(SQLException e){
            LOGGER.log(Level.SEVERE, "", e);
        }
    }

    /**
     *
     * @param tableName
     */
    public void clearTicketIdInTable(String tableName){
        try{
            SQL = "UPDATE places SET TICKETID=null WHERE NAME=?";
            pstmt=con.prepareStatement(SQL);
            pstmt.setString(1,tableName);     
            pstmt.executeUpdate();
        }catch(SQLException e){
            LOGGER.log(Level.SEVERE, "", e);
        }
    }

    /**
     *
     * @param tableID
     */
    public void clearTicketIdInTableById(String tableID){
        try{
            SQL = "UPDATE places SET TICKETID=null WHERE ID=?";
            pstmt=con.prepareStatement(SQL);
            pstmt.setString(1,tableID);     
            pstmt.executeUpdate();
        }catch(SQLException e){
            LOGGER.log(Level.SEVERE, "", e);
        }
    }

    /**
     *
     * @param ticketID
     * @return
     */
    public Integer countTicketIdInTable(String ticketID){
        try{
            SQL = "SELECT COUNT(*) AS RECORDCOUNT FROM places WHERE TICKETID='"+ ticketID + "'";   
            stmt = (Statement) con.createStatement();  
            rs = stmt.executeQuery(SQL);
    
            if (rs.next()){
                Integer count =rs.getInt("RECORDCOUNT");
                return(count);
            }    
        }catch(SQLException e){
            LOGGER.log(Level.SEVERE, "", e);
        }
    
        return 0;
    }

    /**
     *
     * @param ticketID
     * @return
     */
    public String getTableDetails (String ticketID){
        try{
            SQL = "SELECT NAME FROM places WHERE TICKETID='"+ ticketID + "'";   
            stmt = (Statement) con.createStatement();  
            rs = stmt.executeQuery(SQL);

            if (rs.next()){
                String name =rs.getString("NAME");
                return(name);
            }    
        }catch(SQLException e){
            LOGGER.log(Level.SEVERE, "", e);
        }
    
        return "";
    }

    /**
     *
     * @param tableID
     */
    public void setTableMovedFlag (String tableID){
        try{
            SQL = "UPDATE places SET TABLEMOVED='true' WHERE ID=?";
            pstmt=con.prepareStatement(SQL);
            pstmt.setString(1,tableID);     
            pstmt.executeUpdate();
        }catch(SQLException e){
            LOGGER.log(Level.SEVERE, "", e);
        }
    }

    /**
     *
     * @param ticketID
     * @return
     */
    public String getTableMovedName (String ticketID){
        try{
            SQL = "SELECT NAME FROM places WHERE TICKETID='"+ ticketID + "' AND TABLEMOVED ='true'";   
            stmt = (Statement) con.createStatement();  
            rs = stmt.executeQuery(SQL);

            if (rs.next()){
                String name =rs.getString("NAME");
                return(name);
            }    
        }catch(SQLException e){
            LOGGER.log(Level.SEVERE, "", e);
        }
        
        return null;
    }  

    /**
     *
     * @param ticketID
     * @return
     */
    public Boolean getTableMovedFlag (String ticketID){
        try{
            SQL = "SELECT TABLEMOVED FROM places WHERE TICKETID='"+ ticketID + "'";   
            stmt = (Statement) con.createStatement();  
            rs = stmt.executeQuery(SQL);
            
            if (rs.next()){
                return(rs.getBoolean("TABLEMOVED"));
            }    
        }catch(SQLException e){
            LOGGER.log(Level.SEVERE, "", e);
        }
        
        return false;
    }

    /**
     *
     * @param tableID
     */
    public void clearTableMovedFlag (String tableID){
        try{
            SQL = "UPDATE places SET TABLEMOVED='false' WHERE NAME=?";
            pstmt=con.prepareStatement(SQL);
            pstmt.setString(1,tableID);     
            pstmt.executeUpdate();
        }catch(SQLException e){
            LOGGER.log(Level.SEVERE, "", e);
        }
    }  
}