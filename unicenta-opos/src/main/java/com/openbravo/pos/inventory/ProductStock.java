//    uniCenta oPOS  - Touch Friendly Point Of Sale
//    Copyright (c) 2009-2018 uniCenta
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

package com.openbravo.pos.inventory;

import com.openbravo.basic.BasicException;
import com.openbravo.data.loader.DataRead;
import com.openbravo.data.loader.SerializerRead;
import java.util.Date;

/**
 *
 * @author JG uniCenta May 15
 * Used in Product stock tab to display all this Product's
 * location values
 */
public class ProductStock {

    String pId;
    String location;
    Double units;
    Double minimum;
    Double maximum;
    Double pricebuy;
    Double pricesell;
    Date memodate;    

    /**
     * Main method to return a product's "live" stock position 
     */
    public ProductStock() {
    }

    /**
     *
     * @param pId
     * @param location
     * @param units
     * @param minimum
     * @param maximum
     * @param pricebuy
     * @param pricesell 
     * @param memodate 
     */
    public ProductStock(String pId, String location, Double units, Double minimum, 
            Double maximum, Double pricebuy, Double pricesell, Date memodate) {

        this.pId = pId;
        this.location = location;
        this.units = units;
        this.minimum = minimum;
        this.maximum = maximum;
        this.pricebuy = pricebuy;
        this.pricesell = pricesell;
        this.memodate = memodate;
    }

    /**
     *
     * @return product id string
     */
    public String getProductId() {
        return pId;
    }
    public void setProductId(String pId) {
        this.pId = pId;
    }

    /**
     *
     * @return product's location string 
     */
    public String getLocation() {
        return location;
    }
    public void setLocation(String location) {
        this.location = location;
    }    

    /**
     *
     * @return product's quantity string value
     */
    public Double getUnits() {
        return units;
    }
    public void setUnits(Double units) {
        this.units = units;
    }

    /**
     *
     * @return 
     */
    public Double getMinimum() {
        return minimum;
    }
    public void setMinimum(Double minimum) {
        this.minimum = minimum;
    }

    /**
     *
     * @return maximum value
     */
    public Double getMaximum() {
        return maximum;
    }
    public void setMaximum(Double maximum) {
        this.maximum = maximum;
    }

    /**
     *
     * @return price buy
     */
    public Double getPriceBuy() {
        return pricebuy;
    }
    public void setPriceBuy(Double pricebuy) {
        this.pricebuy = pricebuy;
    }
    
    /**
     *
     * @return price sell
     */
    public Double getPriceSell() {
        return pricesell;
    }
    public void setPriceSell(Double pricesell) {
        this.pricesell = pricesell;
    }
    /**
     *
     * @return memodate Date value
     */
    public Date getMemoDate() {
        return memodate;
    }
    public void setMemoDate(Date memodate) {
        this.memodate = memodate;
    }    

    
    /**
     *
     * @return stock for this product
     */
    public static SerializerRead getSerializerRead() {
        return new SerializerRead() {

            @Override
            public Object readValues(DataRead dr) throws BasicException {

                String pId = dr.getString(1);                
                String location = dr.getString(2);
                Double units = dr.getDouble(3);
                Double minimum = dr.getDouble(4);
                Double maximum = dr.getDouble(5);
                Double pricebuy = dr.getDouble(6);                
                Double pricesell = dr.getDouble(7);
                Date memodate = dr.getTimestamp(8);                
                
                return 
                    new ProductStock(pId, location, units, minimum, maximum, pricebuy, pricesell, memodate);                
            }
        };
    }
}