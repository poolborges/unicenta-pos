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
import com.openbravo.data.gui.JMessageDialog;
import com.openbravo.data.gui.MessageInf;
import com.openbravo.data.loader.SentenceExec;
import com.openbravo.data.loader.SentenceList;
import com.openbravo.data.user.DirtyManager;
import com.openbravo.data.user.EditorRecord;
import com.openbravo.format.Formats;
import com.openbravo.pos.forms.AppLocal;
import com.openbravo.pos.forms.AppView;
import com.openbravo.pos.forms.DataLogicSales;
import java.awt.Component;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.swing.JPanel;
import com.alee.extended.time.ClockType;
import com.alee.extended.time.WebClock;
import com.alee.managers.notification.NotificationIcon;
import com.alee.managers.notification.NotificationManager;
import com.alee.managers.notification.WebNotification;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.table.AbstractTableModel;
import com.openbravo.pos.catalog.CategoryStock;
import java.awt.Font;
import javax.swing.table.JTableHeader;

/**
 *
 * @author adrianromero
 */
public final class CategoriesEditor extends JPanel implements EditorRecord {

    private Object m_oId;       
    private Object m_id;
    public String cId;
    
    private final SentenceList m_sentcat;
    private ComboBoxValModel m_CategoryModel;
    
    private final SentenceExec m_sentadd;
    private final SentenceExec m_sentdel;
    
    private List<CategoryStock> categoryStockList;
    private CategoriesEditor.StockTableModel stockModel;
    
    private final DataLogicSales dlSales;
        
    /** Creates new form JPanelCategories
     * @param app
     * @param dirty */
    public CategoriesEditor(AppView app, DirtyManager dirty) {
        
//        DataLogicSales dlSales = (DataLogicSales) app.getBean("com.openbravo.pos.forms.DataLogicSales");
        dlSales = (DataLogicSales) app.getBean("com.openbravo.pos.forms.DataLogicSales");
        
        initComponents();
             
        // El modelo de categorias
        m_sentcat = dlSales.getCategoriesList();
        m_CategoryModel = new ComboBoxValModel();
        
        m_sentadd = dlSales.getCatalogCategoryAdd();
        m_sentdel = dlSales.getCatalogCategoryDel();
        
        m_jName.getDocument().addDocumentListener(dirty);
        m_jCategory.addActionListener(dirty);
        m_jImage.addPropertyChangeListener("image", dirty);
        m_jCatNameShow.addActionListener(dirty);
        m_jTextTip.getDocument().addDocumentListener(dirty); 
        m_jCatOrder.getDocument().addDocumentListener(dirty);
        webSwtch_InCatalog.addActionListener(dirty);
     
        writeValueEOF();
    }
    
    /**
     *
     */
    @Override
    public void refresh() {
        
        List a;
        
        try {
            a = m_sentcat.list();
        } catch (BasicException eD) {
            MessageInf msg = new MessageInf(MessageInf.SGN_NOTICE, AppLocal.getIntString("message.cannotloadlists"), eD);
            msg.show(this);
            a = new ArrayList();
        }
        
        a.add(0, null);
        m_CategoryModel = new ComboBoxValModel(a);
        m_jCategory.setModel(m_CategoryModel);
        
        jLblProdCount.setText(null);        
    }
    
    /**
     *
     */
    @Override
    public void writeValueEOF() {
        m_id = null;
        m_jName.setText(null);
        m_CategoryModel.setSelectedKey(null);
        m_jImage.setImage(null);
        m_jName.setEnabled(false);
        m_jCategory.setEnabled(false);
        m_jImage.setEnabled(false);
        webSwtch_InCatalog.isSelected();
        m_jTextTip.setText(null);        
        m_jTextTip.setEnabled(false);
        m_jCatNameShow.setSelected(false);
        m_jCatNameShow.setEnabled(false);
// Added JG Feb 2017
        m_jCatOrder.setText(null); 
        m_jCatOrder.setEnabled(false);

    }
    
    /**
     *
     */
    @Override
    public void writeValueInsert() {
        m_id = UUID.randomUUID().toString();
        m_jName.setText(null);
        m_CategoryModel.setSelectedKey(null);
        m_jImage.setImage(null);
        m_jName.setEnabled(true);
        m_jCategory.setEnabled(true);
        m_jImage.setEnabled(true);
        webSwtch_InCatalog.setEnabled(false);
        m_jTextTip.setText(null);
        m_jTextTip.setEnabled(true);   
        m_jCatNameShow.setSelected(true);
        m_jCatNameShow.setEnabled(true);
// Added JG Feb 2017
        m_jCatOrder.setText(null); 
        m_jCatOrder.setEnabled(true); 

    }

    /**
     *
     * @param value
     */
    @Override
    public void writeValueDelete(Object value) {
        Object[] cat = (Object[]) value;
        m_id = cat[0];
        m_jName.setText(Formats.STRING.formatValue(cat[1]));
        m_CategoryModel.setSelectedKey(cat[2]);
        m_jImage.setImage((BufferedImage) cat[3]);
        m_jTextTip.setText(Formats.STRING.formatValue(cat[4]));
        m_jCatNameShow.setSelected(((Boolean)cat[5]).booleanValue());
        m_jCatOrder.setText(Formats.STRING.formatValue(cat[6]));
        
        m_jName.setEnabled(false);
        m_jCategory.setEnabled(false);
        m_jImage.setEnabled(false);
        webSwtch_InCatalog.setEnabled(false);
        m_jTextTip.setEnabled(false);     
        m_jCatNameShow.setEnabled(false);
        m_jCatOrder.setEnabled(false);   
        
        stockModel = new CategoriesEditor.StockTableModel(getProductOfName((String) m_oId));      
        
    }

    /**
     *
     * @param value
     */
    @Override
    public void writeValueEdit(Object value) {
        Object[] cat = (Object[]) value;
        m_id = cat[0];
        m_jName.setText(Formats.STRING.formatValue(cat[1]));
        m_CategoryModel.setSelectedKey(cat[2]);
        m_jImage.setImage((BufferedImage) cat[3]);
        m_jTextTip.setText(Formats.STRING.formatValue(cat[4])); 
        m_jCatNameShow.setSelected(((Boolean)cat[5]));

        if(m_jCatOrder.getText().length() == 0) {    
            m_jCatOrder.setText(null);        
        }
        m_jCatOrder.setText(Formats.STRING.formatValue(cat [6]));

        m_jName.setEnabled(true);
        m_jCategory.setEnabled(true);
        m_jImage.setEnabled(true);
        webSwtch_InCatalog.setEnabled(true);
        m_jTextTip.setEnabled(true); 
        m_jCatNameShow.setEnabled(true);
// Added JG Feb 2017
       m_jCatOrder.setEnabled(true);           

        resetTranxTable();   
    }

    /**
     *
     * @return
     * @throws BasicException
     */
    @Override
    public Object createValue() throws BasicException {
        
        Object[] cat = new Object[8];

        cat[0] = m_id;
        cat[1] = m_jName.getText();
        cat[2] = m_CategoryModel.getSelectedKey();
        cat[3] = m_jImage.getImage();
        cat[4] = m_jTextTip.getText();
        cat[5] = m_jCatNameShow.isSelected();
        if(m_jCatOrder.getText().length() == 0) {    
            m_jCatOrder.setText(null);        
        }        
        cat[6] = m_jCatOrder.getText();
        
        return cat;
    }

    /**
     *
     * @return
     */
    @Override
    public Component getComponent() {
        return this;
    }
    
public void resetTranxTable() {

    jTableCategoryStock.getColumnModel().getColumn(0).setPreferredWidth(250);
    
    // set font for headers
    Font f = new Font("Arial", Font.BOLD, 14);
    JTableHeader header = jTableCategoryStock.getTableHeader();
    header.setFont(f);
      
    jTableCategoryStock.getTableHeader().setReorderingAllowed(true); 
    jTableCategoryStock.setAutoCreateRowSorter(true);    
    jTableCategoryStock.repaint();    
}

    private List<CategoryStock> getProductOfName(String pId) {

        try {
            categoryStockList = dlSales.getCategorysProductList(pId);

        } catch (BasicException ex) {
            Logger.getLogger(CategoriesEditor.class.getName()).log(Level.SEVERE, null, ex);
        }

        List<CategoryStock> categoryList = new ArrayList<>();

        for (CategoryStock categoryStock : categoryStockList) {
            String categoryId = categoryStock.getCategoryId();
            if (categoryId.equals(pId)) {
                categoryList.add(categoryStock);
            }
        }
        
        repaint();
        refresh();

        return categoryList;
    }

    class StockTableModel extends AbstractTableModel {
        String nam = AppLocal.getIntString("label.prodname");
        String cod = AppLocal.getIntString("label.prodbarcode");

        List<CategoryStock> stockList;
        String[] columnNames = {nam, cod};

        public StockTableModel(List<CategoryStock> list) {
            stockList = list;
        }

        @Override
        public int getColumnCount() {
            return 2;
        }

        @Override
        public int getRowCount() {
            return stockList.size();
        }

        @Override
        public Object getValueAt(int row, int column) {
            CategoryStock categoryStock = stockList.get(row);
        
            switch (column) {
                case 0:
                    return categoryStock.getProductName();                                        
                case 1:
                    return categoryStock.getProductCode();
                case 2:
                    return categoryStock.getProductId();
                default:
                    return "";
            }
        }

        @Override
        public String getColumnName(int col) {
            return columnNames[col];
        }
    } 
    
    
    public void Notify(String msg){
        final WebNotification notification = new WebNotification ();
        notification.setIcon ( NotificationIcon.information );
        notification.setDisplayTime ( 4000 );

        final WebClock clock = new WebClock ();
        clock.setClockType ( ClockType.timer );
        clock.setTimeLeft ( 5000 );
        clock.setTimePattern ( msg );        
        notification.setContent ( clock );

        NotificationManager.showNotification ( notification );
        clock.start ();    
    } 
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jInternalFrame1 = new javax.swing.JInternalFrame();
        jLblName = new javax.swing.JLabel();
        m_jName = new javax.swing.JTextField();
        jLblCategory = new javax.swing.JLabel();
        m_jCategory = new javax.swing.JComboBox();
        jLblTextTip = new javax.swing.JLabel();
        m_jTextTip = new javax.swing.JTextField();
        jLblCatShowName = new javax.swing.JLabel();
        m_jCatNameShow = new javax.swing.JCheckBox();
        jLblCatOrder = new javax.swing.JLabel();
        m_jCatOrder = new javax.swing.JTextField();
        jLblInCat = new javax.swing.JLabel();
        webSwtch_InCatalog = new com.alee.extended.button.WebSwitch();
        m_jImage = new com.openbravo.data.gui.JImageEditor();
        jBtnShowTrans = new javax.swing.JButton();
        jLblProdCount = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTableCategoryStock = new com.alee.laf.table.WebTable();

        jInternalFrame1.setVisible(true);

        setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        setPreferredSize(new java.awt.Dimension(500, 500));

        jLblName.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLblName.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/info.png"))); // NOI18N
        jLblName.setText(AppLocal.getIntString("label.namem")); // NOI18N
        jLblName.setPreferredSize(new java.awt.Dimension(125, 30));
        jLblName.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLblNameMouseClicked(evt);
            }
        });

        m_jName.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        m_jName.setPreferredSize(new java.awt.Dimension(250, 30));

        jLblCategory.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLblCategory.setText(AppLocal.getIntString("label.prodcategory")); // NOI18N
        jLblCategory.setPreferredSize(new java.awt.Dimension(125, 30));

        m_jCategory.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        m_jCategory.setPreferredSize(new java.awt.Dimension(250, 30));

        jLblTextTip.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("pos_messages"); // NOI18N
        jLblTextTip.setText(bundle.getString("label.texttip")); // NOI18N
        jLblTextTip.setPreferredSize(new java.awt.Dimension(125, 30));

        m_jTextTip.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        m_jTextTip.setPreferredSize(new java.awt.Dimension(250, 30));

        jLblCatShowName.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLblCatShowName.setText(bundle.getString("label.subcategorytitle")); // NOI18N
        jLblCatShowName.setPreferredSize(new java.awt.Dimension(150, 30));

        m_jCatNameShow.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        m_jCatNameShow.setSelected(true);
        m_jCatNameShow.setPreferredSize(new java.awt.Dimension(30, 30));

        jLblCatOrder.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLblCatOrder.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLblCatOrder.setText(bundle.getString("label.ccatorder")); // NOI18N
        jLblCatOrder.setPreferredSize(new java.awt.Dimension(60, 30));

        m_jCatOrder.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        m_jCatOrder.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        m_jCatOrder.setPreferredSize(new java.awt.Dimension(60, 30));

        jLblInCat.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLblInCat.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLblInCat.setText(bundle.getString("label.CatalogueStatusYes")); // NOI18N
        jLblInCat.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        jLblInCat.setPreferredSize(new java.awt.Dimension(125, 30));

        webSwtch_InCatalog.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        webSwtch_InCatalog.setPreferredSize(new java.awt.Dimension(80, 30));
        webSwtch_InCatalog.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                webSwtch_InCatalogActionPerformed(evt);
            }
        });

        jBtnShowTrans.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jBtnShowTrans.setText(bundle.getString("button.CatProds")); // NOI18N
        jBtnShowTrans.setToolTipText("");
        jBtnShowTrans.setPreferredSize(new java.awt.Dimension(140, 30));
        jBtnShowTrans.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBtnShowTransActionPerformed(evt);
            }
        });

        jLblProdCount.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLblProdCount.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLblProdCount.setOpaque(true);
        jLblProdCount.setPreferredSize(new java.awt.Dimension(237, 30));

        jScrollPane2.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jScrollPane2.setPreferredSize(new java.awt.Dimension(340, 502));

        jTableCategoryStock.setAutoCreateRowSorter(true);
        jTableCategoryStock.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null}
            },
            new String [] {
                "Name", "Barcode"
            }
        ));
        jTableCategoryStock.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jTableCategoryStock.setGridColor(new java.awt.Color(102, 204, 255));
        jTableCategoryStock.setRowHeight(25);
        jTableCategoryStock.setSelectionBackground(new java.awt.Color(0, 120, 215));
        jTableCategoryStock.setShowVerticalLines(false);
        jScrollPane2.setViewportView(jTableCategoryStock);
        if (jTableCategoryStock.getColumnModel().getColumnCount() > 0) {
            jTableCategoryStock.getColumnModel().getColumn(0).setPreferredWidth(250);
        }

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLblInCat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(webSwtch_InCatalog, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addComponent(jLblName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addComponent(jLblCategory, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addComponent(jLblTextTip, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(m_jName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(m_jTextTip, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(m_jCategory, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLblCatShowName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(m_jCatNameShow, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(jLblCatOrder, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(m_jCatOrder, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jBtnShowTrans, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLblProdCount, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addComponent(m_jImage, javax.swing.GroupLayout.PREFERRED_SIZE, 263, javax.swing.GroupLayout.PREFERRED_SIZE))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(m_jName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLblName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(m_jCategory, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLblCategory, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(m_jTextTip, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLblTextTip, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLblInCat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(webSwtch_InCatalog, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLblCatShowName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(m_jCatNameShow, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLblCatOrder, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(m_jCatOrder, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLblProdCount, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jBtnShowTrans, javax.swing.GroupLayout.DEFAULT_SIZE, 32, Short.MAX_VALUE)))
                    .addComponent(m_jImage, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 231, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void webSwtch_InCatalogActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_webSwtch_InCatalogActionPerformed
        if (webSwtch_InCatalog.isSelected()) {

            try {
                Object param = m_id;
                m_sentdel.exec(param);
                m_sentadd.exec(param);
                jLblInCat.setText(AppLocal.getIntString("label.CatalogueStatusYes"));
                Notify(AppLocal.getIntString("notify.added"));                                                    
            } catch (BasicException e) {
                JMessageDialog.showMessage(this, new MessageInf(MessageInf.SGN_WARNING, AppLocal.getIntString("message.cannotexecute"), e));
            }            
        } else {

            try {
                m_sentdel.exec(m_id);
                jLblInCat.setText(AppLocal.getIntString("label.CatalogueStatusNo"));
                Notify(AppLocal.getIntString("notify.removed"));                                                                    
            } catch (BasicException e) {
                JMessageDialog.showMessage(this, new MessageInf(MessageInf.SGN_WARNING, AppLocal.getIntString("message.cannotexecute"), e));
            }            
        }
    }//GEN-LAST:event_webSwtch_InCatalogActionPerformed

    private void jLblNameMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLblNameMouseClicked

        if (evt.getClickCount() == 2) {
            String uuidString = m_id.toString();
            StringSelection stringSelection = new StringSelection(uuidString);
            Clipboard clpbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
            clpbrd.setContents(stringSelection, null);
        
            JOptionPane.showMessageDialog(null, 
                AppLocal.getIntString("message.uuidcopy"));
        }
    }//GEN-LAST:event_jLblNameMouseClicked

    private void jBtnShowTransActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtnShowTransActionPerformed
        String pId = m_id.toString();
        if (pId != null) {
            stockModel = new CategoriesEditor.StockTableModel(getProductOfName(pId));
            jTableCategoryStock.setModel(stockModel);

            if (stockModel.getRowCount()> 0){
                jTableCategoryStock.setVisible(true);
                String ProdCount = String.valueOf(stockModel.getRowCount());
                jLblProdCount.setText(ProdCount + " for " + m_jName.getText());                
            }else{
                jTableCategoryStock.setVisible(false);
                JOptionPane.showMessageDialog(null,
                    "No Products for this Category", "Products", JOptionPane.INFORMATION_MESSAGE);
            }
            resetTranxTable();
        }
    }//GEN-LAST:event_jBtnShowTransActionPerformed
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jBtnShowTrans;
    private javax.swing.JInternalFrame jInternalFrame1;
    private javax.swing.JLabel jLblCatOrder;
    private javax.swing.JLabel jLblCatShowName;
    private javax.swing.JLabel jLblCategory;
    private javax.swing.JLabel jLblInCat;
    private javax.swing.JLabel jLblName;
    private javax.swing.JLabel jLblProdCount;
    private javax.swing.JLabel jLblTextTip;
    private javax.swing.JScrollPane jScrollPane2;
    private com.alee.laf.table.WebTable jTableCategoryStock;
    private javax.swing.JCheckBox m_jCatNameShow;
    private javax.swing.JTextField m_jCatOrder;
    private javax.swing.JComboBox m_jCategory;
    private com.openbravo.data.gui.JImageEditor m_jImage;
    private javax.swing.JTextField m_jName;
    private javax.swing.JTextField m_jTextTip;
    private com.alee.extended.button.WebSwitch webSwtch_InCatalog;
    // End of variables declaration//GEN-END:variables
    
}