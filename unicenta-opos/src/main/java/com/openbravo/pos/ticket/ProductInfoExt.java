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
package com.openbravo.pos.ticket;

import com.openbravo.basic.BasicException;
import com.openbravo.data.loader.DataRead;
import com.openbravo.data.loader.ImageUtils;
import com.openbravo.data.loader.SerializerRead;
import java.util.Properties;
import com.openbravo.format.Formats;
import java.awt.image.BufferedImage;
import java.util.Date;

/**
 *
 * @author adrianromero
 *
 */
public class ProductInfoExt {

    private static final long serialVersionUID = 7587696873036L;

    protected String m_ID;
    protected String m_sRef;
    protected String m_sCode;
    protected String m_sCodetype;    
    protected String m_sName;
    protected double m_dPriceBuy;
    protected double m_dPriceSell;
    protected String categoryid;
    protected String taxcategoryid;
    protected String attributesetid;
    protected double m_stockCost;
    protected double m_stockVolume;
    protected BufferedImage m_Image;
    protected boolean m_bCom;
    protected boolean m_bScale;
    protected boolean m_bConstant;
    protected boolean m_bPrintKB;
    protected boolean m_bSendStatus;    
    private boolean m_bService;
    protected Properties attributes;
    protected String m_sDisplay;
    protected boolean m_bVprice;
    protected boolean m_bVerpatrib;
    protected String m_sTextTip;
    protected boolean m_bWarranty;
    public double m_dStockUnits;
    public String m_sPrinter;
    public String supplierid;
    private String uomid;   
    protected String memodate;

    public ProductInfoExt() {
        m_ID = null;
        m_sRef = "0000";
        m_sCode = "0000";
        m_sCodetype = null;
        m_sName = null;
        m_dPriceBuy = 0.0;
        m_dPriceSell = 0.0;
        categoryid = null;
        taxcategoryid = null;
        attributesetid = null;
        m_stockCost = 0.0;
        m_stockVolume = 0.0;
        m_Image = null;
        m_bCom = false;
        m_bScale = false;
        m_bConstant = false;
        m_bPrintKB = false;
        m_bSendStatus = false;
        m_bService = false;
        attributes = new Properties();
        m_sDisplay = null;
        m_bVprice = false;
        m_bVerpatrib = false;
        m_sTextTip = null;
        m_bWarranty = false;
        m_dStockUnits = 0.0;
        m_sPrinter = null;
        supplierid = "0";
        uomid = "0";        
        memodate = null;
    }

    /**
     *
     * @return
     */
    public final String getID() {
        return m_ID;
    }
    public final void setID(String id) {
        m_ID = id;
    }

    public final String getReference() {
        return m_sRef;
    }
    public final void setReference(String sRef) {
        m_sRef = sRef;
    }

    public final String getCode() {
        return m_sCode;
    }
    public final void setCode(String sCode) {
        m_sCode = sCode;
    }

    public final String getCodetype() {
        return m_sCodetype;
    }
    public final void setCodetype(String sCodetype) {
        m_sCodetype = sCodetype;
    }
    
    public final String getName() {
        return m_sName;
    }
    public final void setName(String sName) {
        m_sName = sName;
    }

    public final double getPriceBuy() {
        return m_dPriceBuy;
    }
    public final void setPriceBuy(double dPrice) {
        m_dPriceBuy = dPrice;
    }

    public final double getPriceSell() {
        return m_dPriceSell;
    }
    public final void setPriceSell(double dPrice) {
        m_dPriceSell = dPrice;
    }    

    public final String getCategoryID() {
        return categoryid;
    }
    public final void setCategoryID(String sCategoryID) {
        categoryid = sCategoryID;
    }

    public final String getTaxCategoryID() {
        return taxcategoryid;
    }
    public final void setTaxCategoryID(String value) {
        taxcategoryid = value;
    }

    public final String getAttributeSetID() {
        return attributesetid;
    }
    public final void setAttributeSetID(String value) {
        attributesetid = value;
    }

    public final double getStockCost() {
        return m_stockCost;
    }
    public final void setStockCost(double dPrice) {
        m_stockCost = dPrice;
    }

    public final double getStockVolume() {
        return m_stockVolume;
    }
    public final void setStockVolume(double dStockVolume) {
        m_stockVolume = dStockVolume;
    }

    public BufferedImage getImage() {
        return m_Image;
    }
    public void setImage(BufferedImage img) {
        m_Image = img;
    }
    
    public final boolean isCom() {
        return m_bCom;
    }
    public final void setCom(boolean bValue) {
        m_bCom = bValue;
    }

    public final boolean isScale() {
        return m_bScale;
    }
    public final void setScale(boolean bValue) {
        m_bScale = bValue;
    }

    public final boolean isConstant() {
        return m_bConstant;
    }
    public final void setConstant(boolean bValue) {
        m_bConstant = bValue;
    }

    public final boolean isPrintKB() {
        return m_bPrintKB;
    }
    public final void setPrintKB(boolean bValue) {
        m_bPrintKB = bValue;
    }
    
    public final boolean isSendStatus() {
        return m_bSendStatus;
    }
    public final void setSendStatus(boolean bValue) {
        m_bSendStatus = bValue;
    }

    public final boolean isService() {
        return m_bService;
    }
    public final void setService(boolean bValue) {
        m_bService = bValue;
    }

    public String getProperty(String key) {
        return attributes.getProperty(key);
    }
    public String getProperty(String key, String defaultvalue) {
        return attributes.getProperty(key, defaultvalue);
    }
    public void setProperty(String key, String value) {
        attributes.setProperty(key, value);
    }
    public Properties getProperties() {
        return attributes;
    }

    public final String getDisplay() {
        return m_sDisplay;
    }
    public final void setDisplay(String sDisplay) {
        m_sDisplay = sDisplay;
    }

    public final boolean isVprice() {
        return m_bVprice;
    }

    public final boolean isVerpatrib() {
        return m_bVerpatrib;
    }

    public final String getTextTip() {
        return m_sTextTip;
    }
    public final void setTextTip(String value) {
        m_sTextTip = value;
    }

    public final boolean getWarranty() {
        return m_bWarranty;
    }
    public final void setWarranty(boolean bValue) {
        m_bWarranty = bValue;
    }

    public final Double getStockUnits() { 
        return m_dStockUnits;
    }
    public final void setStockUnits(double dStockUnits) {    
        m_dStockUnits = dStockUnits;
    }

    public String printPriceSell() {
        return Formats.CURRENCY.formatValue(getPriceSell());
    }
    
    public final double getPriceSellTax(TaxInfo tax) {
        return m_dPriceSell * (1.0 + tax.getRate());
    }
    public String printPriceSellTax(TaxInfo tax) {        
        return Formats.CURRENCY.formatValue(getPriceSellTax(tax));
    }
    
    public final String getPrinter() {
        return m_sPrinter;
    }
    public final void setPrinter(String value) {
        m_sPrinter = value;
    }    

    public final String getSupplierID() {
        return supplierid;
    }
    public final void setSupplierID(String sSupplierID) {
        supplierid = sSupplierID;
    }
    
    public final String getUomID() {
          return uomid;
    }
    public final void setUomID(String sUomID) {
	uomid = sUomID;
    }    
    
    /**
     *
     * @return memo date string
     */
    public String getMemoDate() {
        return memodate;
    }
    public void setMemoDate(String memodate) {
        this.memodate = memodate;
    }
    public String printMemoDate() {       
        return Formats.STRING.formatValue(memodate);
    }    

    /**
     *
     * @return
     */
    public static SerializerRead getSerializerRead() {
        return new SerializerRead() {
            @Override
            public Object readValues(DataRead dr) throws BasicException {
                ProductInfoExt product = new ProductInfoExt();
                product.m_ID = dr.getString(1);                                 
                product.m_sRef = dr.getString(2);                               
                product.m_sCode = dr.getString(3);                              
                product.m_sCodetype = dr.getString(4);                              
                product.m_sName = dr.getString(5);                              
                product.m_dPriceBuy = dr.getDouble(6);                          
                product.m_dPriceSell = dr.getDouble(7);                         
                product.categoryid = dr.getString(8);                          
                product.taxcategoryid = dr.getString(9);                        
                product.attributesetid = dr.getString(10); 
                product.m_stockCost = dr.getDouble(11);
                product.m_stockVolume = dr.getDouble(12);
                product.m_Image = ImageUtils.readImage(dr.getBytes(13));            
                product.m_bCom = dr.getBoolean(14);                              
                product.m_bScale = dr.getBoolean(15);                            
                product.m_bConstant = dr.getBoolean(16);                         
                product.m_bPrintKB = dr.getBoolean(17);                         
                product.m_bSendStatus = dr.getBoolean(18);                                         
                product.m_bService = dr.getBoolean(19);                                         
                product.attributes = ImageUtils.readProperties(dr.getBytes(20));
                product.m_sDisplay = dr.getString(21); 
                product.m_bVprice = dr.getBoolean(22);                          
                product.m_bVerpatrib = dr.getBoolean(23);                                       
                product.m_sTextTip = dr.getString(24);                          
                product.m_bWarranty = dr.getBoolean(25);                        
                product.m_dStockUnits = dr.getDouble(26); 
                product.m_sPrinter = dr.getString(27);
                product.supplierid = dr.getString(28);
                product.uomid = dr.getString(29);
                product.memodate = dr.getString(30);

                return product;
            }
        };
    }

    @Override
    public final String toString() {
        return m_sRef + " - " + m_sName;
    }
}