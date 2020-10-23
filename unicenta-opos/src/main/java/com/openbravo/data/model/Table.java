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

package com.openbravo.data.model;

/**
 *
 * @author adrian
 */
public class Table {
    
    private String name;
    private Column[] columns;
    
    /**
     *
     * @param name
     * @param columns
     */
    public Table(String name, Column... columns) {
        this.name = name;
        this.columns = columns;
    }
    
    /**
     *
     * @return
     */
    public String getName() {
        return name;
    }
    
    /**
     *
     * @return
     */
    public Column[] getColumns() {
        return columns;
    }
    
    /**
     *
     * @return
     */
    public String getListSQL() {
        StringBuilder sent = new StringBuilder();
        sent.append("select ");

        for (int i = 0; i < columns.length; i ++) {
            if (i > 0) {
                sent.append(", ");
            }
            sent.append(columns[i].getName());
        }        
        
        sent.append(" from ");        
        sent.append(name);
        
        return sent.toString();          
    }

    /**
     *
     * @return
     */
    public String getInsertSQL() {
        
        StringBuilder sent = new StringBuilder();
        StringBuilder values = new StringBuilder();
        
        sent.append("insert into ");
        sent.append(name);
        sent.append(" (");        
        
        for (int i = 0; i < columns.length; i ++) {
            if (i > 0) {
                sent.append(", ");
                values.append(", ");
            }
            sent.append(columns[i].getName());
            values.append("?");
        }
        
        sent.append(") values (");
        sent.append(values.toString());
        sent.append(")");

        return sent.toString();       
    }

    /**
     *
     * @return
     */
    public String getUpdateSQL() {
        
        StringBuilder values = new StringBuilder();
        StringBuilder filter = new StringBuilder();
        
        for (int i = 0; i < columns.length; i ++) {
            if (columns[i].isPK()) {
                if (filter.length() == 0) {
                    filter.append(" where ");
                } else  {
                    filter.append(" and ");
                }
                filter.append(columns[i].getName());
                filter.append(" = ?");                
            } else {
                if (values.length() > 0) {
                    values.append(", ");
                }
                values.append(columns[i].getName());
                values.append(" = ?");                
            }
        }
        
        return "update " + name + " set " + values + filter;             
    }

    /**
     *
     * @return
     */
    public String getDeleteSQL() {
        
        StringBuilder filter = new StringBuilder();

        for (int i = 0; i < columns.length; i ++) {
            if (columns[i].isPK()) {
                if (filter.length() == 0) {
                    filter.append(" where ");
                } else  {
                    filter.append(" and ");
                }
                filter.append(columns[i].getName());
                filter.append(" = ?"); 
            }
        }
        
        return "delete from " + name + filter;     
    }    
}
