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

package com.openbravo.pos.panels;

import com.openbravo.basic.BasicException;
import com.openbravo.data.gui.*;
import com.openbravo.data.loader.ComparatorCreator;
import com.openbravo.data.loader.Vectorer;
import com.openbravo.data.user.*;
import com.openbravo.pos.customers.CustomerInfoGlobal;
import com.openbravo.pos.forms.*;
import java.awt.BorderLayout;
import java.awt.Component;
import java.util.Collections;
import java.util.Comparator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;

/**
 *
 * @author adrianromero
 */
public abstract class JPanelTable extends JPanel implements JPanelView, BeanFactoryApp {
    
    /**
     *
     */
    protected BrowsableEditableData bd;    

    /**
     *
     */
    protected DirtyManager dirty;    

    /**
     *
     */
    protected AppView app;
    
    /** Creates new form JPanelTableEditor */
    public JPanelTable() {

        initComponents();
    }
    
    /**
     *
     * @param app
     * @throws BeanFactoryException
     */
    @Override
    public void init(AppView app) throws BeanFactoryException {
        
        this.app = app;
        dirty = new DirtyManager();
        bd = null;
        init();
    }

    /**
     *
     * @return
     */
    @Override
    public Object getBean() {
        return this;
    }
    
    /**
     *
     */
    protected void startNavigation() {
        
        if (bd == null) {
            
            // init browsable editable data
            bd = new BrowsableEditableData(getListProvider(), getSaveProvider(), getEditor(), dirty);

            // Add the filter panel
            Component c = getFilter();
            if (c != null) {
                c.applyComponentOrientation(getComponentOrientation());
                add(c, BorderLayout.NORTH);
            }

            // Add the editor
            c = getEditor().getComponent();
            if (c != null) {
                c.applyComponentOrientation(getComponentOrientation());                
                container.add(c, BorderLayout.CENTER);            
            }

            // el panel este
            ListCellRenderer cr = getListCellRenderer();
            if (cr != null) {
                JListNavigator nl = new JListNavigator(bd);
                nl.applyComponentOrientation(getComponentOrientation());
                if (cr != null) {
                    nl.setCellRenderer(cr);
                }
                container.add(nl, java.awt.BorderLayout.LINE_START);
            }

            // add toolbar extras
            c = getToolbarExtras();
            if (c != null) {
                c.applyComponentOrientation(getComponentOrientation());
                toolbar.add(c);
            }

            // La Toolbar
            c = new JLabelDirty(dirty);
            c.applyComponentOrientation(getComponentOrientation());
            toolbar.add(c);
            c = new JCounter(bd);
            c.applyComponentOrientation(getComponentOrientation());
            toolbar.add(c);
            c = new JNavigator(bd, getVectorer(), getComparatorCreator());
            c.applyComponentOrientation(getComponentOrientation());
            toolbar.add(c);
            c = new JSaver(bd);
            c.applyComponentOrientation(getComponentOrientation());
            toolbar.add(c);
        }
    }
    
    /**
     *
     * @return
     */
    public Component getToolbarExtras() {
        return null;
    }

    /**
     *
     * @return
     */
    public Component getFilter() {    
        return null;
    }
    
    /**
     *
     */
    protected abstract void init();
    
    /**
     *
     * @return
     */
    public abstract EditorRecord getEditor();
    
    /**
     *
     * @return
     */
    public abstract ListProvider getListProvider();
    
    /**
     *
     * @return
     */
    public abstract SaveProvider getSaveProvider();
    
    /**
     *
     * @return
     */
    public Vectorer getVectorer() {
        return null;
    }
    
    /**
     *
     * @return
     */
    public ComparatorCreator getComparatorCreator() {
        return null;
    }
    
    /**
     *
     * @return
     */
    public ListCellRenderer getListCellRenderer() {
        return null;
    }

    /**
     *
     * @return
     */
    @Override
    public JComponent getComponent() {
        return this;
    }

    /**
     *
     * @throws BasicException
     */
    @Override
    public void activate() throws BasicException {
        startNavigation();
        bd.actionLoad();
        
        //HS insert new customer 20.03.2014
        if (CustomerInfoGlobal.getInstance()!=null){
            bd.actionInsert();
    }    
    
    }

    /**
     *
     * @return
     */
    @Override
    public boolean deactivate() {

        try {
            return bd.actionClosingForm(this);
        } catch (BasicException eD) {
            MessageInf msg = new MessageInf(MessageInf.SGN_NOTICE, AppLocal.getIntString("message.CannotMove"), eD);
            msg.show(this);
            return false;
        }
    }  
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        container = new javax.swing.JPanel();
        toolbar = new javax.swing.JPanel();

        setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        setLayout(new java.awt.BorderLayout());

        container.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        container.setLayout(new java.awt.BorderLayout());
        container.add(toolbar, java.awt.BorderLayout.NORTH);

        add(container, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents
  
        
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel container;
    private javax.swing.JPanel toolbar;
    // End of variables declaration//GEN-END:variables
    
}
