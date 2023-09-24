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

package com.openbravo.pos.inventory;

import com.openbravo.basic.BasicException;
import com.openbravo.data.loader.DataRead;
import com.openbravo.data.loader.SerializerRead;
import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author JG uniCenta May 15
 * Used in Product stock tab to display all this Product's
 * location values
 */
public class ProductStock implements Serializable{

    private static final long serialVersionUID = 1L;

    String pId;
    String location;
    Double units;
    Double minimum;
    Double maximum;
    Double pricebuy;
    Double pricesell;
    Date memodate;    


    public ProductStock() {}

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

    public String getProductId() {
        return pId;
    }
    
    public void setProductId(String pId) {
        this.pId = pId;
    }

    public String getLocation() {
        return location;
    }
    public void setLocation(String location) {
        this.location = location;
    }    

    public Double getUnits() {
        return units;
    }
    public void setUnits(Double units) {
        this.units = units;
    }

    public Double getMinimum() {
        return minimum;
    }
    public void setMinimum(Double minimum) {
        this.minimum = minimum;
    }

    public Double getMaximum() {
        return maximum;
    }
    public void setMaximum(Double maximum) {
        this.maximum = maximum;
    }

    public Double getPriceBuy() {
        return pricebuy;
    }
    public void setPriceBuy(Double pricebuy) {
        this.pricebuy = pricebuy;
    }

    public Double getPriceSell() {
        return pricesell;
    }
    public void setPriceSell(Double pricesell) {
        this.pricesell = pricesell;
    }

    public Date getMemoDate() {
        return memodate;
    }
    public void setMemoDate(Date memodate) {
        this.memodate = memodate;
    }    

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