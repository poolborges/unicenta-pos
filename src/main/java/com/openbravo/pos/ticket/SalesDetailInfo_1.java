//    uniCenta oPOS  - Touch Friendly Point Of Sale
//    Copyright (c) 2009-2017
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
//*****************************************************************************

package com.openbravo.pos.ticket;

import com.openbravo.basic.BasicException;
import com.openbravo.data.loader.DataRead;
import com.openbravo.data.loader.IKeyed;
import com.openbravo.data.loader.SerializerRead;
import com.openbravo.format.Formats;

/**
 *
 * @author  Adrian
 * @version 
 */
public class SalesDetailInfo_1 implements IKeyed {

    private static final long serialVersionUID = 8612449444103L;
    private String productName;
    private int lineNO;

    /**
     *
     * @return
     */
    public int getLineNO() {
        return lineNO;
    }

    /**
     *
     * @param lineNO
     */
    public void setLineNO(int lineNO) {
        this.lineNO = lineNO;
    }
    
    /**
     *
     * @return
     */
    public double getPrice() {
        return price;
    }
    
    /**
     *
     * @return
     */
    public String printPrice() {
        return Formats.CURRENCY.formatValue(price);
    }

    /**
     *
     * @param price
     */
    public void setPrice(double price) {
        this.price = price;
    }

    /**
     *
     * @return
     */
    public String getProductName() {
        return productName;
    }

    /**
     *
     * @param productName
     */
    public void setProductName(String productName) {
        this.productName = productName;
    }
    private double price;

    /** Creates new CategoryInfo
     * @param lineNo
     * @param productName
     * @param price */
    public SalesDetailInfo_1(int lineNo, String productName, double price) {
        this.lineNO = lineNo;
        this.productName = productName;
        this.price = price;
    }

    /**
     *
     * @return
     */
    public static SerializerRead getSerializerRead() {
        return new SerializerRead() {@Override
 public Object readValues(DataRead dr) throws BasicException {
            return new SalesDetailInfo(
                    dr.getInt(1), 
                    dr.getString(2), 
                    dr.getDouble(3));
        }};
    }

    /**
     *
     * @return
     */
    @Override
    public Object getKey() {
       return getLineNO();
    }
}