/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.proteanit.sql;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.Vector;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

/**
 *
 * @author JG uniCenta
 */
public class DbUtils {

    /**
     *
     * @param rs
     * @return
     */
    public static TableModel resultSetToTableModel(ResultSet rs) {
         try {
             ResultSetMetaData metaData = rs.getMetaData();
             int numberOfColumns = metaData.getColumnCount();
             Vector columnNames = new Vector();
 
            // Get the column names
             for (int column = 0; column < numberOfColumns; column++) {
                 columnNames.addElement(metaData.getColumnLabel(column + 1));
             }

             // Get all rows.
             Vector rows = new Vector();
 
            while (rs.next()) {
                 Vector newRow = new Vector();
 
                for (int i = 1; i <= numberOfColumns; i++) {
                     newRow.addElement(rs.getObject(i));
                 }

                 rows.addElement(newRow);
             }

             return new DefaultTableModel(rows, columnNames);
         } catch (Exception e) {
             e.printStackTrace();

             return null;
         }
     }
 }

