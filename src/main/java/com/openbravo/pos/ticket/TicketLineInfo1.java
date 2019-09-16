//    uniCenta oPOS  - Touch Friendly Point Of Sale
//    Copyright (c) 2009-2017 uniCenta
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
import com.openbravo.data.loader.DataWrite;
import com.openbravo.data.loader.SerializableRead;
import com.openbravo.data.loader.SerializableWrite;
import com.openbravo.format.Formats;
import com.openbravo.pos.forms.AppLocal;
import com.openbravo.pos.util.StringUtils;
import java.io.*;
import java.util.Properties;

/**
 *
 * @author adrianromero
 */
public class TicketLineInfo1 implements SerializableWrite, SerializableRead, Serializable {

    private static final long serialVersionUID = 6608012948284450199L;
    private String m_sTicket;
    private int m_iLine;
    private double multiply;
    private double price;
    private TaxInfo tax;
    private Properties attributes;
    private String productid;
    private String attsetinstid;

    /** Creates new TicketLineInfo
     * @param productid
     * @param dMultiply
     * @param dPrice
     * @param tax
     * @param props */
    public TicketLineInfo1(String productid, double dMultiply, double dPrice, TaxInfo tax, Properties props) {
        init(productid, null, dMultiply, dPrice, tax, props);
    }

    /**
     *
     * @param productid
     * @param dMultiply
     * @param dPrice
     * @param tax
     */
    public TicketLineInfo1(String productid, double dMultiply, double dPrice, TaxInfo tax) {
        init(productid, null, dMultiply, dPrice, tax, new Properties());
    }

    /**
     *
     * @param productid
     * @param productname
     * @param producttaxcategory
     * @param dMultiply
     * @param dPrice
     * @param tax
     */
    public TicketLineInfo1(String productid, String productname, String producttaxcategory, double dMultiply, double dPrice, TaxInfo tax) {
        Properties props = new Properties();
        props.setProperty("product.name", productname);
        props.setProperty("product.taxcategoryid", producttaxcategory);
        init(productid, null, dMultiply, dPrice, tax, props);
    }

    /**
     *
     * @param productname
     * @param producttaxcategory
     * @param dMultiply
     * @param dPrice
     * @param tax
     */
    public TicketLineInfo1(String productname, String producttaxcategory, double dMultiply, double dPrice, TaxInfo tax) {

        Properties props = new Properties();
        props.setProperty("product.name", productname);
        props.setProperty("product.taxcategoryid", producttaxcategory);
        init(null, null, dMultiply, dPrice, tax, props);
    }

    /**
     *
     */
    public TicketLineInfo1() {
        init(null, null, 0.0, 0.0, null, new Properties());
    }

    /**
     *
     * @param product
     * @param dMultiply
     * @param dPrice
     * @param tax
     * @param attributes
     */
    public TicketLineInfo1(ProductInfoExt product, double dMultiply, double dPrice, TaxInfo tax, Properties attributes) {

        String pid;

        if (product == null) {
            pid = null;
        } else {
            pid = product.getID();
            attributes.setProperty("product.name", product.getName());
            attributes.setProperty("product.com", product.isCom() ? "true" : "false");
            attributes.setProperty("product.constant", product.isConstant() ? "true" : "false");
            attributes.setProperty("product.printer", product.getPrinter());
            attributes.setProperty("product.service", product.isService() ? "true" : "false");
            attributes.setProperty("product.vprice", product.isVprice() ? "true" : "false");
            attributes.setProperty("product.verpatrib", product.isVerpatrib() ? "true" : "false");

            if (product.getTextTip() != null) {
                attributes.setProperty("product.texttip", product.getTextTip());
            }

            attributes.setProperty("product.warranty", product.getWarranty()? "true" : "false");        
        
            if (product.getAttributeSetID() != null) {
                attributes.setProperty("product.attsetid", product.getAttributeSetID());
            }
                attributes.setProperty("product.taxcategoryid", product.getTaxCategoryID());

            if (product.getCategoryID() != null) {
                attributes.setProperty("product.categoryid", product.getCategoryID());
            }
        }
        init(pid, null, dMultiply, dPrice, tax, attributes);
    }

    /**
     *
     * @param oProduct
     * @param dPrice
     * @param tax
     * @param attributes
     */
    public TicketLineInfo1(ProductInfoExt oProduct, double dPrice, TaxInfo tax, Properties attributes) {
        this(oProduct, 1.0, dPrice, tax, attributes);
    }

    /**
     *
     * @param line
     */
    public TicketLineInfo1(TicketLineInfo1 line) {
        init(line.productid, line.attsetinstid, line.multiply, line.price, line.tax, (Properties) line.attributes.clone());
    }

    private void init(String productid, String attsetinstid, double dMultiply, double dPrice, TaxInfo tax, Properties attributes) {

        this.productid = productid;
        this.attsetinstid = attsetinstid;
        multiply = dMultiply;
        price = dPrice;
        this.tax = tax;
        this.attributes = attributes;

        m_sTicket = null;
        m_iLine = -1;
    }

    void setTicket(String ticket, int line) {
        m_sTicket = ticket;
        m_iLine = line;
    }

    /**
     *
     * @param dp
     * @throws BasicException
     */
    @Override
    public void writeValues(DataWrite dp) throws BasicException {
        dp.setString(1, m_sTicket);
        dp.setInt(2, new Integer(m_iLine));
        dp.setString(3, productid);
        dp.setString(4, attsetinstid);

        dp.setDouble(5, new Double(multiply));
        dp.setDouble(6, new Double(price));

        dp.setString(7, tax.getId());
        try {
            ByteArrayOutputStream o = new ByteArrayOutputStream();
            attributes.storeToXML(o, AppLocal.APP_NAME, "UTF-8");
            dp.setBytes(8, o.toByteArray());
        } catch (IOException e) {
            dp.setBytes(8, null);
        }
    }

    /**
     *
     * @param dr
     * @throws BasicException
     */
    @Override
    public void readValues(DataRead dr) throws BasicException {
        m_sTicket = dr.getString(1);
        m_iLine = dr.getInt(2).intValue();
        productid = dr.getString(3);
        attsetinstid = dr.getString(4);
        multiply = dr.getDouble(5);
        price = dr.getDouble(6);
        tax = new TaxInfo(
                dr.getString(7), 
                dr.getString(8), 
                dr.getString(9), 
                dr.getString(10), 
                dr.getString(11), 
                dr.getDouble(12), 
                dr.getBoolean(13), 
                dr.getInt(14));
        attributes = new Properties();
        try {
            byte[] img = dr.getBytes(15);
            if (img != null) {
                attributes.loadFromXML(new ByteArrayInputStream(img));
            }
        } catch (IOException e) {
        }
    }

    /**
     *
     * @return
     */
    public TicketLineInfo1 copyTicketLine() {
        TicketLineInfo1 l = new TicketLineInfo1();
        l.productid = productid;
        l.attsetinstid = attsetinstid;
        l.multiply = multiply;
        l.price = price;
        l.tax = tax;
        l.attributes = (Properties) attributes.clone();
        return l;
    }

    /**
     *
     * @return
     */
    public int getTicketLine() {
        return m_iLine;
    }

    /**
     *
     * @return
     */
    public String getProductID() {
        return productid;
    }

    /**
     *
     * @return
     */
    public String getProductName() {
        return attributes.getProperty("product.name");
    }

    /**
     *
     * @return
     */
    public String getProductPrinter() {
        return attributes.getProperty("product.printer");
    }    

    /**
     *
     * @return
     */
    public String getProductAttSetId() {
        return attributes.getProperty("product.attsetid");
    }

    /**
     *
     * @return
     */
    public String getProductAttSetInstDesc() {
        return attributes.getProperty("product.attsetdesc", "");
    }

    /**
     *
     * @param value
     */
    public void setProductAttSetInstDesc(String value) {
        if (value == null) {
            attributes.remove(value);
        } else {
            attributes.setProperty("product.attsetdesc", value);
        }
    }

    /**
     *
     * @return
     */
    public String getProductAttSetInstId() {
        return attsetinstid;
    }

    /**
     *
     * @param value
     */
    public void setProductAttSetInstId(String value) {
        attsetinstid = value;
    }

    /**
     *
     * @return
     */
    public boolean isProductCom() {
        return "true".equals(attributes.getProperty("product.com"));
    }

    /**
     *
     * @return
     */
    public String getProductTaxCategoryID() {
        return (attributes.getProperty("product.taxcategoryid"));
    }

    /**
     *
     * @param taxID
     */
    public void setProductTaxCategoryID(String taxID){
        attributes.setProperty("product.taxcategoryid",taxID);
    }
    
    /**
     *
     * @return
     */
    public String getProductCategoryID() {
        return (attributes.getProperty("product.categoryid"));
    }

    /**
     *
     * @return
     */
    public double getMultiply() {
        return multiply;
    }

    /**
     *
     * @param dValue
     */
    public void setMultiply(double dValue) {
        multiply = dValue;
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
     * @param dValue
     */
    public void setPrice(double dValue) {
        price = dValue;
    }

    /**
     *
     * @return
     */
    public double getPriceTax() {
        return price * (1.0 + getTaxRate());
    }

    /**
     *
     * @param dValue
     */
    public void setPriceTax(double dValue) {
        price = dValue / (1.0 + getTaxRate());
    }

    /**
     *
     * @return
     */
    public TaxInfo getTaxInfo() {
        return tax;
    }

    /**
     *
     * @param value
     */
    public void setTaxInfo(TaxInfo value) {
        tax = value;
    }

    /**
     *
     * @param key
     * @return
     */
    public String getProperty(String key) {
        return attributes.getProperty(key);
    }

    /**
     *
     * @param key
     * @param defaultvalue
     * @return
     */
    public String getProperty(String key, String defaultvalue) {
        return attributes.getProperty(key, defaultvalue);
    }

    /**
     *
     * @param key
     * @param value
     */
    public void setProperty(String key, String value) {
        attributes.setProperty(key, value);
    }

    /**
     *
     * @return
     */
    public Properties getProperties() {
        return attributes;
    }

    /**
     *
     * @return
     */
    public double getTaxRate() {
        return tax == null ? 0.0 : tax.getRate();
    }

    /**
     *
     * @return
     */
    public double getSubValue() {
        return price * multiply;
    }

    /**
     *
     * @return
     */
    public double getTax() {
        return price * multiply * getTaxRate();
    }

    /**
     *
     * @return
     */
    public double getValue() {
        return price * multiply * (1.0 + getTaxRate());
    }

    /**
     *
     * @return
     */
    public String printName() {
        return StringUtils.encodeXML(attributes.getProperty("product.name"));
    }
    
    /**
     *
     * @return
     */
    public String printPrinter() {
        return StringUtils.encodeXML(attributes.getProperty("product.printer"));
    }    

    /**
     *
     * @return
     */
    public String printMultiply() {
        return Formats.DOUBLE.formatValue(multiply);
    }

    /**
     *
     * @return
     */
    public String printPrice() {
        return Formats.CURRENCY.formatValue(getPrice());
    }

    /**
     *
     * @return
     */
    public String printPriceTax() {
        return Formats.CURRENCY.formatValue(getPriceTax());
    }

    /**
     *
     * @return
     */
    public String printTax() {
        return Formats.CURRENCY.formatValue(getTax());
    }

    /**
     *
     * @return
     */
    public String printTaxRate() {
        return Formats.PERCENT.formatValue(getTaxRate());
    }

    /**
     *
     * @return
     */
    public String printSubValue() {
        return Formats.CURRENCY.formatValue(getSubValue());
    }

    /**
     *
     * @return
     */
    public String printValue() {
        return Formats.CURRENCY.formatValue(getValue());
    }
// ADDED JG 20.12.10 - Kitchen Print

    /**
     *
     * @return
     */
    public boolean isProductConstant() {
	return "true".equals(attributes.getProperty("product.constant"));
}
// ***
// ADDED JG 25.06.11 - Is Service

    /**
     *
     * @return
     */
    public boolean isProductService() {
	return "true".equals(attributes.getProperty("product.service"));
}
// Added JDL 19.12.12 - Variable price product

    /**
     *
     * @return
     */
    public boolean isProductVprice() {
	return "true".equals(attributes.getProperty("product.vprice"));
//

}

// Added JDL 09.02.13 for Chris

    /**
     *
     * @return
     */
    public boolean isProductVerpatrib() {
	return "true".equals(attributes.getProperty("product.verpatrib"));
//

}

// Added JDL 09.04.12 - Variable price product

    /**
     *
     * @return
     */
    public String printTextTip() {
	return attributes.getProperty("product.texttip");
//

}

// Added JDL 09.02.13

    /**
     *
     * @return
     */
    public boolean isProductWarranty() {
	return "true".equals(attributes.getProperty("product.warranty"));
//

    }
}
