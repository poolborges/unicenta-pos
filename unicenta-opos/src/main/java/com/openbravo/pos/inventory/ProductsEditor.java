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

import com.openbravo.basic.BasicException;
import com.openbravo.data.gui.ComboBoxValModel;
import com.openbravo.data.loader.SentenceList;
import com.openbravo.data.loader.SentenceFind;
import com.openbravo.data.user.DirtyManager;
import com.openbravo.data.user.EditorRecord;
import com.openbravo.format.Formats;
import com.openbravo.pos.forms.AppLocal;
import com.openbravo.pos.forms.AppView;
import com.openbravo.pos.forms.DataLogicSales;
import com.openbravo.pos.sales.TaxesLogic;
import com.openbravo.pos.suppliers.DataLogicSuppliers;
import com.openbravo.pos.suppliers.JDialogNewSupplier;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import com.openbravo.beans.JCalendarDialog;
import com.openbravo.data.loader.Session;
import com.openbravo.pos.catalog.JCatalog;
import com.openbravo.pos.forms.AppProperties;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.AbstractTableModel;
import com.openbravo.pos.forms.DataLogicSystem;
import java.awt.Color;
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author Jack Gerrard
 */
public final class ProductsEditor extends javax.swing.JPanel implements EditorRecord {

    private AppProperties m_props;
    
    private static final long serialVersionUID = 1L;
    private Object m_oId;

    private final SentenceList m_sentcat;
    private ComboBoxValModel m_CategoryModel;

    private final SentenceList taxcatsent;
    private ComboBoxValModel taxcatmodel;

    private final SentenceList attsent;
    private ComboBoxValModel attmodel;
    
    private final SentenceList m_sentsuppliers;
    private ComboBoxValModel m_SuppliersModel;      

    private final SentenceList taxsent;
    private TaxesLogic taxeslogic;

    private Object pricesell;
    private boolean priceselllock = false;

    private boolean reportlock = false;
    private int btn;
    
    private List<ProductStock> productStockList;
    private ProductsEditor.StockTableModel stockModel;   
    
    private SentenceList m_sentuom;
    private ComboBoxValModel m_UomModel;

    private DirtyManager m_Dirty;
    private DataLogicSales dlSales;    
    private DataLogicSystem m_dlSystem;     
    private DataLogicSuppliers m_dlSuppliers;    
    
    private AppView appView;   

    private final SentenceFind loadimage; // JG 3 feb 16 speedup    
    
    private Session s;
    private Connection con;  
    private Statement stmt;
    private PreparedStatement pstmt;
    private String SQL;
    private ResultSet rs;
    private AppView m_App;

    protected DataLogicSystem dlSystem;    
    
    
    /** Creates new form JEditProduct
     * @param app
     * @param dirty */
    public ProductsEditor(AppView app, DirtyManager dirty) {

        setAppView(app);
            dlSales = (DataLogicSales) app.getBean("com.openbravo.pos.forms.DataLogicSales");
            m_dlSystem = (DataLogicSystem) app.getBean("com.openbravo.pos.forms.DataLogicSystem");            
            m_dlSuppliers = (DataLogicSuppliers) app.getBean("com.openbravo.pos.suppliers.DataLogicSuppliers");
            
        initComponents();
        
        loadimage = dlSales.getProductImage(); // JG 3 feb 16 speedup

        taxsent = dlSales.getTaxList();

        m_sentcat = dlSales.getCategoriesList();
        m_CategoryModel = new ComboBoxValModel();

        taxcatsent = dlSales.getTaxCategoriesList();
        taxcatmodel = new ComboBoxValModel();

        attsent = dlSales.getAttributeSetList();
        attmodel = new ComboBoxValModel();
        
        m_sentsuppliers = m_dlSuppliers.getSupplierList();
        m_SuppliersModel =  new ComboBoxValModel();
        
        m_sentuom = dlSales.getUomList();
        m_UomModel = new ComboBoxValModel();        

// Tab General        
        m_jRef.getDocument().addDocumentListener(dirty);
        m_jCode.getDocument().addDocumentListener(dirty);
        m_jCodetype.addActionListener(dirty);
        m_jName.getDocument().addDocumentListener(dirty);
        m_jCategory.addActionListener(dirty);
        m_jAtt.addActionListener(dirty);
        m_jVerpatrib.addActionListener(dirty); 
        m_jTax.addActionListener(dirty);
        m_jUom.addActionListener(dirty);        
        m_jPriceBuy.getDocument().addDocumentListener(dirty);
        m_jPriceSell.getDocument().addDocumentListener(dirty);
        m_jPrintTo.addActionListener(dirty);
        
// Tab Stock        
        m_jInCatalog.addActionListener(dirty);
        m_jConstant.addActionListener(dirty);
        m_jCatalogOrder.getDocument().addDocumentListener(dirty);
        m_jSupplier.addActionListener(dirty);        
        
        m_jService.addActionListener(dirty);
        m_jCheckWarrantyReceipt.addActionListener(dirty);       
        m_jComment.addActionListener(dirty);
        m_jScale.addActionListener(dirty);
        m_jVprice.addActionListener(dirty);
        m_jstockcost.getDocument().addDocumentListener(dirty);
        m_jstockvolume.getDocument().addDocumentListener(dirty);
        m_jPrintKB.addActionListener(dirty);
        m_jSendStatus.addActionListener(dirty);        
        
// Tab Image
        m_jImage.addPropertyChangeListener("image", dirty);

// Tab Button
        m_jDisplay.getDocument().addDocumentListener(dirty); 
        m_jTextTip.getDocument().addDocumentListener(dirty); 
        colourChooser.addActionListener(dirty);
        m_jDisplay.addCaretListener(null);

// Tab Properties
        txtAttributes.getDocument().addDocumentListener(dirty);
        
        FieldsManager fm = new FieldsManager();
        m_jPriceBuy.getDocument().addDocumentListener(fm);
        m_jPriceSell.getDocument().addDocumentListener(new PriceSellManager());
        m_jTax.addActionListener(fm);
        m_jPriceSellTax.getDocument().addDocumentListener(new PriceTaxManager());
        m_jmargin.getDocument().addDocumentListener(new MarginManager());
        m_jGrossProfit.getDocument().addDocumentListener(new MarginManager());

        m_jdate.getDocument().addDocumentListener(dirty);        
        
            init();
    }
    
    private void init() {
        writeValueEOF(); 
    }        

    /**
     * Instantiate object
     * @throws BasicException
     */
    @SuppressWarnings("unchecked")
    public void activate() throws BasicException {
        
        taxeslogic = new TaxesLogic(taxsent.list());

        m_CategoryModel = new ComboBoxValModel(m_sentcat.list());
        m_jCategory.setModel(m_CategoryModel);

        taxcatmodel = new ComboBoxValModel(taxcatsent.list());
        m_jTax.setModel(taxcatmodel);

        attmodel = new ComboBoxValModel(attsent.list());
        attmodel.add(0, null);
        m_jAtt.setModel(attmodel);
        
        m_SuppliersModel = new ComboBoxValModel(m_sentsuppliers.list());
        m_jSupplier.setModel(m_SuppliersModel);

        m_UomModel = new ComboBoxValModel(m_sentuom.list());
        m_jUom.setModel(m_UomModel);         
        
        String pId = null;              
    }

    /**
     *
     */
    @Override
    public void refresh() {
    }

    /**
     *
     */
    @Override
    public void writeValueEOF() {

        reportlock = true;

        m_jTitle.setText(AppLocal.getIntString("label.recordeof"));

// Tab General        
        m_oId = null;
        m_jRef.setText(null);
        m_jCode.setText(null);
        m_jCodetype.setSelectedIndex(0);
        m_jName.setText(null);
        m_CategoryModel.setSelectedKey(null);
        taxcatmodel.setSelectedKey(null);
        attmodel.setSelectedKey(null);
        m_jVerpatrib.setSelected(false);
        m_UomModel.setSelectedKey(0);        
        m_jPriceBuy.setText("0");
        setPriceSell(null);
        m_SuppliersModel.setSelectedKey(0);               
        
// Tab Stock        
        m_jInCatalog.setSelected(false);
        m_jConstant.setSelected(false);
        m_jCatalogOrder.setText(null);
        m_jPrintTo.setSelectedIndex(1);        
	m_jService.setSelected(false);        
        m_jCheckWarrantyReceipt.setSelected(false);   
        m_jComment.setSelected(false);
        m_jScale.setSelected(false);        
        m_jVprice.setSelected(false);
        m_jstockcost.setText("0");
        m_jstockvolume.setText("0");
        m_jPrintKB.setVisible(false);
        m_jSendStatus.setVisible(false);
        m_jStockUnits.setVisible(false);
        m_jdate.setText(null);

// Tab Image
        m_jImage.setImage(null);

// Tab Button
        m_jDisplay.setText(null);
        m_jTextTip.setText(null); 
//        colourChooser.setText("#000000");
        
// Tab Properties
        txtAttributes.setText(null);

        reportlock = false;

// Tab General
        m_jRef.setEnabled(false);
        m_jCode.setEnabled(false);
        m_jCodetype.setEnabled(false);
        m_jName.setEnabled(false);
        m_jCategory.setEnabled(false);
        m_jAtt.setEnabled(false);
        m_jVerpatrib.setEnabled(false);  
        m_jTax.setEnabled(false);
        m_jUom.setEnabled(false);        
        m_jPriceBuy.setEnabled(false);
        m_jPriceSell.setEnabled(false);
        m_jPriceSellTax.setEnabled(false);
        m_jmargin.setEnabled(false);
        m_jSupplier.setEnabled(false);        
        
// Tab Stock        
        m_jInCatalog.setEnabled(false);
	m_jConstant.setEnabled(false);        
        m_jCatalogOrder.setEnabled(false);
        m_jPrintTo.setEnabled(false);
	m_jService.setEnabled(false);
        m_jCheckWarrantyReceipt.setEnabled(false);        
        m_jComment.setEnabled(false);
        m_jScale.setEnabled(false);
        m_jVprice.setEnabled(false);
        m_jstockcost.setEnabled(false);
        m_jstockvolume.setEnabled(false);        
        jTableProductStock.setEnabled(false);   
        m_jdate.setEnabled(false);
        
// Tab Image
        m_jImage.setEnabled(false);

// Tab Button
        m_jDisplay.setEnabled(false);
        m_jTextTip.setEnabled(false); 
        colourChooser.setEnabled(false);
        
// Tab Properties
        txtAttributes.setEnabled(false);

        calculateMargin();
        calculatePriceSellTax();
        calculateGP();
    }

    @Override
    public void writeValueInsert() {

        final int selectedIndex = 0;

        reportlock = true;

        m_jTitle.setText(AppLocal.getIntString("label.recordnew"));

// Tab General        
        m_oId = UUID.randomUUID().toString();
        m_jRef.setText(null);
        m_jCode.setText(null);
        m_jCodetype.setSelectedIndex(0);
        m_jName.setText(null);
        m_CategoryModel.setSelectedKey("000");
        attmodel.setSelectedKey(null);
        m_jVerpatrib.setSelected(false);                
        taxcatmodel.setSelectedKey("001");
        m_UomModel.setSelectedKey("0");        
        m_jPriceBuy.setText("0");
        setPriceSell(null);        
//        m_SuppliersModel.setSelectedKey(0);
        m_jSupplier.setSelectedIndex(selectedIndex);        
        
// Tab Stock        
        m_jInCatalog.setSelected(true);
        m_jConstant.setSelected(false);
        m_jCatalogOrder.setText(null);
        m_jPrintTo.setSelectedIndex(1);
        m_jService.setSelected(false);
        m_jCheckWarrantyReceipt.setSelected(false); 
        m_jComment.setSelected(false);
        m_jScale.setSelected(false);
        m_jVprice.setSelected(false);
        m_jstockcost.setText("0");
        m_jstockvolume.setText("0");
        m_jdate.setText(null);

// Tab Image
        m_jImage.setImage(null);

// Tab Button
        m_jDisplay.setText(null);        
        m_jTextTip.setText(null);       
        colourChooser.setEnabled(false);        
        
// Tab Properties
        txtAttributes.setText(null);

        reportlock = false;

// Tab General
        m_jRef.setEnabled(true);
        m_jCode.setEnabled(true);
        m_jCodetype.setEnabled(true);
        m_jName.setEnabled(true);
        m_jCategory.setEnabled(true);
        m_jTax.setEnabled(true);
        m_jAtt.setEnabled(true);
        m_jVerpatrib.setEnabled(true);
        m_jUom.setEnabled(true);        
        m_jPriceBuy.setEnabled(true);
        m_jPriceSell.setEnabled(true);
        m_jPriceSellTax.setEnabled(true);
        m_jmargin.setEnabled(true);
        m_jSupplier.setEnabled(true);        

// Tab Stock        
        m_jInCatalog.setEnabled(true);
	m_jConstant.setEnabled(true);
        m_jCatalogOrder.setEnabled(false);
	m_jPrintTo.setEnabled(true);
        m_jService.setEnabled(true);
        m_jCheckWarrantyReceipt.setEnabled(true);
        m_jComment.setEnabled(true);
        m_jScale.setEnabled(true);
        m_jVprice.setEnabled(true);
        m_jstockcost.setEnabled(true);
        m_jstockvolume.setEnabled(true);
        jTableProductStock.setEnabled(false);
        m_jdate.setEnabled(true);        
        
// Tab Image        
        m_jImage.setEnabled(true);

// Tab Button
        m_jDisplay.setEnabled(true);
        m_jTextTip.setEnabled(true);
        colourChooser.setEnabled(true);        
        
// Tab Properties        
        txtAttributes.setEnabled(true);
        
        calculateMargin();
        calculatePriceSellTax();
        calculateGP();        
    }

    /**
     *
     * @return myprod
     * @throws BasicException
     */
    @Override
    public Object createValue() throws BasicException {

        Object[] myprod = new Object[32];        

        myprod[0] = m_oId == null ? UUID.randomUUID().toString() : m_oId;        
        myprod[1] = m_jRef.getText();
        myprod[2] = m_jCode.getText();
        myprod[3] = m_jCodetype.getSelectedItem();
        myprod[4] = m_jName.getText();
        myprod[5] = Formats.CURRENCY.parseValue(m_jPriceBuy.getText());
        myprod[6] = pricesell;
        myprod[7] = m_CategoryModel.getSelectedKey();
        myprod[8] = taxcatmodel.getSelectedKey();
        myprod[9] = attmodel.getSelectedKey();
        myprod[10] = Formats.CURRENCY.parseValue(m_jstockcost.getText());
        myprod[11] = Formats.DOUBLE.parseValue(m_jstockvolume.getText());        
        myprod[12] = m_jImage.getImage();
        myprod[13] = m_jComment.isSelected();
        myprod[14] = m_jScale.isSelected();
	myprod[15] = m_jConstant.isSelected();
	myprod[16] = m_jPrintKB.isSelected(); 
	myprod[17] = m_jSendStatus.isSelected();         
	myprod[18] = m_jService.isSelected();
        myprod[19] = Formats.BYTEA.parseValue(txtAttributes.getText());
        myprod[20] = m_jDisplay.getText();        
	myprod[21] = m_jVprice.isSelected();  
        myprod[22] = m_jVerpatrib.isSelected();
        myprod[23] = m_jTextTip.getText();
        myprod[24] = m_jCheckWarrantyReceipt.isSelected();
        myprod[25] = Formats.DOUBLE.parseValue(m_jStockUnits.getText());        
        myprod[26] = m_jPrintTo.getSelectedItem().toString();        
        myprod[27] = m_SuppliersModel.getSelectedKey();
        myprod[28] = m_UomModel.getSelectedKey();
        myprod[29] = Formats.TIMESTAMP.parseValue(m_jdate.getText());

        myprod[30] = m_jInCatalog.isSelected();
        myprod[31] = Formats.INT.parseValue(m_jCatalogOrder.getText());

        return myprod;        
    }

    /**
     *
     * @param value
     */
    @Override
    public void writeValueEdit(Object value) {

        reportlock = true;
        
        Object[] myprod = (Object[]) value;
        
        m_jTitle.setText(Formats.STRING.formatValue(myprod[1]) 
                + " - " + Formats.STRING.formatValue(myprod[4]));
        m_oId = myprod[0];
        m_jRef.setText(Formats.STRING.formatValue(myprod[1]));
        m_jCode.setText(Formats.STRING.formatValue(myprod[2]));
        m_jCodetype.setSelectedItem(myprod[3]);
        m_jName.setText(Formats.STRING.formatValue(myprod[4]));
        m_jPriceBuy.setText(Formats.CURRENCY.formatValue(myprod[5]));
        setPriceSell(myprod[6]);
        m_CategoryModel.setSelectedKey(myprod[7]);
        taxcatmodel.setSelectedKey(myprod[8]);
        attmodel.setSelectedKey(myprod[9]);
        m_jstockcost.setText(Formats.CURRENCY.formatValue(myprod[10]));
        m_jstockvolume.setText(Formats.DOUBLE.formatValue(myprod[11]));
//  JG 3 feb 16 speedup   m_jImage.setImage((BufferedImage) myprod[12]); 
        m_jImage.setImage(findImage(m_oId));       
        m_jComment.setSelected(((Boolean)myprod[13]));
        m_jScale.setSelected(((Boolean)myprod[14]));
	m_jConstant.setSelected(((Boolean)myprod[15]));
        m_jPrintKB.setSelected(((Boolean)myprod[16]));
        m_jSendStatus.setSelected(((Boolean)myprod[17]));        
	m_jService.setSelected(((Boolean)myprod[18]));
        txtAttributes.setText(Formats.BYTEA.formatValue(myprod[19]));
        m_jDisplay.setText(Formats.STRING.formatValue(myprod[20]));
	m_jVprice.setSelected(((Boolean)myprod[21]));
        m_jVerpatrib.setSelected(((Boolean)myprod[22]));
        m_jTextTip.setText(Formats.STRING.formatValue(myprod[23]));
        m_jCheckWarrantyReceipt.setSelected(((Boolean)myprod[24]));  
        m_jStockUnits.setText(Formats.DOUBLE.formatValue(myprod[25]));
        m_jPrintTo.setSelectedItem(myprod[26]);         
        m_SuppliersModel.setSelectedKey(myprod[27]);
        m_UomModel.setSelectedKey(myprod[28]);        

        m_jdate.setText(Formats.DATE.formatValue(myprod[29]));      
        
        m_jInCatalog.setSelected(((Boolean)myprod[30]));
        m_jCatalogOrder.setText(Formats.INT.formatValue(myprod[31]));
        
        txtAttributes.setCaretPosition(0);
        reportlock = false;

// Tab General
        m_jRef.setEnabled(true);
        m_jCode.setEnabled(true);
        m_jCodetype.setEnabled(true);
        m_jName.setEnabled(true);
        m_jCategory.setEnabled(true);
        m_jTax.setEnabled(true);
        m_jAtt.setEnabled(true);
        m_jVerpatrib.setEnabled(true);
        m_jUom.setEnabled(true);
        m_jPriceBuy.setEnabled(true);
        m_jPriceSell.setEnabled(true);
        m_jPriceSellTax.setEnabled(true);
        m_jmargin.setEnabled(true);
        m_jSupplier.setEnabled(true);        
        
// Tab Stock        
        m_jInCatalog.setEnabled(true);
        m_jConstant.setEnabled(true);
        m_jCatalogOrder.setEnabled(m_jInCatalog.isSelected());
        m_jPrintTo.setEnabled(true);
	m_jService.setEnabled(true);
        m_jCheckWarrantyReceipt.setEnabled(true);         
        m_jComment.setEnabled(true);
        m_jScale.setEnabled(true);
        m_jVprice.setEnabled(true);
        m_jstockcost.setEnabled(true);
        m_jstockvolume.setEnabled(true);   
        m_jdate.setEnabled(true);
        jTableProductStock.setVisible(false);
        jTableProductStock.setEnabled(true);
        
// Tab Image        
        m_jImage.setEnabled(true);

// Tab Button        
        m_jDisplay.setEnabled(true);
        m_jTextTip.setEnabled(true);
        colourChooser.setEnabled(true);
        setButtonHTML();
        
        resetTranxTable();     
        jTableProductStock.repaint();        

// Tab Properties
        txtAttributes.setEnabled(true);

        calculateMargin();
        calculatePriceSellTax();
        calculateGP();        
    }
    
    /**
     *
     * @param value
     */
    @Override
    public void writeValueDelete(Object value) {

        reportlock = true;
        Object[] myprod = (Object[]) value;
        m_jTitle.setText(Formats.STRING.formatValue(myprod[1]) 
                + " - " + Formats.STRING.formatValue(myprod[4]) 
                + " " + AppLocal.getIntString("label.recorddeleted"));
        
        m_oId = myprod[0];
        m_jRef.setText(Formats.STRING.formatValue(myprod[1]));
        m_jCode.setText(Formats.STRING.formatValue(myprod[2]));
        m_jCodetype.setSelectedItem(myprod[3]);
        m_jName.setText(Formats.STRING.formatValue(myprod[4]));
        m_jPriceBuy.setText(Formats.CURRENCY.formatValue(myprod[5]));
        setPriceSell(myprod[6]);
        m_CategoryModel.setSelectedKey(myprod[7]);
        taxcatmodel.setSelectedKey(myprod[8]);
        attmodel.setSelectedKey(myprod[9]);
        m_jstockcost.setText(Formats.CURRENCY.formatValue(myprod[10]));
        m_jstockvolume.setText(Formats.DOUBLE.formatValue(myprod[11]));
// JG 3 feb 16 speed test        m_jImage.setImage((BufferedImage) myprod[12]);
        m_jImage.setImage(findImage(m_oId));
        m_jComment.setSelected(((Boolean)myprod[13]));
        m_jScale.setSelected(((Boolean)myprod[14]));
	m_jConstant.setSelected(((Boolean)myprod[15]));
        m_jPrintKB.setSelected(((Boolean)myprod[16]));
        m_jService.setSelected(((Boolean)myprod[17]));      
        m_jSendStatus.setSelected(((Boolean)myprod[18]));
        txtAttributes.setText(Formats.BYTEA.formatValue(myprod[19]));
        m_jDisplay.setText(Formats.STRING.formatValue(myprod[20]));        
	m_jVprice.setSelected(((Boolean)myprod[21]));
        m_jVerpatrib.setSelected(((Boolean)myprod[22])); 
        m_jTextTip.setText(Formats.STRING.formatValue(myprod[23])); 
        m_jCheckWarrantyReceipt.setSelected(((Boolean)myprod[24]));
        m_jStockUnits.setText(Formats.DOUBLE.formatValue(myprod[25]));       
        m_jPrintTo.setSelectedItem(myprod[26]);                
        m_SuppliersModel.setSelectedKey(myprod[27]);
        m_UomModel.setSelectedKey(myprod[28]);   
        m_jdate.setText(Formats.DATE.formatValue(myprod[29]));
        
        m_jInCatalog.setSelected(((Boolean)myprod[30]));
        m_jCatalogOrder.setText(Formats.INT.formatValue(myprod[31]));

        txtAttributes.setCaretPosition(0);
        
        reportlock = false;

// Tab General
        m_jRef.setEnabled(false);
        m_jCode.setEnabled(false);
        m_jCodetype.setEnabled(false);
        m_jName.setEnabled(false);
        m_jCategory.setEnabled(false);
        m_jTax.setEnabled(false);
        m_jAtt.setEnabled(false);
        m_jVerpatrib.setEnabled(false); 
        m_jUom.setEnabled(false);
        m_jPriceBuy.setEnabled(false);
        m_jPriceSell.setEnabled(false);
        m_jPriceSellTax.setEnabled(false);
        m_jmargin.setEnabled(false);
        m_jPrintTo.setEnabled(false);
        
// Tab Stock
        m_jInCatalog.setEnabled(false);
        m_jConstant.setEnabled(false);
        m_jCatalogOrder.setEnabled(false);
        m_jSupplier.setEnabled(false);        
	m_jService.setEnabled(false);
        m_jCheckWarrantyReceipt.setEnabled(false);
        m_jComment.setEnabled(false);
        m_jScale.setEnabled(false);
        m_jVprice.setEnabled(false);        
        m_jstockcost.setEnabled(false);
        m_jstockvolume.setEnabled(false);
        stockModel = new ProductsEditor.StockTableModel(getProductOfName((String) m_oId));        
        jTableProductStock.setModel(stockModel);
        jTableProductStock.setEnabled(false);
        m_jdate.setEnabled(false);        

// Tab Image
        m_jImage.setEnabled(false);        
        
// Tab Button
        m_jDisplay.setEnabled(false);        
        m_jTextTip.setEnabled(false);
        colourChooser.setEnabled(false);
        
// Tab Properties        
        txtAttributes.setEnabled(false);

        calculateMargin();
        calculatePriceSellTax();
        calculateGP();
    }


    public void resetTranxTable() {

    jTableProductStock.getColumnModel().getColumn(0).setPreferredWidth(100);                    
    jTableProductStock.getColumnModel().getColumn(1).setPreferredWidth(50);                            
    jTableProductStock.getColumnModel().getColumn(2).setPreferredWidth(50);                
    jTableProductStock.getColumnModel().getColumn(3).setPreferredWidth(50);        
    
    jTableProductStock.repaint();
    
}
    
        /**
     *
     * @return this
     */
    @Override
    public Component getComponent() {
        return this;
    }

    private void setCode() {
        
        Long lDateTime= new Date().getTime();
        
        if (!reportlock) {
            reportlock = true;
            
            if (m_jRef == null) {
                m_jCode.setText(Long.toString(lDateTime));
            } else {
                if (m_jCode.getText()==null || "".equals(m_jCode.getText())){
                m_jCode.setText(m_jRef.getText());}
            }
            reportlock = false;
        }
    }
    
    private List<ProductStock> getProductOfName(String pId) {

        try {
            productStockList = dlSales.getProductStockList(pId);

        } catch (BasicException ex) {
            Logger.getLogger(ProductsEditor.class.getName()).log(Level.SEVERE, null, ex);
        }

        List<ProductStock> productList = new ArrayList<>();

        for (ProductStock productStock : productStockList) {
            String productId = productStock.getProductId();
            if (productId.equals(pId)) {
                productList.add(productStock);
            }
        }
        
        repaint();
        refresh();

        return productList;
    }

    public AppView getAppView() {
        return appView;
    }

    public void setAppView(AppView appView) {
        this.appView = appView;
    }

    class StockTableModel extends AbstractTableModel {
        String loc = AppLocal.getIntString("label.tblProdHeaderCol1");
        String qty = AppLocal.getIntString("label.tblProdHeaderCol2");
        String min = AppLocal.getIntString("label.tblProdHeaderCol3");
        String max = AppLocal.getIntString("label.tblProdHeaderCol4");

        List<ProductStock> stockList;
        String[] columnNames = {loc, qty, min, max};

        public StockTableModel(List<ProductStock> list) {
            stockList = list;
        }

        @Override
        public int getColumnCount() {
            return 4;
        }

        @Override
        public int getRowCount() {
            return stockList.size();
        }

        @Override
        public Object getValueAt(int row, int column) {
            ProductStock productStock = stockList.get(row);
        
            switch (column) {
                case 0:
                    return productStock.getLocation();                                        
                case 1:
                    return productStock.getUnits();
                case 2:
                    return productStock.getMinimum();
                case 3:
                    return productStock.getMaximum();
                case 4:
                    return productStock.getProductId();
                default:
                    return "";
            }
        }

        @Override
        public String getColumnName(int col) {
            return columnNames[col];
        }
    }    
    

    private void setDisplay(int btn) {
    
        String htmlString = (m_jDisplay.getText());
        String ohtmlString = "<html><center>" + m_jName.getText();

        switch (btn) {
            case 1:
                m_jDisplay.insert("<br>", m_jDisplay.getCaretPosition());
                break;
            case 2: 
                String hexcolor = color2HexString(colourChooser.getColor());                
                m_jDisplay.insert("<font color=" + hexcolor + ">"                
                    , m_jDisplay.getCaretPosition());
                break;
            case 3:
                m_jDisplay.insert("<font size=+2>"
                    , m_jDisplay.getCaretPosition());
                break;
            case 4:
                m_jDisplay.insert("<font size=-2>"
                    , m_jDisplay.getCaretPosition());
                break;
            case 5:
                m_jDisplay.insert("<b> </b>"
                    , m_jDisplay.getCaretPosition());
                break;
            case 6:
                m_jDisplay.insert("<i> </i>"
                    , m_jDisplay.getCaretPosition());
                break;
            case 7:
// defaults to file:/ for local disk
// http:// also usable for remote image                
                JFileChooser fc = new JFileChooser();
                FileFilter imageFilter = new FileNameExtensionFilter(
                    "Image files", ImageIO.getReaderFileSuffixes());
                fc.setFileFilter(imageFilter);                
                int returnValue = fc.showOpenDialog(null);
                File selectedFile = fc.getSelectedFile();
                if (selectedFile != null) {
                    m_jDisplay.insert("<img src=file:" +  selectedFile.getAbsolutePath() + ">"
                    , m_jDisplay.getCaretPosition());
                }
                break;                
                
            case 8: htmlString = ohtmlString;
                m_jDisplay.setText(htmlString);
                break;
            case 9: 
                m_jDisplay.insert("<div style=background-color:black;color:white;padding:10px;>"
                    , m_jDisplay.getCaretPosition());
                break;
            default: htmlString +="";
                m_jDisplay.setText(htmlString);                            
        }
    }

    private void setButtonHTML() {

        jButtonHTML.setText(m_jDisplay.getText());
    }
    
    public String color2HexString(Color color) {
        return "#" + Integer.toHexString(color.getRGB() & 0x00ffffff);
    }   

// 3 feb 16 speed test
    private BufferedImage findImage(Object id) {
        try {
            return (BufferedImage) loadimage.find(id);
        } catch (BasicException e) {
            return null;
        }
    }
// end of speed test
    private void calculateMargin() {

        if (!reportlock) {
            reportlock = true;

            Double dPriceBuy = readCurrency(m_jPriceBuy.getText());
            Double dPriceSell = (Double) pricesell;

            if (dPriceBuy == null || dPriceSell == null) {
                m_jmargin.setText(null);
            } else {
                m_jmargin.setText(Formats.PERCENT.formatValue(dPriceSell.doubleValue() / dPriceBuy.doubleValue() - 1.0));
            }
            reportlock = false;
        }
    }

    private void calculatePriceSellTax() {

        if (!reportlock) {
            reportlock = true;

            Double dPriceSell = (Double) pricesell;
            
            if (dPriceSell == null) {
                m_jPriceSellTax.setText(null);
            } else {
                double dTaxRate = taxeslogic.getTaxRate((TaxCategoryInfo) taxcatmodel.getSelectedItem());
                m_jPriceSellTax.setText(Formats.CURRENCY.formatValue(dPriceSell.doubleValue() * (1.0 + dTaxRate)));
            }
            reportlock = false;
        }
    }

        private void calculateGP() {

        if (!reportlock) {
            reportlock = true;

            Double dPriceBuy = readCurrency(m_jPriceBuy.getText());
            Double dPriceSell = (Double) pricesell;
 
            if (dPriceBuy == null || dPriceSell == null) {
                m_jGrossProfit.setText(null);
            } else {
                m_jGrossProfit.setText(Formats.PERCENT.formatValue(
                    (dPriceSell.doubleValue() - dPriceBuy.doubleValue())
                    /dPriceSell.doubleValue()));
            }
            reportlock = false;
        }
    }
    
    
    private void calculatePriceSellfromMargin() {

        if (!reportlock) {
            reportlock = true;

            Double dPriceBuy = readCurrency(m_jPriceBuy.getText());
            Double dMargin = readPercent(m_jmargin.getText());
            
            if (dMargin == null || dPriceBuy == null) {
                setPriceSell(null);
            } else {
                setPriceSell(dPriceBuy.doubleValue() * (1.0 + dMargin.doubleValue()));
            }

            reportlock = false;
        }

    }

    private void calculatePriceSellfromPST() {

        if (!reportlock) {
            reportlock = true;

            Double dPriceSellTax = readCurrency(m_jPriceSellTax.getText());

            if (dPriceSellTax == null) {
                setPriceSell(null);
            } else {
                double dTaxRate = taxeslogic.getTaxRate((TaxCategoryInfo) taxcatmodel.getSelectedItem());
                setPriceSell(dPriceSellTax.doubleValue() / (1.0 + dTaxRate));
            }

            reportlock = false;
        }
    }

    
    private void setPriceSell(Object value) {

        if (!priceselllock) {
            priceselllock = true;
            pricesell = value;
            m_jPriceSell.setText(Formats.CURRENCY.formatValue(pricesell));
            priceselllock = false;
        }
    }

    private class PriceSellManager implements DocumentListener {
        @Override
        public void changedUpdate(DocumentEvent e) {
            if (!priceselllock) {
                priceselllock = true;
                pricesell = readCurrency(m_jPriceSell.getText());
                priceselllock = false;
            }
            calculateMargin();
            calculatePriceSellTax();
            calculateGP();            
        }
        @Override
        public void insertUpdate(DocumentEvent e) {
            if (!priceselllock) {
                priceselllock = true;
                pricesell = readCurrency(m_jPriceSell.getText());
                priceselllock = false;
            }
            calculateMargin();
            calculatePriceSellTax();
            calculateGP();            
        }
        
        @Override
        public void removeUpdate(DocumentEvent e) {
            if (!priceselllock) {
                priceselllock = true;
                pricesell = readCurrency(m_jPriceSell.getText());
                priceselllock = false;
            }
            calculateMargin();
            calculatePriceSellTax();
            calculateGP();            
        }
    }

    private class FieldsManager implements DocumentListener, ActionListener {
        @Override
        public void changedUpdate(DocumentEvent e) {
            calculateMargin();
            calculatePriceSellTax();
            calculateGP();           
            
        }
        @Override
        public void insertUpdate(DocumentEvent e) {
            calculateMargin();
            calculatePriceSellTax();
            calculateGP();            
        }
        @Override
        public void removeUpdate(DocumentEvent e) {
            calculateMargin();
            calculatePriceSellTax();
            calculateGP();            
        }
        @Override
        public void actionPerformed(ActionEvent e) {
            calculateMargin();
            calculatePriceSellTax();
            calculateGP();            
        }
    }

    private class PriceTaxManager implements DocumentListener {
        @Override
        public void changedUpdate(DocumentEvent e) {
            calculatePriceSellfromPST();
            calculateMargin();
            calculateGP();            
        }
        @Override
        public void insertUpdate(DocumentEvent e) {
            calculatePriceSellfromPST();
            calculateMargin();
            calculateGP();            
        }
        @Override
        public void removeUpdate(DocumentEvent e) {
            calculatePriceSellfromPST();
            calculateMargin();
            calculateGP();            
        }
    }

    private class MarginManager implements DocumentListener  {
        @Override
        public void changedUpdate(DocumentEvent e) {
            calculatePriceSellfromMargin();
            calculatePriceSellTax();
            calculateGP();
        }
        @Override
        public void insertUpdate(DocumentEvent e) {
            calculatePriceSellfromMargin();
            calculatePriceSellTax();
            calculateGP();
        }
        @Override
        public void removeUpdate(DocumentEvent e) {
            calculatePriceSellfromMargin();
            calculatePriceSellTax();
            calculateGP();            
        }
    }

    private static Double readCurrency(String sValue) {
        try {
            return (Double) Formats.CURRENCY.parseValue(sValue);
        } catch (BasicException e) {
            return null;
        }
    }

    private static Double readPercent(String sValue) {
        try {
            return (Double) Formats.PERCENT.parseValue(sValue);
        } catch (BasicException e) {
            return null;
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        m_jTitle = new javax.swing.JLabel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        m_jRef = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        m_jCode = new javax.swing.JTextField();
        m_jCodetype = new javax.swing.JComboBox();
        jLabel2 = new javax.swing.JLabel();
        m_jName = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        m_jCategory = new javax.swing.JComboBox();
        jLabel13 = new javax.swing.JLabel();
        m_jAtt = new javax.swing.JComboBox();
        jLabel7 = new javax.swing.JLabel();
        m_jTax = new javax.swing.JComboBox();
        jLabel16 = new javax.swing.JLabel();
        m_jPriceSellTax = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        m_jPriceSell = new javax.swing.JTextField();
        jLabel19 = new javax.swing.JLabel();
        m_jmargin = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        m_jPriceBuy = new javax.swing.JTextField();
        m_jVerpatrib = new javax.swing.JCheckBox();
        m_jGrossProfit = new javax.swing.JTextField();
        jLabel22 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        m_jUom = new javax.swing.JComboBox();
        jLabel17 = new javax.swing.JLabel();
        m_jSupplier = new javax.swing.JComboBox();
        jBtnSupplier = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        m_jstockcost = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        m_jstockvolume = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        m_jInCatalog = new javax.swing.JCheckBox();
        jLabel18 = new javax.swing.JLabel();
        m_jCatalogOrder = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        m_jService = new javax.swing.JCheckBox();
        jLabel11 = new javax.swing.JLabel();
        m_jComment = new javax.swing.JCheckBox();
        jLabel12 = new javax.swing.JLabel();
        m_jScale = new javax.swing.JCheckBox();
        m_jConstant = new javax.swing.JCheckBox();
        jLabel14 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        m_jVprice = new javax.swing.JCheckBox();
        jLabel33 = new javax.swing.JLabel();
        m_jCheckWarrantyReceipt = new javax.swing.JCheckBox();
        jLabel23 = new javax.swing.JLabel();
        webLabel1 = new com.alee.laf.label.WebLabel();
        m_jPrintTo = new javax.swing.JComboBox();
        jBtnShowTrans = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTableProductStock = new com.alee.laf.table.WebTable();
        m_jPrintKB = new javax.swing.JCheckBox();
        m_jSendStatus = new javax.swing.JCheckBox();
        m_jStockUnits = new javax.swing.JTextField();
        jLblDate = new javax.swing.JLabel();
        m_jbtndate = new javax.swing.JButton();
        m_jdate = new javax.swing.JTextField();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtAttributes = new javax.swing.JTextArea();
        jBtnXml = new javax.swing.JButton();
        jPanel6 = new javax.swing.JPanel();
        jLabel34 = new javax.swing.JLabel();
        m_jImage = new com.openbravo.data.gui.JImageEditor();
        jPanel4 = new javax.swing.JPanel();
        jLabel28 = new javax.swing.JLabel();
        jButtonHTML = new javax.swing.JButton();
        jLabel21 = new javax.swing.JLabel();
        m_jTextTip = new javax.swing.JTextField();
        jScrollPane3 = new javax.swing.JScrollPane();
        m_jDisplay = new com.alee.laf.text.WebTextArea();
        jBtnBreak = new javax.swing.JButton();
        jBtnColour = new javax.swing.JButton();
        jBtnLarge = new javax.swing.JButton();
        jBtnSmall = new javax.swing.JButton();
        jBtnBold = new javax.swing.JButton();
        jBtnItalic = new javax.swing.JButton();
        jBtnImage = new javax.swing.JButton();
        jBtnReset = new javax.swing.JButton();
        jBtnStyle = new javax.swing.JButton();
        colourChooser = new com.alee.extended.colorchooser.WebColorPicker();

        setLayout(null);

        m_jTitle.setBackground(new java.awt.Color(255, 255, 255));
        m_jTitle.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        m_jTitle.setForeground(new java.awt.Color(102, 102, 102));
        m_jTitle.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        m_jTitle.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        m_jTitle.setFocusable(false);
        m_jTitle.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        m_jTitle.setOpaque(true);
        m_jTitle.setPreferredSize(new java.awt.Dimension(260, 25));
        add(m_jTitle);
        m_jTitle.setBounds(365, 5, 260, 25);

        jTabbedPane1.setToolTipText("");
        jTabbedPane1.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jTabbedPane1.setPreferredSize(new java.awt.Dimension(680, 420));

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("pos_messages"); // NOI18N
        jPanel1.setToolTipText(bundle.getString("tooltip.product.general.tab")); // NOI18N
        jPanel1.setPreferredSize(new java.awt.Dimension(0, 0));

        jLabel1.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/info.png"))); // NOI18N
        jLabel1.setText(AppLocal.getIntString("label.prodrefm")); // NOI18N
        jLabel1.setPreferredSize(new java.awt.Dimension(110, 30));
        jLabel1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel1MouseClicked(evt);
            }
        });

        m_jRef.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        m_jRef.setToolTipText("");
        m_jRef.setPreferredSize(new java.awt.Dimension(150, 30));
        m_jRef.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                m_jRefFocusLost(evt);
            }
        });

        jLabel6.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel6.setText(AppLocal.getIntString("label.prodbarcodem")); // NOI18N
        jLabel6.setPreferredSize(new java.awt.Dimension(100, 30));

        m_jCode.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        m_jCode.setPreferredSize(new java.awt.Dimension(125, 30));
        m_jCode.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                m_jCodeFocusLost(evt);
            }
        });

        m_jCodetype.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        m_jCodetype.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "EAN-13", "EAN-8", "CODE128", "Upc-A", "Upc-E" }));
        m_jCodetype.setPreferredSize(new java.awt.Dimension(80, 30));

        jLabel2.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel2.setText(AppLocal.getIntString("label.prodnamem")); // NOI18N
        jLabel2.setPreferredSize(new java.awt.Dimension(110, 30));

        m_jName.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        m_jName.setPreferredSize(new java.awt.Dimension(450, 30));
        m_jName.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                m_jNameFocusLost(evt);
            }
        });

        jLabel5.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel5.setText(AppLocal.getIntString("label.prodcategorym")); // NOI18N
        jLabel5.setPreferredSize(new java.awt.Dimension(110, 30));

        m_jCategory.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        m_jCategory.setBorder(null);
        m_jCategory.setPreferredSize(new java.awt.Dimension(450, 30));

        jLabel13.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel13.setText(AppLocal.getIntString("label.attributes")); // NOI18N
        jLabel13.setPreferredSize(new java.awt.Dimension(110, 30));

        m_jAtt.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        m_jAtt.setPreferredSize(new java.awt.Dimension(200, 30));

        jLabel7.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel7.setText(AppLocal.getIntString("label.taxcategorym")); // NOI18N
        jLabel7.setPreferredSize(new java.awt.Dimension(110, 30));

        m_jTax.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        m_jTax.setPreferredSize(new java.awt.Dimension(200, 30));

        jLabel16.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel16.setText(AppLocal.getIntString("label.prodpriceselltaxm")); // NOI18N
        jLabel16.setPreferredSize(new java.awt.Dimension(110, 30));

        m_jPriceSellTax.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        m_jPriceSellTax.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        m_jPriceSellTax.setPreferredSize(new java.awt.Dimension(200, 30));

        jLabel4.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel4.setText(AppLocal.getIntString("label.prodpricesell")); // NOI18N
        jLabel4.setPreferredSize(new java.awt.Dimension(110, 30));

        m_jPriceSell.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        m_jPriceSell.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        m_jPriceSell.setPreferredSize(new java.awt.Dimension(200, 30));

        jLabel19.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel19.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel19.setText(bundle.getString("label.margin")); // NOI18N
        jLabel19.setPreferredSize(new java.awt.Dimension(110, 30));

        m_jmargin.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        m_jmargin.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        m_jmargin.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        m_jmargin.setEnabled(false);
        m_jmargin.setPreferredSize(new java.awt.Dimension(110, 30));

        jLabel3.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel3.setText(AppLocal.getIntString("label.prodpricebuym")); // NOI18N
        jLabel3.setPreferredSize(new java.awt.Dimension(110, 30));

        m_jPriceBuy.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        m_jPriceBuy.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        m_jPriceBuy.setText("0");
        m_jPriceBuy.setPreferredSize(new java.awt.Dimension(200, 30));

        m_jVerpatrib.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        m_jVerpatrib.setText(bundle.getString("label.mandatory")); // NOI18N
        m_jVerpatrib.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        m_jVerpatrib.setPreferredSize(new java.awt.Dimension(49, 30));
        m_jVerpatrib.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                none(evt);
            }
        });

        m_jGrossProfit.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        m_jGrossProfit.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        m_jGrossProfit.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        m_jGrossProfit.setEnabled(false);
        m_jGrossProfit.setPreferredSize(new java.awt.Dimension(110, 30));

        jLabel22.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel22.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel22.setText(bundle.getString("label.grossprofit")); // NOI18N
        jLabel22.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jLabel22.setPreferredSize(new java.awt.Dimension(110, 30));

        jLabel26.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel26.setText(AppLocal.getIntString("label.UOM")); // NOI18N
        jLabel26.setPreferredSize(new java.awt.Dimension(70, 30));

        m_jUom.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        m_jUom.setPreferredSize(new java.awt.Dimension(150, 30));

        jLabel17.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel17.setText(AppLocal.getIntString("label.prodsupplier")); // NOI18N
        jLabel17.setPreferredSize(new java.awt.Dimension(110, 30));

        m_jSupplier.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        m_jSupplier.setPreferredSize(new java.awt.Dimension(200, 30));

        jBtnSupplier.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jBtnSupplier.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/customer_add_sml.png"))); // NOI18N
        jBtnSupplier.setText(bundle.getString("label.supplier")); // NOI18N
        jBtnSupplier.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBtnSupplierActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addContainerGap()
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addGap(10, 10, 10)
                            .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addContainerGap()
                            .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                            .addContainerGap()
                            .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(m_jPriceSell, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(m_jGrossProfit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(m_jUom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addComponent(jLabel22, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(18, 18, 18)
                                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(m_jmargin, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                        .addGap(115, 115, 115))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(m_jName, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(m_jAtt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(m_jVerpatrib, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(m_jTax, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(m_jPriceSellTax, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel26, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(m_jCategory, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addGap(50, 50, 50))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(m_jRef, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(m_jCode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(m_jCodetype, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(m_jPriceBuy, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(m_jSupplier, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jBtnSupplier)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(m_jRef, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(m_jCode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(m_jCodetype, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(m_jName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(m_jCategory, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(m_jAtt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(m_jVerpatrib, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(m_jTax, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(m_jPriceSellTax, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel26, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(m_jUom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(m_jPriceSell, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(m_jPriceBuy, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel22, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(m_jGrossProfit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(m_jmargin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(m_jSupplier, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jBtnSupplier))
                .addGap(14, 14, 14))
        );

        jTabbedPane1.addTab(AppLocal.getIntString("label.prodgeneral"), jPanel1); // NOI18N

        jPanel2.setToolTipText(bundle.getString("tooltip.product.stock.tab")); // NOI18N
        jPanel2.setPreferredSize(new java.awt.Dimension(0, 0));

        jLabel9.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel9.setText(AppLocal.getIntString("label.prodstockcost")); // NOI18N
        jLabel9.setPreferredSize(new java.awt.Dimension(110, 30));

        m_jstockcost.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        m_jstockcost.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        m_jstockcost.setPreferredSize(new java.awt.Dimension(85, 30));

        jLabel10.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel10.setText(AppLocal.getIntString("label.prodstockvol")); // NOI18N
        jLabel10.setPreferredSize(new java.awt.Dimension(110, 30));

        m_jstockvolume.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        m_jstockvolume.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        m_jstockvolume.setPreferredSize(new java.awt.Dimension(85, 30));

        jLabel8.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel8.setText(AppLocal.getIntString("label.prodincatalog")); // NOI18N
        jLabel8.setPreferredSize(new java.awt.Dimension(130, 30));

        m_jInCatalog.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        m_jInCatalog.setSelected(true);
        m_jInCatalog.setPreferredSize(new java.awt.Dimension(30, 30));
        m_jInCatalog.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jInCatalogActionPerformed(evt);
            }
        });

        jLabel18.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel18.setText(AppLocal.getIntString("label.prodorder")); // NOI18N
        jLabel18.setPreferredSize(new java.awt.Dimension(130, 30));

        m_jCatalogOrder.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        m_jCatalogOrder.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        m_jCatalogOrder.setPreferredSize(new java.awt.Dimension(50, 30));

        jLabel15.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel15.setText(bundle.getString("label.service")); // NOI18N
        jLabel15.setPreferredSize(new java.awt.Dimension(130, 30));

        m_jService.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        m_jService.setToolTipText("A Service Item will not be deducted from the Inventory");
        m_jService.setPreferredSize(new java.awt.Dimension(30, 30));

        jLabel11.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel11.setText(AppLocal.getIntString("label.prodaux")); // NOI18N
        jLabel11.setPreferredSize(new java.awt.Dimension(130, 30));

        m_jComment.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        m_jComment.setPreferredSize(new java.awt.Dimension(30, 30));

        jLabel12.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel12.setText(AppLocal.getIntString("label.prodscale")); // NOI18N
        jLabel12.setPreferredSize(new java.awt.Dimension(130, 30));

        m_jScale.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        m_jScale.setPreferredSize(new java.awt.Dimension(30, 30));

        m_jConstant.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        m_jConstant.setPreferredSize(new java.awt.Dimension(30, 30));

        jLabel14.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel14.setText(bundle.getString("label.prodconstant")); // NOI18N
        jLabel14.setPreferredSize(new java.awt.Dimension(130, 30));

        jLabel20.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel20.setText(bundle.getString("label.variableprice")); // NOI18N
        jLabel20.setPreferredSize(new java.awt.Dimension(130, 30));

        m_jVprice.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        m_jVprice.setPreferredSize(new java.awt.Dimension(30, 30));

        jLabel33.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel33.setText(bundle.getString("label.warranty")); // NOI18N
        jLabel33.setToolTipText(bundle.getString("label.warranty")); // NOI18N
        jLabel33.setPreferredSize(new java.awt.Dimension(130, 30));

        m_jCheckWarrantyReceipt.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        m_jCheckWarrantyReceipt.setText(bundle.getString("label.productreceipt")); // NOI18N
        m_jCheckWarrantyReceipt.setPreferredSize(new java.awt.Dimension(30, 30));

        jLabel23.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel23.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel23.setText(bundle.getString("label.prodminmax")); // NOI18N
        jLabel23.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        jLabel23.setPreferredSize(new java.awt.Dimension(531, 20));

        webLabel1.setText(bundle.getString("label.printto")); // NOI18N
        webLabel1.setToolTipText(bundle.getString("tooltip.printto")); // NOI18N
        webLabel1.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        webLabel1.setPreferredSize(new java.awt.Dimension(130, 30));

        m_jPrintTo.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        m_jPrintTo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "0", "1", "2", "3", "4", "5", "6" }));
        m_jPrintTo.setPreferredSize(new java.awt.Dimension(50, 30));

        jBtnShowTrans.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jBtnShowTrans.setText(bundle.getString("button.ProductStock")); // NOI18N
        jBtnShowTrans.setToolTipText("");
        jBtnShowTrans.setPreferredSize(new java.awt.Dimension(140, 25));
        jBtnShowTrans.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBtnShowTransActionPerformed(evt);
            }
        });

        jScrollPane2.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N

        jTableProductStock.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Location", "Current", "Minimum", "Maximum"
            }
        ));
        jTableProductStock.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jTableProductStock.setRowHeight(25);
        jScrollPane2.setViewportView(jTableProductStock);

        m_jPrintKB.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N

        m_jSendStatus.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N

        m_jStockUnits.setEditable(false);
        m_jStockUnits.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        m_jStockUnits.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        m_jStockUnits.setText("0");
        m_jStockUnits.setBorder(null);

        jLblDate.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLblDate.setText(bundle.getString("label.proddate")); // NOI18N
        jLblDate.setPreferredSize(new java.awt.Dimension(130, 30));

        m_jbtndate.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/date.png"))); // NOI18N
        m_jbtndate.setToolTipText("Open Calendar");
        m_jbtndate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jbtndateActionPerformed(evt);
            }
        });

        m_jdate.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        m_jdate.setPreferredSize(new java.awt.Dimension(160, 30));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel8, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(m_jCatalogOrder, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(m_jInCatalog, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(webLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(m_jPrintTo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(m_jConstant, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(m_jstockvolume, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(m_jstockcost, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(0, 0, 0))
                            .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)))
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addGroup(jPanel2Layout.createSequentialGroup()
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGroup(jPanel2Layout.createSequentialGroup()
                                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jLabel33, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(m_jService, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(m_jCheckWarrantyReceipt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(m_jComment, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(m_jScale, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(m_jVprice, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGroup(jPanel2Layout.createSequentialGroup()
                                    .addComponent(m_jPrintKB, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addComponent(m_jSendStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(m_jStockUnits, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGap(18, 18, 18)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 372, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                            .addComponent(jLblDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(m_jbtndate, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(0, 0, 0)
                            .addComponent(m_jdate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jBtnShowTrans, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(m_jstockcost, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(m_jConstant, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(m_jstockvolume, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(m_jInCatalog, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(m_jCatalogOrder, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(webLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(m_jPrintTo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jBtnShowTrans, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(m_jbtndate, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(m_jdate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLblDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 206, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(m_jService, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel33, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(m_jCheckWarrantyReceipt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(m_jComment, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(m_jScale, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(m_jVprice, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(m_jPrintKB, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(m_jSendStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(m_jStockUnits, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(42, 42, 42))
        );

        m_jService.getAccessibleContext().setAccessibleDescription("null");

        jTabbedPane1.addTab(AppLocal.getIntString("label.prodstock"), jPanel2); // NOI18N

        jPanel3.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        jPanel3.setToolTipText(bundle.getString("tooltip.product.properties.tab")); // NOI18N
        jPanel3.setPreferredSize(new java.awt.Dimension(0, 0));
        jPanel3.setLayout(new java.awt.BorderLayout());

        jScrollPane1.setPreferredSize(new java.awt.Dimension(680, 400));

        txtAttributes.setFont(new java.awt.Font("Monospaced", 0, 14)); // NOI18N
        txtAttributes.setLineWrap(true);
        txtAttributes.setWrapStyleWord(true);
        txtAttributes.setPreferredSize(new java.awt.Dimension(580, 300));
        jScrollPane1.setViewportView(txtAttributes);

        jPanel3.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        jBtnXml.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jBtnXml.setText(bundle.getString("button.injectxml")); // NOI18N
        jBtnXml.setToolTipText(bundle.getString("tooltip.xmlheader")); // NOI18N
        jBtnXml.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBtnXmlActionPerformed(evt);
            }
        });
        jPanel3.add(jBtnXml, java.awt.BorderLayout.PAGE_START);

        jTabbedPane1.addTab(AppLocal.getIntString("label.properties"), jPanel3); // NOI18N

        jPanel6.setToolTipText(bundle.getString("tooltip.product.image.tab")); // NOI18N
        jPanel6.setPreferredSize(new java.awt.Dimension(0, 0));

        jLabel34.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel34.setText(bundle.getString("label.imagesize")); // NOI18N
        jLabel34.setPreferredSize(new java.awt.Dimension(500, 30));

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(jLabel34, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(115, Short.MAX_VALUE))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(m_jImage, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(243, 243, 243))))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(m_jImage, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel34, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jTabbedPane1.addTab(bundle.getString("label.image"), jPanel6); // NOI18N

        jPanel4.setToolTipText(bundle.getString("tooltip.product.button.tab")); // NOI18N
        jPanel4.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jPanel4.setPreferredSize(new java.awt.Dimension(0, 0));

        jLabel28.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel28.setText(bundle.getString("label.prodbuttonhtml")); // NOI18N
        jLabel28.setPreferredSize(new java.awt.Dimension(250, 30));

        jButtonHTML.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jButtonHTML.setText(bundle.getString("button.htmltest")); // NOI18N
        jButtonHTML.setMargin(new java.awt.Insets(1, 1, 1, 1));
        jButtonHTML.setMaximumSize(new java.awt.Dimension(96, 72));
        jButtonHTML.setMinimumSize(new java.awt.Dimension(96, 72));
        jButtonHTML.setPreferredSize(new java.awt.Dimension(96, 72));
        jButtonHTML.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonHTMLActionPerformed(evt);
            }
        });

        jLabel21.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel21.setText(bundle.getString("label.texttip")); // NOI18N
        jLabel21.setPreferredSize(new java.awt.Dimension(110, 30));

        m_jTextTip.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        m_jTextTip.setPreferredSize(new java.awt.Dimension(400, 30));

        m_jDisplay.setColumns(20);
        m_jDisplay.setLineWrap(true);
        m_jDisplay.setRows(4);
        m_jDisplay.setWrapStyleWord(true);
        m_jDisplay.setPreferredSize(new java.awt.Dimension(160, 100));
        jScrollPane3.setViewportView(m_jDisplay);

        jBtnBreak.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jBtnBreak.setText(bundle.getString("button.prodhtmldisplayBreak")); // NOI18N
        jBtnBreak.setToolTipText("<html><center><h4>Inserts a Line Break<br> (a new line) for the button text");
        jBtnBreak.setPreferredSize(new java.awt.Dimension(70, 35));
        jBtnBreak.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBtnBreakActionPerformed(evt);
            }
        });

        jBtnColour.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jBtnColour.setForeground(new java.awt.Color(0, 204, 255));
        jBtnColour.setText(bundle.getString("button.prodhtmldisplayColour")); // NOI18N
        jBtnColour.setToolTipText("<html><center><h4>Set the colour <br>for the button text");
        jBtnColour.setPreferredSize(new java.awt.Dimension(70, 35));
        jBtnColour.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBtnColourActionPerformed(evt);
            }
        });

        jBtnLarge.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jBtnLarge.setText(bundle.getString("button.prodhtmldisplayLarge")); // NOI18N
        jBtnLarge.setToolTipText("<html><center><h4>Set the button<br> text to Large");
        jBtnLarge.setPreferredSize(new java.awt.Dimension(70, 35));
        jBtnLarge.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBtnLargeActionPerformed(evt);
            }
        });

        jBtnSmall.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jBtnSmall.setText(bundle.getString("button.prodhtmldisplaySmall")); // NOI18N
        jBtnSmall.setToolTipText("<html><center><h4>Set the button<br>text to Small");
        jBtnSmall.setPreferredSize(new java.awt.Dimension(70, 35));
        jBtnSmall.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBtnSmallActionPerformed(evt);
            }
        });

        jBtnBold.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jBtnBold.setText(bundle.getString("button.prodhtmldisplayBold")); // NOI18N
        jBtnBold.setToolTipText("<html><center><h4>Set the button<br> text to Italic");
        jBtnBold.setPreferredSize(new java.awt.Dimension(70, 35));
        jBtnBold.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBtnBoldActionPerformed(evt);
            }
        });

        jBtnItalic.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jBtnItalic.setText(bundle.getString("button.prodhtmldisplayItalic")); // NOI18N
        jBtnItalic.setToolTipText("<html><center><h4>Set the button<br> text to Italic");
        jBtnItalic.setPreferredSize(new java.awt.Dimension(70, 35));
        jBtnItalic.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBtnItalicActionPerformed(evt);
            }
        });

        jBtnImage.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jBtnImage.setText(bundle.getString("button.prodhtmldisplayImage")); // NOI18N
        jBtnImage.setToolTipText("<html><center><h4>Insert image from<br>local disk or internet URL");
        jBtnImage.setPreferredSize(new java.awt.Dimension(70, 35));
        jBtnImage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBtnImageActionPerformed(evt);
            }
        });

        jBtnReset.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jBtnReset.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/reload.png"))); // NOI18N
        jBtnReset.setText(bundle.getString("button.prodhtmldisplayReset")); // NOI18N
        jBtnReset.setPreferredSize(new java.awt.Dimension(100, 35));
        jBtnReset.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBtnResetActionPerformed(evt);
            }
        });

        jBtnStyle.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jBtnStyle.setText(bundle.getString("button.prodhtmldisplayStyle")); // NOI18N
        jBtnStyle.setToolTipText("<html><center><h4>Insert <style> tag to change<br>button background colour");
        jBtnStyle.setPreferredSize(new java.awt.Dimension(70, 35));
        jBtnStyle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBtnStyleActionPerformed(evt);
            }
        });

        colourChooser.setToolTipText(bundle.getString("tooltip.prodhtmldisplayColourChooser")); // NOI18N
        colourChooser.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                colourChooserActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel28, javax.swing.GroupLayout.PREFERRED_SIZE, 230, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(272, 272, 272)
                        .addComponent(jBtnReset, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(m_jTextTip, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(jBtnStyle, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jBtnBreak, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel4Layout.createSequentialGroup()
                                        .addComponent(jBtnColour, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jBtnLarge, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jBtnSmall, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel4Layout.createSequentialGroup()
                                        .addGap(13, 13, 13)
                                        .addComponent(colourChooser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(49, 49, 49)
                                        .addComponent(jButtonHTML, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jBtnBold, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jBtnItalic, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jBtnImage, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel28, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jBtnReset, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jBtnSmall, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jBtnColour, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jBtnLarge, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jBtnBold, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jBtnItalic, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jBtnImage, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jBtnStyle, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jBtnBreak, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButtonHTML, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(colourChooser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(m_jTextTip, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(58, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab(AppLocal.getIntString("label.button"), jPanel4); // NOI18N

        add(jTabbedPane1);
        jTabbedPane1.setBounds(10, 10, 630, 420);
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonHTMLActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonHTMLActionPerformed
        setButtonHTML();
    }//GEN-LAST:event_jButtonHTMLActionPerformed

    private void none(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_none

    }//GEN-LAST:event_none

    private void m_jNameFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_m_jNameFocusLost
        setDisplay(btn);
    }//GEN-LAST:event_m_jNameFocusLost

    private void m_jRefFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_m_jRefFocusLost
        setCode();
    }//GEN-LAST:event_m_jRefFocusLost

    private void jLabel1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel1MouseClicked

        if (evt.getClickCount() == 2) {
            String uuidString = m_oId.toString();
            StringSelection stringSelection = new StringSelection(uuidString);
            Clipboard clpbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
            clpbrd.setContents(stringSelection, null);
        
            JOptionPane.showMessageDialog(null, 
                AppLocal.getIntString("message.uuidcopy"));
        }
    }//GEN-LAST:event_jLabel1MouseClicked

    private void jBtnXmlActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtnXmlActionPerformed
        txtAttributes.setText(
            "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>  \n" +
            "<!DOCTYPE properties SYSTEM \"http://java.sun.com/dtd/properties.dtd\">\n" +
            "<properties>\n" +
            "    <entry key=\"identifier\">value</entry>\n" +
            "</properties>");
    }//GEN-LAST:event_jBtnXmlActionPerformed

    private void jBtnSmallActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtnSmallActionPerformed
        btn=4;
        setDisplay(btn);
    }//GEN-LAST:event_jBtnSmallActionPerformed

    private void jBtnBreakActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtnBreakActionPerformed
        btn=1;
        setDisplay(btn);
    }//GEN-LAST:event_jBtnBreakActionPerformed

    private void jBtnColourActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtnColourActionPerformed
        btn=2;
        setDisplay(btn);
    }//GEN-LAST:event_jBtnColourActionPerformed

    private void jBtnLargeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtnLargeActionPerformed
        btn=3;
        setDisplay(btn);
    }//GEN-LAST:event_jBtnLargeActionPerformed

    private void jBtnBoldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtnBoldActionPerformed
        btn=5;
        setDisplay(btn);
    }//GEN-LAST:event_jBtnBoldActionPerformed

    private void jBtnItalicActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtnItalicActionPerformed
        btn=6;
        setDisplay(btn);
    }//GEN-LAST:event_jBtnItalicActionPerformed

    private void jBtnImageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtnImageActionPerformed
        btn=7;
        setDisplay(btn);
    }//GEN-LAST:event_jBtnImageActionPerformed

    private void jBtnResetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtnResetActionPerformed
        btn=8;
        setDisplay(btn);
    }//GEN-LAST:event_jBtnResetActionPerformed

    private void jBtnStyleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtnStyleActionPerformed
        btn=9;
        setDisplay(btn);
    }//GEN-LAST:event_jBtnStyleActionPerformed

    private void jBtnSupplierActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtnSupplierActionPerformed
        JDialogNewSupplier dialog = JDialogNewSupplier.getDialog(this, appView);
        dialog.setVisible(true);
  
        if (dialog.getSelectedSupplier()!=null){
            try {
                m_SuppliersModel = new ComboBoxValModel(m_sentsuppliers.list());
                m_jSupplier.setModel(m_SuppliersModel);
            } catch (BasicException ex) {
                Logger.getLogger(ProductsEditor.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_jBtnSupplierActionPerformed

    private void colourChooserActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_colourChooserActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_colourChooserActionPerformed

    private void m_jCodeFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_m_jCodeFocusLost
        if (m_jCode.getText().length()< 8) {
            m_jCodetype.setSelectedIndex(2);
        }
    }//GEN-LAST:event_m_jCodeFocusLost

    private void m_jbtndateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jbtndateActionPerformed

        Date date;
        try {
            date = (Date) Formats.TIMESTAMP.parseValue(m_jdate.getText());
        } catch (BasicException e) {
            date = null;
        }
        date = JCalendarDialog.showCalendarTime(this, date);
        if (date != null) {
            m_jdate.setText(Formats.TIMESTAMP.formatValue(date));
        }
    }//GEN-LAST:event_m_jbtndateActionPerformed

    private void jBtnShowTransActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtnShowTransActionPerformed
        String pId = m_oId.toString();
        if (pId != null) {
            stockModel = new StockTableModel(getProductOfName(pId));
            jTableProductStock.setModel(stockModel);
            if (stockModel.getRowCount()> 0){
                jTableProductStock.setVisible(true);
            }else{
                jTableProductStock.setVisible(false);
                JOptionPane.showMessageDialog(null,
                    "No Stock Locations for this Product", "Locations", JOptionPane.INFORMATION_MESSAGE);
            }
            resetTranxTable();
        }
    }//GEN-LAST:event_jBtnShowTransActionPerformed

    private void m_jInCatalogActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jInCatalogActionPerformed

        if (m_jInCatalog.isSelected()) {
            m_jCatalogOrder.setEnabled(true);
        } else {
            m_jCatalogOrder.setEnabled(false);
            m_jCatalogOrder.setText(null);
        }
    }//GEN-LAST:event_m_jInCatalogActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.alee.extended.colorchooser.WebColorPicker colourChooser;
    private javax.swing.JButton jBtnBold;
    private javax.swing.JButton jBtnBreak;
    private javax.swing.JButton jBtnColour;
    private javax.swing.JButton jBtnImage;
    private javax.swing.JButton jBtnItalic;
    private javax.swing.JButton jBtnLarge;
    private javax.swing.JButton jBtnReset;
    private javax.swing.JButton jBtnShowTrans;
    private javax.swing.JButton jBtnSmall;
    private javax.swing.JButton jBtnStyle;
    private javax.swing.JButton jBtnSupplier;
    private javax.swing.JButton jBtnXml;
    private javax.swing.JButton jButtonHTML;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel jLblDate;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTabbedPane jTabbedPane1;
    private com.alee.laf.table.WebTable jTableProductStock;
    private javax.swing.JComboBox m_jAtt;
    private javax.swing.JTextField m_jCatalogOrder;
    private javax.swing.JComboBox m_jCategory;
    private javax.swing.JCheckBox m_jCheckWarrantyReceipt;
    private javax.swing.JTextField m_jCode;
    private javax.swing.JComboBox m_jCodetype;
    private javax.swing.JCheckBox m_jComment;
    private javax.swing.JCheckBox m_jConstant;
    private com.alee.laf.text.WebTextArea m_jDisplay;
    private javax.swing.JTextField m_jGrossProfit;
    private com.openbravo.data.gui.JImageEditor m_jImage;
    private javax.swing.JCheckBox m_jInCatalog;
    private javax.swing.JTextField m_jName;
    private javax.swing.JTextField m_jPriceBuy;
    private javax.swing.JTextField m_jPriceSell;
    private javax.swing.JTextField m_jPriceSellTax;
    private javax.swing.JCheckBox m_jPrintKB;
    private javax.swing.JComboBox m_jPrintTo;
    private javax.swing.JTextField m_jRef;
    private javax.swing.JCheckBox m_jScale;
    private javax.swing.JCheckBox m_jSendStatus;
    private javax.swing.JCheckBox m_jService;
    private javax.swing.JTextField m_jStockUnits;
    private javax.swing.JComboBox m_jSupplier;
    private javax.swing.JComboBox m_jTax;
    private javax.swing.JTextField m_jTextTip;
    private javax.swing.JLabel m_jTitle;
    private javax.swing.JComboBox m_jUom;
    private javax.swing.JCheckBox m_jVerpatrib;
    private javax.swing.JCheckBox m_jVprice;
    private javax.swing.JButton m_jbtndate;
    private javax.swing.JTextField m_jdate;
    private javax.swing.JTextField m_jmargin;
    private javax.swing.JTextField m_jstockcost;
    private javax.swing.JTextField m_jstockvolume;
    private javax.swing.JTextArea txtAttributes;
    private com.alee.laf.label.WebLabel webLabel1;
    // End of variables declaration//GEN-END:variables
    
}