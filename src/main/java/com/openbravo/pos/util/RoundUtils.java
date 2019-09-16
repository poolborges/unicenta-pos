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

import com.openbravo.format.Formats;

/**
 *
 * @author JG uniCenta
 */
public class RoundUtils {
    
    /** Creates a new instance of DoubleUtils */
    private RoundUtils() {
    }
    
    /**
     *
     * @param dValue
     * @return
     */
    public static double round(double dValue) {
        double fractionMultiplier = Math.pow(10.0, Formats.getCurrencyDecimals());
        return Math.rint(dValue * fractionMultiplier) / fractionMultiplier;
    }
    
    /**
     *
     * @param d1
     * @param d2
     * @return
     */
    public static int compare(double d1, double d2) {
        
        return Double.compare(round(d1), round(d2));
    }

    /**
     *
     * @param value
     * @return
     */
    public static double getValue(Double value) {
        return value == null ? 0.0 : value.doubleValue();
    }
}
