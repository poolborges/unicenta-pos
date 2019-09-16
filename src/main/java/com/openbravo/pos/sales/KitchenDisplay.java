/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.openbravo.pos.sales;

import com.openbravo.data.loader.Session;
import com.openbravo.pos.forms.AppView;
import com.openbravo.pos.forms.DataLogicSystem;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author Administrator
 */
public class KitchenDisplay {
    private Session s;
    private Connection con;  
    private Statement stmt;
    private PreparedStatement pstmt;
    private String SQL;
    private ResultSet rs;
    private AppView m_App;

    /**
     *
     */
    protected DataLogicSystem dlSystem;

    /**
     *
     * @param oApp
     */
    public KitchenDisplay(AppView oApp) {
        m_App=oApp;
                                    
//get database connection details        
       try{
            s=m_App.getSession();
            con=s.getConnection();                      
        }
//        catch (Exception e){System.out.print("No session or connection");
        catch (Exception e){ }   
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
    public void addRecord(String ID, String table, String pickupID, String product, String multiply, String attributes){
        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        Date date = new Date();
     
     try{
            SQL = "INSERT INTO KITCHENDISPLAY (ID, ORDERTIME, PLACE, PICKUPID, PRODUCT, MULTIPLY, ATTRIBUTES) VALUES (?, ?, ?, ?, ?, ?, ?) "; 
            pstmt=con.prepareStatement(SQL);
            pstmt.setString(1,ID);            
            pstmt.setString(2,dateFormat.format(date).toString()); 
            pstmt.setString(3,table);
            pstmt.setString(4,pickupID);  
            pstmt.setString(5,product);  
            pstmt.setString(6,multiply);
            pstmt.setString(7,attributes);            
            pstmt.executeUpdate();
        }catch(Exception e){
            }
     
 }       

 
//              stmt = (Statement) con.createStatement();  
//            rs = stmt.executeQuery(SQL); 
 
        
}
