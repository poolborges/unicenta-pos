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

package com.openbravo.data.loader;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * Utility class to get SQL string representation of some classes.
 * 
 * @author  adrian, poolborges
 */
public abstract class DataWriteUtils {
    
    private final static DateFormat TSF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS"); 
    
    /**
     * Get Object SQL string represention.
     *   Support instanceof Double, Integer, Boolean, String, Date, byte[]
     * 
     * @param value 
     * @return string representation
     */
    public static String getSQLValue(Object value) {
        if (value == null) {
            return "NULL";
        } else if (value instanceof Double) {
            return getSQLValue((Double) value);
        } else if (value instanceof Integer) {
            return getSQLValue((Integer) value);
        } else if (value instanceof Boolean) {
            return getSQLValue((Boolean) value);
        } else if (value instanceof String) {
            return getSQLValue((String) value);
        } else if (value instanceof Date) {
            return getSQLValue((Date) value);
        }else if (value instanceof byte[]){
            return getSQLValue((byte[])value);
        }else {
            return getSQLValue(value.toString());
        }            
    }
    
    /**
     * Get string represention of Integer. 
     * 
     * @param value
     * @return string representatin of interger, otherwich "NULL" 
     * 
     * @see Integer#toString()
     */
    public static String getSQLValue(Integer value) {
        if (value == null) {
            return "NULL";
        } else {
            return value.toString();
        }
    }
    
    /**
     * Get Double SQL string representation.  
     * 
     * @param value
     * @return string of value or "NULL" if value is null
     * 
     * @see Double#toString()
     */
    public static String getSQLValue(Double value) {
        if (value == null) {
            return "NULL";
        } else {
            return value.toString();
        }
    }
    
    /**
     * Get SQL string boolean representation. 
     * 
     * @param value boolean value
     * @return  "TRUE" or "FALSE" or "NULL"
     * 
     * @see Boolean#toString()
     */
    public static String getSQLValue(Boolean value) {
        if (value == null) {
            return "NULL";
        } else {
            return value ? "TRUE" : "FALSE";
        }
    }
    
    /**
     * Get String SQL represention. 
     * 
     *    eg: Paulo would represented as 'Paulo'
     * 
     * @param value string 
     * @return the string enclosure by 'EXAMPLE OF STRING'
     * 
     * @see this#getEscaped
     */
    public static String getSQLValue(String value) {
        if (value == null) {
            return "NULL";
        } else {
            return '\'' + getEscaped(value) + '\'';
        }
    }
    
    /**
     * Get Date SQL representation. 
     * 
     *   SQL example return {ts 'yyyy-MM-dd HH:mm:ss.SSS'}
     *
     * @param value
     * @return return in formated as {ts 'yyyy-MM-dd HH:mm:ss.SSS'}
     */
    public static String getSQLValue(Date value) {
        if (value == null) {
            return "NULL";
        } else {
            return "{ts '" + TSF.format(value) + "'}";
        }
    }
    
    /**
     * Escape special charater with. 
     * Example special "\", "\n", "\r", ""
     *
     * @param value string
     * @return escaped string
     */
    public static String getEscaped(String value) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < value.length(); i++) {
            switch (value.charAt(i)) {
                case '\\':
                    sb.append("\\\\");
                    break;
                 case '\'':
                    sb.append("\\'");
                    break;
                case '\n':
                    sb.append("\\n");
                    break;
                case '\r':
                    sb.append("\\r");
                    break;
                default: 
                    sb.append(value.charAt(i));
                    break;
            }
        }
        return sb.toString();
    }

    /**
     * Get byte array SQL string representation
     * Use String(byte[], UTF-8)
     * 
     * @param value byte array (byte[])
     * @return "NULL" if null, or converted UTF-F string eclosure with ''
     */
    public static String getSQLValue(byte[] value) {
        String res = "NULL";
        if (value == null) {
            return res;
        } else {
            res = new String(value, StandardCharsets.UTF_8);
            return "'" + res + "'";
        }
    }
 }
