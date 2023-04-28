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

import com.openbravo.basic.BasicException;
import com.openbravo.data.loader.DataRead;
import com.openbravo.data.loader.IKeyed;
import com.openbravo.data.loader.SerializerRead;

/**
 *
 * @author Jack
 * @version
 */
public class ProductInfo implements IKeyed {

    private static final long serialVersionUID = 8712449444103L;
    private String m_sID;
    private String m_sRef;
    private String m_sCode;
    private String m_sCodetype;
    private String m_sName;
    private double m_dPriceBuy;
    private double m_dPriceSell;
    private String categoryid;
    private String taxcategoryid; //Tax Category ID

    /**
     * Creates new ProductInfo
     *
     * @param id
     * @param ref
     * @param code
     * @param name
     */
    public ProductInfo(String id, String ref, String code, String name) {
        m_sID = id;
        m_sRef = ref;
        m_sCode = code;
        m_sName = name;
    }

    /**
     *
     * @return
     */
    @Override
    public Object getKey() {
        return m_sID;
    }

    /**
     *
     * @param sID
     */
    public void setID(String sID) {
        m_sID = sID;
    }

    public String getID() {
        return m_sID;
    }

    /**
     *
     * @return
     */
    public String getRef() {
        return m_sRef;
    }

    public void setRef(String sRef) {
        m_sRef = sRef;
    }

    /**
     *
     * @return
     */
    public String getCode() {
        return m_sCode;
    }

    public void setCode(String sCode) {
        m_sCode = sCode;
    }

    /**
     *
     * @return
     */
    public String getName() {
        return m_sName;
    }

    public void setName(String sName) {
        m_sName = sName;
    }

    public double getPriceBuy() {
        return m_dPriceBuy;
    }

    public void setPriceBuy(double m_dPriceBuy) {
        this.m_dPriceBuy = m_dPriceBuy;
    }
    
    public String getCodetype() {
        return m_sCodetype;
    }

    public void setCodetype(String m_sCodetype) {
        this.m_sCodetype = m_sCodetype;
    }

    public double getPriceSell() {
        return m_dPriceSell;
    }

    public void setPriceSell(double m_dPriceSell) {
        this.m_dPriceSell = m_dPriceSell;
    }

    public String getCategoryID() {
        return categoryid;
    }

    public void setCategoryID(String categoryid) {
        this.categoryid = categoryid;
    }

    public String getTaxID() {
        return taxcategoryid;
    }

    public void setTaxID(String taxcategoryid) {
        this.taxcategoryid = taxcategoryid;
    }

    @Override
    public String toString() {
        return m_sName;
    }

    /**
     *
     * @return
     */
    public static SerializerRead<ProductInfo> getSerializerRead() {
        return new SerializerRead<ProductInfo>() {
            @Override
            public ProductInfo readValues(DataRead dr) throws BasicException {
                ProductInfo prod = new ProductInfo(
                        dr.getString(1),
                        dr.getString(2),
                        dr.getString(3),
                        dr.getString(5));

                prod.setCodetype(dr.getString(4));
                prod.setPriceBuy(dr.getDouble(6));
                prod.setPriceSell(dr.getDouble(7));
                prod.setCategoryID(dr.getString(8));
                prod.setTaxID(dr.getString(9));

                return prod;
            }
        };
    }
}
