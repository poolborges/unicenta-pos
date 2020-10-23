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
import com.openbravo.data.gui.JMessageDialog;
import com.openbravo.data.gui.MessageInf;
import com.openbravo.pos.forms.AppLocal;
import com.openbravo.pos.forms.DataLogicSales;
import com.openbravo.pos.sales.TaxesLogic;
import com.openbravo.pos.ticket.CategoryInfo;
import com.openbravo.pos.ticket.ProductInfoExt;
import com.openbravo.pos.ticket.TaxInfo;
import com.openbravo.pos.util.ThumbNailBuilder;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import javax.swing.*;
import javax.swing.event.EventListenerList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 *
 * @author adrianromero
 */
public class JCatalog extends JPanel implements ListSelectionListener, CatalogSelector {
    
    /**
     *
     */
    protected EventListenerList listeners = new EventListenerList();
    private DataLogicSales m_dlSales;   
    private TaxesLogic taxeslogic;
    
    private boolean pricevisible;
    private boolean taxesincluded;
    
    // Set of Products panels
    private final Map<String, ProductInfoExt> m_productsset = new HashMap<>();
    
    // Set of Categoriespanels
     private final Set<String> m_categoriesset = new HashSet<>();
        
    private ThumbNailBuilder tnbbutton;
    private ThumbNailBuilder tnbcat;
    private ThumbNailBuilder tnbsubcat;
    
    private CategoryInfo showingcategory = null;
        
    /** Creates new form JCatalog
     * @param dlSales */
    public JCatalog(DataLogicSales dlSales) {
        this(dlSales, false, false, 90, 60);
    }

    /**
     *
     * @param dlSales
     * @param pricevisible
     * @param taxesincluded
     * @param width
     * @param height
     */
    public JCatalog(DataLogicSales dlSales, boolean pricevisible, 
            boolean taxesincluded, int width, int height) {
        
        m_dlSales = dlSales;
        this.pricevisible = pricevisible;
        this.taxesincluded = taxesincluded;
        
        initComponents();
        
        m_jListCategories.addListSelectionListener(this);
        
        m_jscrollcat.getVerticalScrollBar().setPreferredSize(new Dimension(35, 35));
        
        tnbcat = new ThumbNailBuilder(48, 48, "com/openbravo/images/category.png");  
        tnbsubcat = new ThumbNailBuilder(width, height, "com/openbravo/images/subcategory.png"); 
        tnbbutton = new ThumbNailBuilder(width, height, "com/openbravo/images/null.png");        

    }
    
    /**
     *
     * @return
     */
    @Override
    public Component getComponent() {
        return this;
    }
    
    /**
     *
     * @param id
     */
    @Override
    public void showCatalogPanel(String id) {
           
        if (id == null) {
            showRootCategoriesPanel();
        } else {       
            showProductPanel(id);
        }
    }
    
    /**
     *
     * @throws BasicException
     */
    @Override
    public void loadCatalog() throws BasicException {
        
        // delete all categories panel
        m_jProducts.removeAll();
        
        m_productsset.clear();        
        m_categoriesset.clear();
        
        showingcategory = null;
                
        // Load the taxes logic
        taxeslogic = new TaxesLogic(m_dlSales.getTaxList().list());

        // Load all categories.
        java.util.List<CategoryInfo> categories = m_dlSales.getRootCategories(); 
        
        // Select the first category
        m_jListCategories.setCellRenderer(new SmallCategoryRenderer());
        m_jListCategories.setModel(new CategoriesListModel(categories)); // aCatList

        if (m_jListCategories.getModel().getSize() == 0) {
            m_jscrollcat.setVisible(false);
            jPanel2.setVisible(false);
        } else {
            m_jscrollcat.setVisible(true);
            jPanel2.setVisible(true);
            m_jListCategories.setSelectedIndex(0);
        }
            
        showRootCategoriesPanel();
    }
    
    /**
     *
     * @param value
     */
    @Override
    public void setComponentEnabled(boolean value) {
        
        m_jListCategories.setEnabled(value);
        m_jscrollcat.setEnabled(value);
        m_lblIndicator.setEnabled(value);
        m_btnBack1.setEnabled(value);
        m_jProducts.setEnabled(value); 

        synchronized (m_jProducts.getTreeLock()) {
            int compCount = m_jProducts.getComponentCount();
            for (int i = 0 ; i < compCount ; i++) {
                m_jProducts.getComponent(i).setEnabled(value);
            }
        }
     
        this.setEnabled(value);
    }
    
    /**
     *
     * @param l
     */
    @Override
    public void addActionListener(ActionListener l) {
        listeners.add(ActionListener.class, l);
    }

    /**
     *
     * @param l
     */
    @Override
    public void removeActionListener(ActionListener l) {
        listeners.remove(ActionListener.class, l);
    }

    @Override
    public void valueChanged(ListSelectionEvent evt) {
        
        if (!evt.getValueIsAdjusting()) {
            int i = m_jListCategories.getSelectedIndex();
            if (i >= 0) {
                // Lo hago visible...
                Rectangle oRect = m_jListCategories.getCellBounds(i, i);
                m_jListCategories.scrollRectToVisible(oRect);       
            }
        }
    }

    /**
     *
     * @param prod
     */
    protected void fireSelectedProduct(ProductInfoExt prod) {
        EventListener[] l = listeners.getListeners(ActionListener.class);
        ActionEvent e = null;
        for (EventListener l1 : l) {
            if (e == null) {
                e = new ActionEvent(prod, ActionEvent.ACTION_PERFORMED, prod.getID());
            }
            ((ActionListener) l1).actionPerformed(e);	       
        }
    }   
    
    private void selectCategoryPanel(String catid) {
        try {
            if (!m_categoriesset.contains(catid)) {
                
                JCatalogTab jcurrTab = new JCatalogTab();     
                jcurrTab.applyComponentOrientation(getComponentOrientation());
                m_jProducts.add(jcurrTab, catid);
                m_categoriesset.add(catid);
               
                java.util.List<CategoryInfo> categories = m_dlSales.getSubcategories(catid);
                for (CategoryInfo cat : categories) {
                   
                    if (cat.getCatShowName()) {
                        jcurrTab.addButton(new ImageIcon(tnbsubcat.getThumbNailText
                        (cat.getImage(), cat.getName())), 
                            new SelectedCategory(cat),cat.getTextTip());
                    }else{
                        jcurrTab.addButton(new ImageIcon(
                        tnbsubcat.getThumbNailText(cat.getImage(), "")), 
                            new SelectedCategory(cat),cat.getTextTip());
                    }
                }

                java.util.List<ProductInfoExt> prods = m_dlSales.getProductConstant();
                for (ProductInfoExt prod : prods) {
                    jcurrTab.addButton(
                    new ImageIcon(tnbbutton.getThumbNailText(prod.getImage(), 
                    getProductLabel(prod))), 
                    new SelectedAction(prod), 
                    prod.getTextTip());
                }                
                
                java.util.List<ProductInfoExt> products = m_dlSales.getProductCatalog(catid);

                for (ProductInfoExt prod : products) {
                    jcurrTab.addButton(
                    new ImageIcon(tnbbutton.getThumbNailText(prod.getImage(), 
                    getProductLabel(prod))), 
                    new SelectedAction(prod),prod.getTextTip());
                }
            }
            
            CardLayout cl = (CardLayout)(m_jProducts.getLayout());
            cl.show(m_jProducts, catid);  
        } catch (BasicException e) {
            JMessageDialog.showMessage(this, new MessageInf(MessageInf.SGN_WARNING, 
                AppLocal.getIntString("message.notactive"), e));            
        }
    }
    
    private String getProductLabel(ProductInfoExt product) {

        if (pricevisible) {
            if (taxesincluded) {
                TaxInfo tax = taxeslogic.getTaxInfo(product.getTaxCategoryID());
                if(!"".equals(product.getDisplay())){
                    return "<html><center>" + product.getDisplay() + "<br>" + product.printPriceSellTax(tax);                
                } else {
                    return "<html><center>" + product.getName() + "<br>" + product.printPriceSellTax(tax);                                    
                }
            } else {
                if(!"".equals(product.getDisplay())){
                    return "<html><center>" + product.getDisplay() + "<br>" + product.printPriceSell();                
                } else {
                    return "<html><center>" + product.getName() + "<br>" + product.printPriceSell();                
                }                
            }
        } else {

            if (!"".equals(product.getDisplay())) {
                return product.getDisplay();                
            } else {
                return product.getName();
            }
        }
    }
    
    private void selectIndicatorPanel(Icon icon, String label, String texttip) {
        
        m_lblIndicator.setText(label);
        m_lblIndicator.setIcon(icon);
        
        // Show subcategories panel
        CardLayout cl = (CardLayout)(m_jCategories.getLayout());
        cl.show(m_jCategories, "subcategories");
    }
    
    private void selectIndicatorCategories() {
        // Show root categories panel
        CardLayout cl = (CardLayout)(m_jCategories.getLayout());
        cl.show(m_jCategories, "rootcategories");
    }
    
    private void showRootCategoriesPanel() {
        
        selectIndicatorCategories();
        // Show selected root category
        CategoryInfo cat = (CategoryInfo) m_jListCategories.getSelectedValue();
        
        if (cat != null) {
            selectCategoryPanel(cat.getID());
        }
        showingcategory = null;
    }
    
    private void showSubcategoryPanel(CategoryInfo category) {
// Modified JDL 13.04.13
// this is the new panel that displays when a sub catergory is selected mouse does not work here        
        selectIndicatorPanel(new ImageIcon(tnbsubcat.getThumbNail(
            category.getImage())),category.getName(), category.getTextTip());
        selectCategoryPanel(category.getID());
        showingcategory = category;
    }
   
    private void showProductPanel(String id) {
        
        ProductInfoExt product = m_productsset.get(id);

        if (product == null) {
            if (m_productsset.containsKey(id)) {
                // It is an empty panel
                if (showingcategory == null) {
                    showRootCategoriesPanel();                         
                } else {
                    showSubcategoryPanel(showingcategory);
                }
            } else {
                try {
                    // Create  products panel
                    java.util.List<ProductInfoExt> products = m_dlSales.getProductComments(id);

                    if (products.isEmpty()) {                    
                        m_productsset.put(id, null);

                        if (showingcategory == null) {
                            showRootCategoriesPanel();                         
                        } else {
                            showSubcategoryPanel(showingcategory);
                        }
                    } else {

                        product = m_dlSales.getProductInfo(id);
                        m_productsset.put(id, product);

                        JCatalogTab jcurrTab = new JCatalogTab();      
                        jcurrTab.applyComponentOrientation(getComponentOrientation());
                        m_jProducts.add(jcurrTab, "PRODUCT." + id);                        

                        // Add products
                        for (ProductInfoExt prod : products) {
                            jcurrTab.addButton(new ImageIcon(tnbbutton.getThumbNailText(prod.getImage(), 
                                getProductLabel(prod))), new SelectedAction(prod),prod.getTextTip());                            
                        }                       
                        selectIndicatorPanel(new ImageIcon(tnbbutton.getThumbNail(product.getImage())),
                            product.getDisplay(), product.getTextTip());                        
                        
                        CardLayout cl = (CardLayout)(m_jProducts.getLayout());
                        cl.show(m_jProducts, "PRODUCT." + id); 
                    }
                } catch (BasicException eb) {
                    m_productsset.put(id, null);
                    if (showingcategory == null) {
                        showRootCategoriesPanel();                         
                    } else {
                        showSubcategoryPanel(showingcategory);
                    }
                }
            }
        } else {
            selectIndicatorPanel(new ImageIcon(tnbbutton.getThumbNail(
                product.getImage())), product.getName(), product.getTextTip());            
            
            CardLayout cl = (CardLayout)(m_jProducts.getLayout());
            cl.show(m_jProducts, "PRODUCT." + id); 
        }
    }
    
    private class SelectedAction implements ActionListener {
        private final ProductInfoExt prod;
        public SelectedAction(ProductInfoExt prod) {
            this.prod = prod;
        }
        @Override
        public void actionPerformed(ActionEvent e) {
            fireSelectedProduct(prod);
        }
    }
    
    private class SelectedCategory implements ActionListener {
        private final CategoryInfo category;
        public SelectedCategory(CategoryInfo category) {
            this.category = category;
        }
        @Override
        public void actionPerformed(ActionEvent e) {
            showSubcategoryPanel(category);
        }
    }
    
    private class CategoriesListModel extends AbstractListModel {
        private final java.util.List m_aCategories;
        public CategoriesListModel(java.util.List aCategories) {
            m_aCategories = aCategories;
        }
        @Override
        public int getSize() { 
            return m_aCategories.size(); 
        }
        @Override
        public Object getElementAt(int i) {
            return m_aCategories.get(i);
        }    
    }
    
    private class SmallCategoryRenderer extends DefaultListCellRenderer {

        @Override
        public Component getListCellRendererComponent(JList list, Object value, 
          int index, boolean isSelected, boolean cellHasFocus) {

            super.getListCellRendererComponent(list, null, index, isSelected, cellHasFocus);
            CategoryInfo cat = (CategoryInfo) value;
            setText(cat.getName());
            setIcon(new ImageIcon(tnbcat.getThumbNail(cat.getImage())));

            return this;
        }      
    }            
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        m_jCategories = new javax.swing.JPanel();
        m_jRootCategories = new javax.swing.JPanel();
        m_jscrollcat = new javax.swing.JScrollPane();
        m_jListCategories = new javax.swing.JList();
        jPanel2 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        m_jSubCategories = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        m_lblIndicator = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        m_btnBack1 = new javax.swing.JButton();
        m_jProducts = new javax.swing.JPanel();

        setLayout(new java.awt.BorderLayout());

        m_jCategories.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        m_jCategories.setMaximumSize(new java.awt.Dimension(275, 600));
        m_jCategories.setPreferredSize(new java.awt.Dimension(265, 0));
        m_jCategories.setLayout(new java.awt.CardLayout());

        m_jRootCategories.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        m_jRootCategories.setMinimumSize(new java.awt.Dimension(200, 100));
        m_jRootCategories.setPreferredSize(new java.awt.Dimension(275, 130));
        m_jRootCategories.setLayout(new java.awt.BorderLayout());

        m_jscrollcat.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        m_jscrollcat.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        m_jscrollcat.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        m_jscrollcat.setPreferredSize(new java.awt.Dimension(265, 130));

        m_jListCategories.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        m_jListCategories.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        m_jListCategories.setFocusable(false);
        m_jListCategories.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                m_jListCategoriesValueChanged(evt);
            }
        });
        m_jscrollcat.setViewportView(m_jListCategories);

        m_jRootCategories.add(m_jscrollcat, java.awt.BorderLayout.WEST);

        jPanel2.setLayout(new java.awt.BorderLayout());

        jPanel3.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 5, 0, 5));
        jPanel3.setLayout(new java.awt.GridLayout(0, 1, 0, 5));
        jPanel2.add(jPanel3, java.awt.BorderLayout.NORTH);

        m_jRootCategories.add(jPanel2, java.awt.BorderLayout.LINE_END);

        m_jCategories.add(m_jRootCategories, "rootcategories");

        m_jSubCategories.setLayout(new java.awt.BorderLayout());

        jPanel4.setLayout(new java.awt.BorderLayout());

        m_lblIndicator.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        m_lblIndicator.setText("jLabel1");
        jPanel4.add(m_lblIndicator, java.awt.BorderLayout.NORTH);

        m_jSubCategories.add(jPanel4, java.awt.BorderLayout.WEST);

        jPanel1.setLayout(new java.awt.BorderLayout());

        jPanel5.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 5, 0, 5));
        jPanel5.setLayout(new java.awt.GridLayout(0, 1, 0, 5));

        m_btnBack1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/2uparrow.png"))); // NOI18N
        m_btnBack1.setFocusPainted(false);
        m_btnBack1.setFocusable(false);
        m_btnBack1.setMargin(new java.awt.Insets(8, 14, 8, 14));
        m_btnBack1.setPreferredSize(new java.awt.Dimension(60, 45));
        m_btnBack1.setRequestFocusEnabled(false);
        m_btnBack1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_btnBack1ActionPerformed(evt);
            }
        });
        jPanel5.add(m_btnBack1);

        jPanel1.add(jPanel5, java.awt.BorderLayout.NORTH);

        m_jSubCategories.add(jPanel1, java.awt.BorderLayout.LINE_END);

        m_jCategories.add(m_jSubCategories, "subcategories");

        add(m_jCategories, java.awt.BorderLayout.LINE_START);

        m_jProducts.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        m_jProducts.setLayout(new java.awt.CardLayout());
        add(m_jProducts, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void m_jListCategoriesValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_m_jListCategoriesValueChanged

        if (!evt.getValueIsAdjusting()) {
            CategoryInfo cat = (CategoryInfo) m_jListCategories.getSelectedValue();
            if (cat != null) {
                selectCategoryPanel(cat.getID());
            }
        }
        
    }//GEN-LAST:event_m_jListCategoriesValueChanged

    private void m_btnBack1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_btnBack1ActionPerformed

        showRootCategoriesPanel();

    }//GEN-LAST:event_m_btnBack1ActionPerformed

    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JButton m_btnBack1;
    private javax.swing.JPanel m_jCategories;
    private javax.swing.JList m_jListCategories;
    private javax.swing.JPanel m_jProducts;
    private javax.swing.JPanel m_jRootCategories;
    private javax.swing.JPanel m_jSubCategories;
    private javax.swing.JScrollPane m_jscrollcat;
    private javax.swing.JLabel m_lblIndicator;
    // End of variables declaration//GEN-END:variables
    
}
