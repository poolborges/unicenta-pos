//    uniCenta oPOS  - Touch Friendly Point Of Sale
//    Copyright (c) 2009-2016 uniCenta
//    https://unicenta.com
//
//    This file is part of uniCenta oPOS
//
//    uniCenta oPOS is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    (at your option) any later version.
//
//   uniCenta oPOS is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with uniCenta oPOS.  If not, see <http://www.gnu.org/licenses/>.

package com.openbravo.pos.util;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Random;

/**
 *
 * @author JG uniCenta
 */
public class StringUtils {
    
    private static final char [] hexchars = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
     
    private static final NumberFormat cardformat = new DecimalFormat("000000");
    private static final Random cardrandom = new Random();
    
    /** Creates a new instance of StringUtils */
    private StringUtils() {
    }
    
    /**
     *
     * @return
     */
    public static String getCardNumber() {
    return cardformat.format(Math.abs(System.currentTimeMillis()) % 1000000L)
         + cardformat.format(Math.abs(cardrandom.nextLong()) % 1000000L);
    }
    
    /**
     *
     * @param sValue
     * @return
     */
    public static String encodeXML(String sValue) {
        
        if (sValue == null) {
            return null;
        } else {
            StringBuilder buffer = new StringBuilder();      
            for (int i = 0; i < sValue.length(); i++) {
                char charToCompare = sValue.charAt(i);
                if (charToCompare == '&') {
                    buffer.append("&amp;");
                } else if (charToCompare == '<') {
                    buffer.append("&lt;");
                } else if (charToCompare == '>') {
                    buffer.append("&gt;");
                } else if (charToCompare == '\"') {
                    buffer.append("&quot;");
                } else if (charToCompare == '\'') {
                    buffer.append("&apos;");
                } else {
                    buffer.append(charToCompare);
                }
            }
            return buffer.toString();
        }
    }

    /**
     *
     * @param binput
     * @return
     */
    public static String byte2hex(byte[] binput) {
        
        StringBuilder sb = new StringBuilder(binput.length * 2);
        for (int i = 0; i < binput.length; i++) {
            int high = ((binput[i] & 0xF0) >> 4);
            int low = (binput[i] & 0x0F);
            sb.append(hexchars[high]);
            sb.append(hexchars[low]);
        }
        return sb.toString();
    }    

    /**
     *
     * @param sinput
     * @return
     */
    public static byte [] hex2byte(String sinput) {
        int length = sinput.length();

        if ((length & 0x01) != 0) {
            throw new IllegalArgumentException("odd number of characters.");
        }

        byte[] out = new byte[length >> 1];

        // two characters form the hex value.
        for (int i = 0, j = 0; j < length; i++) {
            int f = Character.digit(sinput.charAt(j++), 16) << 4;
            f = f | Character.digit(sinput.charAt(j++), 16);
            out[i] = (byte) (f & 0xFF);
        }

        return out;
    }

    /**
     *
     * @param resource
     * @return
     * @throws IOException
     */
    public static String readResource(String resource) throws IOException {
        
        InputStream in = StringUtils.class.getResourceAsStream(resource);
        if (in == null) {
            throw new FileNotFoundException(resource);
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len;
        while ((len = in.read(buffer)) > 0) {
            out.write(buffer, 0, len);
        }
        byte[] data = out.toByteArray();

        return new String(data, "UTF-8");
    }
        
    /**
     *
     * @param sCardNumber
     * @return
     */
    public static boolean isNumber(String sCardNumber){
        
        if ( (sCardNumber==null) || (sCardNumber.equals("")) ){
            return false;
        }
        
        for (int i = 0; i < sCardNumber.length(); i++) {
            char c = sCardNumber.charAt(i);
            if (c != '0' && c != '1' && c != '2' && c != '3' && c != '4' 
                && c != '5' && c != '6' && c != '7' && c != '8' && c != '9') {
                return false;
            }
        }
        
        return true;
    }
}