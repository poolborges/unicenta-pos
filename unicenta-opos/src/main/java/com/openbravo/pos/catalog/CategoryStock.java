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

package com.openbravo.pos.catalog;

import com.openbravo.basic.BasicException;
import com.openbravo.data.loader.DataRead;
import com.openbravo.data.loader.SerializerRead;

/**
 *
 * @author JG uniCenta Dec 17
 * Used in Categories to display all this Categories Products
 */

public class CategoryStock {

    String productId;
    String productName;
    String productCode;
    String categoryId;   

    /**
     * Main method to return all customer's transactions 
     */
    public CategoryStock() {
    }

    /**
     *
     * @param productId
     * @param productName
     * @param cId
     */
    public CategoryStock(String productId, String productName, String productCode, String pId) {
        this.productId = productId;
        this.productName = productName;
        this.productCode = productCode;
        this.categoryId = pId;        
    }

    /**
     *
     * @return product string
     */
    public String getProductId() {
        return productId;
    }
    public void setProductId(String productId) {
        this.productId = productId;
    }

    /**
     *
     * @return product name string 
     */
    public String getProductName() {
        return productName;
    }
    public void setProductName(String productName) {
        this.productName = productName;
    }
    
    /**
     *
     * @return product barcode string 
     */
    public String getProductCode() {
        return productCode;
    }
    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }    

    /**
     *
     * @return category name string
     */
    public String getCategoryId() {
        return categoryId;
    }
    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    /**
     *
     * @return products for this category
     */
    public static SerializerRead getSerializerRead() {
        return new SerializerRead() {
            @Override
            public Object readValues(DataRead dr) throws BasicException {
                String productId = dr.getString(1);
                String productName = dr.getString(2);
                String productCode = dr.getString(3);                
                String categoryId = dr.getString(4);
                return new CategoryStock(productId, productName, productCode, categoryId);
            }
        };
    }
}