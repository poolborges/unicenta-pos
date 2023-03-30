//    KrOS POS
//    Copyright (c) 2009-2018 uniCenta
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

import java.awt.image.BufferedImage;
import java.util.Properties;

/**
 *
 * @author adrianromero
 * Created on 21 de marzo de 2007, 21:28
 *
 */
public class ProductInfoEdit {
    
    /**
     *
     */
    protected String m_ID;
    protected String m_sRef;
    protected String m_sCode;
    protected String m_sCodeType;    
    protected String m_sName;
    protected double m_dPriceBuy;
    protected double m_dPriceSell; 
    protected String m_sCategoryID;    
    protected String m_sTaxID;
    protected String attributeuseid;
    protected Double m_dStockCost;
    protected Double m_dStockVolume;
    protected BufferedImage m_Image;
    protected boolean m_bCom;
    protected boolean m_bScale;
    protected boolean m_bKitchen;    
    protected boolean m_bPrintKB;
    protected boolean m_bSendStatus;        
    protected boolean m_bService;
    protected Properties attributes;
    protected String m_sDisplay;
    protected boolean m_bVprice;
    protected boolean m_bVerpatrib;
    protected String m_sTextTip;
    protected boolean m_bWarranty;
    protected double m_dStockUnits;    
    
    protected Integer m_iCatalogOrder;

    protected String m_sSupplierID;     
    protected String m_sUomID;     
    
    protected String m_sDatePick;
    
    
    /** Creates a new instance of ProductInfoEdit */
    public ProductInfoEdit() {
        m_ID = null;
        m_sRef = "0000";
        m_sCode = "0000";
        m_sCodeType = null;        
        m_sName = null;
        m_dPriceBuy = 0.0;
        m_dPriceSell = 0.0;
        m_sCategoryID = null;
        m_sTaxID = null;
        attributeuseid = null;
        m_dStockCost = null;
        m_dStockVolume = null;
        m_Image = null;
        m_bCom = false;
        m_bScale = false;
        m_bKitchen = false;
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

        m_iCatalogOrder = null;     
        
        m_sSupplierID = null;
        m_sUomID= null;
        
        m_sDatePick=null;
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

    public final String getReference(){
        return m_sRef;
    }
    public final void setReference(String sRef){
        m_sRef = sRef;
    }    

    public final String getCode(){
        return m_sCode;
    }
    public final void setCode(String sCode){
        m_sCode = sCode;
    }

    public final String getName() {            
        return m_sName;
    }
    public final void setName(String sName){            
        m_sName = sName;
    }

    public final boolean isCom() {            
        return m_bCom;
    }
    public final void setCom(boolean bValue){            
        m_bCom = bValue;
    }

    public final boolean isScale() {            
        return m_bScale;
    }
    public final void setScale(boolean bValue){            
        m_bScale = bValue;
    }

    public final String getCategoryID() {
        return m_sCategoryID;
    }
    public final void setCategoryID(String sCategoryID) {
        m_sCategoryID = sCategoryID;
    }

    public final String getTaxID() {
        return m_sTaxID;
    }
    public final void setTaxID(String sTaxID) {
        m_sTaxID = sTaxID;
    }

    public final String getAttributeUseID() {
        return attributeuseid;
    }
    public final void setAttributeUseID(String value) {
        attributeuseid = value;
    }

    public final double getPriceBuy(){
        return m_dPriceBuy;
    }    
    public final void setPriceBuy(double dPrice) {
        m_dPriceBuy = dPrice;
    }        

    public final double getPriceSell(){        
        return m_dPriceSell;
    }
    public final void setPriceSell(double dPrice) {        
        m_dPriceSell = dPrice;
    }      

    public BufferedImage getImage() {
        return m_Image;
    }
    public void setImage(BufferedImage img) {
        m_Image = img;
    }
    
    @Override
    public final String toString() {
        return m_sRef + " - " + m_sName;
    }    
    
    public final String getDatePick(){
        return m_sDatePick;
    }
    public final void setDatePick(String sDatePick){
        m_sDatePick = sDatePick;
    }  
}
