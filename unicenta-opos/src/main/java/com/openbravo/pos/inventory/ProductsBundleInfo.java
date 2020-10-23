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

package com.openbravo.pos.inventory;

import com.openbravo.data.loader.DataRead;
import com.openbravo.data.loader.SerializerRead;

/**
 *
 * @author Jack G
 */
public class ProductsBundleInfo {
    private static final long serialVersionUID = 7587646873036L;
    
    protected String id;
    protected String productId;
    protected String productBundleId;
    protected Double quantity;

    /**
     * 
     * @param id
     * @param productId
     * @param productBundleId
     * @param quantity 
     */
    public ProductsBundleInfo(String id, String productId, String productBundleId, Double quantity) {
        this.id = id;
        this.productId = productId;
        this.productBundleId = productBundleId;
        this.quantity = quantity;
    }
    

    public void setM_ID(String id) {
        this.id = id;
    }

    public void setM_sProduct(String productId) {
        this.productId = productId;
    }

    public void setM_sProductBundle(String productBundleId) {
        this.productBundleId = productBundleId;
    }

    public void setM_dQuantity(Double m_dQuantity) {
        this.quantity = quantity;
    }

    public String getId() {
        return id;
    }

    public String getProductId() {
        return productId;
    }

    public String getProductBundleId() {
        return productBundleId;
    }

    public Double getQuantity() {
        return quantity;
    }
    
    
    public static SerializerRead getSerializerRead() {
        return (DataRead dr) -> new ProductsBundleInfo(dr.getString(1)
                , dr.getString(2)
                , dr.getString(3)
                , dr.getDouble(4));
    }
    
}