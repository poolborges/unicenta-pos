package com.openbravo.pos.ticket;

import com.openbravo.data.loader.DataRead;
import com.openbravo.data.loader.SerializerRead;

/**
 *
 * @author poolborges
 */
public class ProductInfoExtA {
    
    private String id;
    private String name;
    private String stockUnits;
    private String stockLocations;
    private double pricesell;
    private double taxerate;
    private double pricesellWithTax;
    
    private String categoryId; //Category ID
    private boolean m_bCom; //Companion Product
    private boolean m_bScale; //Is a Scale (product weight)
    private boolean m_bConstant; //Is a constant
    private boolean m_bService; // Is a Service

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPricesell() {
        return pricesell;
    }

    public void setPricesell(double pricesell) {
        this.pricesell = pricesell;
    }

    public double getTaxerate() {
        return taxerate;
    }

    public void setTaxerate(double taxerate) {
        this.taxerate = taxerate;
    }

    public double getPricesellWithTax() {
        return pricesellWithTax;
    }

    public void setPricesellWithTax(double pricesellWithTax) {
        this.pricesellWithTax = pricesellWithTax;
    }

    public String getStockUnits() {
        return stockUnits;
    }

    public void setStockUnits(String stockUnits) {
        this.stockUnits = stockUnits;
    }

    public String getStockLocations() {
        return stockLocations;
    }

    public void setStockLocations(String stockLocations) {
        this.stockLocations = stockLocations;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public boolean isCom() {
        return m_bCom;
    }

    public void setCom(boolean m_bCom) {
        this.m_bCom = m_bCom;
    }

    public boolean isScale() {
        return m_bScale;
    }

    public void setScale(boolean m_bScale) {
        this.m_bScale = m_bScale;
    }

    public boolean isConstant() {
        return m_bConstant;
    }

    public void setConstant(boolean m_bConstant) {
        this.m_bConstant = m_bConstant;
    }

    public boolean isService() {
        return m_bService;
    }

    public void setService(boolean m_bService) {
        this.m_bService = m_bService;
    }
    
    public static SerializerRead<ProductInfoExtA> getSerializerRead() {
        return (DataRead dr) -> {
            ProductInfoExtA product = new ProductInfoExtA();
            product.id = dr.getString(1);
            product.name = dr.getString(2);
            product.stockUnits = dr.getString(3);
            product.stockLocations = dr.getString(4);
            product.pricesell = dr.getDouble(5);
            product.taxerate = dr.getDouble(6);
            product.pricesellWithTax = dr.getDouble(7);
            product.setCategoryId(dr.getString(8));
            product.setCom(dr.getBoolean(9));
            product.setScale(dr.getBoolean(10));
            product.setConstant(dr.getBoolean(11));
            product.setService(dr.getBoolean(12));
            return product;
        };
    }

}
