//    KrOS POS
//    Copyright (c) 2019-2023 KriolOS
//    
//
//     
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
//    along with KrOS POS.  If not, see <http://www.gnu.org/licenses/>.

package com.openbravo.pos.ticket;

import com.openbravo.data.loader.IKeyed;
import java.io.Serializable;

/**
 *
 * @author adrianromero
 */
public class TaxInfo implements Serializable, IKeyed {
    
    private static final long serialVersionUID = -2705212098856473043L;
    private String id;
    private String name;
    private String taxcategoryid; // Product Tax Category ID
    private String taxcustcategoryid; // Customer Tax Category ID
    private String parentid;
    
    private double rate;
    private boolean cascade;
    private Integer order;
    
    /** 
     * Creates new TaxInfo
     * 
     * @param id Tax ID
     * @param name Tax name
     * @param taxcategoryid Product Tax Category ID
     * @param taxcustcategoryid Customer Tax Category ID
     * @param rate Tax rate
     * @param cascade if tax calcule is cascade
     * @param parentid Parent Tax ID
     * @param order (Order/Position)
     */
    public TaxInfo(String id, String name, String taxcategoryid, String taxcustcategoryid, 
            String parentid, double rate, boolean cascade, Integer order) {
        this.id = id;
        this.name = name;
        this.taxcategoryid = taxcategoryid;
        this.taxcustcategoryid = taxcustcategoryid;
        this.parentid = parentid;
        
        this.rate = rate;
        this.cascade = cascade;
        this.order = order;
    }
    
    /**
     * Get Tax ID
     * 
     * @return Tax ID
     */
    @Override
    public String getKey() {
        return id;
    }
    
    /**
     * Set Tax ID
     * @param value
     */
    public void setID(String value) {
        id = value;
    }
    
    /**
     *  Get Tax ID
     * 
     * @return
     */
    public String getId() {
        return id;
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
     * @param value
     */
    public void setName(String value) {
        name = value;
    }

    /**
     * Get Product Tax Category ID
     * 
     * @return
     */
    public String getTaxCategoryID() {
        return taxcategoryid;
    }
    
    /**
     *
     * @param value
     */
    public void setTaxCategoryID(String value) {
        taxcategoryid = value;
    }

    /**
     * Get Customer Tax Category ID
     * @return
     */
    public String getTaxCustCategoryID() {
        return taxcustcategoryid;
    }
    
    /**
     *
     * @param value
     */
    public void setTaxCustCategoryID(String value) {
        taxcustcategoryid = value;
    }    

    /**
     *
     * @return
     */
    public String getParentID() {
        return parentid;
    }
    
    /**
     *
     * @param value
     */
    public void setParentID(String value) {
        parentid = value;
    }
    
    /**
     *
     * @return
     */
    public double getRate() {
        return rate;
    }
    
    /**
     *
     * @param value
     */
    public void setRate(double value) {
        rate = value;
    }

    /**
     *
     * @return
     */
    public boolean isCascade() {
        return cascade;
    }
    
    /**
     *
     * @param value
     */
    public void setCascade(boolean value) {
        cascade = value;
    }
    
    /**
     * Get Tax Order/Position
     * 
     * @return
     */
    public Integer getOrder() {
        return order;
    }
    
    /**
     *
     * @return
     */
    public Integer getApplicationOrder() {
        return order != null ?  order: Integer.MAX_VALUE;
    }

    /**
     *
     * @param value
     */
    public void setOrder(Integer value) {
        order = value;
    }
    
    @Override
    public String toString(){
        return name;
    }
}
